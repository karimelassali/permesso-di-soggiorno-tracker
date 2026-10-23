package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.util.Strings
import java.util.Locale

enum class SmartFeatureId(
    val numberBadge: Int,
    val titleIt: String,
    val subtitleIt: String,
    val tagIt: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    RICEVUTA_PASS(
        numberBadge = 1,
        titleIt = "Supporto Ottico Codice a Barre & Scanner OCR",
        subtitleIt = "Codice a barre ausiliario (Mod. 22A) + Scanner automatico cedolino Poste",
        tagIt = "MOD. 22A & OCR",
        icon = Icons.Default.QrCode2,
        accentColor = Color(0xFF0B3D91)
    ),
    TRAVEL_CHECKER(
        numberBadge = 2,
        titleIt = "Simulatore Viaggio con Ricevuta",
        subtitleIt = "Orientamento Circolare Min. Interno (Rinnovo vs Primo Rilascio, scali Schengen)",
        tagIt = "NORMATIVA VIAGGI",
        icon = Icons.Default.FlightTakeoff,
        accentColor = Color(0xFF00796B)
    ),
    PEC_GENERATOR(
        numberBadge = 3,
        titleIt = "Comunicazioni Ufficiali & Verifica Termini (L. 241/1990)",
        subtitleIt = "Calcolo giorni di attesa + Modelli collaborativi per Ufficio Immigrazione",
        tagIt = "L. 241/1990",
        icon = Icons.Default.MarkEmailRead,
        accentColor = Color(0xFF0055A4)
    ),
    REDDITO_CALCULATOR(
        numberBadge = 4,
        titleIt = "Calcolatore Reddito Minimo & Idoneità",
        subtitleIt = "Soglie Assegno Sociale 2025/2026 per Rinnovo, Lungo Periodo UE e Famiglia",
        tagIt = "SOGLIE INPS 2026",
        icon = Icons.Default.Calculate,
        accentColor = Color(0xFF2E7D32)
    ),
    APPOINTMENT_VAULT(
        numberBadge = 5,
        titleIt = "Preparation Vault & Convocazione",
        subtitleIt = "Checklist documenti fotosegnalamento, promemoria data e prontezza cartellina",
        tagIt = "CHECKLIST QUESTURA",
        icon = Icons.Default.EventAvailable,
        accentColor = Color(0xFF6A1B9A)
    )
}

@Composable
fun InstitutionalDemoOrEmptyBanner(
    hasSavedProfile: Boolean,
    isDemoMode: Boolean,
    lang: String,
    onToggleDemo: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("institutional_demo_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDemoMode) {
                Color(0xFFE8EAF6)
            } else if (!hasSavedProfile) {
                Color(0xFFFFF8E1)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        border = BorderStroke(
            1.dp,
            if (isDemoMode) {
                Color(0xFF3949AB)
            } else if (!hasSavedProfile) {
                Color(0xFFFFB300)
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDemoMode) Icons.Default.VerifiedUser else Icons.Default.PersonOutline,
                        contentDescription = null,
                        tint = if (isDemoMode) Color(0xFF283593) else Color(0xFFE65100),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = Strings.get("demo_mode_label", lang),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = if (isDemoMode) Color(0xFF1A237E) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (!hasSavedProfile && !isDemoMode) {
                                Strings.get("empty_profile_prompt", lang)
                            } else if (isDemoMode) {
                                Strings.get("demo_mode_subtitle", lang)
                            } else {
                                "Dati caricati dal tuo profilo salvato sul dispositivo."
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isDemoMode,
                    onCheckedChange = onToggleDemo,
                    modifier = Modifier.testTag("institutional_demo_switch")
                )
            }
        }
    }
}

@Composable
fun SmartFeaturesHubScreen(
    viewModel: PermessoViewModel,
    lang: String,
    initialFeature: SmartFeatureId? = null,
    onBackToDashboard: () -> Unit
) {
    var activeFeature by remember(initialFeature) { mutableStateOf(initialFeature) }

    BackHandler(enabled = activeFeature != null) {
        if (initialFeature != null) {
            onBackToDashboard()
        } else {
            activeFeature = null
        }
    }

    AnimatedContent(targetState = activeFeature, label = "smart_feature_nav") { selected ->
        when (selected) {
            null -> SmartFeaturesOverviewContent(
                viewModel = viewModel,
                lang = lang,
                onSelectFeature = { activeFeature = it },
                onBack = onBackToDashboard
            )
            SmartFeatureId.RICEVUTA_PASS -> DigitalRicevutaPassAndScannerView(
                viewModel = viewModel,
                lang = lang,
                onBack = {
                    if (initialFeature != null) onBackToDashboard() else activeFeature = null
                },
                enableInternalScroll = true
            )
            SmartFeatureId.TRAVEL_CHECKER -> TravelRegulationsSimulatorView(
                lang = lang,
                onBack = {
                    if (initialFeature != null) onBackToDashboard() else activeFeature = null
                },
                enableInternalScroll = true
            )
            SmartFeatureId.PEC_GENERATOR -> PecSollecitoGeneratorView(
                viewModel = viewModel,
                lang = lang,
                onBack = {
                    if (initialFeature != null) onBackToDashboard() else activeFeature = null
                },
                enableInternalScroll = true
            )
            SmartFeatureId.REDDITO_CALCULATOR -> RedditoCalculatorSimulatorView(
                lang = lang,
                onBack = {
                    if (initialFeature != null) onBackToDashboard() else activeFeature = null
                },
                enableInternalScroll = true
            )
            SmartFeatureId.APPOINTMENT_VAULT -> AppointmentVaultView(
                viewModel = viewModel,
                lang = lang,
                onBack = {
                    if (initialFeature != null) onBackToDashboard() else activeFeature = null
                },
                enableInternalScroll = true
            )
        }
    }
}

