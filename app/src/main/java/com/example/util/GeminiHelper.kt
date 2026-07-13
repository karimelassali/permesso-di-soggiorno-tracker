package com.example.util

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

object GeminiHelper {
    private const val TAG = "GeminiHelper"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    suspend fun askAssistant(prompt: String, guideContentJson: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or is placeholder. Falling back to NVIDIA AI.")
            return@withContext generateNvidiaResponse("You are an expert Italian immigration assistant. Keep answers concise, accurate, and empathetic. Answer in the same language as the user's query.", prompt, guideContentJson)
        }

        try {
            val url = URL("$BASE_URL?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            // Construct structured request payload
            val root = JSONObject()
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()

            // Construct user prompt + context
            val fullPrompt = """
                You are an expert Italian immigration assistant. Answer the user's question about the following guide.
                
                === GUIDE DATA ===
                $guideContentJson
                
                === USER QUESTION ===
                $prompt
                
                Provide a clear, helpful, and concise response. Adopt an empathetic tone, and match the language of the user's question (it, en, ar, es, or fr). Bold key steps or names. Keep it under 250 words.
            """.trimIndent()

            partObj.put("text", fullPrompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            root.put("contents", contentsArray)

            // System instructions
            val systemInstruction = JSONObject()
            val sysPartsArray = JSONArray()
            val sysPartObj = JSONObject()
            sysPartObj.put("text", "You are Soggiorno Track's Immigration Assistant. Keep answers concise, accurate, and empathetic. Answer in the same language as the user's query.")
            sysPartsArray.put(sysPartObj)
            systemInstruction.put("parts", sysPartsArray)
            root.put("systemInstruction", systemInstruction)

            // Write output
            OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                writer.write(root.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(conn.inputStream, "utf-8")).use { reader ->
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    
                    // Parse response
                    val jsonResponse = JSONObject(response.toString())
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        if (content != null) {
                            val parts = content.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                return@withContext parts.getJSONObject(0).optString("text", "No text response.")
                            }
                        }
                    }
                    return@withContext "Error: Unexpected response format from AI Service."
                }
            } else {
                BufferedReader(InputStreamReader(conn.errorStream, "utf-8")).use { reader ->
                    val errorResponse = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        errorResponse.append(line)
                    }
                    Log.e(TAG, "Error from Gemini API: code=$responseCode, response=$errorResponse. Falling back to NVIDIA.")
                }
                return@withContext generateNvidiaResponse("You are Soggiorno Track's Immigration Assistant.", prompt, guideContentJson)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API call. Falling back to NVIDIA.", e)
            return@withContext generateNvidiaResponse("You are Soggiorno Track's Immigration Assistant.", prompt, guideContentJson)
        }
    }

    private suspend fun generateNvidiaResponse(
        systemInstruction: String,
        prompt: String,
        guideContentJson: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = "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"
        val model = "meta/llama-3.3-70b-instruct"
        val baseUrl = "https://integrate.api.nvidia.com/v1/chat/completions"

        try {
            val url = URL(baseUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            val root = JSONObject()
            root.put("model", model)
            root.put("temperature", 0.3)
            root.put("max_tokens", 1024)

            val messagesArray = JSONArray()

            // 1. System Instruction as system role message
            if (systemInstruction.isNotBlank()) {
                val systemMsg = JSONObject()
                systemMsg.put("role", "system")
                systemMsg.put("content", systemInstruction)
                messagesArray.put(systemMsg)
            }

            // 2. User current message combining the prompt and context
            val userMsgObj = JSONObject()
            userMsgObj.put("role", "user")
            userMsgObj.put("content", """
                Answer the user's question about the following guide.
                
                === GUIDE DATA ===
                $guideContentJson
                
                === USER QUESTION ===
                $prompt
            """.trimIndent())
            messagesArray.put(userMsgObj)

            root.put("messages", messagesArray)

            OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                writer.write(root.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(conn.inputStream, "utf-8")).use { reader ->
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    val jsonResponse = JSONObject(response.toString())
                    val choices = jsonResponse.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        val firstChoice = choices.getJSONObject(0)
                        val message = firstChoice.optJSONObject("message")
                        if (message != null) {
                            return@withContext message.optString("content", "No content text.")
                        }
                    }
                    return@withContext "Error: No response text from NVIDIA AI."
                }
            } else {
                BufferedReader(InputStreamReader(conn.errorStream, "utf-8")).use { reader ->
                    val errorResponse = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        errorResponse.append(line)
                    }
                    Log.e("NvidiaAi", "Error from Nvidia API: code=$responseCode, response=$errorResponse")
                    return@withContext "Error: NVIDIA Service responded with HTTP $responseCode"
                }
            }
        } catch (e: Exception) {
            Log.e("NvidiaAi", "Exception during NVIDIA API call", e)
            return@withContext "Failed to connect to NVIDIA AI Service. Error: ${e.message}"
        }
    }
}
