package com.example.ui

import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserCredentials
import com.example.ui.theme.*
import com.example.util.Localization
import java.util.Calendar
import java.util.Locale

// ============================================================================
// NAVIGATION DESTINATIONS FOR GLOBAL BURGER DRAWER & TABS
// ============================================================================
enum class AppleScreenRoute {
    STATO_HOME,
    STRUMENTI_HUB,
    PROFILO_PASS,
    SOFIA_CHAT,
    SOLLECITO_PEC,
    SIMULATORE_VIAGGI,
    ESPLORA_QUESTURE,
    CHECKLIST_FOTOSEGNALAMENTO,
    PORTALE_WEB,
    REDDITO_SIMULATOR,
    GUIDE_IMMIGRAZIONE
}

// ============================================================================
// TACTILE PRESS PHYSICS
// ============================================================================
fun Modifier.appleBounceClick(scaleFactor: Float = 0.97f) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleFactor else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tactileScale"
    )
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

fun Modifier.appleBounceClickable(
    scaleFactor: Float = 0.97f,
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) scaleFactor else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tactileClickable"
    )
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onClick()
                    }
                )
            }
        }
}

// ============================================================================
// GLOBAL NAVIGATION: HIGH-PRECISION TECHNICAL TOP BAR + MENU TRIGGER
// ============================================================================
@Composable
fun AppleTopHeaderBar(
    currentRoute: AppleScreenRoute,
    selectedLanguageCode: String,
    credentials: UserCredentials? = null,
    onSelectLanguage: ((String) -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onOpenBurgerMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLangMenuExpanded by remember { mutableStateOf(false) }

    val hasConfiguredCodes = remember(credentials) {
        credentials != null && (
            credentials.numeroAssicurata.isNotBlank() ||
                credentials.numeroPratica.isNotBlank() ||
                (credentials.userId.isNotBlank() && credentials.password.isNotBlank())
            )
    }
    val hasVerifiedStatus = remember(credentials) {
        credentials != null && (
            (credentials.lastPoliziaStatus.isNotBlank() && credentials.lastPoliziaStatus != "No checks yet") ||
                (credentials.lastPortaleStatus.isNotBlank() && credentials.lastPortaleStatus != "No checks yet")
            )
    }

    val screenTitle = when (currentRoute) {
        AppleScreenRoute.STATO_HOME -> when (selectedLanguageCode) {
            "en" -> "Status Monitor"
            "ar" -> "مراقبة الحالة"
            "fr" -> "Suivi Dossier"
            "es" -> "Monitor de Estado"
            else -> "Monitoraggio Pratica"
        }
        AppleScreenRoute.STRUMENTI_HUB -> when (selectedLanguageCode) {
            "en" -> "Legal & Tech Tools"
            "ar" -> "الأدوات القانونية"
            "fr" -> "Outils Juridiques"
            "es" -> "Herramientas Legales"
            else -> "Strumenti Operativi"
        }
        AppleScreenRoute.PROFILO_PASS -> when (selectedLanguageCode) {
            "en" -> "Dossier & Vault"
            "ar" -> "الملف والخزنة"
            "fr" -> "Dossier & Coffre"
            "es" -> "Expediente y Bóveda"
            else -> "Dossier & Credenziali"
        }
        AppleScreenRoute.SOFIA_CHAT -> when (selectedLanguageCode) {
            "en" -> "Legal Reference D.Lgs. 286/98"
            "ar" -> "المستشار القانوني"
            else -> "Consulenza Normativa"
        }
        AppleScreenRoute.SOLLECITO_PEC -> "Generatore Diffida PEC"
        AppleScreenRoute.SIMULATORE_VIAGGI -> "Verifica Viaggi Mod. 22A"
        AppleScreenRoute.ESPLORA_QUESTURE -> "Registro Uffici Questura"
        AppleScreenRoute.CHECKLIST_FOTOSEGNALAMENTO -> "Checklist Documentale"
        AppleScreenRoute.PORTALE_WEB -> "Terminale Portali Ufficiali"
        AppleScreenRoute.REDDITO_SIMULATOR -> "Calcolo Soglie Reddito"
        AppleScreenRoute.GUIDE_IMMIGRAZIONE -> "Prontuario Normativo"
    }

    val telemetryStatusText = when {
        hasVerifiedStatus -> when (selectedLanguageCode) {
            "en" -> "SYNCED"
            "ar" -> "متصل"
            else -> "SINCRONIZZATO"
        }
        hasConfiguredCodes -> when (selectedLanguageCode) {
            "en" -> "READY"
            "ar" -> "جاهز"
            else -> "CODICI PRONTI"
        }
        else -> when (selectedLanguageCode) {
            "en" -> "UNCONFIGURED"
            "ar" -> "غير مهيأ"
            else -> "NON CONFIGURATO"
        }
    }

    val telemetryDotColor = when {
        hasVerifiedStatus -> VividEmerald
        hasConfiguredCodes -> ElectricBlue
        else -> SunsetTangerine
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (onBackClick != null) {
                            Surface(
                                onClick = onBackClick,
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .size(36.dp)
                                    .appleBounceClick()
                                    .testTag("top_back_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Indietro",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Permesso di Soggiorno",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(telemetryDotColor, CircleShape)
                                )
                                Text(
                                    text = "$screenTitle • $telemetryStatusText",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (onSelectLanguage != null) {
                            Box {
                                Surface(
                                    onClick = { isLangMenuExpanded = !isLangMenuExpanded },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .appleBounceClick()
                                        .testTag("top_bar_language_toggle")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Language,
                                            contentDescription = "Lingua",
                                            tint = ElectricBlue,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = selectedLanguageCode.uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = isLangMenuExpanded,
                                    onDismissRequest = { isLangMenuExpanded = false }
                                ) {
                                    val langs = listOf(
                                        "it" to "Italiano (IT)",
                                        "en" to "English (EN)",
                                        "fr" to "Français (FR)",
                                        "es" to "Español (ES)",
                                        "ar" to "العربية (AR)"
                                    )
                                    langs.forEach { (code, label) ->
                                        val isSelected = selectedLanguageCode == code
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = label,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurface
                                                )
                                            },
                                            trailingIcon = {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = ElectricBlue,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            },
                                            onClick = {
                                                onSelectLanguage(code)
                                                isLangMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Precision Menu Button pinned Top-Right
                        Surface(
                            onClick = onOpenBurgerMenu,
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .size(38.dp)
                                .appleBounceClick()
                                .testTag("top_right_burger_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )
        }
    }
}

// ============================================================================
// SLIDE-OVER NAVIGATION DRAWER
// ============================================================================
@Composable
fun SlideOverBurgerDrawer(
    isDrawerOpen: Boolean,
    onClose: () -> Unit,
    currentRoute: AppleScreenRoute,
    onNavigate: (AppleScreenRoute) -> Unit,
    credentials: UserCredentials?,
    selectedLanguageCode: String,
    onSelectLanguage: (String) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    isDemoMode: Boolean,
    onToggleDemoMode: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = isDrawerOpen,
        enter = fadeIn(animationSpec = tween(220)),
        exit = fadeOut(animationSpec = tween(180))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.48f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(animationSpec = tween(220)),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            SlideOverDrawerContent(
                onClose = onClose,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    onNavigate(route)
                    onClose()
                },
                credentials = credentials,
                selectedLanguageCode = selectedLanguageCode,
                onSelectLanguage = onSelectLanguage,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                isDemoMode = isDemoMode,
                onToggleDemoMode = onToggleDemoMode
            )
        }
    }
}

@Composable
fun SlideOverDrawerContent(
    onClose: () -> Unit,
    currentRoute: AppleScreenRoute = AppleScreenRoute.STATO_HOME,
    onNavigate: (AppleScreenRoute) -> Unit = {},
    credentials: UserCredentials? = null,
    selectedLanguageCode: String = "it",
    onSelectLanguage: (String) -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleDarkMode: (Boolean) -> Unit = {},
    isDemoMode: Boolean = false,
    onToggleDemoMode: (Boolean) -> Unit = {}
) {
    val applicantName = credentials?.fullName?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_FULL_NAME else "Intestatario non configurato"

    val dossierCode = credentials?.codiceIstanza?.takeIf { it.isNotBlank() }
        ?: credentials?.numeroPratica?.takeIf { it.isNotBlank() }
        ?: credentials?.numeroAssicurata?.takeIf { it.isNotBlank() }
        ?: credentials?.userId?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_ASSICURATA else "Nessun codice salvato"

    val rawPolizia = credentials?.lastPoliziaStatus ?: "No checks yet"
    val rawPortale = credentials?.lastPortaleStatus ?: "No checks yet"
    val hasRealStatus = (rawPolizia != "No checks yet" && rawPolizia.isNotBlank()) ||
        (rawPortale != "No checks yet" && rawPortale.isNotBlank())
    val hasConfiguredCodes = credentials != null && (
        credentials.numeroAssicurata.isNotBlank() ||
            credentials.numeroPratica.isNotBlank() ||
            (credentials.userId.isNotBlank() && credentials.password.isNotBlank())
        )
    val isReady = hasRealStatus && (
        rawPolizia.contains("pronto", ignoreCase = true) ||
            rawPolizia.contains("consegna", ignoreCase = true) ||
            rawPortale.contains("pronto", ignoreCase = true) ||
            rawPortale.contains("consegna", ignoreCase = true)
        )

    var isLanguageMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 340.dp)
            .fillMaxWidth(0.86f)
            .shadow(16.dp, RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
            .testTag("slide_over_burger_drawer"),
        shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Permesso di Soggiorno",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Menu & Opzioni di Sistema",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        onClick = onClose,
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .size(38.dp)
                            .appleBounceClick()
                            .testTag("drawer_close_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Chiudi Menu",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Honest User Dossier Status Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        UserPersistentAvatar(
                            fullName = credentials?.fullName,
                            size = 42.dp,
                            fontSize = 14.sp,
                            borderColor = if (hasConfiguredCodes) VividEmerald else MaterialTheme.colorScheme.outlineVariant,
                            borderWidth = 1.5.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = applicantName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "RIF: $dossierCode",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val (drawerBadgeText, drawerBadgeColor, drawerBadgeBg) = when {
                        isReady -> Triple("✓ PRONTO PER IL RITIRO", Color(0xFF007A33), VividEmeraldTint)
                        hasRealStatus -> Triple("● IN ISTRUTTORIA UFFICIALE", ElectricBlue, ElectricBlueTint)
                        hasConfiguredCodes -> Triple("● CODICI PRONTI PER VERIFICA", ElectricBlue, ElectricBlueTint)
                        else -> Triple("○ NESSUNA PRATICA CONFIGURATA", Color(0xFF9A3412), SunsetTangerineTint)
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = drawerBadgeBg,
                        border = BorderStroke(1.dp, drawerBadgeColor.copy(alpha = 0.28f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = drawerBadgeText,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = drawerBadgeColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Preferences & Language Options Toggle first for fast access
            Text(
                text = "OPZIONI E LINGUA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val languageOptions = listOf(
                        Triple("it", "Italiano", "IT"),
                        Triple("en", "English", "EN"),
                        Triple("fr", "Français", "FR"),
                        Triple("es", "Español", "ES"),
                        Triple("ar", "العربية", "AR")
                    )
                    val currentLang = languageOptions.find { it.first == selectedLanguageCode } ?: languageOptions.first()

                    // Expandable Language Options Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isLanguageMenuExpanded = !isLanguageMenuExpanded }
                            .testTag("drawer_language_toggle_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Lingua dell'App",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tocca per cambiare lingua",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            onClick = { isLanguageMenuExpanded = !isLanguageMenuExpanded },
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricBlueTint,
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${currentLang.second} (${currentLang.third})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue
                                )
                                Icon(
                                    imageVector = if (isLanguageMenuExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = isLanguageMenuExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            languageOptions.forEach { (code, name, shortCode) ->
                                val selected = selectedLanguageCode == code
                                Surface(
                                    onClick = {
                                        onSelectLanguage(code)
                                        isLanguageMenuExpanded = false
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) VividEmeraldTint else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) VividEmerald else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 9.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$name ($shortCode)",
                                            fontSize = 13.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) Color(0xFF007A33) else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = VividEmerald,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Tema Scuro",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onToggleDarkMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VividEmerald
                            )
                        )
                    }
                }
            }

            // Main Screens
            Text(
                text = "SEZIONI PRINCIPALI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    DrawerNavRow(
                        title = "Monitoraggio Stato Live",
                        subtitle = "Interrogazione Polizia di Stato & Portale",
                        icon = Icons.Default.Verified,
                        iconBg = VividEmerald,
                        isSelected = currentRoute == AppleScreenRoute.STATO_HOME,
                        onClick = { onNavigate(AppleScreenRoute.STATO_HOME) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Strumenti Legali & Operativi",
                        subtitle = "Diffida PEC, Viaggi, Questure & Checklist",
                        icon = Icons.Default.GridView,
                        iconBg = ElectricBlue,
                        isSelected = currentRoute == AppleScreenRoute.STRUMENTI_HUB,
                        onClick = { onNavigate(AppleScreenRoute.STRUMENTI_HUB) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Dossier & Cassaforte Room",
                        subtitle = "Ricevuta Mod. 22A, Biometria & OCR",
                        icon = Icons.Default.QrCodeScanner,
                        iconBg = SunsetTangerine,
                        isSelected = currentRoute == AppleScreenRoute.PROFILO_PASS,
                        onClick = { onNavigate(AppleScreenRoute.PROFILO_PASS) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Consulente Normativo D.Lgs. 286/98",
                        subtitle = "Banca dati T.U. Immigrazione & Procedure",
                        icon = Icons.Default.Gavel,
                        iconBg = SiriViolet,
                        isSelected = currentRoute == AppleScreenRoute.SOFIA_CHAT,
                        onClick = { onNavigate(AppleScreenRoute.SOFIA_CHAT) }
                    )
                }
            }

            // Direct Tools
            Text(
                text = "STRUMENTI OPERATIVI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    DrawerNavRow(
                        title = "Generatore Sollecito PEC",
                        subtitle = "Atto formale ex art. 2 L. 241/1990",
                        icon = Icons.Default.MarkEmailRead,
                        iconBg = ElectricBlue,
                        isSelected = currentRoute == AppleScreenRoute.SOLLECITO_PEC,
                        onClick = { onNavigate(AppleScreenRoute.SOLLECITO_PEC) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Verifica Regole di Viaggio",
                        subtitle = "Circolare Min. Interno su Mod. 22A",
                        icon = Icons.Default.FlightTakeoff,
                        iconBg = VividEmerald,
                        isSelected = currentRoute == AppleScreenRoute.SIMULATORE_VIAGGI,
                        onClick = { onNavigate(AppleScreenRoute.SIMULATORE_VIAGGI) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Registro Questure & PEC",
                        subtitle = "Indirizzi Uffici Immigrazione, Orari e PEC",
                        icon = Icons.Default.AccountBalance,
                        iconBg = SunsetTangerine,
                        isSelected = currentRoute == AppleScreenRoute.ESPLORA_QUESTURE,
                        onClick = { onNavigate(AppleScreenRoute.ESPLORA_QUESTURE) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Checklist Fotosegnalamento",
                        subtitle = "Verifica documentale e marca da bollo",
                        icon = Icons.Default.FactCheck,
                        iconBg = SiriViolet,
                        isSelected = currentRoute == AppleScreenRoute.CHECKLIST_FOTOSEGNALAMENTO,
                        onClick = { onNavigate(AppleScreenRoute.CHECKLIST_FOTOSEGNALAMENTO) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Terminale Web Ufficiale",
                        subtitle = "Connessione diretta Portale & Polizia",
                        icon = Icons.Default.Language,
                        iconBg = Color(0xFF00838F),
                        isSelected = currentRoute == AppleScreenRoute.PORTALE_WEB,
                        onClick = { onNavigate(AppleScreenRoute.PORTALE_WEB) }
                    )
                }
            }

            // Local Security Badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = VividEmeraldTint.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, VividEmerald.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF007A33),
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Archiviazione Locale Room + BiometricPrompt",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007A33)
                        )
                        Text(
                            text = "Le credenziali vengono trasmesse esclusivamente ai server ufficiali Polizia di Stato / Portale Immigrazione.",
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            color = Color(0xFF007A33).copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) iconBg.copy(alpha = 0.08f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) iconBg else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ============================================================================
// SCREEN 1 COMPONENT: HERO STATUS TELEMETRY WIDGET (ZERO FAKE DATA)
// ============================================================================
@Composable
fun AppleHeroStatusWidget(
    credentials: UserCredentials?,
    isDemoMode: Boolean,
    selectedLanguageCode: String,
    onEditCredentials: () -> Unit
) {
    val hasPoliziaCode = !credentials?.numeroAssicurata.isNullOrBlank() ||
        !credentials?.numeroPratica.isNullOrBlank() ||
        (!credentials?.password.isNullOrBlank() && credentials?.password?.length == 12)
    val hasPortaleCreds = !credentials?.userId.isNullOrBlank() && !credentials?.password.isNullOrBlank()
    val hasAnyCredentials = hasPoliziaCode || hasPortaleCreds || isDemoMode

    val rawPolizia = credentials?.lastPoliziaStatus ?: "No checks yet"
    val rawPortale = credentials?.lastPortaleStatus ?: "No checks yet"
    val hasPoliziaResult = rawPolizia.isNotBlank() && rawPolizia != "No checks yet"
    val hasPortaleResult = rawPortale.isNotBlank() && rawPortale != "No checks yet"
    val hasRealStatus = hasPoliziaResult || hasPortaleResult

    val realDossierCode = credentials?.codiceIstanza?.takeIf { it.isNotBlank() }
        ?: credentials?.numeroPratica?.takeIf { it.isNotBlank() }
        ?: credentials?.numeroAssicurata?.takeIf { it.isNotBlank() }
        ?: credentials?.userId?.takeIf { it.isNotBlank() }
        ?: if (isDemoMode) PermessoViewModel.DEMO_ASSICURATA else ""

    val isReady = hasRealStatus && (
        rawPolizia.contains("pronto", ignoreCase = true) ||
            rawPolizia.contains("consegna", ignoreCase = true) ||
            rawPortale.contains("pronto", ignoreCase = true) ||
            rawPortale.contains("consegna", ignoreCase = true)
        )

    val isNotFoundOrAnomaly = hasRealStatus && (
        rawPolizia.contains("anomalia", ignoreCase = true) ||
            rawPortale.contains("anomalia", ignoreCase = true) ||
            rawPolizia.contains("non presente", ignoreCase = true) ||
            rawPolizia.contains("non trovato", ignoreCase = true) ||
            !credentials?.dettaglioAnomalie.isNullOrBlank()
        )

    val (badgeLabel, badgeColor, badgeBg) = when {
        !hasAnyCredentials -> Triple(
            when (selectedLanguageCode) {
                "en" -> "○ UNCONFIGURED DOSSIER"
                "ar" -> "○ لم يتم إدخال بيانات الإيصال"
                else -> "○ PRATICA NON CONFIGURATA"
            },
            Color(0xFFB45309),
            SunsetTangerineTint
        )
        !hasRealStatus -> Triple(
            when (selectedLanguageCode) {
                "en" -> "● READY FOR LIVE CHECK"
                "ar" -> "● جاهز للتحقق الفوري"
                else -> "● PRONTO PER VERIFICA LIVE"
            },
            ElectricBlue,
            ElectricBlueTint
        )
        isReady -> Triple(
            when (selectedLanguageCode) {
                "en" -> "✓ READY FOR PICKUP"
                "ar" -> "✓ جاهز للاستلام"
                else -> "✓ PRONTO PER IL RITIRO"
            },
            Color(0xFF007A33),
            VividEmeraldTint
        )
        isNotFoundOrAnomaly -> Triple(
            when (selectedLanguageCode) {
                "en" -> "⚠️ ATTENTION / NOT IN ARCHIVE"
                "ar" -> "⚠️ تنبيه / غير مسجل حالياً"
                else -> "⚠️ VERIFICA STATO / ARCHIVIO"
            },
            AlertRed,
            AlertRedTint
        )
        else -> Triple(
            when (selectedLanguageCode) {
                "en" -> "● IN OFFICIAL PROCESSING"
                "ar" -> "● قيد المعالجة الرسمية"
                else -> "● IN ISTRUTTORIA QUESTURA"
            },
            ElectricBlue,
            ElectricBlueTint
        )
    }

    val headlineTitle = when {
        !hasAnyCredentials -> when (selectedLanguageCode) {
            "en" -> "Configure Your Mod. 22A Postal Receipt"
            "ar" -> "أدخل بيانات الإيصال البريدي (Mod. 22A)"
            "fr" -> "Configurez votre récépissé postal Mod. 22A"
            "es" -> "Configura tu recibo postal Mod. 22A"
            else -> "Configura i Codici della Ricevuta Mod. 22A"
        }
        !hasRealStatus -> when (selectedLanguageCode) {
            "en" -> "Receipt Codes Loaded — Run Official Check"
            "ar" -> "تم حفظ الأرقام — اضغط للتحقق المباشر"
            "fr" -> "Codes enregistrés — Lancez la vérification"
            "es" -> "Códigos guardados — Ejecuta la verificación"
            else -> "Codici Acquisiti — Esegui Verifica Telematica"
        }
        else -> {
            if (hasPoliziaResult) rawPolizia else rawPortale
        }
    }

    val guidanceText = when {
        !hasAnyCredentials -> when (selectedLanguageCode) {
            "en" -> "Enter your 12-digit Codice Assicurata (or 10-character Questura Pratica number) in the Dossier tab or scan your Mod. 22A receipt via OCR to query Polizia di Stato and Portale Immigrazione."
            "ar" -> "أدخل رقم التأمين المكون من 12 رقماً (Assicurata) أو رقم ملف الكويستورا في قسم الملف الشخصي، أو امسح الإيصال بالكاميرا للتحقق المباشر من موقع الشرطة وبوابة الهجرة."
            else -> "Per interrogare i server ufficiali della Polizia di Stato e del Portale Immigrazione, inserisci il Codice Assicurata (12 cifre) o il Numero Pratica (10 caratteri) presenti sulla ricevuta Poste Italiane Mod. 22A."
        }
        !hasRealStatus -> when (selectedLanguageCode) {
            "en" -> "Your dossier parameters ($realDossierCode) are stored in the local Room database. Tap 'Verifica Stato Live' below to query the official ministry endpoints."
            "ar" -> "تم حفظ بيانات ملفك ($realDossierCode) محلياً. اضغط على زر التحقق المباشر أدناه للاتصال بالبوابة الرسمية."
            else -> "I parametri della ricevuta ($realDossierCode) sono registrati nel database locale Room. Premi il pulsante 'Verifica Stato Live' qui sotto per interrogare in tempo reale la Questura e il Portale Immigrazione."
        }
        !credentials?.dettaglioAnomalie.isNullOrBlank() -> credentials?.dettaglioAnomalie.orEmpty()
        isReady -> when (selectedLanguageCode) {
            "en" -> "Your electronic permit (PSE) is reported as ready for delivery. Bring your original Mod. 22A receipt, valid passport, and expiring permit to the Questura."
            "ar" -> "تصريح الإقامة الإلكتروني الخاص بك جاهز للتسليم. أحضر إيصال البريد الأصلي (Mod. 22A) وجواز السفر الساري والتصريح القديم إلى الكويستورا."
            else -> "Il Permesso di Soggiorno Elettronico (PSE) risulta pronto per la consegna. Presentati presso l'Ufficio Immigrazione con Ricevuta Mod. 22A originale, Passaporto valido e titolo in scadenza."
        }
        isNotFoundOrAnomaly -> when (selectedLanguageCode) {
            "en" -> "The dossier is either still being indexed in the central Questura archive, already collected, or requires document verification. Check both portals below."
            "ar" -> "الملف إما لا يزال قيد الفهرسة في أرشيف الكويستورا أو يتطلب مراجعة المستندات. راجع تفاصيل القناتين أدناه."
            else -> "Il codice inserito non risulta ancora indicizzato nell'archivio pubblico oppure richiede verifica istruttoria. Controlla il dettaglio dei due canali ufficiali qui sotto."
        }
        else -> when (selectedLanguageCode) {
            "en" -> "Your application is currently in active processing between the Questura Immigration Office and IPZS (State Mint). Automatic background checks will notify you of any changes."
            "ar" -> "طلبك قيد المعالجة الرسمية حالياً لدى مكتب الهجرة ودار الطباعة الحكومية. سيتم إشعارك تلقائياً عند تحديث الحالة."
            else -> "La pratica è attualmente in fase di istruttoria presso l'Ufficio Immigrazione o in stampa presso il Poligrafico dello Stato (IPZS)."
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f))
            .testTag("hero_status_widget")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Telemetry Bar: Status Pill + Dossier Action Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg,
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = badgeLabel,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    onClick = onEditCredentials,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, AppleBorderHairline),
                    modifier = Modifier.appleBounceClick()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (hasAnyCredentials) Icons.Default.Tag else Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (realDossierCode.isNotBlank()) realDossierCode else "INSERISCI CODICE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Main Status Headline
            Text(
                text = headlineTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                lineHeight = 26.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Dual-Channel Live Telemetry Grid (Always shows REAL database state)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Channel 1: Polizia di Stato (Questura)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, AppleBorderHairline),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (hasPoliziaResult) VividEmerald else if (hasPoliziaCode) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "POLIZIA DI STATO",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = when {
                                hasPoliziaResult -> rawPolizia
                                hasPoliziaCode -> "Codice configurato (In attesa)"
                                else -> "Codice non configurato"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Channel 2: Portale Immigrazione (Poste Italiane)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, AppleBorderHairline),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = if (hasPortaleResult) ElectricBlue else if (hasPortaleCreds) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "PORTALE POSTE",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = when {
                                hasPortaleResult -> rawPortale.substringBefore(" - ")
                                hasPortaleCreds -> "Credenziali pronte (In attesa)"
                                else -> "User ID non configurato"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Technical Guidance Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, AppleBorderHairline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (!hasAnyCredentials) Icons.Default.QrCodeScanner else Icons.Default.Info,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = if (!hasAnyCredentials) "ISTRUZIONI DI CONFIGURAZIONE" else "NOTA OPERATIVA UFFICIALE",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = guidanceText,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// SCREEN 1 COMPONENT: APPOINTMENT CARD (ZERO FAKE DATES)
// ============================================================================
@Composable
fun AppleAppointmentAlertCard(
    appointmentDate: String,
    appointmentTime: String,
    appointmentLocation: String,
    selectedProvince: String,
    onUpdateAppointment: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    val hasAppointment = appointmentDate.isNotBlank()

    if (showEditDialog) {
        var editDate by remember { mutableStateOf(appointmentDate) }
        var editTime by remember { mutableStateOf(appointmentTime) }
        var editLoc by remember {
            mutableStateOf(
                appointmentLocation.ifBlank { "Questura di $selectedProvince - Ufficio Immigrazione" }
            )
        }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = if (hasAppointment) "Modifica Convocazione Questura" else "Registra Convocazione Questura",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editDate,
                        onValueChange = { editDate = it },
                        label = { Text("Data Appuntamento (es. 15/11/2026)") },
                        placeholder = { Text("GG/MM/AAAA") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editTime,
                        onValueChange = { editTime = it },
                        label = { Text("Orario (es. 09:00)") },
                        placeholder = { Text("HH:MM") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editLoc,
                        onValueChange = { editLoc = it },
                        label = { Text("Ufficio / Indirizzo Questura") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateAppointment(editDate.trim(), editTime.trim(), editLoc.trim())
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Salva", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                if (hasAppointment) {
                    TextButton(
                        onClick = {
                            onUpdateAppointment("", "", "")
                            showEditDialog = false
                        }
                    ) {
                        Text("Rimuovi", color = AlertRed)
                    }
                } else {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Annulla")
                    }
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (!hasAppointment) {
        // Honest Empty State: Compact row allowing user to log an appointment if they have one
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("apple_appointment_wallet_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Agenda Convocazione Questura",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Nessun appuntamento registrato ( sincronizzato automaticamente dal Portale )",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    onClick = { showEditDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    color = ElectricBlueTint,
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f)),
                    modifier = Modifier.appleBounceClick()
                ) {
                    Text(
                        text = "+ Aggiungi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    } else {
        // Real Appointment Registered State
        val dayDigits = remember(appointmentDate) {
            Regex("""\b(\d{1,2})\b""").find(appointmentDate)?.groupValues?.getOrNull(1)?.padStart(2, '0') ?: "GG"
        }
        val displayLocation = appointmentLocation.ifBlank { "Questura di $selectedProvince • Ufficio Immigrazione" }
        val displayDateTime = if (appointmentTime.isNotBlank()) "$appointmentDate • Ore $appointmentTime" else appointmentDate

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SunsetTangerineTint,
            border = BorderStroke(1.dp, SunsetTangerine.copy(alpha = 0.45f)),
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("apple_appointment_wallet_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SunsetTangerine
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "CONVOCAZIONE REGISTRATA",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = "Modifica Data",
                            tint = Color(0xFFB25000),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, SunsetTangerine.copy(alpha = 0.35f)),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "DATA",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SunsetTangerine
                            )
                            Text(
                                text = dayDigits,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppleInk
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayDateTime,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppleInk
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = displayLocation,
                            fontSize = 12.sp,
                            color = Color(0xFF7A4100)
                        )
                    }
                }

                Button(
                    onClick = {
                        try {
                            val calIntent = Intent(Intent.ACTION_INSERT).apply {
                                data = CalendarContract.Events.CONTENT_URI
                                putExtra(CalendarContract.Events.TITLE, "Convocazione Questura - Permesso di Soggiorno")
                                putExtra(CalendarContract.Events.EVENT_LOCATION, displayLocation)
                                putExtra(
                                    CalendarContract.Events.DESCRIPTION,
                                    "Portare Ricevuta Mod. 22A originale, Passaporto valido e documentazione richiesta."
                                )
                            }
                            context.startActivity(calIntent)
                        } catch (_: Exception) {
                            Toast.makeText(
                                context,
                                "Promemoria pronto per il Calendario di sistema",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SunsetTangerine,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 11.dp, horizontal = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .appleBounceClick()
                        .testTag("add_to_wallet_calendar_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Esporta nel Calendario di Sistema",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ============================================================================
// SCREEN 1 COMPONENT: 4-STAGE VERTICAL STEPPER (REAL STATE DRIVEN)
// ============================================================================
private data class StepperStage(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val badge: String,
    val isCompleted: Boolean,
    val isActiveNow: Boolean
)

@Composable
fun AppleVerticalStepperCard(
    credentials: UserCredentials?,
    selectedLanguageCode: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val hasCredentials = credentials != null && (
        credentials.numeroAssicurata.isNotBlank() ||
            credentials.numeroPratica.isNotBlank() ||
            (credentials.userId.isNotBlank() && credentials.password.isNotBlank())
        )
    val rawPolizia = credentials?.lastPoliziaStatus ?: "No checks yet"
    val rawPortale = credentials?.lastPortaleStatus ?: "No checks yet"
    val hasRealStatus = (rawPolizia != "No checks yet" && rawPolizia.isNotBlank()) ||
        (rawPortale != "No checks yet" && rawPortale.isNotBlank())

    val isReady = hasRealStatus && (
        rawPolizia.contains("pronto", ignoreCase = true) ||
            rawPolizia.contains("consegna", ignoreCase = true) ||
            rawPortale.contains("pronto", ignoreCase = true) ||
            rawPortale.contains("consegna", ignoreCase = true)
        )
    val isInTrattazione = hasRealStatus && !isReady && (
        rawPolizia.contains("trattazione", ignoreCase = true) ||
            rawPortale.contains("istruttoria", ignoreCase = true)
        )

    // Determine active step (0 = unconfigured, 1 = kit registered, 2 = fotosegnalamento, 3 = istruttoria, 4 = ready)
    val activeStep = when {
        !hasCredentials -> 0
        !hasRealStatus -> 1
        isReady -> 4
        isInTrattazione -> 3
        else -> 2
    }

    val stages = remember(activeStep, selectedLanguageCode) {
        listOf(
            StepperStage(
                stepNumber = 1,
                title = "1. Spedizione Kit Postale (Mod. 22A)",
                subtitle = "Acquisizione codici Assicurata / Ologramma da ricevuta Poste Italiane",
                badge = when {
                    activeStep >= 1 -> "REGISTRATO"
                    else -> "DA INSERIRE"
                },
                isCompleted = activeStep > 1,
                isActiveNow = activeStep == 1
            ),
            StepperStage(
                stepNumber = 2,
                title = "2. Preistruttoria & Fotosegnalamento",
                subtitle = "Convocazione Ufficio Immigrazione e rilievi dattiloscopici",
                badge = when {
                    activeStep > 2 -> "COMPLETATO"
                    activeStep == 2 -> "IN VERIFICA"
                    else -> "IN ATTESA"
                },
                isCompleted = activeStep > 2,
                isActiveNow = activeStep == 2
            ),
            StepperStage(
                stepNumber = 3,
                title = "3. Istruttoria Questura & Stampa IPZS",
                subtitle = "Controllo requisiti D.Lgs. 286/98 e produzione smartcard PSE",
                badge = when {
                    activeStep > 3 -> "APPROVATO"
                    activeStep == 3 -> "IN LAVORAZIONE"
                    else -> "IN ATTESA"
                },
                isCompleted = activeStep > 3,
                isActiveNow = activeStep == 3
            ),
            StepperStage(
                stepNumber = 4,
                title = "4. Consegna Permesso Elettronico",
                subtitle = "Disponibilità PSE presso lo sportello della Questura competente",
                badge = when {
                    activeStep == 4 -> "PRONTO RITIRO"
                    else -> "IN ATTESA"
                },
                isCompleted = false,
                isActiveNow = activeStep == 4
            )
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("apple_vertical_stepper_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STATO AVANZAMENTO PROCEDIMENTO",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        letterSpacing = 0.7.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Iter Amministrativo in 4 Fasi",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.25).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeStep > 0) ElectricBlueTint else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "FASE $activeStep / 4",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (activeStep > 0) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            stages.forEachIndexed { index, stage ->
                val isLast = index == stages.lastIndex
                val nodeColor = when {
                    stage.isCompleted -> VividEmerald
                    stage.isActiveNow -> ElectricBlue
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(30.dp)
                        ) {
                            if (stage.isActiveNow) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .graphicsLayer {
                                            scaleX = pulseScale
                                            scaleY = pulseScale
                                            alpha = pulseAlpha
                                        }
                                        .background(nodeColor, CircleShape)
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = nodeColor,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    when {
                                        stage.isCompleted -> {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        stage.isActiveNow -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(Color.White, CircleShape)
                                            )
                                        }
                                        else -> {
                                            Text(
                                                text = stage.stepNumber.toString(),
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (!isLast) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(42.dp)
                                    .background(
                                        if (stage.isCompleted) VividEmerald else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = if (isLast) 0.dp else 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stage.title,
                                fontSize = 14.sp,
                                fontWeight = if (stage.isActiveNow || stage.isCompleted) FontWeight.Bold else FontWeight.Medium,
                                color = if (stage.isActiveNow) ElectricBlue else if (stage.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    stage.isCompleted -> VividEmeraldTint
                                    stage.isActiveNow -> ElectricBlueTint
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = stage.badge,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        stage.isCompleted -> Color(0xFF007A33)
                                        stage.isActiveNow -> ElectricBlue
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = stage.subtitle,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// SCREEN 2: "STRUMENTI" — LEGAL & TECHNICAL UTILITY HUB (NO EMOJI SLOP)
// ============================================================================
@Composable
fun AppleStrumentiVividHubScreen(
    selectedLanguageCode: String,
    onOpenSofiaAi: () -> Unit,
    onOpenPecGenerator: () -> Unit,
    onOpenTravelSimulator: () -> Unit,
    onOpenQuestureMap: () -> Unit,
    onOpenChecklist: () -> Unit,
    onOpenPortalWebView: () -> Unit,
    onOpenRedditoSimulator: () -> Unit,
    onOpenImmigrationGuides: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp)
    ) {
        // 1. LEGAL REFERENCE ASSISTANT CARD (Clean Authoritative Legal Tech Card)
        item {
            Surface(
                onClick = onOpenSofiaAi,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .appleBounceClick()
                    .testTag("strumenti_hero_sofia_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricBlueTint
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "PRONTUARIO NORMATIVO • D.LGS. 286/98",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VividEmeraldTint
                        ) {
                            Text(
                                text = "T.U. IMMIGRAZIONE",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007A33),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Consulente Normativo & Procedurale",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Consulta riferimenti normativi su D.Lgs. 286/98, termini ex art. 2 L. 241/90, documentazione per il ritiro in Questura e requisiti di reddito.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onOpenSofiaAi,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Balance,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Apri Consultazione Normativa",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Section Header: 4 Operational Utilities
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Strumenti Legali & Operativi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.25).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = Localization.string("badge_4_tools", selectedLanguageCode),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // 2x2 Grid with Vector Icons (No emojis)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VividSquircleUtilityCard(
                        icon = Icons.Default.MarkEmailRead,
                        badge = "L. 241/90",
                        title = "Sollecito PEC",
                        subtitle = "Atto di diffida 90 giorni e messa in mora Questura",
                        accentColor = ElectricBlue,
                        tintBg = ElectricBlueTint,
                        onClick = onOpenPecGenerator,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("utility_card_pec")
                    )

                    VividSquircleUtilityCard(
                        icon = Icons.Default.FlightTakeoff,
                        badge = "MOD. 22A",
                        title = "Regole di Viaggio",
                        subtitle = "Verifica volo diretto extra-Schengen con ricevuta",
                        accentColor = VividEmerald,
                        tintBg = VividEmeraldTint,
                        onClick = onOpenTravelSimulator,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("utility_card_travel")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VividSquircleUtilityCard(
                        icon = Icons.Default.AccountBalance,
                        badge = "QUESTURE",
                        title = "Sedi & Indirizzi PEC",
                        subtitle = "Uffici Immigrazione, orari di apertura e contatti PEC",
                        accentColor = SunsetTangerine,
                        tintBg = SunsetTangerineTint,
                        onClick = onOpenQuestureMap,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("utility_card_questure")
                    )

                    VividSquircleUtilityCard(
                        icon = Icons.Default.FactCheck,
                        badge = "DOSSIER",
                        title = "Checklist Documenti",
                        subtitle = "Elenco documenti obbligatori per fotosegnalamento",
                        accentColor = SiriViolet,
                        tintBg = SiriVioletTint,
                        onClick = onOpenChecklist,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("utility_card_checklist")
                    )
                }
            }
        }

        // Additional Official Portals & Simulators
        item {
            Text(
                text = "PORTALI TELEMATICI & CALCOLATORI",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.7.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    DrawerNavRow(
                        title = "Terminale Web Ufficiale (Auto-Fill)",
                        subtitle = "Connessione diretta a Polizia di Stato e Portale Immigrazione",
                        icon = Icons.Default.Language,
                        iconBg = ElectricBlue,
                        isSelected = false,
                        onClick = onOpenPortalWebView
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Calcolatore Soglia Reddito",
                        subtitle = "Parametri Assegno Sociale per rinnovo e ricongiungimento",
                        icon = Icons.Default.Calculate,
                        iconBg = VividEmerald,
                        isSelected = false,
                        onClick = onOpenRedditoSimulator
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                    DrawerNavRow(
                        title = "Prontuario Guide Immigrazione",
                        subtitle = "Procedure ufficiali per lavoro, studio, famiglia e lungo periodo UE",
                        icon = Icons.Default.MenuBook,
                        iconBg = SunsetTangerine,
                        isSelected = false,
                        onClick = onOpenImmigrationGuides
                    )
                }
            }
        }
    }
}

@Composable
private fun VividSquircleUtilityCard(
    icon: ImageVector,
    badge: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    tintBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp,
        modifier = modifier
            .heightIn(min = 160.dp)
            .appleBounceClick()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tintBg
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (accentColor == VividEmerald) Color(0xFF007A33) else accentColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// SCREEN 3 COMPONENT: DIGITAL RECEIPT PASS CARD (LIGHT-MODE NATIVE, NO BLACK CARD)
// ============================================================================
@Composable
fun AppleWalletPassCard(
    fullName: String,
    userId: String,
    assicurataCode: String,
    praticaCode: String,
    selectedProvince: String,
    onScanWithCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayFullName = fullName.trim().ifBlank { "NON CONFIGURATO" }.uppercase(Locale.getDefault())
    val displayAssicurata = assicurataCode.trim().ifBlank { "Non inserito" }
    val displayUserId = userId.trim().ifBlank { "Non inserito" }
    val displayPratica = praticaCode.trim().ifBlank { "Non inserito" }
    val activeBarcodePayload = assicurataCode.trim().ifBlank {
        praticaCode.trim().ifBlank { userId.trim() }
    }
    val hasValidBarcode = activeBarcodePayload.isNotBlank() && !activeBarcodePayload.contains("•")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("apple_wallet_pass_card")
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Pass Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricBlueTint,
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.25f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "DOSSIER DIGITALE • MOD. 22A",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Ricevuta Assicurata Poste Italiane",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            text = selectedProvince.uppercase(Locale.getDefault()),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                // Structured 2x2 Grid for Dossier Parameters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "INTESTATARIO PRATICA",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = displayFullName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (fullName.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "CODICE PRATICA",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = displayPratica,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (praticaCode.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "USER ID (OLOGRAMMA)",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = displayUserId,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (userId.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "ASSICURATA (12 CIFRE)",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = displayAssicurata,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (assicurataCode.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Barcode Strip (renders real Code128 only when user has entered a code)
                if (hasValidBarcode) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Code128BarcodeCanvas(
                                data = activeBarcodePayload,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "* $activeBarcodePayload *",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AppleInk,
                                letterSpacing = 1.6.sp
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Il codice a barre Code-128 viene generato automaticamente dopo l'inserimento dei codici Mod. 22A.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 1-Tap OCR Camera Scan Button (Restrained 10dp radius, single line)
                Button(
                    onClick = onScanWithCamera,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VividEmerald,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .appleBounceClick()
                        .testTag("wallet_scan_camera_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scansiona Ricevuta Mod. 22A con OCR",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