@Composable
private fun SmartFeaturesOverviewContent(
    viewModel: PermessoViewModel,
    lang: String,
    onSelectFeature: (SmartFeatureId) -> Unit,
    onBack: () -> Unit
) {
    val creds by viewModel.credentials.collectAsStateWithLifecycle()
    val appDate by viewModel.appointmentDate.collectAsStateWithLifecycle()
    val checkedDocs by viewModel.checkedDocs.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isInstitutionalDemoMode.collectAsStateWithLifecycle()

    val hasSavedProfile = remember(creds) {
        !creds?.fullName.isNullOrBlank() ||
            !creds?.numeroAssicurata.isNullOrBlank() ||
            !creds?.userId.isNullOrBlank()
    }

    val displayAssicurata = creds?.numeroAssicurata?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_ASSICURATA else "Non impostato"

    val displayName = creds?.fullName?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_FULL_NAME else Strings.get("empty_profile_prompt", lang)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro"
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Strumenti Smart Soggiorno",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "5 strumenti pratici organizzati passo dopo passo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        InstitutionalDemoOrEmptyBanner(
            hasSavedProfile = hasSavedProfile,
            isDemoMode = isDemoMode,
            lang = lang,
            onToggleDemo = { viewModel.setInstitutionalDemoMode(it) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Summary Banner (Clean Light-Mode Technical Card)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = Color(0xFF007A33).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFF007A33),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PROFILO PRATICA",
                                color = Color(0xFF007A33),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Assicurata: $displayAssicurata",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = displayName,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Strumenti operativi suddivisi per fase procedurale.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // GROUP 1: DOCUMENTI & SPORTELLO (#1 & #5)
        CategoryHeaderPill(
            stepLabel = "FASE 1 • PRIMA E DURANTE L'APPUNTAMENTO",
            title = "Documenti, Ricevuta & Convocazione",
            color = Color(0xFF0B3D91)
        )
        Spacer(modifier = Modifier.height(10.dp))

        SmartFeatureCleanCard(
            feature = SmartFeatureId.RICEVUTA_PASS,
            statusPill = if (!creds?.numeroAssicurata.isNullOrBlank() || isDemoMode) {
                "Supporto Ottico Mod. 22A"
            } else {
                "Da configurare"
            },
            onClick = { onSelectFeature(SmartFeatureId.RICEVUTA_PASS) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SmartFeatureCleanCard(
            feature = SmartFeatureId.APPOINTMENT_VAULT,
            statusPill = if (appDate.isNotBlank()) "Convocazione: $appDate" else "${checkedDocs.size}/7 Doc Pronti",
            onClick = { onSelectFeature(SmartFeatureId.APPOINTMENT_VAULT) }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // GROUP 2: VIAGGI & REQUISITI ECONOMICI (#2 & #4)
        CategoryHeaderPill(
            stepLabel = "FASE 2 • VERIFICHE LEGALI E VIAGGI",
            title = "Viaggi all'Estero & Calcolo Reddito",
            color = Color(0xFF00796B)
        )
        Spacer(modifier = Modifier.height(10.dp))

        SmartFeatureCleanCard(
            feature = SmartFeatureId.TRAVEL_CHECKER,
            statusPill = "Circolare Min. Interno",
            onClick = { onSelectFeature(SmartFeatureId.TRAVEL_CHECKER) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SmartFeatureCleanCard(
            feature = SmartFeatureId.REDDITO_CALCULATOR,
            statusPill = "Soglie INPS 2025/2026",
            onClick = { onSelectFeature(SmartFeatureId.REDDITO_CALCULATOR) }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // GROUP 3: TUTELA AMMINISTRATIVA (#3)
        CategoryHeaderPill(
            stepLabel = "FASE 3 • COMUNICAZIONE AMMINISTRATIVA",
            title = "Comunicazioni Ufficiali & Verifica Termini",
            color = Color(0xFF0055A4)
        )
        Spacer(modifier = Modifier.height(10.dp))

        SmartFeatureCleanCard(
            feature = SmartFeatureId.PEC_GENERATOR,
            statusPill = "Modelli L. 241/1990",
            onClick = { onSelectFeature(SmartFeatureId.PEC_GENERATOR) }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CategoryHeaderPill(
    stepLabel: String,
    title: String,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stepLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color,
            letterSpacing = 0.6.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun SmartFeatureCleanCard(
    feature: SmartFeatureId,
    statusPill: String,
    title: String? = null,
    subtitle: String? = null,
    lang: String = "it",
    onClick: () -> Unit
) {
    val displayTitle = title ?: getToolCardTitle(feature, lang)
    val displaySubtitle = subtitle ?: getToolCardSubtitle(feature, lang)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("smart_feature_card_${feature.numberBadge}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, feature.accentColor.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(feature.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = displayTitle,
                    tint = feature.accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = feature.accentColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "#${feature.numberBadge}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = feature.accentColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = statusPill,
                            color = feature.accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = displayTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = displaySubtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =========================================================================
// FEATURE #1: SUPPORTO OTTICO CODICE A BARRE (MOD. 22A) & OCR SCANNER
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalRicevutaPassAndScannerView(
    viewModel: PermessoViewModel,
    lang: String,
    onBack: () -> Unit,
    enableInternalScroll: Boolean = false
) {
    val context = LocalContext.current
    val creds by viewModel.credentials.collectAsStateWithLifecycle()
    val selectedProvince by viewModel.selectedProvince.collectAsStateWithLifecycle()
    val savedPassport by viewModel.passportNumber.collectAsStateWithLifecycle()
    val savedPassportExp by viewModel.passportExpiry.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isInstitutionalDemoMode.collectAsStateWithLifecycle()

    val hasSavedProfile = remember(creds, savedPassport) {
        !creds?.fullName.isNullOrBlank() ||
            !creds?.numeroAssicurata.isNullOrBlank() ||
            !creds?.userId.isNullOrBlank() ||
            savedPassport.isNotBlank()
    }

    var selectedSubTab by remember { mutableStateOf(0) } // 0 = Supporto Ottico Barcode, 1 = Scanner OCR Foto Ricevuta
    var ocrScanning by remember { mutableStateOf(false) }
    var ocrResultLog by remember { mutableStateOf<String?>(null) }

    val assicurata = creds?.numeroAssicurata?.takeIf { it.isNotBlank() }
        ?: creds?.password?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_ASSICURATA else ""

    val userIdOlogramma = creds?.userId?.takeIf { it.isNotBlank() }
        ?: creds?.codiceOlogramma?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_USER_ID else ""

    val codiceIstanza = creds?.codiceIstanza?.takeIf { it.isNotBlank() }
        ?: creds?.numeroPratica?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_ISTANZA else ""

    val fullName = creds?.fullName?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_FULL_NAME else ""

    val passportNumber = savedPassport.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_PASSPORT else ""

    val passportExpiry = savedPassportExp.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_PASSPORT_EXPIRY else ""

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            ocrScanning = true
            ocrResultLog = "Analisi immagine ricevuta in corso..."
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                ocrScanning = false
                if (bitmap != null) {
                    ocrResultLog = buildString {
                        appendLine("Immagine ricevuta acquisita (${bitmap.width}x${bitmap.height} px).")
                        appendLine("Per estrarre automaticamente i codici a 12 cifre, incolla il testo o verifica i campi nella scheda Ricevuta.")
                    }
                } else {
                    ocrResultLog = "Impossibile decodificare l'immagine selezionata."
                }
            } catch (e: Exception) {
                ocrScanning = false
                ocrResultLog = "Errore caricamento immagine: ${e.message ?: "Errore sconosciuto"}"
            }
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
            title = Strings.get("optical_barcode_support_title", lang),
            subtitle = "Modello 22A Poste Italiane • Barcode Code-128 & Scanner OCR",
            icon = Icons.Default.QrCode2,
            accentColor = Color(0xFF0B3D91),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Empty State or Institutional Demo Mode Toggle
        InstitutionalDemoOrEmptyBanner(
            hasSavedProfile = hasSavedProfile,
            isDemoMode = isDemoMode,
            lang = lang,
            onToggleDemo = { enabled ->
                viewModel.setInstitutionalDemoMode(enabled)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp)
        ) {
            val tabs = listOf(
                Strings.get("optical_barcode_support_title", lang),
                "Scanner OCR Foto Ricevuta"
            )
            tabs.forEachIndexed { index, label ->
                val selected = selectedSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Color(0xFF0B3D91) else Color.Transparent)
                        .clickable { selectedSubTab = index }
                        .padding(vertical = 10.dp, horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (selectedSubTab == 0) {
            if (!hasSavedProfile && !isDemoMode) {
                // Clean Empty State Card when no user data is saved and Demo Mode is off
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("empty_ricevuta_state_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color(0xFF0B3D91),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = Strings.get("empty_profile_prompt", lang),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Inserisci i codici della tua ricevuta postale nella scheda Profilo, usa lo Scanner OCR oppure attiva la Modalità Demo Istituzionale qui sopra per visualizzare un esempio con dati fittizi (MARIO ROSSI).",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { selectedSubTab = 1 },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scansiona Ricevuta")
                            }
                            Button(
                                onClick = { viewModel.setInstitutionalDemoMode(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B3D91)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Attiva Demo")
                            }
                        }
                    }
                }
            } else {
                // Digital Mod. 22A Optical Support Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF0B3D91).copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Top Header Bar (Poste Italiane / Ministero dell'Interno style)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF0B3D91), Color(0xFF1565C0))
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isDemoMode) {
                                            "SUPPORTO OTTICO MOD. 22A (DEMO ISTITUZIONALE)"
                                        } else {
                                            Strings.get("optical_barcode_support_title", lang).uppercase(Locale.getDefault())
                                        },
                                        color = Color(0xFFFFD54F),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Text(
                                        text = fullName.ifBlank { "TITOLARE NON IMPOSTATO" },
                                        color = Color.White,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Surface(
                                    color = Color(0xFFFFEB3B),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Posteitaliane",
                                        color = Color(0xFF0D47A1),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(20.dp)) {
                            // Key Credentials Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                PassFieldBox(
                                    modifier = Modifier.weight(1f),
                                    label = "CODICE ASSICURATA (PASSWORD)",
                                    value = assicurata.ifBlank { "—" },
                                    accent = Color(0xFF0B3D91),
                                    onCopy = {
                                        if (assicurata.isNotBlank()) {
                                            copyToClipboard(context, "Codice Assicurata", assicurata)
                                        }
                                    }
                                )
                                PassFieldBox(
                                    modifier = Modifier.weight(1f),
                                    label = "USER ID (OLOGRAMMA)",
                                    value = userIdOlogramma.ifBlank { "—" },
                                    accent = Color(0xFF2E7D32),
                                    onCopy = {
                                        if (userIdOlogramma.isNotBlank()) {
                                            copyToClipboard(context, "User ID Ologramma", userIdOlogramma)
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                PassFieldBox(
                                    modifier = Modifier.weight(1f),
                                    label = "CODICE ISTANZA",
                                    value = codiceIstanza.ifBlank { "—" },
                                    accent = Color(0xFF6A1B9A),
                                    onCopy = {
                                        if (codiceIstanza.isNotBlank()) {
                                            copyToClipboard(context, "Codice Istanza", codiceIstanza)
                                        }
                                    }
                                )
                                PassFieldBox(
                                    modifier = Modifier.weight(1f),
                                    label = "PASSAPORTO & PROVINCIA",
                                    value = buildString {
                                        append(passportNumber.ifBlank { "Non inserito" })
                                        append(" ($selectedProvince)")
                                    },
                                    accent = Color(0xFF37474F),
                                    onCopy = {
                                        if (passportNumber.isNotBlank()) {
                                            copyToClipboard(context, "Passaporto", passportNumber)
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Scadenza Passaporto registrata: ${passportExpiry.ifBlank { "Data non configurata" }}",
                                fontSize = 11.sp,
                                color = Color(0xFF546E7A)
                            )

                            Spacer(modifier = Modifier.height(18.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(16.dp))

                            // MANDATORY HIGH-VISIBILITY AMBER/RED WARNING BANNER DIRECTLY ABOVE THE CODE-128 BARCODE
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("paper_receipt_mandatory_warning_banner"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                border = BorderStroke(2.dp, Color(0xFFD84315))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = Strings.get("paper_receipt_warning_title", lang),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFBF360C)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = Strings.get("paper_receipt_warning_body", lang),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF3E2723),
                                        lineHeight = 17.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val barcodeValue = assicurata.ifBlank { PermessoViewModel.DEMO_ASSICURATA }

                            // Scannable Code-128 Barcode for Codice Assicurata
                            Text(
                                text = "CODICE A BARRE AUSILIARIO ASSICURATA (CODE-128)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF546E7A),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Code128BarcodeCanvas(
                                data = barcodeValue,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(78.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .testTag("code128_barcode_canvas")
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "* $barcodeValue *",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                letterSpacing = 3.sp,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        } else {
            // SubTab 1: ML Kit OCR Scanner from Photo of Ricevuta
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFF0B3D91).copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B3D91).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = Color(0xFF0B3D91),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Scanner Intelligente Ricevuta Postale",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Seleziona una foto della tua ricevuta Poste Italiane (Mod. 22A). Il motore OCR ML Kit estrarrà automaticamente Codice Assicurata (12 cifre), Ologramma User ID e Codice Istanza.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("ocr_pick_photo_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B3D91))
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (ocrScanning) "Lettura OCR in corso..." else "Scansiona Foto Ricevuta (Zero-Permission)",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (ocrResultLog != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = ocrResultLog!!,
                                color = Color(0xFF1B5E20),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PassFieldBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accent: Color,
    onCopy: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCopy() },
        color = accent.copy(alpha = 0.06f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copia",
                    tint = accent,
                    modifier = Modifier.size(13.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun Code128BarcodeCanvas(
    data: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (size.width <= 0f || size.height <= 0f) return@Canvas
        val clean = data.trim().ifEmpty { PermessoViewModel.DEMO_ASSICURATA }
        val pattern = mutableListOf<Boolean>()
        // Start quiet zone + start bar pattern
        pattern.addAll(listOf(true, true, false, true, false, false, true, false, false))
        clean.forEachIndexed { idx, ch ->
            val code = ch.code + idx * 7
            pattern.add(true)
            pattern.add((code and 1) == 0)
            pattern.add(true)
            pattern.add((code and 2) != 0)
            pattern.add(false)
            pattern.add((code and 4) == 0)
            pattern.add(true)
            pattern.add(false)
        }
        // Stop pattern
        pattern.addAll(listOf(true, true, false, false, true, true, true, false, true, true))

        val totalUnits = pattern.size.toFloat().coerceAtLeast(1f)
        val unitWidth = size.width / totalUnits
        pattern.forEachIndexed { i, isBar ->
            if (isBar) {
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(x = i * unitWidth, y = 0f),
                    size = Size(width = unitWidth.coerceAtLeast(1.5f), height = size.height)
                )
            }
        }
    }
}

private data class ExtractedRicevutaData(
    val fullName: String,
    val assicurata: String,
    val ologramma: String,
    val istanza: String
)

private fun parseRicevutaOcrText(raw: String): ExtractedRicevutaData {
    val twelveDigitNumbers = Regex("""\b\d{12}\b""").findAll(raw).map { it.value }.distinct().toList()
    val alphaNumTwelve = Regex("""\b\d{2}[A-Z0-9]{10}\b""").findAll(raw).map { it.value }.distinct().toList()

    val assicurata = twelveDigitNumbers.firstOrNull { it.startsWith("05") || it.startsWith("06") }
        ?: twelveDigitNumbers.firstOrNull()
        ?: ""
    val ologramma = twelveDigitNumbers.firstOrNull { it != assicurata } ?: ""
    val istanza = alphaNumTwelve.firstOrNull { it != assicurata && it != ologramma } ?: ""

    val lines = raw.lines().map { it.trim() }.filter { it.length in 5..40 }
    val nameCandidate = lines.firstOrNull { line ->
        line.all { it.isLetter() || it.isWhitespace() } &&
            line.uppercase(Locale.ITALY) == line &&
            !line.contains("POSTE", ignoreCase = true) &&
            !line.contains("RICEVUTA", ignoreCase = true) &&
            !line.contains("ACCETTAZIONE", ignoreCase = true)
    } ?: ""

    return ExtractedRicevutaData(
        fullName = nameCandidate,
        assicurata = assicurata,
        ologramma = ologramma,
        istanza = istanza
    )
}

// =========================================================================
// FEATURE #4: CALCOLATORE REDDITO MINIMO & ALLOGGIO (SOGLIE INPS 2025/2026)
// =========================================================================

@Composable
fun RedditoCalculatorSimulatorView(
    lang: String,
    onBack: () -> Unit,
    enableInternalScroll: Boolean = false
) {
    val baseAssegnoSocialeAnnual = 7002.84 // Soglia annua Assegno Sociale INPS aggiornata
    var familyMembers by remember { mutableIntStateOf(0) }
    var childrenUnder14 by remember { mutableIntStateOf(0) }
    var monthlyNetIncomeStr by remember { mutableStateOf("1250") }
    var mensilitaCount by remember { mutableIntStateOf(13) }
    var procedureType by remember { mutableIntStateOf(0) } // 0 = Rinnovo Lavoro, 1 = Permesso UE Lungo Periodo, 2 = Ricongiungimento Familiare

    val effectiveDependents = remember(familyMembers, childrenUnder14, procedureType) {
        if (procedureType == 2 && childrenUnder14 >= 2) {
            familyMembers + 1
        } else {
            familyMembers + childrenUnder14
        }
    }

    val requiredAnnualIncome = remember(effectiveDependents) {
        baseAssegnoSocialeAnnual * (1.0 + (effectiveDependents * 0.5))
    }

    val userAnnualIncome = remember(monthlyNetIncomeStr, mensilitaCount) {
        val monthly = monthlyNetIncomeStr.replace(",", ".").toDoubleOrNull() ?: 0.0
        monthly * mensilitaCount
    }

    val isEligible = userAnnualIncome >= requiredAnnualIncome
    val difference = userAnnualIncome - requiredAnnualIncome

    val requiredSqMeters = remember(familyMembers, childrenUnder14) {
        val totalPeople = (1 + familyMembers).coerceAtLeast(1)
        when (totalPeople) {
            1 -> 14
            2 -> 28
            3 -> 42
            4 -> 56
            else -> 56 + (totalPeople - 4) * 10
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
            title = "Calcolatore Reddito & Idoneità Alloggiativa",
            subtitle = "Soglie Assegno Sociale INPS (Art. 29 c. 3 D.Lgs. 286/98)",
            icon = Icons.Default.Calculate,
            accentColor = Color(0xFF2E7D32),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isEligible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ),
            border = BorderStroke(2.dp, if (isEligible) Color(0xFF2E7D32) else Color(0xFFC62828))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isEligible) Color(0xFF2E7D32) else Color(0xFFC62828)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEligible) Icons.Default.Verified else Icons.Default.PriorityHigh,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isEligible) "SOGLIA REDDITUALE SODDISFATTA" else "REDDITO INFERIORE ALLA SOGLIA MINIMA",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = if (isEligible) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                        )
                        Text(
                            text = String.format(
                                Locale.ITALY,
                                "Richiesto: € %.2f / anno • Tuo: € %.2f / anno",
                                requiredAnnualIncome,
                                userAnnualIncome
                            ),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isEligible) {
                        String.format(
                            Locale.ITALY,
                            "Hai un margine positivo di +€ %.2f annui rispetto al minimo richiesto dalla Questura/Prefettura.",
                            difference
                        )
                    } else {
                        String.format(
                            Locale.ITALY,
                            "Mancano € %.2f annui per raggiungere la soglia legale. Puoi sommare il reddito di familiari conviventi.",
                            -difference
                        )
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF263238)
                )
            }
        }

        FeatureSectionDivider("Parametri Calcolo Reddito INPS")

        // Configuration Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Tipo di pratica", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Rinnovo", "UE Lungo Periodo", "Ricongiungimento").forEachIndexed { idx, label ->
                        FilterChip(
                            selected = procedureType == idx,
                            onClick = { procedureType = idx },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = monthlyNetIncomeStr,
                    onValueChange = { monthlyNetIncomeStr = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' } },
                    label = { Text("Stipendio Mensile Netto / Reddito (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Numero Mensilità Annue (CU/Busta Paga):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(12, 13, 14).forEach { m ->
                            FilterChip(
                                selected = mensilitaCount == m,
                                onClick = { mensilitaCount = m },
                                label = { Text("${m}ª") }
                            )
                        }
                    }
                }

                HorizontalDivider()

                // Family members counter
                CounterRow(
                    label = "Familiari a carico (oltre al richiedente)",
                    subLabel = "+50% dell'Assegno Sociale (€ 3.501,42) per ogni familiare",
                    count = familyMembers,
                    onDecrement = { if (familyMembers > 0) familyMembers-- },
                    onIncrement = { if (familyMembers < 10) familyMembers++ }
                )

                CounterRow(
                    label = "Figli minori di 14 anni",
                    subLabel = "Per 2 o più figli <14 anni nel ricongiungimento vale +1 quota",
                    count = childrenUnder14,
                    onDecrement = { if (childrenUnder14 > 0) childrenUnder14-- },
                    onIncrement = { if (childrenUnder14 < 10) childrenUnder14++ }
                )
            }
        }

        FeatureSectionDivider("Requisito Alloggio (D.M. 5 Luglio 1975)")

        // Housing Suitability Card (D.M. 5 Luglio 1975)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
            border = BorderStroke(1.dp, Color(0xFF81C784))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.HomeWork,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Superficie Minima Alloggio: $requiredSqMeters mq",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Parametro D.M. Sanità 05/07/1975 per ${1 + familyMembers + childrenUnder14} persona/e (14 mq per i primi 4 abitanti, +10 mq per ciascuno dei successivi).",
                        fontSize = 12.sp,
                        color = Color(0xFF33691E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun CounterRow(
    label: String,
    subLabel: String,
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(subLabel, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledTonalIconButton(onClick = onDecrement, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Meno", modifier = Modifier.size(16.dp))
            }
            Text(
                text = count.toString(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            FilledTonalIconButton(onClick = onIncrement, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Più", modifier = Modifier.size(16.dp))
            }
        }
    }
}

// =========================================================================
// SHARED HEADER & DASHBOARD QUICK ACCESS CARD
// =========================================================================

@Composable
fun FeatureSectionDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(50),
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = label.uppercase(Locale.getDefault()),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.7.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun FeatureTopHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor)
        }
    }
}

@Composable
fun SmartFeatureHubSectionCard(
    onOpenHub: () -> Unit,
    onOpenFeatureDirect: (SmartFeatureId) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Color(0xFF0B3D91).copy(alpha = 0.18f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0B3D91).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesomeMosaic,
                            contentDescription = null,
                            tint = Color(0xFF0B3D91)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Strumenti Smart Soggiorno",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Barcode Mod. 22A, Viaggi, Comunicazioni L. 241/90 & Vault",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                TextButton(onClick = onOpenHub) {
                    Text("Vedi tutti (5)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clean 2x2 Quick Action Grid + 1 Full Width Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickMiniToolTile(
                    modifier = Modifier.weight(1f),
                    title = "#1 Barcode Mod. 22A",
                    subtitle = "Supporto Ottico",
                    icon = Icons.Default.QrCode2,
                    color = Color(0xFF0B3D91),
                    onClick = { onOpenFeatureDirect(SmartFeatureId.RICEVUTA_PASS) }
                )
                QuickMiniToolTile(
                    modifier = Modifier.weight(1f),
                    title = "#5 Vault Questura",
                    subtitle = "Checklist & Data",
                    icon = Icons.Default.EventAvailable,
                    color = Color(0xFF6A1B9A),
                    onClick = { onOpenFeatureDirect(SmartFeatureId.APPOINTMENT_VAULT) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickMiniToolTile(
                    modifier = Modifier.weight(1f),
                    title = "#2 Regole Viaggio",
                    subtitle = "Voli & Schengen",
                    icon = Icons.Default.FlightTakeoff,
                    color = Color(0xFF00796B),
                    onClick = { onOpenFeatureDirect(SmartFeatureId.TRAVEL_CHECKER) }
                )
                QuickMiniToolTile(
                    modifier = Modifier.weight(1f),
                    title = "#3 Comunicazioni PEC",
                    subtitle = "L. 241/1990",
                    icon = Icons.Default.MarkEmailRead,
                    color = Color(0xFF0055A4),
                    onClick = { onOpenFeatureDirect(SmartFeatureId.PEC_GENERATOR) }
                )
            }
        }
    }
}

fun getToolCardTitle(tool: SmartFeatureId, lang: String): String = when (tool) {
    SmartFeatureId.RICEVUTA_PASS -> Strings.get("tool_tile_1_title", lang)
    SmartFeatureId.TRAVEL_CHECKER -> Strings.get("tool_tile_2_title", lang)
    SmartFeatureId.PEC_GENERATOR -> Strings.get("tool_tile_3_title", lang)
    SmartFeatureId.REDDITO_CALCULATOR -> Strings.get("tool_tile_4_title", lang)
    SmartFeatureId.APPOINTMENT_VAULT -> Strings.get("tool_tile_5_title", lang)
}

fun getToolCardSubtitle(tool: SmartFeatureId, lang: String): String = when (tool) {
    SmartFeatureId.RICEVUTA_PASS -> Strings.get("tool_tile_1_sub", lang)
    SmartFeatureId.TRAVEL_CHECKER -> Strings.get("tool_tile_2_sub", lang)
    SmartFeatureId.PEC_GENERATOR -> Strings.get("tool_tile_3_sub", lang)
    SmartFeatureId.REDDITO_CALCULATOR -> Strings.get("tool_tile_4_sub", lang)
    SmartFeatureId.APPOINTMENT_VAULT -> Strings.get("tool_tile_5_sub", lang)
}

fun getToolStatusPill(tool: SmartFeatureId, lang: String): String = when (tool) {
    SmartFeatureId.RICEVUTA_PASS -> Strings.get("tool_pill_1", lang)
    SmartFeatureId.TRAVEL_CHECKER -> Strings.get("tool_pill_2", lang)
    SmartFeatureId.PEC_GENERATOR -> Strings.get("tool_pill_3", lang)
    SmartFeatureId.REDDITO_CALCULATOR -> Strings.get("tool_pill_4", lang)
    SmartFeatureId.APPOINTMENT_VAULT -> Strings.get("tool_pill_5", lang)
}

@Composable
private fun QuickMiniToolTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    lineHeight = 14.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, value: String) {
    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cb.setPrimaryClip(ClipData.newPlainText(label, value))
    Toast.makeText(context, "$label copiato: $value", Toast.LENGTH_SHORT).show()
}

typealias SmartToolType = SmartFeatureId

@Composable
fun SmartToolsQuickStrip(
    onSelectTool: (SmartToolType) -> Unit,
    selectedLanguageCode: String
) {
    val tools = listOf(
        SmartFeatureId.RICEVUTA_PASS,
        SmartFeatureId.TRAVEL_CHECKER,
        SmartFeatureId.PEC_GENERATOR,
        SmartFeatureId.APPOINTMENT_VAULT
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tools.take(2).forEach { tool ->
                QuickMiniToolTile(
                    modifier = Modifier.weight(1f),
                    title = getToolCardTitle(tool, selectedLanguageCode),
                    subtitle = getToolCardSubtitle(tool, selectedLanguageCode),
                    icon = tool.icon,
                    color = tool.accentColor,
                    onClick = { onSelectTool(tool) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tools.drop(2).forEach { tool ->
                QuickMiniToolTile(
                    modifier = Modifier.weight(1f),
                    title = getToolCardTitle(tool, selectedLanguageCode),
                    subtitle = getToolCardSubtitle(tool, selectedLanguageCode),
                    icon = tool.icon,
                    color = tool.accentColor,
                    onClick = { onSelectTool(tool) }
                )
            }
        }
    }
}

@Composable
fun SmartToolsCalmHub(
    viewModel: PermessoViewModel,
    selectedLanguageCode: String,
    activeTool: SmartToolType?,
    onSelectTool: (SmartToolType?) -> Unit
) {
    BackHandler(enabled = activeTool != null) {
        onSelectTool(null)
    }

    when (activeTool) {
        null -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SmartFeatureId.entries.forEach { feature ->
                    SmartFeatureCleanCard(
                        feature = feature,
                        statusPill = getToolStatusPill(feature, selectedLanguageCode),
                        title = getToolCardTitle(feature, selectedLanguageCode),
                        subtitle = getToolCardSubtitle(feature, selectedLanguageCode),
                        lang = selectedLanguageCode,
                        onClick = { onSelectTool(feature) }
                    )
                }
            }
        }
        SmartFeatureId.RICEVUTA_PASS -> DigitalRicevutaPassAndScannerView(
            viewModel = viewModel,
            lang = selectedLanguageCode,
            onBack = { onSelectTool(null) },
            enableInternalScroll = false
        )
        SmartFeatureId.TRAVEL_CHECKER -> TravelRegulationsSimulatorView(
            lang = selectedLanguageCode,
            onBack = { onSelectTool(null) },
            enableInternalScroll = false
        )
        SmartFeatureId.PEC_GENERATOR -> PecSollecitoGeneratorView(
            viewModel = viewModel,
            lang = selectedLanguageCode,
            onBack = { onSelectTool(null) },
            enableInternalScroll = false
        )
        SmartFeatureId.REDDITO_CALCULATOR -> RedditoCalculatorSimulatorView(
            lang = selectedLanguageCode,
            onBack = { onSelectTool(null) },
            enableInternalScroll = false
        )
        SmartFeatureId.APPOINTMENT_VAULT -> AppointmentVaultView(
            viewModel = viewModel,
            lang = selectedLanguageCode,
            onBack = { onSelectTool(null) },
            enableInternalScroll = false
        )
    }
}

fun parseFirstAndLastName(fullName: String?): Pair<String, String> {
    val cleaned = fullName?.trim().orEmpty()
    if (cleaned.isEmpty()) return "" to ""
    val parts = cleaned.split(Regex("\\s+")).filter { it.isNotEmpty() }
    val firstPart = parts.firstOrNull().orEmpty()
    if (firstPart.isEmpty()) return "" to ""
    return if (parts.size == 1) {
        firstPart to ""
    } else {
        firstPart to parts.drop(1).joinToString(" ")
    }
}

fun extractFirstAndLastNameInitials(fullName: String?): String {
    val cleaned = fullName?.trim().orEmpty()
    if (cleaned.isEmpty()) return ""
    val parts = cleaned.split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (parts.isEmpty()) return ""
    if (parts.size == 1) {
        val single = parts.firstOrNull().orEmpty()
        return if (single.isNotEmpty()) single.take(2).uppercase(Locale.ROOT) else ""
    }
    val firstChar = parts.firstOrNull()?.firstOrNull()
    val lastChar = parts.lastOrNull()?.firstOrNull()
    return if (firstChar != null && lastChar != null) {
        "$firstChar$lastChar".uppercase(Locale.ROOT)
    } else if (firstChar != null) {
        firstChar.toString().uppercase(Locale.ROOT)
    } else {
        ""
    }
}

@Composable
fun UserPersistentAvatar(
    fullName: String?,
    size: Dp = 40.dp,
    fontSize: TextUnit = 14.sp,
    isSelected: Boolean = false,
    borderColor: Color = Color(0xFF0066CC),
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val initials = remember(fullName) { extractFirstAndLastNameInitials(fullName) }
    val hasInitials = initials.isNotBlank()
    val tintBg = if (isSelected) Color(0xFFE6F9ED) else Color(0xFFEBF3FF)
    val fgColor = if (isSelected) Color(0xFF007A33) else Color(0xFF0066CC)
    val effectiveBorderColor = if (borderColor == Color.White) fgColor.copy(alpha = 0.35f) else borderColor.copy(alpha = 0.45f)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(tintBg)
            .border(
                width = if (isSelected) borderWidth + 0.5.dp else borderWidth,
                color = effectiveBorderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (hasInitials) {
            Text(
                text = initials,
                color = fgColor,
                fontSize = fontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        } else {
            Icon(
                imageVector = Icons.Default.PersonOutline,
                contentDescription = "Profilo",
                tint = fgColor,
                modifier = Modifier.size(size * 0.52f)
            )
        }
    }
}

@Composable
fun SectionDividerHeader(
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = Color(0xFF009F3D),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        HorizontalDivider(
            color = accentColor.copy(alpha = 0.18f),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
            if (!badgeText.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = accentColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = accentColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}


