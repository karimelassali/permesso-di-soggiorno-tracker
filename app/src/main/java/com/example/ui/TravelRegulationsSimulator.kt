package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelRegulationsSimulatorView(
    lang: String,
    onBack: () -> Unit,
    enableInternalScroll: Boolean = false
) {
    var isRinnovo by remember { mutableStateOf(true) } // true = Rinnovo, false = Primo Rilascio
    var destinationType by remember { mutableStateOf(0) } // 0 = Paese d'Origine (Volo Diretto), 1 = Con Scalo Schengen, 2 = Turismo in Paese Schengen
    var sameFrontier by remember { mutableStateOf(true) }
    var hasOriginalReceipt by remember { mutableStateOf(true) }
    var hasExpiredPermit by remember { mutableStateOf(true) }
    var hasValidPassport by remember { mutableStateOf(true) }

    val travelVerdict = remember(isRinnovo, destinationType, hasOriginalReceipt, hasExpiredPermit, hasValidPassport, lang) {
        when {
            !hasValidPassport || !hasOriginalReceipt -> Triple(
                "DOCUMENTAZIONE INCOMPLETA PER L'IMBARCO",
                "Per viaggiare con la ricevuta postale è obbligatorio avere il Passaporto in corso di validità e la Ricevuta Postale ORIGINALE (Mod. 22A) con ologramma e timbro Poste Italiane.",
                Color(0xFFC62828)
            )
            !isRinnovo -> Triple(
                Strings.get("travel_verdict_first_issue", lang),
                "Con la ricevuta di PRIMO RILASCIO non è consentito uscire e rientrare in Italia senza un nuovo visto di reingresso, né circolare nell'area Schengen (Direttiva Min. Interno 5 agosto 2006).",
                Color(0xFFE65100)
            )
            destinationType == 1 -> Triple(
                Strings.get("travel_verdict_schengen_denied", lang),
                "La ricevuta postale di rinnovo NON è riconosciuta dagli altri Stati Schengen. Non puoi fare scalo aereo in Francia, Germania, Spagna, Svizzera o altri Paesi Schengen: il volo deve essere DIRETTO tra l'Italia e un Paese extra-Schengen.",
                Color(0xFFC62828)
            )
            destinationType == 2 -> Triple(
                Strings.get("travel_verdict_schengen_denied", lang),
                "In fase di rinnovo con sola ricevuta postale non puoi recarti per turismo o visita in altri Stati dell'area Schengen. Devi attendere il ritiro della tessera elettronica (PSE).",
                Color(0xFFC62828)
            )
            !hasExpiredPermit -> Triple(
                "MANCA IL PERMESSO SCADUTO ORIGINALE",
                "Per il rinnovo, la Polizia di Frontiera richiede di esibire sia la ricevuta postale originale sia il vecchio Permesso di Soggiorno scaduto (o denuncia di smarrimento/furto).",
                Color(0xFFE65100)
            )
            else -> Triple(
                Strings.get("travel_verdict_compliant", lang),
                "Orientamento conforme alla Circolare Min. Interno del 5 agosto 2006: con ricevuta di RINNOVO originale, passaporto valido, vecchio permesso scaduto e volo DIRETTO senza scali Schengen, l'uscita e il rientro in Italia sono previsti dalla normativa. Verifica sempre con la compagnia aerea prima dell'acquisto.",
                Color(0xFF2E7D32)
            )
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
            title = "Simulatore Viaggio con Ricevuta",
            subtitle = "Orientamento Circolare Min. Interno (Rinnovo vs Primo Rilascio)",
            icon = Icons.Default.FlightTakeoff,
            accentColor = Color(0xFF00796B),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Mandatory Regulatory Disclaimer Card at the top of the simulator result
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("travel_regulatory_disclaimer_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            border = BorderStroke(1.5.dp, Color(0xFFFFA000))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "Avviso Normativo",
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = Strings.get("travel_regulations_disclaimer", lang),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3E2723),
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Verdict Banner Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("travel_verdict_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = travelVerdict.third.copy(alpha = 0.1f)),
            border = BorderStroke(2.dp, travelVerdict.third)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(travelVerdict.third),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (travelVerdict.third == Color(0xFF2E7D32)) {
                                Icons.Default.FactCheck
                            } else {
                                Icons.Default.WarningAmber
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ORIENTAMENTO NORMATIVO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = travelVerdict.third
                        )
                        Text(
                            text = travelVerdict.first,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = travelVerdict.third
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = travelVerdict.second,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 19.sp
                )
            }
        }

        FeatureSectionDivider("1. Tipologia di Ricevuta")

        // Step 1: Tipo di Ricevuta
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Seleziona il tipo di pratica sulla ricevuta",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = isRinnovo,
                        onClick = { isRinnovo = true },
                        label = { Text("Rinnovo / Aggiornamento") },
                        leadingIcon = {
                            if (isRinnovo) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("travel_chip_rinnovo")
                    )
                    FilterChip(
                        selected = !isRinnovo,
                        onClick = { isRinnovo = false },
                        label = { Text("Primo Rilascio") },
                        leadingIcon = {
                            if (!isRinnovo) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("travel_chip_primo_rilascio")
                    )
                }
            }
        }

        FeatureSectionDivider("2. Rotta Aerea & Scali")

        // Step 2: Rotta e Destinazione
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Seleziona l'itinerario del volo", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                val routes = listOf(
                    Triple(0, "Paese d'origine (Volo DIRETTO dall'Italia)", "Nessuno scalo in altri Paesi UE/Schengen"),
                    Triple(1, "Paese d'origine CON SCALO Schengen", "Es: Scalo a Parigi, Francoforte, Madrid, Zurigo"),
                    Triple(2, "Viaggio / Turismo in altro Stato Schengen", "Es: Vacanza o visita in Francia, Spagna, Germania")
                )

                routes.forEach { (idx, title, desc) ->
                    Surface(
                        onClick = { destinationType = idx },
                        shape = RoundedCornerShape(12.dp),
                        color = if (destinationType == idx) {
                            Color(0xFF00796B).copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (destinationType == idx) Color(0xFF00796B) else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("travel_route_option_$idx")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = destinationType == idx, onClick = { destinationType = idx })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        FeatureSectionDivider("3. Verifica Documenti per la Frontiera")

        // Step 3: Checklist Documenti Obbligatori per l'Imbarco
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Documenti richiesti al controllo Polizia di Frontiera",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasValidPassport, onCheckedChange = { hasValidPassport = it })
                    Text("Passaporto originale in corso di validità", fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasOriginalReceipt, onCheckedChange = { hasOriginalReceipt = it })
                    Text("Ricevuta Postale ORIGINALE (Mod. 22A) con ologramma", fontSize = 13.sp)
                }
                if (isRinnovo) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = hasExpiredPermit, onCheckedChange = { hasExpiredPermit = it })
                        Text("Permesso di Soggiorno scaduto in originale", fontSize = 13.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = sameFrontier, onCheckedChange = { sameFrontier = it })
                    Text("Volo diretto senza transito in altri aeroporti Schengen", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Official Legal Reference Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF00695C))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Riferimento Normativo: Direttiva Min. Interno 5 Agosto 2006 & Circolare 11 Marzo 2009",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF004D40)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lo straniero in attesa di rinnovo del permesso di soggiorno può uscire dall'Italia e farvi rientro a condizione che il viaggio non preveda il transito in altri Paesi Schengen, esibendo passaporto, ricevuta postale originale e permesso scaduto. Dal 2009 non è più obbligatorio utilizzare lo stesso valico di frontiera in uscita e in entrata.",
                        fontSize = 12.sp,
                        color = Color(0xFF00695C),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
