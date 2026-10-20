package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppRepository
import com.example.ui.CheckUiState
import com.example.ui.PermessoViewModel
import com.example.util.Strings
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
    fun `verify institutional demo placeholders and compliance localization strings`() {
        assertEquals("MARIO ROSSI", PermessoViewModel.DEMO_FULL_NAME)
        assertEquals("AA0000000", PermessoViewModel.DEMO_PASSPORT)
        assertEquals("050000000000", PermessoViewModel.DEMO_ASSICURATA)
        assertEquals("00XX00000000", PermessoViewModel.DEMO_ISTANZA)
        assertEquals("000000000000", PermessoViewModel.DEMO_USER_ID)
        assertEquals("Comune di Esempio", PermessoViewModel.DEMO_COMUNE)

        // Verify all 5 languages have the required compliance strings
        val languages = listOf("it", "en", "ar", "es", "fr")
        for (lang in languages) {
            assertTrue(Strings.get("pec_generator_title", lang).isNotBlank())
            assertTrue(Strings.get("pec_antispam_checkbox", lang).isNotBlank())
            assertTrue(Strings.get("paper_receipt_warning_title", lang).isNotBlank())
            assertTrue(Strings.get("paper_receipt_warning_body", lang).isNotBlank())
            assertTrue(Strings.get("travel_regulations_disclaimer", lang).isNotBlank())
            assertTrue(Strings.get("sofia_persistent_disclaimer", lang).isNotBlank())
            assertTrue(Strings.get("community_wait_estimates_title", lang).isNotBlank())
            assertTrue(Strings.get("portal_unreachable_msg", lang).isNotBlank())
        }

        val unreachableState = CheckUiState.PortalUnreachable()
        assertEquals(AppRepository.PORTAL_UNREACHABLE_MSG, unreachableState.message)
    }
}
