package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.coroutines.resume

data class ReceiptData(
    val fullName: String = "",
    val userId: String = "",
    val password: String = "",
    val numeroAssicurata: String = "",
    val numeroPratica: String = "",
    val rawText: String = ""
)

object ReceiptScannerHelper {

    suspend fun scanReceipt(context: Context, uri: Uri): Result<ReceiptData> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            try {
                val image = InputImage.fromFilePath(context, uri)
                processInputImage(image) { result ->
                    continuation.resume(result)
                }
            } catch (e: Exception) {
                // Fallback attempt: decode stream with downsampling to avoid memory crashes
                try {
                    val stream: InputStream? = context.contentResolver.openInputStream(uri)
                    val options = BitmapFactory.Options().apply { inSampleSize = 2 }
                    val bmp = BitmapFactory.decodeStream(stream, null, options)
                    stream?.close()
                    if (bmp != null) {
                        val image = InputImage.fromBitmap(bmp, 0)
                        processInputImage(image) { result ->
                            continuation.resume(result)
                        }
                        return@suspendCancellableCoroutine
                    }
                } catch (_: Exception) {}
                continuation.resume(Result.failure(e))
            }
        }
    }

    suspend fun scanReceipt(bitmap: Bitmap): Result<ReceiptData> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                processInputImage(image) { result ->
                    continuation.resume(result)
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }

    private fun processInputImage(image: InputImage, callback: (Result<ReceiptData>) -> Unit) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val parsed = parseReceiptText(visionText.text)
                callback(Result.success(parsed))
            }
            .addOnFailureListener { error ->
                callback(Result.failure(error))
            }
    }

    fun parseReceiptText(rawText: String): ReceiptData {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
        var userId = ""
        var password = ""
        var cognome = ""
        var nome = ""
        var detectedPratica = ""

        // 1. First pass: look for contextual labels (USER ID, PASSWORD, COGNOME, NOME, PRATICA)
        for (i in lines.indices) {
            val line = lines[i]
            val upper = line.uppercase()

            // Check for USER ID (Codice Ologramma a 12 cifre in alto a destra)
            if (upper.contains("USER") || upper.contains("OLOGRAMMA")) {
                val candidates = listOfNotNull(
                    extract12Digits(line),
                    if (i > 0) extract12Digits(lines[i - 1]) else null,
                    if (i < lines.size - 1) extract12Digits(lines[i + 1]) else null
                )
                if (candidates.isNotEmpty() && userId.isBlank()) {
                    userId = candidates.first()
                }
            }

            // Check for PASSWORD (sotto codice a barre a sinistra, es. 05598747694-1)
            if (upper.contains("PASSWORD") || upper.contains("PASSWD") || upper.contains("BARCODE") || upper == "PASS") {
                val candidates = listOfNotNull(
                    extractPasswordPattern(line),
                    if (i > 0) extractPasswordPattern(lines[i - 1]) else null,
                    if (i < lines.size - 1) extractPasswordPattern(lines[i + 1]) else null
                )
                if (candidates.isNotEmpty() && password.isBlank()) {
                    password = candidates.first()
                }
            }

            // Check for COGNOME (can be above the label on Italian Poste Mod. 22A forms)
            if (upper.contains("COGNOME")) {
                val inline = line.replace(Regex("(?i)COGNOME[:.]?"), "").trim()
                if (inline.isNotBlank()) {
                    cognome = cleanName(inline)
                } else {
                    val lineAbove = if (i > 0) lines[i - 1] else ""
                    val lineBelow = if (i < lines.size - 1) lines[i + 1] else ""
                    val isAboveValid = lineAbove.isNotBlank() && !isSystemWord(lineAbove)
                    if (isAboveValid) {
                        cognome = cleanName(lineAbove)
                    } else if (lineBelow.isNotBlank() && !isSystemWord(lineBelow)) {
                        cognome = cleanName(lineBelow)
                    }
                }
            }

            // Check for NOME (can be above the label)
            if (upper.contains("NOME") && !upper.contains("COGNOME")) {
                val inline = line.replace(Regex("(?i)NOME[:.]?"), "").trim()
                if (inline.isNotBlank()) {
                    nome = cleanName(inline)
                } else {
                    val lineAbove = if (i > 0) lines[i - 1] else ""
                    val lineBelow = if (i < lines.size - 1) lines[i + 1] else ""
                    val isAboveValid = lineAbove.isNotBlank() && !isSystemWord(lineAbove)
                    if (isAboveValid) {
                        nome = cleanName(lineAbove)
                    } else if (lineBelow.isNotBlank() && !isSystemWord(lineBelow)) {
                        nome = cleanName(lineBelow)
                    }
                }
            }

            // Check for Numero Pratica Polizia di Stato (e.g. 25PC006466 or 06RM123456)
            if (upper.contains("PRATICA") || upper.contains("ISTANZA")) {
                val cand = extractPraticaPattern(line)
                    ?: if (i < lines.size - 1) extractPraticaPattern(lines[i + 1]) else null
                if (cand != null && detectedPratica.isBlank()) {
                    detectedPratica = cand
                }
            }
        }

        // 2. Second pass: fallback if labels were displaced or slightly obscured
        val all12Digits = Regex("""\b\d{12}\b""").findAll(rawText).map { it.value }.toList()
        val allPasswords = Regex("""\b\d{10,11}-\d\b""").findAll(rawText).map { it.value }.toList()
        val allPratiche = Regex("""\b([0-9]{2}[A-Za-z]{2}[0-9]{5,7})\b""").findAll(rawText).map { it.value.uppercase() }.toList()

        if (password.isBlank() && allPasswords.isNotEmpty()) {
            password = allPasswords.first()
        }

        if (userId.isBlank()) {
            // Find 12-digit candidate that is not the password digits
            val cleanPassDigits = password.replace("-", "").replace(" ", "").trim()
            val found = all12Digits.firstOrNull { it != cleanPassDigits }
            if (found != null) {
                userId = found
            }
        }

        // If password is still empty but we have multiple 12 digits, second one could be password
        if (password.isBlank() && all12Digits.size >= 2) {
            val other = all12Digits.firstOrNull { it != userId }
            if (other != null) password = other
        }

        if (detectedPratica.isBlank() && allPratiche.isNotEmpty()) {
            detectedPratica = allPratiche.first()
        }

        // Clean and prepare Assicurata number (digits only)
        val cleanAssicurata = if (password.isNotBlank()) {
            password.replace("-", "").replace(" ", "").trim()
        } else if (all12Digits.isNotEmpty()) {
            all12Digits.first()
        } else ""

        // Combine Full Name
        val fullName = when {
            cognome.isNotBlank() && nome.isNotBlank() -> "$nome $cognome".trim()
            cognome.isNotBlank() -> cognome
            nome.isNotBlank() -> nome
            else -> ""
        }

        return ReceiptData(
            fullName = fullName,
            userId = userId,
            password = password,
            numeroAssicurata = cleanAssicurata,
            numeroPratica = detectedPratica,
            rawText = rawText
        )
    }

    private fun extract12Digits(text: String): String? {
        val match = Regex("""\b(\d{12})\b""").find(text)
        return match?.groupValues?.get(1)
    }

    private fun extractPasswordPattern(text: String): String? {
        val hyphenMatch = Regex("""\b(\d{10,11}-\d)\b""").find(text)
        if (hyphenMatch != null) return hyphenMatch.groupValues[1]

        val twelveMatch = Regex("""\b(\d{12})\b""").find(text)
        return twelveMatch?.groupValues?.get(1)
    }

    private fun extractPraticaPattern(text: String): String? {
        val match = Regex("""\b([0-9]{2}[A-Za-z]{2}[0-9]{5,7})\b""").find(text)
        return match?.groupValues?.get(1)?.uppercase()
    }

    private fun isSystemWord(text: String): Boolean {
        val up = text.uppercase().trim()
        return up.contains("POSTE") || up.contains("ACCETTAZIONE") ||
                up.contains("ASSICURATA") || up.contains("RICEVUTA") ||
                up.contains("DESTINATARIO") || up.contains("MITTENTE") ||
                up.contains("QUESTURA") || up.contains("MOD.") ||
                up.contains("PASSWORD") || up.contains("USER") ||
                up.contains("FRAZ.") || up.contains("NUM.")
    }

    private fun cleanName(text: String): String {
        return text.replace(Regex("""[^a-zA-Z\s'-]"""), "").trim()
            .split(" ")
            .filter { it.isNotBlank() && it.length > 1 }
            .joinToString(" ") { part ->
                part.lowercase().replaceFirstChar { it.uppercase() }
            }
    }
}
