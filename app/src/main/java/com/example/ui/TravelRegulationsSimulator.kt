package com.example.ui

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelRegulationsSimulatorView(
    lang: String,
    onBack: () -> Unit,
    enableInternalScroll: Boolean = false
) {
    var isRinnovo by remember { mutableStateOf(true) } // true = Rinnovo, false = Primo Rilascio
    var destinationType by remember { mutableIntStateOf(0) } // 0 = Volo Diretto, 1 = Con Scalo Schengen, 2 = Turismo Schengen
    var sameFrontier by remember { mutableStateOf(true) }
    var hasOriginalReceipt by remember { mutableStateOf(true) }
    var hasExpiredPermit by remember { mutableStateOf(true) }
    var hasValidPassport by remember { mutableStateOf(true) }

    val travelVerdict = remember(
        isRinnovo,
        destinationType,
        hasOriginalReceipt,
        hasExpiredPermit,
        hasValidPassport,
        sameFrontier,
        lang
    ) {
        when {
            destinationType == 1 || destinationType == 2 || !sameFrontier -> Triple(
                "⚠️ DIVIETO TRANSITO SCHENGEN",
                "La ricevuta postale Mod. 22A NON è riconosciuta dagli altri Stati dell'area Schengen. È severamente vietato fare scalo aereo a Parigi, Francoforte, Madrid, Zurigo o recarsi per turismo in altri Paesi UE: il volo deve essere DIRETTO tra l'Italia e il Paese d'origine extra-Schengen.",
                AlertRed
            )
            !isRinnovo -> Triple(
                "⚠️ PRIMO RILASCIO: SERVE VISTO DI REINGRESSO",
                "Con la ricevuta di PRIMO RILASCIO non è consentito uscire e rientrare liberamente in Italia senza un nuovo visto di reingresso consolare, né transitare nell'area Schengen (Direttiva Min. Interno 5 agosto 2006).",
                SunsetTangerine
            )
            !hasValidPassport || !hasOriginalReceipt || !hasExpiredPermit -> Triple(
                "⚠️ DOCUMENTAZIONE DI FRONTIERA INCOMPLETA",
                "Per l'imbarco con ricevuta di rinnovo è obbligatorio esibire alla Polizia di Frontiera: 1) Passaporto valido, 2) Ricevuta Postale ORIGINALE (Mod. 22A) con ologramma e timbro Poste, 3) Permesso di Soggiorno scaduto in originale.",
                AlertRed
            )
            else -> Triple(
                "✓ VIAGGIO CONSENTITO",
                "Itinerario conforme alla Direttiva Min. Interno 5 agosto 2006: con ricevuta di RINNOVO Mod. 22A originale, passaporto valido, vecchio permesso scaduto e volo DIRETTO senza scali Schengen, l'uscita e il rientro in Italia sono consentiti.",
                VividEmerald
            )
        }
    }

    val isAllowed = travelVerdict.third == VividEmerald
    val verdictTint = when (travelVerdict.third) {
        VividEmerald -> VividEmeraldTint
        SunsetTangerine -> SunsetTangerineTint
        else -> AlertRedTint
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
        // 1. DYNAMIC VERDICT HERO SQUIRCLE (Glowing Green vs Red Alert)
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = verdictTint,
            border = BorderStroke(2.dp, travelVerdict.third),
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("travel_verdict_card")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = travelVerdict.third
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isAllowed) Icons.Default.Verified else Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = travelVerdict.first,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = if (destinationType == 0) "✈️ VOLO DIRETTO" else "🚫 SCALO SCHENGEN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppleInk,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = if (isAllowed) {
                        "Rientro in Italia Autorizzato con Mod. 22A"
                    } else {
                        "Attenzione: Rischio Respingimento alla Frontiera"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.28).sp,
                    color = AppleInk
                )

                Text(
                    text = travelVerdict.second,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = AppleInk.copy(alpha = 0.88f)
                )
            }
        }

        // 2. INTERACTIVE ROUTE SELECTOR (Direct vs Schengen Transit & Rinnovo vs Primo Rilascio)
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, AppleBorderHairline),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "1. TIPOLOGIA DI RICEVUTA POSTALE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue,
                    letterSpacing = 0.6.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = { isRinnovo = true },
                        shape = CircleShape,
                        color = if (isRinnovo) VividEmerald else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .appleBounceClick()
                            .testTag("travel_chip_rinnovo")
                    ) {
                        Text(
                            text = "✓ Rinnovo / Aggiornamento",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRinnovo) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 11.dp, horizontal = 8.dp)
                        )
                    }

                    Surface(
                        onClick = { isRinnovo = false },
                        shape = CircleShape,
                        color = if (!isRinnovo) SunsetTangerine else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .appleBounceClick()
                            .testTag("travel_chip_primo_rilascio")
                    ) {
                        Text(
                            text = "Primo Rilascio",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isRinnovo) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 11.dp, horizontal = 8.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)

                Text(
                    text = "2. ROTTA AEREA & SCALI INTERNAZIONALI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue,
                    letterSpacing = 0.6.sp
                )

                val routes = listOf(
                    Triple(
                        0,
                        "✈️ Volo DIRETTO (Italia ↔ Paese d'Origine)",
                        "Nessuno scalo in aeroporti dell'area Schengen"
                    ),
                    Triple(
                        1,
                        "⚠️ Volo CON SCALO in Paese Schengen",
                        "Es: Transito a Parigi, Francoforte, Madrid, Monaco, Zurigo"
                    ),
                    Triple(
                        2,
                        "🇪🇺 Viaggio / Turismo in altro Stato Schengen",
                        "Spostamento intra-UE senza permesso elettronico PSE"
                    )
                )

                routes.forEach { (idx, title, desc) ->
                    val selected = destinationType == idx
                    val activeAccent = if (idx == 0) VividEmerald else AlertRed
                    val activeTint = if (idx == 0) VividEmeraldTint else AlertRedTint

                    Surface(
                        onClick = {
                            destinationType = idx
                            sameFrontier = (idx == 0)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (selected) activeTint else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(
                            if (selected) 1.5.dp else 1.dp,
                            if (selected) activeAccent else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .appleBounceClick()
                            .testTag("travel_route_option_$idx")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = {
                                    destinationType = idx
                                    sameFrontier = (idx == 0)
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = activeAccent)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = if (selected) AppleInk else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = if (selected) AppleInk.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. BORDER DOCUMENT CHECKLIST WITH INTERACTIVE TOGGLES
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, AppleBorderHairline),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3. CHECKLIST POLIZIA DI FRONTIERA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VividEmerald,
                        letterSpacing = 0.6.sp
                    )
                    Surface(
                        shape = CircleShape,
                        color = VividEmeraldTint
                    ) {
                        Text(
                            text = "OBBLIGATORI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007A33),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                BorderToggleRow(
                    title = "Passaporto originale in corso di validità",
                    subtitle = "Deve coprire l'intera durata del viaggio",
                    checked = hasValidPassport,
                    onCheckedChange = { hasValidPassport = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)

                BorderToggleRow(
                    title = "Ricevuta Postale ORIGINALE (Mod. 22A)",
                    subtitle = "Con ologramma Poste Italiane e timbro a data",
                    checked = hasOriginalReceipt,
                    onCheckedChange = { hasOriginalReceipt = it }
                )

                if (isRinnovo) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                    BorderToggleRow(
                        title = "Permesso di Soggiorno scaduto in originale",
                        subtitle = "Richiesto insieme alla ricevuta di rinnovo",
                        checked = hasExpiredPermit,
                        onCheckedChange = { hasExpiredPermit = it }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                BorderToggleRow(
                    title = "Biglietto A/R senza scali in area Schengen",
                    subtitle = "Tratta diretta tra aeroporto italiano e Paese extra-UE",
                    checked = sameFrontier && destinationType == 0,
                    onCheckedChange = {
                        sameFrontier = it
                        if (it) destinationType = 0 else destinationType = 1
                    }
                )
            }
        }

        // 4. MANDATORY REGULATORY DISCLAIMER CARD
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = SunsetTangerineTint,
            border = BorderStroke(1.dp, SunsetTangerine.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("travel_regulatory_disclaimer_card")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "Avviso Normativo",
                    tint = SunsetTangerine,
                    modifier = Modifier.size(20.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Direttiva Min. Interno 5 Agosto 2006 & Circolare 11 Marzo 2009",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppleInk
                    )
                    Text(
                        text = Strings.get("travel_regulations_disclaimer", lang),
                        fontSize = 12.sp,
                        color = AppleInk.copy(alpha = 0.8f),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun BorderToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VividEmerald
            )
        )
    }
}
