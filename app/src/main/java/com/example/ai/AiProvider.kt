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
 * Direct REST implementation of the Google Gemini 3.5 Flash Model.
 */
class GeminiAiProvider : AiProvider {
    private val modelName = "gemini-3.5-flash"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

    override suspend fun generateResponse(
        systemInstruction: String,
        history: List<AiMessage>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("GeminiAiProvider", "Gemini API key is not configured or is placeholder.")
            return@withContext "ERR_NO_KEY"
        }

        try {
            val url = URL("$baseUrl?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            // 1. Root Object
            val root = JSONObject()

            // 2. Contents Array (History + Current query)
            val contentsArray = JSONArray()

            // Append historical messages
            history.forEach { msg ->
                val contentObj = JSONObject()
                val role = if (msg.sender == "user") "user" else "model"
                contentObj.put("role", role)
                
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", msg.text)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                
                contentsArray.put(contentObj)
            }

            // Append final user message
            val lastContentObj = JSONObject()
            lastContentObj.put("role", "user")
            val lastPartsArray = JSONArray()
            val lastPartObj = JSONObject()
            lastPartObj.put("text", userMessage)
            lastPartsArray.put(lastPartObj)
            lastContentObj.put("parts", lastPartsArray)
            contentsArray.put(lastContentObj)

            root.put("contents", contentsArray)

            // 3. System Instructions
            if (systemInstruction.isNotBlank()) {
                val sysInstructionObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemInstruction)
                sysPartsArray.put(sysPartObj)
                sysInstructionObj.put("parts", sysPartsArray)
                root.put("systemInstruction", sysInstructionObj)
            }

            // Write output stream
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
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        if (content != null) {
                            val parts = content.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                return@withContext parts.getJSONObject(0).optString("text", "No response text.")
                            }
                        }
                    }
                    return@withContext "Error: No text response from AI Service."
                }
            } else {
                BufferedReader(InputStreamReader(conn.errorStream, "utf-8")).use { reader ->
                    val errorResponse = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        errorResponse.append(line)
                    }
                    Log.e("GeminiAiProvider", "Error from Gemini API: code=$responseCode, response=$errorResponse")
                    return@withContext "Error: AI Service responded with HTTP $responseCode"
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiAiProvider", "Exception during Gemini API call", e)
            return@withContext "Failed to connect to AI Service. Error: ${e.message}"
        }
    }
}
