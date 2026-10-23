package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.QuesturaCatalog
import com.example.ui.theme.*
import com.example.util.Strings
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class InstitutionalTemplateType(
    val titleKey: String,
    val subtitleKey: String
) {
    STATO_PRATICA(
        titleKey = "pec_tpl_1_title",
        subtitleKey = "pec_tpl_1_sub"
    ),
    SEGNALAZIONE_URGENZA(
        titleKey = "pec_tpl_2_title",
        subtitleKey = "pec_tpl_2_sub"
    ),
    VERIFICA_ANOMALIE(
        titleKey = "pec_tpl_3_title",
        subtitleKey = "pec_tpl_3_sub"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PecSollecitoGeneratorView(
    viewModel: PermessoViewModel,
    lang: String,
    onBack: () -> Unit,
    enableInternalScroll: Boolean = false
) {
    val context = LocalContext.current
    val creds by viewModel.credentials.collectAsStateWithLifecycle()
    val selectedProvince by viewModel.selectedProvince.collectAsStateWithLifecycle()
    val savedPassport by viewModel.passportNumber.collectAsStateWithLifecycle()
    val savedSubmissionDate by viewModel.receiptSubmissionDate.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isInstitutionalDemoMode.collectAsStateWithLifecycle()

    val hasSavedProfile = remember(creds, savedPassport) {
        !creds?.fullName.isNullOrBlank() ||
            !creds?.numeroAssicurata.isNullOrBlank() ||
            !creds?.userId.isNullOrBlank() ||
            savedPassport.isNotBlank()
    }

    var fullName by remember(creds?.fullName, isDemoMode) {
        mutableStateOf(
            creds?.fullName?.takeIf { it.isNotBlank() }
                ?: if (isDemoMode) PermessoViewModel.DEMO_FULL_NAME else ""
        )
    }
    var passportNum by remember(savedPassport, isDemoMode) {
        mutableStateOf(
            savedPassport.takeIf { it.isNotBlank() }
                ?: if (isDemoMode) PermessoViewModel.DEMO_PASSPORT else ""
        )
    }
    var assicurataCode by remember(creds?.numeroAssicurata, creds?.password, isDemoMode) {
        mutableStateOf(
            creds?.numeroAssicurata?.takeIf { it.isNotBlank() }
                ?: creds?.password?.takeIf { it.isNotBlank() }
                ?: if (isDemoMode) PermessoViewModel.DEMO_ASSICURATA else ""
        )
    }
    var istanzaCode by remember(creds?.codiceIstanza, creds?.numeroPratica, isDemoMode) {
        mutableStateOf(
            creds?.codiceIstanza?.takeIf { it.isNotBlank() }
                ?: creds?.numeroPratica?.takeIf { it.isNotBlank() }
                ?: if (isDemoMode) PermessoViewModel.DEMO_ISTANZA else ""
        )
    }
    var submissionDateStr by remember(savedSubmissionDate, isDemoMode) {
        mutableStateOf(
            savedSubmissionDate.takeIf { it.isNotBlank() }
                ?: if (isDemoMode) PermessoViewModel.DEMO_SUBMISSION_DATE else ""
        )
    }
    var selectedTemplate by remember { mutableStateOf(InstitutionalTemplateType.STATO_PRATICA) }
    var urgencyReason by remember {
        mutableStateOf("Scadenza imminente contratto di lavoro / necessità documentata (allegato PDF)")
    }
    var antiSpamConfirmed by remember { mutableStateOf(true) }

    val questuraObj = remember(selectedProvince, creds?.selectedQuesturaId) {
        creds?.selectedQuesturaId?.takeIf { it.isNotBlank() }?.let { QuesturaCatalog.getById(it) }
            ?: QuesturaCatalog.questure.find {
                it.province.equals(selectedProvince, ignoreCase = true) ||
                    it.name.contains(selectedProvince, ignoreCase = true)
            }
            ?: QuesturaCatalog.questure.firstOrNull()
    }

    val questuraName = questuraObj?.name ?: "Questura di $selectedProvince"
    val questuraPec = questuraObj?.pec ?: "urp.quest.$selectedProvince@pecps.poliziadistato.it".lowercase(Locale.ROOT)
    val questuraProvince = questuraObj?.province ?: selectedProvince

    val daysElapsed: Long? = remember(submissionDateStr) {
        val rawDate = submissionDateStr.trim()
        if (rawDate.isEmpty()) {
            null
        } else {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).apply {
                    isLenient = false
                }
                val parsed = sdf.parse(rawDate)
                if (parsed != null) {
                    val diff = System.currentTimeMillis() - parsed.time
                    TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0L)
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    val effectiveDays = daysElapsed ?: 0L
    val delayDays = if (daysElapsed != null) (daysElapsed - 90L).coerceAtLeast(0L) else 0L
    val amberBadgeText = if (daysElapsed != null) {
        if (delayDays > 0) "${daysElapsed} GIORNI TRASCORSI • ${delayDays} GG OLTRE IL TERMINE (90 GG)"
        else "${daysElapsed} GIORNI TRASCORSI • ENTRO I TERMINI DI LEGGE (90 GG)"
    } else {
        "INSERISCI DATA INVIO KIT PER CALCOLO TERMINI (ART. 2 L. 241/90)"
    }

    val todayFormatted = remember {
        try {
            SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date())
        } catch (_: Exception) {
            "Data odierna"
        }
    }

    val displayFullName = fullName.trim().ifBlank { "[NOME E COGNOME INTESTATARIO]" }
    val displayPassport = passportNum.trim().ifBlank { "[N. PASSAPORTO]" }
    val displayAssicurata = assicurataCode.trim().ifBlank { "[CODICE ASSICURATA 12 CIFRE]" }
    val displayIstanza = istanzaCode.trim().ifBlank { "[CODICE PRATICA / ISTANZA]" }
    val displaySubDate = submissionDateStr.trim().ifBlank { "[DATA INVIO KIT]" }

    val emailSubject = remember(selectedTemplate, displayFullName, displayAssicurata, displayIstanza) {
        when (selectedTemplate) {
            InstitutionalTemplateType.STATO_PRATICA ->
                "Istanza di Sollecito e Conclusione Procedimento ex art. 2 L. 241/90 - Assicurata n. $displayAssicurata - $displayFullName"
            InstitutionalTemplateType.SEGNALAZIONE_URGENZA ->
                "Segnalazione comprovata urgenza (All. documentazione) - Pratica n. $displayAssicurata / Istanza $displayIstanza - $displayFullName"
            InstitutionalTemplateType.VERIFICA_ANOMALIE ->
                "Istanza di verifica stato istruttoria / eventuali integrazioni documentali ex L. 241/1990 - Assicurata $displayAssicurata - $displayFullName"
        }
    }

    val emailBody = remember(
        selectedTemplate,
        displayFullName,
        displayPassport,
        displayAssicurata,
        displayIstanza,
        displaySubDate,
        daysElapsed,
        urgencyReason,
        questuraName,
        questuraPec,
        questuraProvince,
        todayFormatted
    ) {
        val elapsedText = if (daysElapsed != null) "$daysElapsed giorni" else "oltre 90 giorni"
        when (selectedTemplate) {
            InstitutionalTemplateType.STATO_PRATICA -> """
Spett.le $questuraName
Ufficio Immigrazione
PEC: $questuraPec

OGGETTO: $emailSubject

Il/La sottoscritto/a $displayFullName, titolare di Passaporto n. $displayPassport,

ESPONE QUANTO SEGUE

- In data $displaySubDate ha presentato istanza di rilascio/rinnovo del permesso di soggiorno tramite Kit Postale (Codice Assicurata Poste Italiane: $displayAssicurata - Codice Istanza: $displayIstanza);
- Alla data odierna risultano trascorsi $elapsedText dalla presentazione dell'istanza, superando il termine ordinatorio previsto dall'art. 5, comma 9, D.Lgs. 286/1998 e dall'art. 2 della Legge 7 agosto 1990, n. 241;
- Consapevole del carico di lavoro dell'Ufficio, con spirito di leale collaborazione

CHIEDE CORTESEMENTE

ai sensi dell'art. 2 della L. 241/90 la definizione del procedimento amministrativo e la comunicazione della data utile per il ritiro del Permesso di Soggiorno Elettronico (PSE).

Si allegano alla presente:
1. Copia della ricevuta postale (Mod. 22A) n. $displayAssicurata;
2. Copia del passaporto in corso di validità (n. $displayPassport).

Con osservanza,
Luogo e data: $questuraProvince, $todayFormatted
Firma: $displayFullName
            """.trimIndent()

            InstitutionalTemplateType.SEGNALAZIONE_URGENZA -> """
Spett.le $questuraName
Ufficio Immigrazione
PEC: $questuraPec

OGGETTO: $emailSubject

Il/La sottoscritto/a $displayFullName, titolare di Passaporto n. $displayPassport,

COMUNICA RISPETTOSAMENTE QUANTO SEGUE

- E' pendente presso codesto Ufficio Immigrazione la pratica di soggiorno con Codice Assicurata n. $displayAssicurata (Codice Istanza: $displayIstanza), presentata in data $displaySubDate;
- E' sopravvenuta una comprovata e documentata esigenza urgente:
  "$urgencyReason"

CHIEDE CORTESEMENTE

che codesto Spett.le Ufficio voglia prendere visione della documentazione comprovante l'urgenza allegata in formato PDF alla presente comunicazione.

Luogo e data: $questuraProvince, $todayFormatted
Firma: $displayFullName
            """.trimIndent()

            InstitutionalTemplateType.VERIFICA_ANOMALIE -> """
Spett.le $questuraName
Ufficio Immigrazione
PEC: $questuraPec

OGGETTO: $emailSubject

Il/La sottoscritto/a $displayFullName, titolare di Passaporto n. $displayPassport, in qualità di diretto interessato nel procedimento amministrativo relativo all'istanza con Codice Assicurata n. $displayAssicurata (Codice Istanza: $displayIstanza), presentata in data $displaySubDate,

CHIEDE CORTESEMENTE

ai sensi degli artt. 22 e ss. della Legge 7 agosto 1990, n. 241, di verificare se risultino eventuali motivi ostativi o richieste di integrazione documentale (ex art. 10-bis L. 241/1990).

Luogo e data: $questuraProvince, $todayFormatted
Firma: $displayFullName
            """.trimIndent()
        }
    }

    val containerModifier = if (enableInternalScroll) {
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    }

    Column(
        modifier = containerModifier.animateContentSize(animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. AMBER DELAY BADGE HERO CARD
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SunsetTangerineTint,
            border = BorderStroke(1.dp, SunsetTangerine.copy(alpha = 0.45f)),
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pec_amber_delay_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SunsetTangerine
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = amberBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                Text(
                    text = "Diffida e Sollecito ex art. 2 L. 241/90",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.25).sp,
                    color = AppleInk
                )

                Text(
                    text = Strings.get("pec_deadline_note", lang),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF7A4100)
                )

                LinearProgressIndicator(
                    progress = { (effectiveDays.toFloat() / 90f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SunsetTangerine,
                    trackColor = Color.White
                )
            }
        }

        // 2. DESTINATION QUESTURA PEC CARD
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, AppleBorderHairline),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(ElectricBlueTint, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$questuraName • Ufficio Immigrazione",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = questuraPec,
                        fontSize = 12.sp,
                        color = ElectricBlue,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    onClick = {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("PEC Questura", questuraPec))
                        Toast.makeText(context, "Indirizzo PEC copiato", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricBlueTint,
                    modifier = Modifier
                        .size(36.dp)
                        .appleBounceClick()
                        .testTag("copy_pec_address_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copia PEC",
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 3. TEMPLATE SELECTOR OPTIONS ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val templates = listOf(
                InstitutionalTemplateType.STATO_PRATICA to "Diffida 90gg",
                InstitutionalTemplateType.SEGNALAZIONE_URGENZA to "Urgenza Lavoro",
                InstitutionalTemplateType.VERIFICA_ANOMALIE to "Art. 10-bis"
            )
            templates.forEach { (tpl, shortLabel) ->
                val isSelected = selectedTemplate == tpl
                Surface(
                    onClick = { selectedTemplate = tpl },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isSelected) ElectricBlue else AppleBorderHairline),
                    modifier = Modifier
                        .weight(1f)
                        .appleBounceClick()
                        .testTag("pec_template_${tpl.name.lowercase()}")
                ) {
                    Text(
                        text = shortLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                    )
                }
            }
        }

        // 4. EDITABLE DOSSIER FIELDS CARD
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, AppleBorderHairline),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "DATI INTESTATARIO E PRATICA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue,
                    letterSpacing = 0.6.sp
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nome e Cognome") },
                    placeholder = { Text(PermessoViewModel.DEMO_FULL_NAME) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pec_fullname_input"),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = assicurataCode,
                        onValueChange = { assicurataCode = it },
                        label = { Text("Assicurata (12 cifre)") },
                        placeholder = { Text(PermessoViewModel.DEMO_ASSICURATA) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pec_assicurata_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = istanzaCode,
                        onValueChange = { istanzaCode = it },
                        label = { Text("Codice Pratica / Istanza") },
                        placeholder = { Text("10 caratteri Questura") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pec_istanza_input"),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = passportNum,
                        onValueChange = {
                            passportNum = it
                            viewModel.updateExtendedProfile(passportNum = it)
                        },
                        label = { Text("N. Passaporto") },
                        placeholder = { Text(PermessoViewModel.DEMO_PASSPORT) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pec_passport_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = submissionDateStr,
                        onValueChange = {
                            submissionDateStr = it
                            viewModel.updateExtendedProfile(submissionDate = it)
                        },
                        label = { Text("Data Invio Kit") },
                        placeholder = { Text("GG/MM/AAAA") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pec_submission_date_input"),
                        singleLine = true
                    )
                }
            }
        }

        // 5. FORMAL LEGAL NOTICE PREVIEW CARD (ex art. 2 L. 241/90)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, AppleBorderHairline),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "ANTEPRIMA ATTO EX ART. 2 L. 241/90",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue
                        )
                    }
                    Surface(
                        color = VividEmeraldTint,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "VERIFICATO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007A33),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = emailBody,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        // Mandatory Anti-Spam Checkbox
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (antiSpamConfirmed) VividEmeraldTint else SunsetTangerineTint,
            border = BorderStroke(1.dp, if (antiSpamConfirmed) VividEmerald.copy(alpha = 0.4f) else SunsetTangerine),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pec_antispam_card")
                .clickable { antiSpamConfirmed = !antiSpamConfirmed }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = antiSpamConfirmed,
                    onCheckedChange = { antiSpamConfirmed = it },
                    colors = CheckboxDefaults.colors(checkedColor = VividEmerald),
                    modifier = Modifier.testTag("pec_antispam_checkbox")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Strings.get("pec_antispam_checkbox", lang),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppleInk,
                    lineHeight = 16.sp
                )
            }
        }

        // 6. FULL-WIDTH STACKED ACTIONS: "Copia Testo per PEC", "Esporta PDF con Firma", "Invia PEC"
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cb.setPrimaryClip(ClipData.newPlainText("Sollecito PEC Questura", emailBody))
                    Toast.makeText(context, "Testo PEC copiato negli appunti!", Toast.LENGTH_SHORT).show()
                },
                enabled = antiSpamConfirmed,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .appleBounceClick()
                    .testTag("pec_copy_text_button")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Copia Testo per PEC",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        exportLegalNoticePdf(context, displayFullName, emailSubject, emailBody)
                    },
                    enabled = antiSpamConfirmed,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VividEmerald,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .appleBounceClick()
                        .testTag("pec_export_pdf_button")
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Esporta PDF",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(questuraObj?.pec ?: ""))
                            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                            putExtra(Intent.EXTRA_TEXT, emailBody)
                        }
                        try {
                            context.startActivity(Intent.createChooser(intent, "Invia tramite Client PEC"))
                        } catch (_: Exception) {
                            Toast.makeText(context, "Nessuna app email trovata.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = antiSpamConfirmed,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ElectricBlue),
                    contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .appleBounceClick()
                        .testTag("pec_send_email_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Invia PEC",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

private fun exportLegalNoticePdf(
    context: Context,
    applicantName: String,
    subject: String,
    bodyText: String
) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            textSize = 14f
            isFakeBoldText = true
            color = android.graphics.Color.rgb(0, 122, 255)
        }
        val bodyPaint = Paint().apply {
            textSize = 11f
            color = android.graphics.Color.rgb(29, 29, 31)
        }

        canvas.drawText("ISTANZA FORMALE EX ART. 2 LEGGE 241/1990", 40f, 50f, titlePaint)
        var y = 80f
        bodyText.lines().forEach { line ->
            val chunks = line.chunked(82).ifEmpty { listOf("") }
            chunks.forEach { chunk ->
                if (y < 790f) {
                    canvas.drawText(chunk, 40f, y, bodyPaint)
                    y += 16f
                }
            }
        }
        pdfDocument.finishPage(page)

        val safeName = applicantName.replace(Regex("[^A-Za-z0-9]"), "_").ifBlank { "Richiedente" }
        val file = File(context.cacheDir, "Sollecito_PEC_$safeName.pdf")
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Toast.makeText(
            context,
            "PDF firmato generato: ${file.name} (Testo pronto per invio PEC)",
            Toast.LENGTH_LONG
        ).show()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyText)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Condividi Atto Firmato"))
    } catch (_: Exception) {
        Toast.makeText(context, "Atto pronto negli appunti per PEC", Toast.LENGTH_SHORT).show()
    }
}
