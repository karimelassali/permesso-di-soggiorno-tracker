package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.QuesturaCatalog
import com.example.util.Strings
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
    var antiSpamConfirmed by remember { mutableStateOf(false) }

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
            } catch (e: Exception) {
                null
            }
        }
    }

    val isOver90Days = (daysElapsed ?: 0L) >= 90L
    val isOver60Days = (daysElapsed ?: 0L) >= 60L

    val todayFormatted = remember {
        try {
            SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date())
        } catch (e: Exception) {
            "Data odierna"
        }
    }

    val displayFullName = fullName.ifBlank { "[NOME E COGNOME]" }
    val displayPassport = passportNum.ifBlank { "[NUMERO PASSAPORTO]" }
    val displayAssicurata = assicurataCode.ifBlank { "[CODICE ASSICURATA 12 CIFRE]" }
    val displayIstanza = istanzaCode.ifBlank { "[CODICE ISTANZA]" }
    val displaySubDate = submissionDateStr.ifBlank { "Data non configurata" }

    val emailSubject = remember(selectedTemplate, displayFullName, displayAssicurata, displayIstanza) {
        when (selectedTemplate) {
            InstitutionalTemplateType.STATO_PRATICA ->
                "Richiesta cortese informazioni stato pratica Permesso di Soggiorno - Assicurata n. $displayAssicurata - $displayFullName"
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
        val elapsedText = daysElapsed?.let { "$it giorni" } ?: "Data non configurata"
        when (selectedTemplate) {
            InstitutionalTemplateType.STATO_PRATICA -> """
Spett.le $questuraName
Ufficio Immigrazione
PEC: $questuraPec

OGGETTO: $emailSubject

Il/La sottoscritto/a $displayFullName, titolare di Passaporto n. $displayPassport,

ESPONE QUANTO SEGUE

- In data $displaySubDate ha presentato istanza di rilascio/rinnovo del permesso di soggiorno tramite Kit Postale (Codice Assicurata Poste Italiane: $displayAssicurata - Codice Istanza: $displayIstanza);
- Alla data odierna risultano trascorsi $elapsedText dalla presentazione dell'istanza;
- Consapevole del carico di lavoro dell'Ufficio, con spirito di leale collaborazione

CHIEDE CORTESEMENTE

di conoscere lo stato attuale dell'istruttoria ed eventuale calendarizzazione dell'appuntamento per i rilievi fotodattiloscopici o per il ritiro del titolo di soggiorno.

Si allegano alla presente:
1. Copia della ricevuta postale (Mod. 22A) n. $displayAssicurata;
2. Copia del passaporto in corso di validità (n. $displayPassport).

Ringraziando per la cortese attenzione, si porgono distinti saluti.

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
- Si rende pertanto necessario disporre di riscontro sullo stato della pratica o, ove possibile, di cortese valutazione della documentazione allegata.

CHIEDE CORTESEMENTE

che codesto Spett.le Ufficio voglia prendere visione della documentazione comprovante l'urgenza allegata in formato PDF alla presente comunicazione.

Si allegano in formato PDF:
1. Documentazione attestante il motivo d'urgenza (es. contratto/certificato/iscrizione);
2. Copia della ricevuta Mod. 22A ($displayAssicurata);
3. Copia del passaporto ($displayPassport).

Con osservanza,

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

ai sensi degli artt. 22 e ss. della Legge 7 agosto 1990, n. 241, di verificare se risultino eventuali motivi ostativi, anomalie o richieste di integrazione documentale (ex art. 10-bis L. 241/1990) non ancora perfezionate, rendendosi sin d'ora pienamente disponibile a trasmettere o depositare qualsiasi documento integrativo necessario al completamento dell'istruttoria.

Si allegano:
1. Copia della ricevuta postale assicurata n. $displayAssicurata;
2. Copia del passaporto n. $displayPassport.

Ringraziando per la collaborazione istituzionale, si porgono cordiali saluti.

Luogo e data: $questuraProvince, $todayFormatted

Firma: $displayFullName
            """.trimIndent()
        }
    }

    val containerModifier = if (enableInternalScroll) {
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    }

    Column(
        modifier = containerModifier
    ) {
        FeatureTopHeader(
            title = Strings.get("pec_generator_title", lang),
            subtitle = Strings.get("pec_generator_subtitle", lang),
            icon = Icons.Default.MarkEmailRead,
            accentColor = Color(0xFF0055A4),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Empty Profile / Institutional Demo Mode Banner
        InstitutionalDemoOrEmptyBanner(
            hasSavedProfile = hasSavedProfile,
            isDemoMode = isDemoMode,
            lang = lang,
            onToggleDemo = { enabled ->
                viewModel.setInstitutionalDemoMode(enabled)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Administrative Timeline Meter Card (with province variance note)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOver90Days) Color(0xFFFFF8E1) else Color(0xFFE3F2FD)
            ),
            border = BorderStroke(
                1.5.dp,
                if (isOver90Days) Color(0xFFFFB300) else Color(0xFF1976D2)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isOver90Days) Color(0xFFFFB300) else Color(0xFF1976D2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOver90Days) Icons.Default.Schedule else Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (daysElapsed != null) {
                                "Giorni trascorsi dall'invio: $daysElapsed gg"
                            } else {
                                "Data non configurata (Termini D.Lgs. 286/98)"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = if (isOver90Days) Color(0xFFE65100) else Color(0xFF0D47A1)
                        )
                        Text(
                            text = if (daysElapsed == null) {
                                "Data non configurata: inserisci la data di invio del Kit Postale (GG/MM/AAAA) o attiva la Modalità Demo."
                            } else if (isOver90Days) {
                                "Oltre 90 giorni: è possibile inviare una cortese richiesta informativa o verificare eventuali integrazioni."
                            } else if (isOver60Days) {
                                "Tra 60 e 90 giorni: pratica in ordinaria lavorazione presso la Questura."
                            } else {
                                "Meno di 60 giorni: pratica in fase iniziale di acquisizione."
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF37474F)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { ((daysElapsed ?: 0L).toFloat() / 90f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isOver90Days) Color(0xFFFF8F00) else Color(0xFF1976D2),
                    trackColor = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = Strings.get("pec_deadline_note", lang),
                    fontSize = 11.sp,
                    color = Color(0xFF455A64),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Destination Questura PEC Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF0055A4))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$questuraName - Ufficio Immigrazione",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "PEC: $questuraPec",
                        fontSize = 12.sp,
                        color = Color(0xFF0055A4),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(
                    onClick = {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("PEC Questura", questuraPec))
                        Toast.makeText(context, "Indirizzo PEC copiato", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_pec_address_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copia PEC", modifier = Modifier.size(18.dp))
                }
            }
        }

        FeatureSectionDivider("Modello di Comunicazione Collaborativa")

        // 3 Institutional-Friendly Templates
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InstitutionalTemplateType.entries.forEach { tpl ->
                val isSelected = selectedTemplate == tpl
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pec_template_${tpl.name.lowercase()}")
                        .clickable { selectedTemplate = tpl },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF0055A4).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) Color(0xFF0055A4) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedTemplate = tpl }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Strings.get(tpl.titleKey, lang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = Strings.get(tpl.subtitleKey, lang),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        FeatureSectionDivider("Dati Pratica & Ricevuta")

        // Editable Fields Form (Synced with PermessoViewModel)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Compila o verifica i dati della tua pratica",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nome e Cognome") },
                    placeholder = { Text(PermessoViewModel.DEMO_FULL_NAME) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pec_fullname_input"),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = assicurataCode,
                        onValueChange = { assicurataCode = it },
                        label = { Text("Cod. Assicurata (12 cifre)") },
                        placeholder = { Text(PermessoViewModel.DEMO_ASSICURATA) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pec_assicurata_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = istanzaCode,
                        onValueChange = { istanzaCode = it },
                        label = { Text("Cod. Istanza") },
                        placeholder = { Text(PermessoViewModel.DEMO_ISTANZA) },
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
                        label = { Text("Data Invio Kit (GG/MM/AAAA)") },
                        placeholder = { Text(PermessoViewModel.DEMO_SUBMISSION_DATE) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pec_submission_date_input"),
                        singleLine = true
                    )
                }
                if (selectedTemplate == InstitutionalTemplateType.SEGNALAZIONE_URGENZA) {
                    OutlinedTextField(
                        value = urgencyReason,
                        onValueChange = { urgencyReason = it },
                        label = { Text("Motivo documentato di urgenza (allega PDF alla PEC)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pec_urgency_reason_input")
                    )
                }
            }
        }

        FeatureSectionDivider("Anteprima Comunicazione Ufficiale")

        // Preview Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
            border = BorderStroke(1.dp, Color(0xFFBDBDBD))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TESTO FORMALE GENERATO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0055A4)
                    )
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "L. 241/1990",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = emailBody,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF212121),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mandatory Anti-Spam Friction Gate Checkbox
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pec_antispam_card")
                .clickable { antiSpamConfirmed = !antiSpamConfirmed },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (antiSpamConfirmed) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
            ),
            border = BorderStroke(
                1.5.dp,
                if (antiSpamConfirmed) Color(0xFF2E7D32) else Color(0xFFFFA000)
            )
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = antiSpamConfirmed,
                    onCheckedChange = { antiSpamConfirmed = it },
                    modifier = Modifier.testTag("pec_antispam_checkbox")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.get("pec_antispam_checkbox", lang),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF263238),
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons (Disabled until antiSpamConfirmed is checked)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cb.setPrimaryClip(ClipData.newPlainText("Comunicazione Questura", emailBody))
                    Toast.makeText(context, "Testo copiato negli appunti!", Toast.LENGTH_SHORT).show()
                },
                enabled = antiSpamConfirmed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("pec_copy_text_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copia Testo", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(questuraObj?.pec ?: ""))
                        putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                        putExtra(Intent.EXTRA_TEXT, emailBody)
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, "Invia tramite Client PEC / Email"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Nessuna app email trovata.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = antiSpamConfirmed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("pec_send_email_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0055A4))
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Invia PEC / Email", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
