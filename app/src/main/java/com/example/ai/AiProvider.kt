package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiMessage(val sender: String, val text: String)

interface AiProvider {
    suspend fun generateResponse(systemInstruction: String, history: List<AiMessage>, userMessage: String): String
}

class GeminiAiProvider : AiProvider {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    override suspend fun generateResponse(systemInstruction: String, history: List<AiMessage>, userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("SofiaAI", "No Gemini API Key found in BuildConfig. Trying NVIDIA.")
            return@withContext generateNvidiaResponse(systemInstruction, history, userMessage)
        }

        // Try stable v1 first, then v1beta if needed
        val endpoints = listOf(
            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent",
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
        )

        var lastError = ""

        for (baseUrl in endpoints) {
            try {
                // Determine if we need to use a query param or a Bearer header
                val url = if (apiKey.startsWith("AIza")) "$baseUrl?key=$apiKey" else baseUrl
                
                val contents = JSONArray()
                history.forEach { msg ->
                    contents.put(JSONObject().apply {
                        put("role", if (msg.sender == "user") "user" else "model")
                        put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
                    })
                }
                contents.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
                })

                val root = JSONObject().apply {
                    put("contents", contents)
                    if (systemInstruction.isNotBlank()) {
                        put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstruction))))
                    }
                }

                val requestBuilder = Request.Builder().url(url).post(root.toString().toRequestBody(mediaType))
                
                // If it looks like a GCP token, add the header
                if (!apiKey.startsWith("AIza")) {
                    requestBuilder.addHeader("Authorization", "Bearer $apiKey")
                }

                client.newCall(requestBuilder.build()).execute().use { response ->
                    val body = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        return@withContext JSONObject(body).getJSONArray("candidates")
                            .getJSONObject(0).getJSONObject("content")
                            .getJSONArray("parts").getJSONObject(0).getString("text")
                    } else {
                        Log.e("SofiaAI", "Gemini Endpoint Failed ($baseUrl): ${response.code} - $body")
                        lastError = "Error ${response.code}"
                    }
                }
            } catch (e: Exception) {
                lastError = e.message ?: "Unknown"
                Log.e("SofiaAI", "Connection error: $lastError")
            }
        }

        return@withContext generateNvidiaResponse(systemInstruction, history, userMessage)
    }

    private suspend fun generateNvidiaResponse(systemInstruction: String, history: List<AiMessage>, userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.NVIDIA_API_KEY.ifBlank { "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" }
        val url = "https://integrate.api.nvidia.com/v1/chat/completions"

        try {
            val messages = JSONArray()
            if (systemInstruction.isNotBlank()) {
                messages.put(JSONObject().put("role", "system").put("content", systemInstruction))
            }
            history.forEach { msg ->
                messages.put(JSONObject().put("role", if (msg.sender == "user") "user" else "assistant").put("content", msg.text))
            }
            messages.put(JSONObject().put("role", "user").put("content", userMessage))

            val root = JSONObject().apply {
                put("model", "meta/llama-3.3-70b-instruct")
                put("messages", messages)
                put("temperature", 0.5)
                put("max_tokens", 1024)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(root.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json = JSONObject(body)
                    return@withContext json.getJSONArray("choices").getJSONObject(0)
                        .getJSONObject("message").getString("content")
                } else {
                    return@withContext "Sofia is currently undergoing maintenance. Error Code: ${response.code}"
                }
            }
        } catch (e: Exception) {
            return@withContext "Connection timeout. Please check your internet connection and try again."
        }
    }
}
