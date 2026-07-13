package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Abstract AI message representing a message in conversation history.
 */
data class AiMessage(
    val sender: String, // "user" or "sofia"
    val text: String
)

/**
 * Interface to support multiple AI backends (Gemini, local LLMs, mock, etc.).
 */
interface AiProvider {
    suspend fun generateResponse(
        systemInstruction: String,
        history: List<AiMessage>,
        userMessage: String
    ): String
}

/**
 * Direct REST implementation of the Google Gemini AI Model with NVIDIA Fallback.
 */
class GeminiAiProvider : AiProvider {
    private val modelName = "gemini-1.5-flash"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

    override suspend fun generateResponse(
        systemInstruction: String,
        history: List<AiMessage>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // Log the key format (safely)
        if (apiKey.length > 5) {
            Log.d("GeminiAi", "Using Gemini API Key starting with: ${apiKey.take(4)}")
        }

        // Removed the 'AIza' check to support different key formats.
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("GeminiAi", "Gemini API key is missing or placeholder. Falling back to NVIDIA.")
            return@withContext generateNvidiaResponse(systemInstruction, history, userMessage)
        }

        try {
            val url = URL("$baseUrl?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 30000
            conn.readTimeout = 30000

            val root = JSONObject()
            val contentsArray = JSONArray()

            // Gemini REQUIRES alternating roles: user, model, user, model...
            val processedHistory = mutableListOf<AiMessage>()
            history.forEach { msg ->
                if (processedHistory.isEmpty()) {
                    if (msg.sender == "user") processedHistory.add(msg)
                } else {
                    val last = processedHistory.last()
                    if (last.sender != msg.sender) {
                        processedHistory.add(msg)
                    } else {
                        // Merge consecutive messages from same sender
                        processedHistory[processedHistory.size - 1] = last.copy(text = last.text + "\n" + msg.text)
                    }
                }
            }

            // Add current user message
            if (processedHistory.isNotEmpty() && processedHistory.last().sender == "user") {
                val last = processedHistory.last()
                processedHistory[processedHistory.size - 1] = last.copy(text = last.text + "\n" + userMessage)
            } else {
                processedHistory.add(AiMessage("user", userMessage))
            }

            processedHistory.forEach { msg ->
                val contentObj = JSONObject()
                contentObj.put("role", if (msg.sender == "user") "user" else "model")
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", msg.text)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }

            root.put("contents", contentsArray)

            if (systemInstruction.isNotBlank()) {
                val sysInstructionObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemInstruction)
                sysPartsArray.put(sysPartObj)
                sysInstructionObj.put("parts", sysPartsArray)
                root.put("systemInstruction", sysInstructionObj)
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(root.toString()) }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(responseText)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val text = candidates.getJSONObject(0)
                        .optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")
                    if (text != null) return@withContext text
                }
                return@withContext "Error: The AI returned an empty response."
            } else {
                val errorMsg = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                Log.e("GeminiAi", "HTTP $responseCode: $errorMsg")
                // Fallback on failure
                return@withContext generateNvidiaResponse(systemInstruction, history, userMessage)
            }
        } catch (e: Exception) {
            Log.e("GeminiAi", "Connection failed: ${e.message}")
            return@withContext generateNvidiaResponse(systemInstruction, history, userMessage)
        }
    }

    private suspend fun generateNvidiaResponse(
        systemInstruction: String,
        history: List<AiMessage>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        // Use key from BuildConfig if available, otherwise fallback to hardcoded
        val apiKey = if (BuildConfig.NVIDIA_API_KEY.isNotBlank() && BuildConfig.NVIDIA_API_KEY != "MY_NVIDIA_API_KEY") {
            BuildConfig.NVIDIA_API_KEY
        } else {
            "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"
        }
        
        val model = "meta/llama-3.3-70b-instruct"
        val baseUrl = "https://integrate.api.nvidia.com/v1/chat/completions"

        try {
            val url = URL(baseUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            val root = JSONObject()
            root.put("model", model)
            root.put("temperature", 0.3)
            root.put("max_tokens", 1024)

            val messagesArray = JSONArray()

            if (systemInstruction.isNotBlank()) {
                messagesArray.put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemInstruction)
                })
            }

            history.forEach { msg ->
                messagesArray.put(JSONObject().apply {
                    put("role", if (msg.sender == "user") "user" else "assistant")
                    put("content", msg.text)
                })
            }

            messagesArray.put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })

            root.put("messages", messagesArray)

            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(root.toString()) }

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                return@withContext JSONObject(response)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                Log.e("NvidiaAi", "HTTP ${conn.responseCode}: $err")
                return@withContext "Error: Sofia is currently offline. Please check your internet or API keys."
            }
        } catch (e: Exception) {
            Log.e("NvidiaAi", "Connection failed: ${e.message}")
            return@withContext "Connection failed. Please check your internet connection."
        }
    }
}
