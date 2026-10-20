package com.example.ui

import android.content.Intent
import android.provider.CalendarContract
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.QuesturaCatalog
import com.example.util.Strings

data class VaultDocItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val isMandatory: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentVaultView(
    viewModel: PermessoViewModel,
    lang: String,
    onBack: () -> Unit,
    enableInternalScroll: Boolean = false
) {
    val context = LocalContext.current
    val creds by viewModel.credentials.collectAsStateWithLifecycle()
    val appDate by viewModel.appointmentDate.collectAsStateWithLifecycle()
    val appTime by viewModel.appointmentTime.collectAsStateWithLifecycle()
    val appLoc by viewModel.appointmentLocation.collectAsStateWithLifecycle()
    val checkedDocs by viewModel.checkedDocs.collectAsStateWithLifecycle()
    val selectedProvince by viewModel.selectedProvince.collectAsStateWithLifecycle()
    val savedPassport by viewModel.passportNumber.collectAsStateWithLifecycle()
    val savedPassportExpiry by viewModel.passportExpiry.collectAsStateWithLifecycle()
    val savedHospProt by viewModel.hospitalityProtocol.collectAsStateWithLifecycle()
    val savedMunicipality by viewModel.municipalityName.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isInstitutionalDemoMode.collectAsStateWithLifecycle()

    val hasSavedProfile = remember(creds, savedPassport, savedMunicipality, appDate) {
        !creds?.fullName.isNullOrBlank() ||
            !creds?.numeroAssicurata.isNullOrBlank() ||
            !creds?.userId.isNullOrBlank() ||
            savedPassport.isNotBlank() ||
            savedMunicipality.isNotBlank() ||
            appDate.isNotBlank()
    }

    val questuraObj = remember(selectedProvince, creds?.selectedQuesturaId) {
        creds?.selectedQuesturaId?.takeIf { it.isNotBlank() }?.let { QuesturaCatalog.getById(it) }
            ?: QuesturaCatalog.questure.find {
                it.province.equals(selectedProvince, ignoreCase = true) ||
                    it.name.contains(selectedProvince, ignoreCase = true)
            }
            ?: QuesturaCatalog.questure.firstOrNull()
    }

    val effectiveAssicurata = creds?.numeroAssicurata?.takeIf { it.isNotBlank() }
        ?: creds?.password?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_ASSICURATA else ""

    val effectiveIstanza = creds?.codiceIstanza?.takeIf { it.isNotBlank() }
        ?: creds?.numeroPratica?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_ISTANZA else ""

    val effectiveOlogramma = creds?.codiceOlogramma?.takeIf { it.isNotBlank() }
        ?: creds?.userId?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_USER_ID else ""

    val effectivePassport = savedPassport.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_PASSPORT else ""

    val effectivePassportExp = savedPassportExpiry.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_PASSPORT_EXPIRY else ""

    val effectiveComune = savedMunicipality.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_COMUNE else ""

    val effectiveProtocol = savedHospProt.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_HOSPITALITY_PROT else ""

    val effectiveDate = appDate.takeIf { it.isNotBlank() }
        ?: creds?.convocazioneDate?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) "15/06/2026" else ""

    val effectiveTime = appTime.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) "09:30" else ""

    val effectiveLoc = appLoc.takeIf { it.isNotBlank() }
        ?: creds?.convocazioneLocation?.takeIf { it.isNotBlank() }
        ?: questuraObj?.let { "${it.name} - ${it.address}" }
        ?: "Questura competente"

    var showEditAppointmentDialog by remember { mutableStateOf(false) }
    var showEditProfileMetaDialog by remember { mutableStateOf(false) }

    val vaultChecklist = remember(
        effectiveAssicurata,
        effectiveIstanza,
        effectiveOlogramma,
        effectivePassport,
        effectivePassportExp,
        effectiveComune,
        effectiveProtocol
    ) {
        val receiptSub = if (effectiveAssicurata.isNotBlank()) {
            "Assicurata: $effectiveAssicurata" +
                (if (effectiveIstanza.isNotBlank()) " • Istanza: $effectiveIstanza" else "") +
                (if (effectiveOlogramma.isNotBlank()) " • Ologramma: $effectiveOlogramma" else "")
        } else {
            "Cedolino originale con ologramma e timbro Poste Italiane"
        }

        val passportExpDisplay = effectivePassportExp.ifBlank { "Data non configurata" }
        val passportSub = if (effectivePassport.isNotBlank()) {
            "Passaporto N. $effectivePassport (Scadenza: $passportExpDisplay) + fotocopia pagine"
        } else {
            "Passaporto originale in corso di validità (Scadenza: $passportExpDisplay) + fotocopia pagine"
        }

        val hospitalitySub = if (effectiveComune.isNotBlank() || effectiveProtocol.isNotBlank()) {
            listOfNotNull(
                effectiveProtocol.takeIf { it.isNotBlank() },
                effectiveComune.takeIf { it.isNotBlank() },
                "Ex Art. 7 D.Lgs. 286/98"
            ).joinToString(" - ")
        } else {
            "Dichiarazione di Ospitalità / Cessione Fabbricato (Art. 7 D.Lgs. 286/98) con timbro o ricevuta PEC"
        }

        listOf(
            VaultDocItem(
                id = "vault_ricevuta_orig",
                title = "Ricevuta Postale Originale (Mod. 22A)",
                subtitle = receiptSub
            ),
            VaultDocItem(
                id = "vault_passaporto",
                title = "Passaporto Originale in Corso di Validità",
                subtitle = passportSub
            ),
            VaultDocItem(
                id = "vault_4_foto",
                title = "4 Fototessere Identiche e Recenti",
                subtitle = "Sfondo bianco, formato ICAO biometrico standard per PSE elettronico"
            ),
            VaultDocItem(
                id = "vault_ospitalita",
                title = "Dichiarazione di Ospitalità / Alloggio",
                subtitle = hospitalitySub
            ),
            VaultDocItem(
                id = "vault_permesso_scaduto",
                title = "Permesso di Soggiorno Precedente (Originale)",
                subtitle = "Da esibire e consegnare al momento del ritiro della nuova tessera PSE"
            ),
            VaultDocItem(
                id = "vault_contratto_reddito",
                title = "Documentazione Lavorativa / Reddituale",
                subtitle = "Contratto di soggiorno / Unilav, ultime 3 buste paga e ultima CU / Dichiarazione Redditi"
            ),
            VaultDocItem(
                id = "vault_marca_bollo",
                title = "Marca da Bollo da €16,00 (se richiesta)",
                subtitle = "Verificare se già applicata sul modulo Kit Postale originale"
            )
        )
    }

    val completedCount = vaultChecklist.count { checkedDocs.contains(it.id) }
    val readinessProgress = if (vaultChecklist.isNotEmpty()) {
        completedCount.toFloat() / vaultChecklist.size.toFloat()
    } else {
        0f
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
            title = "Preparation Vault & Convocazione",
            subtitle = "Checklist fotosegnalamento e promemoria appuntamento in Questura",
            icon = Icons.Default.EventAvailable,
            accentColor = Color(0xFF6A1B9A),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Empty State or Institutional Demo Banner
        InstitutionalDemoOrEmptyBanner(
            hasSavedProfile = hasSavedProfile,
            isDemoMode = isDemoMode,
            lang = lang,
            onToggleDemo = { enabled ->
                viewModel.setInstitutionalDemoMode(enabled)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Convocazione Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4A148C)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2))
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFFFD54F),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (effectiveDate.isNotBlank()) {
                                "CONVOCAZIONE REGISTRATA"
                            } else {
                                "APPUNTAMENTO DA IMPOSTARE"
                            },
                            color = Color(0xFF311B92),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { showEditProfileMetaDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .testTag("vault_edit_profile_meta_button")
                        ) {
                            Icon(
                                Icons.Default.Badge,
                                contentDescription = "Dati Passaporto e Comune",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { showEditAppointmentDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .testTag("vault_edit_appointment_button")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Modifica Appuntamento",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (effectiveDate.isNotBlank()) {
                        buildString {
                            append(effectiveDate)
                            if (effectiveTime.isNotBlank()) append(" • Ore $effectiveTime")
                        }
                    } else {
                        "Data non configurata"
                    },
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFE1BEE7),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = effectiveLoc,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_INSERT).apply {
                                data = CalendarContract.Events.CONTENT_URI
                                putExtra(
                                    CalendarContract.Events.TITLE,
                                    "Convocazione Questura - Fotosegnalamento Permesso di Soggiorno"
                                )
                                putExtra(CalendarContract.Events.EVENT_LOCATION, effectiveLoc)
                                putExtra(
                                    CalendarContract.Events.DESCRIPTION,
                                    "Portare Ricevuta Postale Originale ($effectiveAssicurata), Passaporto ($effectivePassport), 4 Fototessere e Dichiarazione Ospitalità."
                                )
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Calendario non disponibile", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF4A148C)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vault_save_calendar_button")
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salva in Calendario", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showEditAppointmentDialog = true },
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("vault_set_date_button")
                    ) {
                        Text("Imposta Data", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Readiness Progress Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prontezza Cartellina Documenti", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = "$completedCount / ${vaultChecklist.size} Pronti (${(readinessProgress * 100).toInt()}%)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = if (readinessProgress == 1f) Color(0xFF2E7D32) else Color(0xFF6A1B9A)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { readinessProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (readinessProgress == 1f) Color(0xFF2E7D32) else Color(0xFF6A1B9A),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        FeatureSectionDivider("Checklist Documenti da Portare in Questura")

        // Checklist Items
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            vaultChecklist.forEach { item ->
                val isChecked = checkedDocs.contains(item.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vault_doc_${item.id}")
                        .clickable { viewModel.toggleDocument(item.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isChecked) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isChecked) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { viewModel.toggleDocument(item.id) },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text(
                                text = item.subtitle,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showEditAppointmentDialog) {
        var tempDate by remember { mutableStateOf(effectiveDate) }
        var tempTime by remember { mutableStateOf(effectiveTime) }
        var tempLoc by remember { mutableStateOf(effectiveLoc) }

        AlertDialog(
            onDismissRequest = { showEditAppointmentDialog = false },
            title = { Text("Aggiorna Data Convocazione", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tempDate,
                        onValueChange = { tempDate = it },
                        label = { Text("Data (es. 15/06/2026)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempTime,
                        onValueChange = { tempTime = it },
                        label = { Text("Orario (es. 09:30)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempLoc,
                        onValueChange = { tempLoc = it },
                        label = { Text("Indirizzo Questura") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAppointment(tempDate.trim(), tempTime.trim(), tempLoc.trim())
                        showEditAppointmentDialog = false
                    }
                ) {
                    Text("Salva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAppointmentDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    if (showEditProfileMetaDialog) {
        var tempPass by remember { mutableStateOf(savedPassport) }
        var tempPassExp by remember { mutableStateOf(savedPassportExpiry) }
        var tempProt by remember { mutableStateOf(savedHospProt) }
        var tempComune by remember { mutableStateOf(savedMunicipality) }

        AlertDialog(
            onDismissRequest = { showEditProfileMetaDialog = false },
            title = { Text("Dati Documenti & Ospitalità", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tempPass,
                        onValueChange = { tempPass = it },
                        label = { Text("Numero Passaporto") },
                        placeholder = { Text(PermessoViewModel.DEMO_PASSPORT) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempPassExp,
                        onValueChange = { tempPassExp = it },
                        label = { Text("Scadenza Passaporto (GG/MM/AAAA)") },
                        placeholder = { Text(PermessoViewModel.DEMO_PASSPORT_EXPIRY) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempProt,
                        onValueChange = { tempProt = it },
                        label = { Text("Protocollo Ospitalità (opzionale)") },
                        placeholder = { Text(PermessoViewModel.DEMO_HOSPITALITY_PROT) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempComune,
                        onValueChange = { tempComune = it },
                        label = { Text("Comune Dichiarazione Ospitalità") },
                        placeholder = { Text(PermessoViewModel.DEMO_COMUNE) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateExtendedProfile(
                            passportNum = tempPass,
                            passportExp = tempPassExp,
                            hospProtocol = tempProt,
                            municipality = tempComune
                        )
                        showEditProfileMetaDialog = false
                    }
                ) {
                    Text("Salva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileMetaDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }
}
