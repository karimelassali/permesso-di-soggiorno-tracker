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
            Log.e(TAG, "Gemini API key is not configured or is placeholder.")
            return@withContext "Error: Gemini API Key is missing. Please configure it in the Secrets Panel."
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
                    Log.e(TAG, "Error from Gemini API: code=$responseCode, response=$errorResponse")
                    return@withContext "Error calling AI Service: HTTP $responseCode"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API call", e)
            return@withContext "Failed to connect to AI Service. Please check your internet connection. Error: ${e.message}"
        }
    }
}
