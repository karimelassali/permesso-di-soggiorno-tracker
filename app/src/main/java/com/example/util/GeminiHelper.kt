package com.example.util

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

object GeminiHelper {
    private const val TAG = "GeminiHelper"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun askAssistant(prompt: String, guideContentJson: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is missing. Trying NVIDIA.")
            return@withContext generateNvidiaResponse(prompt, guideContentJson)
        }

        // Try stable endpoints
        val models = listOf("gemini-3.5-flash", "gemini-3.1-pro-preview")
        
        for (model in models) {
            try {
                val isAccessToken = apiKey.startsWith("AQ")
                val baseUrl = "https://generativelanguage.googleapis.com/v1/models/$model:generateContent"
                val url = if (isAccessToken) baseUrl else "$baseUrl?key=$apiKey"

                val fullPrompt = """
                    You are an expert Italian immigration assistant. Answer the user's question about the following guide.
                    
                    === GUIDE DATA ===
                    $guideContentJson
                    
                    === USER QUESTION ===
                    $prompt
                    
                    Provide a clear, helpful, and concise response. Adopt an empathetic tone, and match the language of the user's question (it, en, ar, es, or fr). Bold key steps or names. Keep it under 250 words.
                """.trimIndent()

                val root = JSONObject().apply {
                    put("contents", JSONArray().put(JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", fullPrompt)))
                    }))
                    put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", "You are Soggiorno Track's Immigration Assistant. Keep answers concise, accurate, and empathetic."))))
                }

                val requestBuilder = Request.Builder().url(url).post(root.toString().toRequestBody(mediaType))
                if (isAccessToken) {
                    requestBuilder.addHeader("Authorization", "Bearer $apiKey")
                }

                client.newCall(requestBuilder.build()).execute().use { response ->
                    val body = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        return@withContext JSONObject(body).getJSONArray("candidates")
                            .getJSONObject(0).getJSONObject("content")
                            .getJSONArray("parts").getJSONObject(0).getString("text")
                    } else {
                        Log.e(TAG, "Model $model failed: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error with model $model: ${e.message}")
            }
        }

        return@withContext generateNvidiaResponse(prompt, guideContentJson)
    }

    private suspend fun generateNvidiaResponse(prompt: String, guideContentJson: String): String = withContext(Dispatchers.IO) {
        val apiKey = "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"
        val url = "https://integrate.api.nvidia.com/v1/chat/completions"

        try {
            val root = JSONObject().apply {
                put("model", "meta/llama-3.3-70b-instruct")
                put("messages", JSONArray().put(JSONObject().apply {
                    put("role", "user")
                    put("content", "Answer based on this guide: $guideContentJson\n\nQuestion: $prompt")
                }))
                put("temperature", 0.5)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(root.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    return@withContext JSONObject(response.body?.string() ?: "").getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content")
                }
            }
        } catch (e: Exception) { }
        return@withContext "Sofia is temporarily unavailable. Please check your internet connection."
    }
}
