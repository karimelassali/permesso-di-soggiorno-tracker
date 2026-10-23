package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Questura
import com.example.data.QuesturaCatalog
import com.example.data.UserCredentials
import com.example.ui.theme.*
import com.example.util.Localization
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.platform.LocalView
import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Build
import java.io.OutputStream
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizedQuesturaCard(
    credentials: UserCredentials?,
    onChooseQuestura: () -> Unit,
    selectedLanguageCode: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    
    var isCapturing by remember { mutableStateOf(false) }
    var composableBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    
    val selectedQuestura = remember(credentials) {
        credentials?.selectedQuesturaId?.let { QuesturaCatalog.getById(it) }
    }

    val cardBorder = if (selectedQuestura != null) {
        BorderStroke(
            width = 3.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF009F3D), // Italian Green
                    Color(0xFFF1F5F9), // Light Silver
                    Color(0xFFCD212A)  // Italian Red
                )
            )
        )
    } else {
        BorderStroke(1.dp, BentoCardBorder)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                if (!isCapturing) {
                    composableBounds = coordinates.boundsInRoot()
                }
            }
            .border(
                border = cardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("personalized_questura_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column {
            if (selectedQuestura != null) {
                // Continuous Italian Flag gradient banner on top
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF009F3D), // Green
                                    Color(0xFFFFFFFF), // White
                                    Color(0xFFCD212A)  // Red
                                )
                            )
                        )
                ) {}
            }
            
            Column(modifier = Modifier.padding(18.dp)) {
                if (selectedQuestura == null) {
                    // Not configured state
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ItalianGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = ItalianGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Localization.string("your_questura_title", selectedLanguageCode),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = BentoTextHeading
                            )
                            Text(
                                text = Localization.string("tracking_to_configure", selectedLanguageCode),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Text(
                        text = Localization.string("onboarding_personalize_desc", selectedLanguageCode),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onChooseQuestura,
                        colors = ButtonDefaults.buttonColors(containerColor = ItalianGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp).testTag("configure_personalization_btn")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.string("credentials_configure", selectedLanguageCode),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Personalized Selected Questura view
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ItalianGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = ItalianGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Localization.string("your_questura_title", selectedLanguageCode),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = ItalianGreen
                            )
                            Text(
                                text = selectedQuestura.province,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = BentoTextHeading
                            )
                        }
                        
                        if (!isCapturing) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            isCapturing = true
                                            delay(150)
                                            try {
                                                composableBounds?.let { bounds ->
                                                    val fullBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                                                    val canvas = Canvas(fullBitmap)
                                                    view.draw(canvas)
                                                    
                                                    val left = bounds.left.toInt().coerceAtLeast(0)
                                                    val top = bounds.top.toInt().coerceAtLeast(0)
                                                    val width = bounds.width.toInt().coerceAtMost(view.width - left)
                                                    val height = bounds.height.toInt().coerceAtMost(view.height - top)
                                                    
                                                    if (width > 0 && height > 0) {
                                                        val croppedBitmap = Bitmap.createBitmap(fullBitmap, left, top, width, height)
                                                        val provinceName = selectedQuestura.province.replace(" ", "_")
                                                        val uri = saveBitmapToGallery(context, croppedBitmap, "Questura_Card_$provinceName")
                                                        if (uri != null) {
                                                            Toast.makeText(context, "Questura Card saved to Gallery (Pictures/PermessoApp)!", Toast.LENGTH_LONG).show()
                                                        } else {
                                                            Toast.makeText(context, "Error saving card image", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        Toast.makeText(context, "Failed to capture card area", Toast.LENGTH_SHORT).show()
                                                    }
                                                } ?: run {
                                                    Toast.makeText(context, "Coordinates not loaded yet", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isCapturing = false
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Download Card as Image",
                                        tint = ItalianGreen
                                    )
                                }

                                IconButton(
                                    onClick = onChooseQuestura,
                                    modifier = Modifier.testTag("change_questura_btn")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Change", tint = ItalianGreen)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = selectedQuestura.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BentoTextHeading
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = BentoTextSecondary,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Text(
                            text = selectedQuestura.address,
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quick Contacts Grid / Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Call Button
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${selectedQuestura.phone}"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot dial: ${selectedQuestura.phone}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.string("call", selectedLanguageCode), fontSize = 11.sp)
                        }
                        
                        // Maps Button
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedQuestura.googleMapsUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open maps", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.string("open_maps", selectedLanguageCode), fontSize = 11.sp)
                        }
                        
                        // Website Button
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedQuestura.website))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open website", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.string("website", selectedLanguageCode), fontSize = 11.sp)
                        }
                    }
                    
                        Spacer(modifier = Modifier.height(10.dp))

                        var showFullQuesturaDetails by remember { mutableStateOf(false) }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showFullQuesturaDetails = !showFullQuesturaDetails },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (showFullQuesturaDetails) "Nascondi orari, PEC e avvisi" else "Mostra orari, PEC e avvisi della Questura",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ItalianGreen
                                )
                                Icon(
                                    imageVector = if (showFullQuesturaDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = ItalianGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (showFullQuesturaDetails) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BentoCardBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Detailed Local Information
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Opening Hours
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = ItalianGreen, modifier = Modifier.size(16.dp))
                                    Column {
                                        Text(Localization.string("opening_hours", selectedLanguageCode), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BentoTextHeading)
                                        Text(selectedQuestura.openingHours.get(selectedLanguageCode), fontSize = 11.sp, color = BentoTextSecondary)
                                    }
                                }

                                // PEC
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = ItalianGreen, modifier = Modifier.size(16.dp))
                                    Column {
                                        Text(Localization.string("pec", selectedLanguageCode), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BentoTextHeading)
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = selectedQuestura.pec,
                                                fontSize = 11.sp,
                                                color = BentoTextSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            if (!isCapturing) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copy",
                                                    tint = ItalianGreen,
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .clickable {
                                                            clipboardManager.setText(AnnotatedString(selectedQuestura.pec))
                                                            Toast.makeText(context, "PEC copied to clipboard", Toast.LENGTH_SHORT).show()
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Average wait
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Timeline, contentDescription = null, tint = ItalianGreen, modifier = Modifier.size(16.dp))
                                    Column {
                                        Text(Localization.string("average_waiting_time", selectedLanguageCode), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BentoTextHeading)
                                        Text(selectedQuestura.averageWaitingTime.get(selectedLanguageCode), fontSize = 11.sp, color = BentoTextSecondary)
                                    }
                                }

                                // Local Warnings (If any)
                                val warningText = selectedQuestura.localWarnings.get(selectedLanguageCode)
                                if (warningText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = BentoAlertRed.copy(alpha = 0.08f)),
                                        border = BorderStroke(1.dp, BentoAlertRed.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = BentoAlertRed, modifier = Modifier.size(14.dp))
                                            Column {
                                                Text(
                                                    text = Localization.string("local_warnings", selectedLanguageCode),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = BentoAlertRed
                                                )
                                                Text(
                                                    text = warningText,
                                                    fontSize = 10.sp,
                                                    color = BentoTextPrimary,
                                                    lineHeight = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestureExplorerScreen(
    onBack: () -> Unit,
    onSelectQuestura: (Questura) -> Unit,
    selectedLanguageCode: String,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0 = Più Vicina, 1 = Sede Centrale, 2 = Commissariati
    val results = remember(searchQuery, selectedFilterIndex) {
        val base = QuesturaCatalog.search(searchQuery)
        when (selectedFilterIndex) {
            0 -> base // Più Vicina (tutte ordinate)
            1 -> base.filter { it.name.contains("Questura", ignoreCase = true) }
            else -> base
        }
    }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 1. FULL-CAPSULE SEARCH PILL
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Cerca Questura, città o provincia...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ElectricBlue
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Cancel, contentDescription = "Cancella")
                    }
                }
            },
            singleLine = true,
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = AppleBorderHairline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("explore_questure_search")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. FILTER CHIPS (📍 Più Vicina, 🏛️ Sede Centrale, 📮 Commissariati)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                0 to "📍 Più Vicina",
                1 to "🏛️ Sede Centrale",
                2 to "📮 Commissariati"
            )
            filters.forEach { (idx, label) ->
                val isSelected = selectedFilterIndex == idx
                Surface(
                    onClick = { selectedFilterIndex = idx },
                    shape = CircleShape,
                    color = if (isSelected) SunsetTangerine else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isSelected) SunsetTangerine else AppleBorderHairline),
                    modifier = Modifier
                        .weight(1f)
                        .appleBounceClick()
                ) {
                    Text(
                        text = label,
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

        Spacer(modifier = Modifier.height(14.dp))

        // 3. UFFICIO IMMIGRAZIONE INSTITUTIONAL ACCESS CARD
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = SunsetTangerineTint,
            border = BorderStroke(1.dp, SunsetTangerine.copy(alpha = 0.45f)),
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sportello_4_counter_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SunsetTangerine
                    ) {
                        Text(
                            text = "UFFICIO IMMIGRAZIONE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = VividEmeraldTint
                    ) {
                        Text(
                            text = "${results.size} SEDI NAZIONALI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007A33),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Modalità di Accesso Sportelli Questura",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppleInk
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, SunsetTangerine.copy(alpha = 0.35f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Fotosegnalamento", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                            Text("Su convocazione Poste / PrenotaFacile", fontSize = 11.sp, color = AppleInk)
                            Text("Portare kit + originali", fontSize = 10.sp, color = AppleTextMuted)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, VividEmerald),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Ritiro Titolo PSE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VividEmerald)
                            Text("Solo con stato Pronto / SMS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AppleInk)
                            Text("Verificare orari sede", fontSize = 10.sp, color = AppleTextMuted)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. QUESTURE LIST WITH 1-TAP APPLE MAPS / GOOGLE MAPS LAUNCH
        if (results.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Localization.string("guides_no_results", selectedLanguageCode),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(results, key = { it.id }) { questura ->
                    var isExpanded by remember { mutableStateOf(questura.id == results.firstOrNull()?.id) }

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, AppleBorderHairline),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("explorer_questura_item_${questura.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateContentSize()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isExpanded = !isExpanded }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(SunsetTangerineTint, RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = SunsetTangerine,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = questura.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${questura.province} (${questura.provinceCode}) • ${questura.address}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = VividEmeraldTint
                                ) {
                                    Text(
                                        text = questura.provinceCode,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF007A33),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Espandi",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Place, contentDescription = null, tint = SunsetTangerine, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = questura.address,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = VividEmerald, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "Orari Ufficio Immigrazione: ${questura.openingHours.get(selectedLanguageCode)}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.clickable {
                                            clipboardManager.setText(AnnotatedString(questura.pec))
                                            Toast.makeText(context, "PEC Copiata: ${questura.pec}", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "PEC: ${questura.pec}",
                                            fontSize = 12.sp,
                                            color = ElectricBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = SiriViolet, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "Attesa media: ${questura.averageWaitingTime.get(selectedLanguageCode)}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Action Pills: 1-Tap Apple Maps / Google Maps + Select Questura
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            try {
                                                val encodedQuery = Uri.encode("${questura.name} ${questura.address}")
                                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encodedQuery"))
                                                context.startActivity(mapIntent)
                                            } catch (_: Exception) {
                                                Toast.makeText(context, "Indirizzo: ${questura.address}", Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricBlue,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .appleBounceClick()
                                    ) {
                                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Apri in Mappe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            onSelectQuestura(questura)
                                            Toast.makeText(context, "Questura impostata: ${questura.province}", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = VividEmerald,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .appleBounceClick()
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Seleziona Sede", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filename: String): Uri? {
    val contentResolver = context.contentResolver
    val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.IS_PENDING, 1)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PermessoApp")
        }
    }
    
    val imageUri = contentResolver.insert(imageCollection, contentValues)
    if (imageUri != null) {
        try {
            val outputStream: OutputStream? = contentResolver.openOutputStream(imageUri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(imageUri, contentValues, null, null)
            }
            return imageUri
        } catch (e: Exception) {
            contentResolver.delete(imageUri, null, null)
            e.printStackTrace()
        }
    }
    return null
}
