package com.example.ai

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AiProviderTest {

    @Test
    fun testRealGeminiApiCall() = runBlocking {
        val provider = GeminiAiProvider()
        try {
            val response = provider.generateResponse(
                systemInstruction = "You are a helpful assistant.",
                history = emptyList(),
                userMessage = "Say hello in exactly 3 words."
            )
            println("=== GEMINI RESPONSE ===")
            println(response)
            println("=======================")
            assertNotNull(response)
            assertTrue(response.isNotBlank())
        } catch (e: Exception) {
            fail("Gemini AI API call failed with exception: ${e.message}")
        }
    }
}
