package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Permesso Status Tracker", appName)
  }

  @Test
  fun `verify real ricevuta credentials against Portale Immigrazione and Polizia di Stato`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = AppRepository(context)

    // Test Portale Immigrazione with exact receipt credentials (including hyphen in password)
    val portaleResult = repository.checkPortaleStatus("550000133317", "05598747694-1")
    println("Portale Result: $portaleResult")
    assertTrue("Expected Portale check to succeed", portaleResult.isSuccess)
    assertEquals("055987476941", portaleResult.codiceAssicurata)
    assertEquals("587979509914", portaleResult.codiceIstanza)
    assertEquals("550000133317", portaleResult.codiceOlogramma)
    assertTrue(portaleResult.statusText.contains("In Preistruttoria", ignoreCase = true))
    assertTrue(portaleResult.dettaglioAnomalie.contains("Nessuna Anomalia Documentale", ignoreCase = true))
    assertTrue(portaleResult.convocazioneDate.contains("8 aprile 2025", ignoreCase = true))
    assertTrue(portaleResult.convocazioneLocation.contains("PIACENZA", ignoreCase = true))
    assertTrue(portaleResult.convocazioneMotivo.contains("Fotosegnalamento", ignoreCase = true))

    // Test Polizia di Stato with receipt number
    val poliziaResult = repository.checkPoliziaStatus("05598747694-1")
    println("Polizia Result: $poliziaResult")
    assertEquals("Non presente in archivio", poliziaResult.statusText)
  }
}
