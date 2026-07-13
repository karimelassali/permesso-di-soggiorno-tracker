package com.example.ui

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.StatusHistory
import com.example.data.UserCredentials
import com.example.ui.theme.*
import com.example.util.Localization
import com.example.util.AppLanguage
import java.text.SimpleDateFormat
import java.util.*

// Dynamic Theme-Aware Bento Colors that adapt seamlessly between Light & Dark modes
private val BentoBackground: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

private val BentoTextPrimary: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurface

private val BentoTextSecondary: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

private val BentoTextHeading: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurface

private val BentoCardPrimaryBg: Color
    @Composable
    get() = MaterialTheme.colorScheme.primaryContainer

private val BentoCardPrimaryText: Color
    @Composable
    get() = MaterialTheme.colorScheme.onPrimaryContainer

private val BentoBadgeGreen: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

private val BentoBadgeGreenBg: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondaryContainer

private val BentoAvatarBg: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondaryContainer

private val BentoAvatarBorder: Color
    @Composable
    get() = MaterialTheme.colorScheme.outline

private val BentoCardBorder: Color
    @Composable
    get() = MaterialTheme.colorScheme.outlineVariant

private val BentoCardLightBg: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermessoAppScreen(
    viewModel: PermessoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val credentials by viewModel.credentials.collectAsStateWithLifecycle()
    val historyList by viewModel.history.collectAsStateWithLifecycle()
    val checkState by viewModel.checkState.collectAsStateWithLifecycle()
    val selectedLanguageCode by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showingGuidesScreen by remember { mutableStateOf(false) }
    var showingQuestureExplorer by remember { mutableStateOf(false) }

    // Request permissions for notifications on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    val layoutDirection = if (selectedLanguageCode == "ar") {
        androidx.compose.ui.unit.LayoutDirection.Rtl
    } else {
        androidx.compose.ui.unit.LayoutDirection.Ltr
    }

    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection) {
        if (!onboardingCompleted) {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = { viewModel.completeOnboarding() }
            )
        } else {
            Scaffold(
        topBar = {
            val initials = remember(credentials) {
                val name = credentials?.fullName?.trim() ?: ""
                if (name.isBlank()) {
                    "AM"
                } else {
                    val parts = name.split("\\s+".toRegex())
                    if (parts.size >= 2) {
                        "${parts[0].take(1).uppercase()}${parts[1].take(1).uppercase()}"
                    } else {
                        name.take(2).uppercase()
                    }
                }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "App Logo",
                                tint = ItalianGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = Localization.string("app_name", selectedLanguageCode),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = BentoTextHeading
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BentoBackground
                    ),
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BentoAvatarBg)
                                .border(1.dp, BentoAvatarBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        }
                    }
                )
                // Premium thin Italian flag colored stripe
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(ItalianGreen))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(ItalianWhite))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(ItalianRed))
                }
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = BentoCardBorder, thickness = 1.dp)
                NavigationBar(
                    containerColor = BentoBackground,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text(Localization.string("tab_status", selectedLanguageCode)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoCardPrimaryText,
                            selectedTextColor = BentoCardPrimaryText,
                            indicatorColor = BentoAvatarBg,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        icon = { Icon(Icons.Default.Language, contentDescription = "Portale Web") },
                        label = { Text(Localization.string("tab_portal", selectedLanguageCode)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoCardPrimaryText,
                            selectedTextColor = BentoCardPrimaryText,
                            indicatorColor = BentoAvatarBg,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_portale")
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Dati Ricevuta") },
                        label = { Text(Localization.string("tab_receipt", selectedLanguageCode)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoCardPrimaryText,
                            selectedTextColor = BentoCardPrimaryText,
                            indicatorColor = BentoAvatarBg,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_ricevuta")
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 3,
                        onClick = { selectedTabIndex = 3 },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profilo") },
                        label = { Text(Localization.string("tab_profile", selectedLanguageCode)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoCardPrimaryText,
                            selectedTextColor = BentoCardPrimaryText,
                            indicatorColor = BentoAvatarBg,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        },
        floatingActionButton = {
            val localCreds = credentials
            val hasCode = localCreds != null && 
                    (localCreds.numeroAssicurata.isNotBlank() || localCreds.numeroPratica.isNotBlank())
            if (selectedTabIndex == 0 && hasCode) {
                FloatingActionButton(
                    onClick = { viewModel.checkStatusNow() },
                    containerColor = BentoBadgeGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 8.dp)
                        .testTag("refresh_status_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Aggiorna Stato",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        containerColor = BentoBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedTabIndex) {
                0 -> {
                    val selectedProvince by viewModel.selectedProvince.collectAsStateWithLifecycle()
                    val appointmentDate by viewModel.appointmentDate.collectAsStateWithLifecycle()
                    val appointmentTime by viewModel.appointmentTime.collectAsStateWithLifecycle()
                    val appointmentLocation by viewModel.appointmentLocation.collectAsStateWithLifecycle()
                    val checkedDocs by viewModel.checkedDocs.collectAsStateWithLifecycle()
                    val activeGuideId by viewModel.activeGuideId.collectAsStateWithLifecycle()

                    if (showingQuestureExplorer) {
                        QuestureExplorerScreen(
                            onBack = { showingQuestureExplorer = false },
                            onSelectQuestura = { questura ->
                                viewModel.selectQuesturaAndProvince(questura.id, questura.provinceCode, questura.province)
                                showingQuestureExplorer = false
                            },
                            selectedLanguageCode = selectedLanguageCode
                        )
                    } else if (showingGuidesScreen || activeGuideId != null) {
                        ImmigrationGuidesScreen(
                            viewModel = viewModel,
                            onBack = {
                                showingGuidesScreen = false
                                viewModel.activeGuideId.value = null
                            },
                            selectedLanguageCode = selectedLanguageCode
                        )
                    } else {
                        DashboardTab(
                            credentials = credentials,
                            history = historyList,
                            checkState = checkState,
                            selectedProvince = selectedProvince,
                            onProvinceSelected = { viewModel.selectProvince(it) },
                            appointmentDate = appointmentDate,
                            appointmentTime = appointmentTime,
                            appointmentLocation = appointmentLocation,
                            onUpdateAppointment = { d, t, l -> viewModel.updateAppointment(d, t, l) },
                            checkedDocs = checkedDocs,
                            onToggleDocument = { viewModel.toggleDocument(it) },
                            onCheckNow = { viewModel.checkStatusNow() },
                            onResetCheckState = { viewModel.resetCheckState() },
                            onClearHistory = { viewModel.clearCheckHistory() },
                            onGoToCredentials = { selectedTabIndex = 2 },
                            onOpenGuides = { showingGuidesScreen = true },
                            onChooseQuestura = { showingQuestureExplorer = true },
                            selectedLanguageCode = selectedLanguageCode
                        )
                    }
                }
                1 -> PortalWebViewTab(
                    credentials = credentials,
                    onStatusDetected = { poliziaStatus, portaleStatus ->
                        viewModel.updateLoggedStatus(poliziaStatus, portaleStatus)
                    },
                    selectedLanguageCode = selectedLanguageCode
                )
                2 -> {
                    val formFullName by viewModel.formFullName.collectAsStateWithLifecycle()
                    val formUserId by viewModel.formUserId.collectAsStateWithLifecycle()
                    val formPassword by viewModel.formPassword.collectAsStateWithLifecycle()
                    val formNumeroAssicurata by viewModel.formNumeroAssicurata.collectAsStateWithLifecycle()
                    val formNumeroPratica by viewModel.formNumeroPratica.collectAsStateWithLifecycle()

                    CredentialsTab(
                        fullName = formFullName,
                        onFullNameChange = { viewModel.formFullName.value = it },
                        userId = formUserId,
                        onUserIdChange = { viewModel.formUserId.value = it },
                        password = formPassword,
                        onPasswordChange = { viewModel.formPassword.value = it },
                        numeroAssicurata = formNumeroAssicurata,
                        onNumeroAssicurataChange = { viewModel.formNumeroAssicurata.value = it },
                        numeroPratica = formNumeroPratica,
                        onNumeroPraticaChange = { viewModel.formNumeroPratica.value = it },
                        onSave = {
                            viewModel.saveCredentials()
                            selectedTabIndex = 0 // Go back to status check
                        },
                        selectedLanguageCode = selectedLanguageCode
                    )
                }
                3 -> {
                    ProfileTab(
                        viewModel = viewModel,
                        selectedLanguageCode = selectedLanguageCode
                    )
                }
            }
        }
    }
    }
    }
}

@Composable
fun DashboardTab(
    credentials: UserCredentials?,
    history: List<StatusHistory>,
    checkState: CheckUiState,
    selectedProvince: String,
    onProvinceSelected: (String) -> Unit,
    appointmentDate: String,
    appointmentTime: String,
    appointmentLocation: String,
    onUpdateAppointment: (String, String, String) -> Unit,
    checkedDocs: Set<String>,
    onToggleDocument: (String) -> Unit,
    onCheckNow: () -> Unit,
    onResetCheckState: () -> Unit,
    onClearHistory: () -> Unit,
    onGoToCredentials: () -> Unit,
    onOpenGuides: () -> Unit,
    onChooseQuestura: () -> Unit,
    selectedLanguageCode: String
) {
    val hasCode = credentials != null && 
            (credentials.numeroAssicurata.isNotBlank() || credentials.numeroPratica.isNotBlank())

    var activeSubTab by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Welcome and Header Card (Always Visible)
        item {
            val name = credentials?.fullName?.trim() ?: ""
            val greetName = if (name.isNotBlank()) name else Localization.string("friend", selectedLanguageCode)
            val context = androidx.compose.ui.platform.LocalContext.current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ciao, $greetName! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextHeading
                    )
                    val lastCheckedStr = if (credentials?.lastCheckedTime != 0L) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        Localization.string("last_checked", selectedLanguageCode, sdf.format(Date(credentials?.lastCheckedTime ?: 0L)))
                    } else {
                        Localization.string("no_checks", selectedLanguageCode)
                    }
                    Text(
                        text = lastCheckedStr,
                        fontSize = 13.sp,
                        color = BentoTextSecondary
                    )
                }

                // Test notifications button
                OutlinedButton(
                    onClick = {
                        com.example.util.NotificationHelper.showStatusNotification(
                            context,
                            Localization.string("notif_active", selectedLanguageCode),
                            Localization.string("notif_active_desc", selectedLanguageCode)
                        )
                    },
                    border = BorderStroke(1.dp, BentoCardBorder),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ItalianGreen),
                    modifier = Modifier.testTag("test_notif_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Test Notifica",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.string("test_button", selectedLanguageCode), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // NEW: Immigration Guides Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenGuides() }
                    .testTag("immigration_guides_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Localization.string("guides_card_title", selectedLanguageCode),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Localization.string("guides_card_desc", selectedLanguageCode),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Sub-Tab Switcher Row (Always Visible)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val subTabs = listOf(
                    0 to Localization.string("subtab_status", selectedLanguageCode),
                    1 to Localization.string("subtab_appointments", selectedLanguageCode),
                    2 to Localization.string("subtab_stats", selectedLanguageCode)
                )
                subTabs.forEach { (index, title) ->
                    val isSelected = activeSubTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) BentoBadgeGreen else MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else BentoCardBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { activeSubTab = index }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else BentoTextSecondary,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // SUB-TAB 0: Stato & Cronologia
        if (activeSubTab == 0) {
            // BENTO CARD 1: Primary Status Card
            item {
                BentoPrimaryStatusCard(credentials = credentials, selectedLanguageCode = selectedLanguageCode)
            }

            // BENTO CARDS 2 & 3: Portale Status & Polizia Status side-by-side
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BentoPortalStatusCard(credentials = credentials, selectedLanguageCode = selectedLanguageCode)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        BentoPoliziaStatusCard(credentials = credentials, selectedLanguageCode = selectedLanguageCode)
                    }
                }
            }

            // PERSONALIZED QUESTURA CARD
            item {
                PersonalizedQuesturaCard(
                    credentials = credentials,
                    onChooseQuestura = onChooseQuestura,
                    selectedLanguageCode = selectedLanguageCode
                )
            }

            // PREMIUM FEATURE: Progressive Visual Timeline Stepper
            if (credentials != null && credentials?.lastCheckedTime != 0L) {
                item {
                    BentoPermessoTimelineCard(credentials = credentials, selectedLanguageCode = selectedLanguageCode)
                }
            }

            // Configuration request helper card (if !hasCode)
            if (!hasCode) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoAlertRed.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = Localization.string("config_required", selectedLanguageCode),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BentoAlertRed,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Localization.string("config_required_desc", selectedLanguageCode),
                                fontSize = 12.sp,
                                color = BentoTextSecondary,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onGoToCredentials,
                                colors = ButtonDefaults.buttonColors(containerColor = BentoBadgeGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("setup_ricevuta_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(Localization.string("config_receipt_btn", selectedLanguageCode), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                // Controlla Ora full-width bento card action
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoBadgeGreen),
                        onClick = onCheckNow
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.string("button_check_now", selectedLanguageCode),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.testTag("check_status_btn")
                            )
                        }
                    }
                }
            }

            // Status Feedback Dialog/Section
            if (checkState !is CheckUiState.Idle) {
                item {
                    CheckStateCard(checkState = checkState, onDismiss = onResetCheckState, selectedLanguageCode = selectedLanguageCode)
                }
            }

            // Status check history logs section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.string("history_title", selectedLanguageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BentoTextHeading
                    )
                    if (history.isNotEmpty()) {
                        TextButton(onClick = onClearHistory) {
                            Icon(Icons.Default.Delete, contentDescription = "Cancella", modifier = Modifier.size(16.dp), tint = BentoBadgeGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.string("history_clear", selectedLanguageCode), fontSize = 13.sp, color = BentoBadgeGreen)
                        }
                    }
                }
            }

            if (history.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Nessuna cronologia",
                                tint = BentoTextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = Localization.string("no_history_recorded", selectedLanguageCode),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = BentoTextSecondary
                            )
                            Text(
                                text = Localization.string("history_help_desc", selectedLanguageCode),
                                fontSize = 12.sp,
                                color = BentoTextSecondary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            } else {
                items(history) { record ->
                    HistoryRecordItem(record = record, selectedLanguageCode = selectedLanguageCode)
                }
            }
        }

        // SUB-TAB 1: Appuntamenti & Strumenti
        if (activeSubTab == 1) {
            // PREMIUM FEATURE: Appointment countdown tracker & Document Vault checklist
            item {
                BentoAppointmentsAndVaultCard(
                    appointmentDate = appointmentDate,
                    appointmentTime = appointmentTime,
                    appointmentLocation = appointmentLocation,
                    onUpdateAppointment = onUpdateAppointment,
                    checkedDocs = checkedDocs,
                    onToggleDocument = onToggleDocument,
                    selectedLanguageCode = selectedLanguageCode
                )
            }

            // PREMIUM FEATURE: Interactive Status Interpreter & AI Advisor Chat Box
            item {
                BentoStatusAdvisorCard(selectedLanguageCode = selectedLanguageCode)
            }
        }

        // SUB-TAB 2: Statistiche, Portali & Credenziali
        if (activeSubTab == 2) {
            // PREMIUM FEATURE: Community Waiting Estimations & Comparative Stats
            item {
                BentoCommunityStatsCard(
                    selectedProvince = selectedProvince,
                    onProvinceSelected = onProvinceSelected,
                    selectedLanguageCode = selectedLanguageCode
                )
            }

            // PREMIUM FEATURE: Portal monitoring & secure anti-scam advice
            item {
                BentoPortalMonitorCard(selectedLanguageCode = selectedLanguageCode)
            }

            // BENTO CARD 4: Credentials Card
            item {
                BentoCredentialsCard(credentials = credentials, onGoToCredentials = onGoToCredentials, selectedLanguageCode = selectedLanguageCode)
            }
        }
    }
}

@Composable
fun BentoPrimaryStatusCard(credentials: UserCredentials?, selectedLanguageCode: String) {
    val hasPolizia = credentials != null && 
            (credentials.numeroAssicurata.isNotBlank() || credentials.numeroPratica.isNotBlank())
    val hasPortale = credentials != null && 
            (credentials.userId.isNotBlank() && credentials.password.isNotBlank())

    val poliziaStatus = credentials?.lastPoliziaStatus ?: "No checks yet"
    val portaleStatus = credentials?.lastPortaleStatus ?: "No checks yet"

    val combinedStatus = when {
        hasPolizia && hasPortale -> {
            if (poliziaStatus != "No checks yet" && portaleStatus != "No checks yet") {
                // Shorten status if combined to avoid UI overflows
                val shortPolizia = if (poliziaStatus.length > 20) poliziaStatus.take(17) + "..." else poliziaStatus
                val shortPortale = if (portaleStatus.length > 20) portaleStatus.take(17) + "..." else portaleStatus
                "$shortPolizia | $shortPortale"
            } else if (poliziaStatus != "No checks yet") {
                poliziaStatus
            } else if (portaleStatus != "No checks yet") {
                portaleStatus
            } else {
                Localization.string("awaiting_check", selectedLanguageCode)
            }
        }
        hasPolizia -> {
            if (poliziaStatus != "No checks yet") poliziaStatus else Localization.string("awaiting_check", selectedLanguageCode)
        }
        hasPortale -> {
            if (portaleStatus != "No checks yet") portaleStatus else Localization.string("awaiting_check", selectedLanguageCode)
        }
        else -> Localization.string("config_required", selectedLanguageCode)
    }

    val statusLabel = if (credentials == null || (!hasPolizia && !hasPortale)) {
        Localization.string("tracking_to_configure", selectedLanguageCode)
    } else {
        combinedStatus
    }

    val isActiveTracking = credentials != null && (hasPolizia || hasPortale)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardPrimaryBg)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BentoBadgeGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = BentoCardPrimaryText,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Active Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(BentoBackground)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActiveTracking) Localization.string("tracking_active", selectedLanguageCode) else Localization.string("tracking_to_configure", selectedLanguageCode),
                        color = BentoBadgeGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Column {
                Text(
                    text = Localization.string("document_status", selectedLanguageCode),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = BentoTextSecondary
                )
                Text(
                    text = statusLabel,
                    fontSize = 20.sp, // Slightly smaller to accommodate dual status elegantly
                    fontWeight = FontWeight.Bold,
                    color = BentoCardPrimaryText
                )
            }
        }
    }
}

@Composable
fun BentoPortalStatusCard(credentials: UserCredentials?, selectedLanguageCode: String) {
    val hasCredentials = credentials != null && 
            credentials.userId.isNotBlank() && credentials.password.isNotBlank()

    val statusText = credentials?.lastPortaleStatus ?: "No checks yet"
    val (titleText, descText) = when {
        !hasCredentials -> Pair(Localization.string("not_active", selectedLanguageCode), Localization.string("configure_credentials_desc", selectedLanguageCode))
        statusText == "No checks yet" -> Pair(Localization.string("not_verified", selectedLanguageCode), Localization.string("manual_check_desc", selectedLanguageCode))
        statusText.contains(" - ") -> {
            val parts = statusText.split(" - ", limit = 2)
            Pair(parts[0], parts[1])
        }
        else -> Pair(Localization.string("status", selectedLanguageCode), statusText)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = Localization.string("portal_status_header", selectedLanguageCode),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoAvatarBorder,
                letterSpacing = 0.5.sp
            )

            Column(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Text(
                    text = titleText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextHeading
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = descText,
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    lineHeight = 14.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val dotColor = when {
                    !hasCredentials -> BentoTextSecondary.copy(alpha = 0.4f)
                    statusText == "No checks yet" -> BentoTextSecondary.copy(alpha = 0.6f)
                    statusText.contains("Step 3") || statusText.contains("Pronto") -> BentoBadgeGreen
                    else -> Color(0xFFFF9800) // Orange/Amber
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Text(
                    text = Localization.string("immigration_portal", selectedLanguageCode),
                    fontSize = 10.sp,
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BentoPoliziaStatusCard(credentials: UserCredentials?, selectedLanguageCode: String) {
    val statusText = credentials?.lastPoliziaStatus ?: "No checks yet"
    val hasCode = credentials != null && 
            (credentials.numeroAssicurata.isNotBlank() || credentials.numeroPratica.isNotBlank())

    val (titleText, descText) = when {
        !hasCode -> Pair(Localization.string("not_active", selectedLanguageCode), Localization.string("configure_receipt_desc", selectedLanguageCode))
        statusText == "No checks yet" -> Pair(Localization.string("not_verified", selectedLanguageCode), Localization.string("manual_check_desc", selectedLanguageCode))
        statusText.contains("consegna", ignoreCase = true) || statusText.contains("Pronto", ignoreCase = true) -> Pair(Localization.string("ready", selectedLanguageCode), statusText)
        else -> Pair(Localization.string("in_processing", selectedLanguageCode), statusText)
    }

    val isReady = statusText.contains("Pronto", ignoreCase = true) || statusText.contains("consegna", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = Localization.string("state_police_header", selectedLanguageCode),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoAvatarBorder,
                letterSpacing = 0.5.sp
            )

            Column(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Text(
                    text = titleText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextHeading
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (statusText != "No checks yet") statusText else descText,
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    lineHeight = 14.sp,
                    maxLines = 2
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val dotColor = when {
                    !hasCode -> BentoTextSecondary.copy(alpha = 0.4f)
                    statusText == "No checks yet" -> BentoTextSecondary.copy(alpha = 0.6f)
                    isReady -> BentoBadgeGreen
                    else -> Color(0xFFFF9800) // Amber
                }
                val label = when {
                    !hasCode -> Localization.string("no_code", selectedLanguageCode)
                    statusText == "No checks yet" -> Localization.string("not_checked", selectedLanguageCode)
                    isReady -> Localization.string("ready_for_pickup", selectedLanguageCode)
                    else -> Localization.string("in_processing", selectedLanguageCode)
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BentoCredentialsCard(
    credentials: UserCredentials?,
    onGoToCredentials: () -> Unit,
    selectedLanguageCode: String
) {
    val maskedUser = remember(credentials?.userId) {
        val uid = credentials?.userId ?: ""
        if (uid.length >= 8) {
            "${uid.take(4)}****${uid.takeLast(4)}"
        } else if (uid.isNotBlank()) {
            "${uid.take(2)}****"
        } else {
            "Non inserito"
        }
    }

    val passLabel = if (credentials?.password?.isNotBlank() == true) "••••••••" else Localization.string("not_entered_f", selectedLanguageCode)
    val userDisplay = if (maskedUser == "Non inserito") Localization.string("not_entered", selectedLanguageCode) else maskedUser

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardLightBg)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon container (rounded-2xl)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BentoTextHeading),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Text details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = Localization.string("secure_account", selectedLanguageCode),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextHeading
                )
                Text(
                    text = Localization.string("credentials_summary", selectedLanguageCode, userDisplay, passLabel),
                    fontSize = 12.sp,
                    color = BentoTextSecondary
                )
            }

            // Edit button (p-3 rounded-full bg-white)
            IconButton(
                onClick = onGoToCredentials,
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .size(40.dp)
                    .testTag("edit_credentials_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = Localization.string("edit_credentials_desc", selectedLanguageCode),
                    tint = BentoTextHeading,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BiometricPermessoCard(credentials: UserCredentials?, selectedLanguageCode: String) {
    val statusText = credentials?.lastPoliziaStatus ?: "Nessun controllo effettuato"
    val isReady = statusText.contains("Pronto", ignoreCase = true)
    val isNotFound = statusText.contains("Non presente", ignoreCase = true)

    val cardColor = animateColorAsState(
        targetValue = when {
            isReady -> EmeraldSuccess.copy(alpha = 0.15f)
            isNotFound -> AmberWarning.copy(alpha = 0.12f)
            credentials?.lastCheckedTime == 0L -> MaterialTheme.colorScheme.surface
            else -> AzureBlue.copy(alpha = 0.12f)
        }, label = "cardColor"
    )

    val borderStrokeColor = animateColorAsState(
        targetValue = when {
            isReady -> EmeraldSuccess
            isNotFound -> AmberWarning
            credentials?.lastCheckedTime == 0L -> MaterialTheme.colorScheme.outlineVariant
            else -> AzureBlue
        }, label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor.value),
        border = BorderStroke(1.5.dp, borderStrokeColor.value)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Draw Italian Republic logo silhouette or background vector
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pathColor = Color.White.copy(alpha = 0.05f)
                drawCircle(
                    color = pathColor,
                    radius = 120.dp.toPx(),
                    center = Offset(size.width - 40.dp.toPx(), size.height - 40.dp.toPx())
                )
                drawRect(
                    color = pathColor,
                    topLeft = Offset(20.dp.toPx(), 20.dp.toPx()),
                    size = Size(40.dp.toPx(), 25.dp.toPx())
                )
            }

            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "REPUBBLICA ITALIANA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = Localization.string("residency_permit", selectedLanguageCode),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Simulated Biometric Gold Chip
                    Box(
                        modifier = Modifier
                            .size(36.dp, 26.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFE5A93B), Color(0xFFFFF1C5), Color(0xFFD49425))
                                )
                            )
                    )
                }

                // Middle: Display user code and names
                Column {
                    Text(
                        text = credentials?.fullName?.uppercase() ?: Localization.string("holder_not_configured", selectedLanguageCode),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    val postCode = credentials?.numeroAssicurata ?: ""
                    val questuraCode = credentials?.numeroPratica ?: ""
                    val displayCode = when {
                        postCode.isNotBlank() && questuraCode.isNotBlank() -> Localization.string("code_both", selectedLanguageCode, postCode, questuraCode)
                        postCode.isNotBlank() -> Localization.string("code_assicurata", selectedLanguageCode, postCode)
                        questuraCode.isNotBlank() -> Localization.string("code_pratica", selectedLanguageCode, questuraCode)
                        else -> Localization.string("no_code_registered", selectedLanguageCode)
                    }

                    Text(
                        text = displayCode,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                // Footer: Last checked date and status tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = Localization.string("last_checked_header", selectedLanguageCode),
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        val lastCheckedStr = if (credentials?.lastCheckedTime != 0L) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            sdf.format(Date(credentials?.lastCheckedTime ?: 0L))
                        } else {
                            Localization.string("never_checked", selectedLanguageCode)
                        }
                        Text(
                            text = lastCheckedStr,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Status Badge
                    val badgeColor = when {
                        isReady -> EmeraldSuccess
                        isNotFound -> AmberWarning
                        credentials?.lastCheckedTime == 0L -> StatusGray
                        else -> LightSecondary
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = statusText.uppercase(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckStateCard(
    checkState: CheckUiState,
    onDismiss: () -> Unit,
    selectedLanguageCode: String
) {
    val isSuccess = checkState is CheckUiState.Success
    val isError = checkState is CheckUiState.Error

    val containerColor = when (checkState) {
        is CheckUiState.Loading -> MaterialTheme.colorScheme.secondaryContainer
        is CheckUiState.Success -> EmeraldSuccess
        is CheckUiState.Error -> BentoAlertRed
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when (checkState) {
        is CheckUiState.Success, is CheckUiState.Error -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSuccess || isError) null else BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (checkState) {
                        is CheckUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(Localization.string("querying_portal", selectedLanguageCode), fontWeight = FontWeight.Bold, color = contentColor)
                        }
                        is CheckUiState.Success -> {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(Localization.string("response_received", selectedLanguageCode), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        is CheckUiState.Error -> {
                            Icon(Icons.Default.Error, contentDescription = "Errore", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(Localization.string("check_error", selectedLanguageCode), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        else -> {}
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Chiudi",
                        modifier = Modifier.size(16.dp),
                        tint = contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (checkState) {
                is CheckUiState.Success -> {
                    Text(
                        text = checkState.statusText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (checkState.detailsText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = checkState.detailsText,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 17.sp
                        )
                    }
                }
                is CheckUiState.Error -> {
                    Text(
                        text = checkState.errorMessage,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
                is CheckUiState.Loading -> {
                    Text(
                        text = Localization.string("querying_portal_desc", selectedLanguageCode),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun HistoryRecordItem(record: StatusHistory, selectedLanguageCode: String) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(record.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BentoCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val statusColor = when {
                !record.isSuccess -> BentoAlertRed
                record.statusText.contains("Pronto", ignoreCase = true) -> BentoBadgeGreen
                else -> BentoTextHeading
            }

            val icon = when {
                !record.isSuccess -> Icons.Default.CloudOff
                record.statusText.contains("Pronto", ignoreCase = true) -> Icons.Default.CheckCircle
                else -> Icons.Default.Schedule
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = record.statusText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BentoTextHeading
                    )
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = BentoTextSecondary
                    )
                }
                if (record.details.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = record.details,
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PortalWebViewTab(
    credentials: UserCredentials?,
    onStatusDetected: (String?, String?) -> Unit,
    selectedLanguageCode: String
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var webProgress by remember { mutableIntStateOf(0) }
    
    // 0 = Portale Immigrazione, 1 = Polizia di Stato
    var currentSite by remember { mutableIntStateOf(0) }
    
    val portalUrl = if (currentSite == 0) {
        "https://www.portaleimmigrazione.it/eli2immigrazioneweb/pagine/startpage.aspx"
    } else {
        "https://questure.poliziadistato.it/stranieri/"
    }

    // Force load the new URL when the user switches portals
    LaunchedEffect(portalUrl) {
        webViewInstance?.loadUrl(portalUrl)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // High-contrast, beautifully designed M3 tabs to switch between both official sites
        TabRow(
            selectedTabIndex = currentSite,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = currentSite == 0,
                onClick = { currentSite = 0 },
                text = { Text(Localization.string("immigration_portal", selectedLanguageCode), fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = currentSite == 1,
                onClick = { currentSite = 1 },
                text = { Text("Polizia di Stato", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        // Top Banner for Credential Auto-Fill helper
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (currentSite == 0) Localization.string("portal_banner_immigrazione", selectedLanguageCode) else Localization.string("portal_banner_polizia", selectedLanguageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = if (currentSite == 0) {
                            Localization.string("portal_autofill_help_portale", selectedLanguageCode)
                        } else {
                            Localization.string("portal_autofill_help_polizia", selectedLanguageCode)
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }

                val hasCredentials = credentials != null && if (currentSite == 0) {
                    credentials.userId.isNotBlank() && credentials.password.isNotBlank()
                } else {
                    credentials.numeroAssicurata.isNotBlank() || credentials.numeroPratica.isNotBlank()
                }

                Button(
                    onClick = {
                        webViewInstance?.let { wv ->
                            if (currentSite == 0) {
                                val user = credentials?.userId ?: ""
                                val pass = credentials?.password ?: ""
                                
                                val jsCode = """
                                    (function() {
                                        var inputs = document.getElementsByTagName('input');
                                        var filledUser = false;
                                        var filledPass = false;
                                        for (var i = 0; i < inputs.length; i++) {
                                            var input = inputs[i];
                                            var type = input.getAttribute('type') || '';
                                            type = type.toLowerCase();
                                            var id = (input.getAttribute('id') || '').toLowerCase();
                                            var name = (input.getAttribute('name') || '').toLowerCase();
                                            
                                            // Fill username/codice field
                                            if (!filledUser && (type === 'text' || id.indexOf('user') !== -1 || id.indexOf('codice') !== -1 || name.indexOf('user') !== -1 || name.indexOf('codice') !== -1 || id.indexOf('username') !== -1)) {
                                                input.value = '$user';
                                                input.dispatchEvent(new Event('input', { bubbles: true }));
                                                input.dispatchEvent(new Event('change', { bubbles: true }));
                                                filledUser = true;
                                            }
                                            // Fill password field
                                            if (!filledPass && (type === 'password' || id.indexOf('pass') !== -1 || id.indexOf('pwd') !== -1 || name.indexOf('pass') !== -1 || name.indexOf('pwd') !== -1)) {
                                                input.value = '$pass';
                                                input.dispatchEvent(new Event('input', { bubbles: true }));
                                                input.dispatchEvent(new Event('change', { bubbles: true }));
                                                filledPass = true;
                                            }
                                        }
                                        return filledUser && filledPass;
                                    })()
                                """.trimIndent()
                                wv.evaluateJavascript(jsCode, null)
                            } else {
                                val code = if (credentials?.numeroAssicurata?.isNotBlank() == true) {
                                    credentials.numeroAssicurata
                                } else {
                                    credentials?.numeroPratica ?: ""
                                }
                                
                                val jsCode = """
                                    (function() {
                                        var inputs = document.getElementsByTagName('input');
                                        var filled = false;
                                        // 1. Try to target elements with specific name or id that are highly unique to the receipt number field
                                        for (var i = 0; i < inputs.length; i++) {
                                            var input = inputs[i];
                                            var id = (input.getAttribute('id') || '').toLowerCase();
                                            var name = (input.getAttribute('name') || '').toLowerCase();
                                            
                                            if (name === 'numero' || id === 'numero' || name.indexOf('numero') !== -1 || id.indexOf('numero') !== -1 || name.indexOf('assicurata') !== -1 || id.indexOf('assicurata') !== -1) {
                                                input.value = '$code';
                                                input.dispatchEvent(new Event('input', { bubbles: true }));
                                                input.dispatchEvent(new Event('change', { bubbles: true }));
                                                filled = true;
                                                break;
                                            }
                                        }
                                        // 2. Fallback to standard text or number field that is not search-related
                                        if (!filled) {
                                            for (var i = 0; i < inputs.length; i++) {
                                                var input = inputs[i];
                                                var type = (input.getAttribute('type') || '').toLowerCase();
                                                var id = (input.getAttribute('id') || '').toLowerCase();
                                                var name = (input.getAttribute('name') || '').toLowerCase();
                                                
                                                if ((type === 'text' || type === 'number') && name.indexOf('search') === -1 && id.indexOf('search') === -1 && name !== 'q') {
                                                    input.value = '$code';
                                                    input.dispatchEvent(new Event('input', { bubbles: true }));
                                                    input.dispatchEvent(new Event('change', { bubbles: true }));
                                                    filled = true;
                                                    break;
                                                }
                                            }
                                        }
                                        return filled;
                                    })()
                                """.trimIndent()
                                wv.evaluateJavascript(jsCode, null)
                            }
                        }
                    },
                    enabled = hasCredentials,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("autofill_btn")
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto-fill", fontSize = 12.sp)
                }
            }
        }

        // WebView Progress Indicator
        if (webProgress < 100) {
            LinearProgressIndicator(
                progress = { webProgress / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        // Web Viewer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        val wv = this
                        // Configure CookieManager to save login sessions persistently
                        android.webkit.CookieManager.getInstance().apply {
                            setAcceptCookie(true)
                            setAcceptThirdPartyCookies(wv, true)
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                webProgress = 100
                                // Force-sync cookies to persistent storage
                                android.webkit.CookieManager.getInstance().flush()

                                // Auto-accept/hide cookie consent and inject padding so user can scroll high above bottom bar
                                val helperJs = """
                                    (function() {
                                        // 1. Add bottom padding to body so the bottom of the page isn't blocked by any UI
                                        var style = document.createElement('style');
                                        style.innerHTML = 'body { padding-bottom: 220px !important; }';
                                        document.head.appendChild(style);
                                        
                                        // 2. Continuous cleaner loop (runs every 500ms for 6 seconds) to destroy cookie modals/overlays as they load
                                        var checkCount = 0;
                                        var cookieTimer = setInterval(function() {
                                            checkCount++;
                                            if (checkCount > 12) clearInterval(cookieTimer);

                                            // Try to automatically click any cookie accept button
                                            var cookieKeywords = ['accetta', 'accetto', 'consenti', 'conferma', 'ok', 'chiudi', 'agree', 'accept', 'allow', 'dismiss'];
                                            var tags = ['button', 'a', 'div', 'span'];
                                            for (var t = 0; t < tags.length; t++) {
                                                var elements = document.getElementsByTagName(tags[t]);
                                                for (var i = 0; i < elements.length; i++) {
                                                    var el = elements[i];
                                                    var text = (el.innerText || el.textContent || '').toLowerCase().trim();
                                                    for (var j = 0; j < cookieKeywords.length; j++) {
                                                        var kw = cookieKeywords[j];
                                                        if (text === kw || (text.indexOf(kw) !== -1 && text.length < 20)) {
                                                            if (typeof el.click === 'function') {
                                                                el.click();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            // Directly hide known cookie banners/overlays if they refuse to close
                                            var commonSelectors = ['#cookie-banner', '#cookie-consent', '.iubenda-cs-banner', '#cookiebanner', '.cookieconsent', '[id*="cookie"]', '[class*="cookie"]', '[id*="consent"]', '[class*="consent"]'];
                                            commonSelectors.forEach(function(sel) {
                                                try {
                                                    var els = document.querySelectorAll(sel);
                                                    els.forEach(function(el) {
                                                        el.style.display = 'none';
                                                        el.style.setProperty('display', 'none', 'important');
                                                    });
                                                } catch(e) {}
                                            });
                                            
                                            // Enable scrolling if locked
                                            document.body.style.overflow = 'auto';
                                            document.documentElement.style.overflow = 'auto';
                                        }, 500);
                                    })()
                                """.trimIndent()
                                view?.evaluateJavascript(helperJs, null)

                                // Scrape the content to automatically detect status changes from manual browsing
                                view?.evaluateJavascript("(function() { return document.body.innerText; })()") { text ->
                                    val cleanText = text?.trim() ?: ""
                                    if (cleanText.isNotBlank()) {
                                        if (currentSite == 0) {
                                            // Portale Immigrazione
                                            val portaleStatus = when {
                                                cleanText.contains("Anomalie non riscontrate", ignoreCase = true) || cleanText.contains("nessuna anomalia", ignoreCase = true) -> "Step 3 - Anomalie non riscontrate"
                                                cleanText.contains("In istruttoria", ignoreCase = true) || cleanText.contains("istruttoria", ignoreCase = true) -> "Step 2 - In istruttoria"
                                                cleanText.contains("Ricevuto", ignoreCase = true) || cleanText.contains("domanda ricevuta", ignoreCase = true) -> "Step 1 - Ricevuto"
                                                cleanText.contains("Area Riservata", ignoreCase = true) || cleanText.contains("Benvenuto", ignoreCase = true) || cleanText.contains("stranieri", ignoreCase = true) -> "Collegato (Area Riservata)"
                                                else -> null
                                            }
                                            if (portaleStatus != null) {
                                                onStatusDetected(null, portaleStatus)
                                            }
                                        } else {
                                            // Polizia di Stato
                                            val poliziaStatus = when {
                                                cleanText.contains("pronto per la consegna", ignoreCase = true) || cleanText.contains("consegna", ignoreCase = true) -> "Pronto per la consegna"
                                                cleanText.contains("non è pronto", ignoreCase = true) || cleanText.contains("in trattazione", ignoreCase = true) -> "In trattazione"
                                                cleanText.contains("non presente in archivio", ignoreCase = true) || cleanText.contains("non trovato", ignoreCase = true) -> "Non presente in archivio"
                                                else -> null
                                            }
                                            if (poliziaStatus != null) {
                                                onStatusDetected(poliziaStatus, null)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        webChromeClient = object : android.webkit.WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                webProgress = newProgress
                            }
                        }
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true // Enable HTML5 local storage databases
                            builtInZoomControls = true
                            displayZoomControls = false
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        webViewInstance = this
                        loadUrl(portalUrl)
                    }
                },
                update = { _ -> },
                modifier = Modifier.fillMaxSize()
            )

            // Custom floating action chip to manually force cookie removal and enable scroll on demand
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 16.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        webViewInstance?.evaluateJavascript("""
                            (function() {
                                // 1. Force remove all elements containing cookie or banner words
                                var els = document.querySelectorAll('*');
                                for (var i = 0; i < els.length; i++) {
                                    var el = els[i];
                                    var id = (el.id || '').toLowerCase();
                                    var cls = (el.className || '');
                                    if (typeof cls === 'string') cls = cls.toLowerCase();
                                    else cls = '';
                                    if (id.indexOf('cookie') !== -1 || cls.indexOf('cookie') !== -1 || id.indexOf('consent') !== -1 || cls.indexOf('consent') !== -1 || id.indexOf('banner') !== -1 || cls.indexOf('banner') !== -1) {
                                        el.style.display = 'none';
                                        try { el.parentNode.removeChild(el); } catch(e){}
                                    }
                                }
                                // 2. Enable scrolling and scroll slightly to verify
                                document.body.style.overflow = 'auto';
                                document.documentElement.style.overflow = 'auto';
                                document.body.style.paddingBottom = '220px';
                            })()
                        """.trimIndent(), null)
                    },
                    containerColor = ItalianRed,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("clean_cookies_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Block, contentDescription = "Rimuovi Cookie", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nascondi Cookie 🍪", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialsTab(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    userId: String,
    onUserIdChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    numeroAssicurata: String,
    onNumeroAssicurataChange: (String) -> Unit,
    numeroPratica: String,
    onNumeroPraticaChange: (String) -> Unit,
    onSave: () -> Unit,
    selectedLanguageCode: String
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Info card explaining Italian permit receipts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Localization.string("guide_title", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Localization.string("guide_body", selectedLanguageCode),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Card Section 1: Dati Personali & Portale
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.string("portal_data_title", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BentoTextHeading
                        )
                    }

                    Text(
                        text = Localization.string("portal_data_desc", selectedLanguageCode),
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = onFullNameChange,
                        label = { Text(Localization.string("input_fullname", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_fullname_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_fullname")
                    )

                    OutlinedTextField(
                        value = userId,
                        onValueChange = onUserIdChange,
                        label = { Text(Localization.string("input_userid", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_userid_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_userid")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text(Localization.string("input_password", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_password_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = Localization.string("show_password_desc", selectedLanguageCode),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_password")
                    )
                }
            }
        }

        // Card Section 2: Polizia di Stato
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.string("polizia_tracking_title", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BentoTextHeading
                        )
                    }

                    Text(
                        text = Localization.string("polizia_tracking_desc", selectedLanguageCode),
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )

                    OutlinedTextField(
                        value = numeroAssicurata,
                        onValueChange = onNumeroAssicurataChange,
                        label = { Text(Localization.string("input_assicurata", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_assicurata_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_assicurata")
                    )

                    OutlinedTextField(
                        value = numeroPratica,
                        onValueChange = onNumeroPraticaChange,
                        label = { Text(Localization.string("input_pratica", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_pratica_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_pratica")
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = ItalianGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(54.dp)
                    .testTag("save_creds_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(Localization.string("button_save", selectedLanguageCode), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun BentoPermessoTimelineCard(credentials: UserCredentials?, selectedLanguageCode: String) {
    val polStatus = credentials?.lastPoliziaStatus?.lowercase(Locale.getDefault()) ?: ""
    val portStatus = credentials?.lastPortaleStatus?.lowercase(Locale.getDefault()) ?: ""
    
    val currentStep = when {
        polStatus.contains("consegna") || polStatus.contains("pronto") -> 5
        polStatus.contains("produzione") || portStatus.contains("stampa") || portStatus.contains("step 3") -> 4
        polStatus.contains("trattazione") || portStatus.contains("step 2") || portStatus.contains("istruttoria") -> 3
        portStatus.contains("step 1") || credentials?.numeroAssicurata?.isNotBlank() == true -> 2
        else -> 1
    }

    val stepLabels = listOf(
        Localization.string("step_1_poste", selectedLanguageCode) to Localization.string("step_1_received", selectedLanguageCode),
        Localization.string("step_2_questura", selectedLanguageCode) to Localization.string("step_2_fingerprinting", selectedLanguageCode),
        Localization.string("step_3_processing", selectedLanguageCode) to Localization.string("step_3_under_investigation", selectedLanguageCode),
        Localization.string("step_4_printing", selectedLanguageCode) to Localization.string("step_4_in_production", selectedLanguageCode),
        Localization.string("step_5_delivery", selectedLanguageCode) to Localization.string("step_5_ready", selectedLanguageCode)
    )

    val explanation = when (currentStep) {
        1 -> Localization.string("timeline_desc_1", selectedLanguageCode)
        2 -> Localization.string("timeline_desc_2", selectedLanguageCode)
        3 -> Localization.string("timeline_desc_3", selectedLanguageCode)
        4 -> Localization.string("timeline_desc_4", selectedLanguageCode)
        5 -> Localization.string("timeline_desc_5", selectedLanguageCode)
        else -> Localization.string("timeline_desc_inactive", selectedLanguageCode)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp))
            .testTag("timeline_card"),
        colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = "Timeline",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Localization.string("timeline_card_title", selectedLanguageCode),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BentoTextHeading
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stepper Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                stepLabels.forEachIndexed { index, pair ->
                    val stepNum = index + 1
                    val isCompleted = stepNum < currentStep
                    val isActive = stepNum == currentStep
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> ItalianGreen.copy(alpha = 0.2f)
                                        isActive -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else -> BentoCardBorder
                                    }
                                )
                                .border(
                                    width = 2.dp,
                                    color = when {
                                        isCompleted -> ItalianGreen
                                        isActive -> MaterialTheme.colorScheme.primary
                                        else -> Color.Gray.copy(alpha = 0.4f)
                                    },
                                    shape = CircleShape
                                )
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = ItalianGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = stepNum.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pair.first,
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) MaterialTheme.colorScheme.primary else BentoTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = pair.second,
                            fontSize = 9.sp,
                            color = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else BentoTextSecondary.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    if (index < stepLabels.size - 1) {
                        Box(
                            modifier = Modifier
                                .weight(0.5f)
                                .height(2.dp)
                                .background(
                                    if (stepNum < currentStep) ItalianGreen else Color.Gray.copy(alpha = 0.3f)
                                )
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Explanation box
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = Localization.string("current_status_explanation", selectedLanguageCode),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = explanation,
                        fontSize = 12.sp,
                        color = BentoTextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BentoCommunityStatsCard(
    selectedProvince: String,
    onProvinceSelected: (String) -> Unit,
    selectedLanguageCode: String
) {
    val provinces = listOf("Milano", "Roma", "Torino", "Firenze", "Bologna", "Bergamo", "Piacenza")
    
    val stats = when (selectedProvince) {
        "Milano" -> Triple("4.2", "3.8", "8.0")
        "Roma" -> Triple("5.5", "4.5", "10.0")
        "Torino" -> Triple("3.9", "3.5", "7.4")
        "Firenze" -> Triple("4.5", "4.0", "8.5")
        "Bologna" -> Triple("3.5", "3.0", "6.5")
        "Bergamo" -> Triple("3.0", "2.5", "5.5")
        else -> Triple("4.8", "3.9", "8.7")
    }

    val monthsLabel = when(selectedLanguageCode) {
        "en" -> "months"
        "es" -> "meses"
        "fr" -> "mois"
        "ar" -> "أشهر"
        else -> "mesi"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp))
            .testTag("community_stats_card"),
        colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Community",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.string("community_wait_estimates_title", selectedLanguageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BentoTextHeading
                    )
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = Localization.string("live_estimates", selectedLanguageCode),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = Localization.string("select_province_stats_desc", selectedLanguageCode),
                fontSize = 12.sp,
                color = BentoTextSecondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Horizontal scroll of Provinces
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                provinces.forEach { province ->
                    val isSelected = province == selectedProvince
                    FilterChip(
                        selected = isSelected,
                        onClick = { onProvinceSelected(province) },
                        label = { Text(province) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Grid of stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(Localization.string("wait_fingerprints", selectedLanguageCode), fontSize = 10.sp, color = BentoTextSecondary, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${stats.first} $monthsLabel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(Localization.string("wait_post_to_fingerprints", selectedLanguageCode), fontSize = 8.sp, color = BentoTextSecondary, textAlign = TextAlign.Center)
                    }
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(Localization.string("wait_printing", selectedLanguageCode), fontSize = 10.sp, color = BentoTextSecondary, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${stats.second} $monthsLabel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(Localization.string("wait_fingerprints_to_ready", selectedLanguageCode), fontSize = 8.sp, color = BentoTextSecondary, textAlign = TextAlign.Center)
                    }
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(Localization.string("wait_total", selectedLanguageCode), fontSize = 10.sp, color = BentoTextSecondary, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${stats.third} $monthsLabel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(Localization.string("global_average", selectedLanguageCode), fontSize = 8.sp, color = BentoTextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonSearch,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Localization.string("people_like_you", selectedLanguageCode, selectedProvince),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Localization.string("people_like_you_desc", selectedLanguageCode),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BentoStatusAdvisorCard(selectedLanguageCode: String) {
    var selectedQuestionIndex by remember { mutableStateOf<Int?>(null) }
    
    val questions = listOf(
        Localization.string("advisor_q1", selectedLanguageCode) to Localization.string("advisor_a1", selectedLanguageCode),
        Localization.string("advisor_q2", selectedLanguageCode) to Localization.string("advisor_a2", selectedLanguageCode),
        Localization.string("advisor_q3", selectedLanguageCode) to Localization.string("advisor_a3", selectedLanguageCode),
        Localization.string("advisor_q4", selectedLanguageCode) to Localization.string("advisor_a4", selectedLanguageCode)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp))
            .testTag("status_advisor_card"),
        colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Advisor",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Localization.string("ai_advisor_title", selectedLanguageCode),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BentoTextHeading
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = Localization.string("ai_advisor_desc", selectedLanguageCode),
                fontSize = 12.sp,
                color = BentoTextSecondary
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                questions.forEachIndexed { index, pair ->
                    val isExpanded = selectedQuestionIndex == index
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedQuestionIndex = if (isExpanded) null else index },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                                             else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pair.first,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isExpanded) MaterialTheme.colorScheme.primary else BentoTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Toggle",
                                    tint = if (isExpanded) MaterialTheme.colorScheme.primary else BentoTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = pair.second,
                                    fontSize = 11.sp,
                                    color = BentoTextPrimary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BentoAppointmentsAndVaultCard(
    appointmentDate: String,
    appointmentTime: String,
    appointmentLocation: String,
    onUpdateAppointment: (String, String, String) -> Unit,
    checkedDocs: Set<String>,
    onToggleDocument: (String) -> Unit,
    selectedLanguageCode: String
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    val documents = listOf(
        "pass" to Localization.string("doc_passport", selectedLanguageCode),
        "rec" to Localization.string("doc_receipt", selectedLanguageCode),
        "marca" to Localization.string("doc_revenue_stamp", selectedLanguageCode),
        "photo" to Localization.string("doc_photos", selectedLanguageCode),
        "res" to Localization.string("doc_accommodation", selectedLanguageCode),
        "work" to Localization.string("doc_work_contract", selectedLanguageCode)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp))
            .testTag("appointment_vault_card"),
        colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section 1: Appointment Countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.string("questura_appointments_title", selectedLanguageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BentoTextHeading
                    )
                }
                
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = Localization.string("edit_appointment_desc", selectedLanguageCode),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (appointmentDate.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Localization.string("next_appointment", selectedLanguageCode),
                                fontSize = 10.sp,
                                color = BentoTextSecondary
                            )
                            Text(
                                text = Localization.string("appointment_formatted", selectedLanguageCode, appointmentDate, appointmentTime),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = appointmentLocation,
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = "Confirmed",
                            tint = ItalianGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().clickable { showEditDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.string("no_appointment_desc", selectedLanguageCode),
                            fontSize = 12.sp,
                            color = BentoTextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Section 2: Document Vault Checklist
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Vault",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Localization.string("doc_vault_title", selectedLanguageCode),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BentoTextHeading
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = Localization.string("doc_vault_desc", selectedLanguageCode),
                fontSize = 12.sp,
                color = BentoTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            
            // Checklist grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                documents.forEach { (id, label) ->
                    val isChecked = checkedDocs.contains(id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onToggleDocument(id) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onToggleDocument(id) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            color = if (isChecked) BentoTextPrimary else BentoTextPrimary.copy(alpha = 0.8f),
                            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
    
    if (showEditDialog) {
        var dateVal by remember { mutableStateOf(appointmentDate) }
        var timeVal by remember { mutableStateOf(appointmentTime) }
        var locVal by remember { mutableStateOf(appointmentLocation) }
        
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(Localization.string("appointment_details_title", selectedLanguageCode), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = dateVal,
                        onValueChange = { dateVal = it },
                        label = { Text(Localization.string("appointment_date_label", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("appointment_date_placeholder", selectedLanguageCode)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = timeVal,
                        onValueChange = { timeVal = it },
                        label = { Text(Localization.string("appointment_time_label", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("appointment_time_placeholder", selectedLanguageCode)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = locVal,
                        onValueChange = { locVal = it },
                        label = { Text(Localization.string("appointment_location_label", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("appointment_location_placeholder", selectedLanguageCode)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateAppointment(dateVal, timeVal, locVal)
                        showEditDialog = false
                    }
                ) {
                    Text(Localization.string("save", selectedLanguageCode))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(Localization.string("cancel", selectedLanguageCode))
                }
            }
        )
    }
}

@Composable
fun BentoPortalMonitorCard(selectedLanguageCode: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp))
            .testTag("portal_monitor_card"),
        colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.RssFeed,
                    contentDescription = "Monitor",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Localization.string("portal_status_monitor_title", selectedLanguageCode),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BentoTextHeading
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ItalianGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Polizia di Stato", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(Localization.string("online_status", selectedLanguageCode), fontSize = 9.sp, color = ItalianGreen)
                        }
                    }
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ItalianGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Portale Immigrazione", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(Localization.string("online_status", selectedLanguageCode), fontSize = 9.sp, color = ItalianGreen)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Shield Security",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Localization.string("anti_scam_warning_title", selectedLanguageCode),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Localization.string("anti_scam_warning_desc", selectedLanguageCode),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: PermessoViewModel,
    onComplete: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val selectedLanguageCode by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val credentials by viewModel.credentials.collectAsStateWithLifecycle()
    
    val name by viewModel.formFullName.collectAsStateWithLifecycle()
    val userId by viewModel.formUserId.collectAsStateWithLifecycle()
    val password by viewModel.formPassword.collectAsStateWithLifecycle()
    val numeroAssicurata by viewModel.formNumeroAssicurata.collectAsStateWithLifecycle()
    
    var isPasswordVisible by remember { mutableStateOf(false) }

    val layoutDirection = if (selectedLanguageCode == "ar") {
        androidx.compose.ui.unit.LayoutDirection.Rtl
    } else {
        androidx.compose.ui.unit.LayoutDirection.Ltr
    }

    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = ItalianGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Permesso Status Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    if (currentPage < 5) {
                        TextButton(onClick = { currentPage = 5 }) {
                            Text(
                                text = Localization.string("button_skip", selectedLanguageCode),
                                color = ItalianGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Central Content based on page
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when (currentPage) {
                        0 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = ItalianGreen,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = Localization.string("onboarding_lang_title", selectedLanguageCode),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = Localization.string("onboarding_lang_subtitle", selectedLanguageCode),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                val languages = listOf(
                                    AppLanguage.ITALIAN,
                                    AppLanguage.ENGLISH,
                                    AppLanguage.ARABIC,
                                    AppLanguage.SPANISH,
                                    AppLanguage.FRENCH
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    languages.forEach { lang ->
                                        val isSelected = selectedLanguageCode == lang.code
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.selectLanguage(lang.code) }
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) ItalianGreen else BentoCardBorder,
                                                    shape = RoundedCornerShape(12.dp)
                                                ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) ItalianGreenLight else BentoCardLightBg
                                            ),
                                            shape = RoundedCornerShape(12.dp)
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
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Text(text = lang.flag, fontSize = 24.sp)
                                                    Text(
                                                        text = lang.displayName,
                                                        fontSize = 16.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) ItalianGreen else BentoTextHeading
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "Selected",
                                                        tint = ItalianGreen,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(ItalianGreen.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Welcome Logo",
                                        tint = ItalianGreen,
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Text(
                                    text = Localization.string("welcome_title", selectedLanguageCode),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = Localization.string("welcome_body", selectedLanguageCode),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                        2 -> {
                            // Questura & Province Personalization Setup
                            var searchQuery by remember { mutableStateOf("") }
                            val results = remember(searchQuery) { com.example.data.QuesturaCatalog.search(searchQuery) }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = Localization.string("onboarding_questura_title", selectedLanguageCode),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = Localization.string("onboarding_personalize_desc", selectedLanguageCode),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                
                                // Search Bar
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text(Localization.string("onboarding_search_placeholder", selectedLanguageCode)) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("questura_search_input")
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                
                                // Results List
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                ) {
                                    items(results) { questura ->
                                        val isSelected = credentials?.selectedQuesturaId == questura.id
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.selectQuesturaAndProvince(questura.id, questura.provinceCode, questura.province)
                                                    currentPage++
                                                }
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) ItalianGreen else BentoCardBorder,
                                                    shape = RoundedCornerShape(12.dp)
                                                ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) ItalianGreenLight else BentoCardLightBg
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(CircleShape)
                                                            .background(ItalianGreen.copy(alpha = 0.12f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Place,
                                                            contentDescription = null,
                                                            tint = ItalianGreen,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                    Column {
                                                        Text(
                                                            text = questura.name,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp,
                                                            color = BentoTextHeading
                                                        )
                                                        Text(
                                                            text = "${questura.province} (${questura.provinceCode}) - ${questura.region}",
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                                        )
                                                    }
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "Selected",
                                                        tint = ItalianGreen,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(
                                    onClick = {
                                        viewModel.selectQuesturaAndProvince(null, null, null)
                                        currentPage++
                                    }
                                ) {
                                    Text(
                                        text = Localization.string("onboarding_choose_later", selectedLanguageCode),
                                        color = ItalianGreen,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                        3 -> {
                            // Permit Type Selection Screen
                            val permitTypes = listOf(
                                "work" to (Icons.Default.Work to Localization.string("permit_work", selectedLanguageCode)),
                                "study" to (Icons.Default.School to Localization.string("permit_study", selectedLanguageCode)),
                                "family_reunification" to (Icons.Default.Group to Localization.string("permit_family_reunification", selectedLanguageCode)),
                                "family_cohesion" to (Icons.Default.Home to Localization.string("permit_family_cohesion", selectedLanguageCode)),
                                "asylum" to (Icons.Default.Shield to Localization.string("permit_asylum", selectedLanguageCode)),
                                "eu_family" to (Icons.Default.Public to Localization.string("permit_eu_family", selectedLanguageCode)),
                                "long_term" to (Icons.Default.Description to Localization.string("permit_long_term", selectedLanguageCode)),
                                "renewal" to (Icons.Default.Autorenew to Localization.string("permit_renewal", selectedLanguageCode)),
                                "not_sure" to (Icons.Default.Help to Localization.string("permit_not_sure", selectedLanguageCode))
                            )
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = Localization.string("onboarding_permit_type_title", selectedLanguageCode),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = Localization.string("onboarding_permit_type_desc", selectedLanguageCode),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                ) {
                                    items(permitTypes) { (typeKey, pair) ->
                                        val (icon, label) = pair
                                        val isSelected = credentials?.selectedPermitType == typeKey
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.selectPermitType(typeKey)
                                                    currentPage++
                                                }
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) ItalianGreen else BentoCardBorder,
                                                    shape = RoundedCornerShape(12.dp)
                                                ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) ItalianGreenLight else BentoCardLightBg
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(CircleShape)
                                                            .background(ItalianGreen.copy(alpha = 0.12f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = icon,
                                                            contentDescription = null,
                                                            tint = ItalianGreen,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = label,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = BentoTextHeading
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "Selected",
                                                        tint = ItalianGreen,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(
                                    onClick = {
                                        viewModel.selectPermitType(null)
                                        currentPage++
                                    }
                                ) {
                                    Text(
                                        text = Localization.string("button_skip", selectedLanguageCode),
                                        color = ItalianGreen,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                        4 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Security Logo",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Text(
                                    text = Localization.string("privacy_title", selectedLanguageCode),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = Localization.string("privacy_body", selectedLanguageCode),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                        5 -> {
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                item {
                                    Text(
                                        text = Localization.string("credentials_card_title", selectedLanguageCode),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = Localization.string("profile_subtitle", selectedLanguageCode),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                
                                item {
                                    OutlinedTextField(
                                        value = name,
                                        onValueChange = { viewModel.formFullName.value = it },
                                        label = { Text(Localization.string("input_fullname", selectedLanguageCode)) },
                                        placeholder = { Text(Localization.string("input_fullname_placeholder", selectedLanguageCode)) },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                
                                item {
                                    OutlinedTextField(
                                        value = numeroAssicurata,
                                        onValueChange = { viewModel.formNumeroAssicurata.value = it },
                                        label = { Text(Localization.string("input_assicurata", selectedLanguageCode)) },
                                        placeholder = { Text(Localization.string("input_assicurata_placeholder", selectedLanguageCode)) },
                                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
 
                                item {
                                    OutlinedTextField(
                                        value = userId,
                                        onValueChange = { viewModel.formUserId.value = it },
                                        label = { Text(Localization.string("input_userid", selectedLanguageCode)) },
                                        placeholder = { Text(Localization.string("input_userid_placeholder", selectedLanguageCode)) },
                                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
 
                                item {
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { viewModel.formPassword.value = it },
                                        label = { Text(Localization.string("input_password", selectedLanguageCode)) },
                                        placeholder = { Text(Localization.string("input_password_placeholder", selectedLanguageCode)) },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                        trailingIcon = {
                                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = Localization.string("show_password_desc", selectedLanguageCode)
                                                )
                                            }
                                        },
                                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
 
                // Bottom Navigation Actions
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Page Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(6) { index ->
                            val isSelected = index == currentPage
                            val dotWidth = if (isSelected) 18.dp else 8.dp
                            val dotColor = if (isSelected) ItalianGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                            
                            Box(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(dotWidth)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(dotColor)
                            )
                        }
                    }
 
                    // Action Button
                    Button(
                        onClick = {
                            if (currentPage < 5) {
                                currentPage++
                            } else {
                                viewModel.saveCredentials()
                                onComplete()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ItalianGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("onboarding_action_btn")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentPage < 5) {
                                    Localization.string("button_continue", selectedLanguageCode)
                                } else {
                                    Localization.string("button_start", selectedLanguageCode)
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (currentPage < 5) Icons.Default.ArrowForward else Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTab(
    viewModel: PermessoViewModel,
    selectedLanguageCode: String
) {
    val context = LocalContext.current
    val fullName by viewModel.formFullName.collectAsStateWithLifecycle()
    val userId by viewModel.formUserId.collectAsStateWithLifecycle()
    val password by viewModel.formPassword.collectAsStateWithLifecycle()
    val numeroAssicurata by viewModel.formNumeroAssicurata.collectAsStateWithLifecycle()
    val numeroPratica by viewModel.formNumeroPratica.collectAsStateWithLifecycle()
    
    var isPasswordVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // User Profile Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ItalianGreenLight.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ItalianGreen)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = if (fullName.isNotBlank()) {
                            fullName.split("\\s+".toRegex()).take(2).map { it.take(1).uppercase() }.joinToString("")
                        } else "AM"
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = fullName.ifBlank { Localization.string("friend", selectedLanguageCode) },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = BentoTextHeading
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Localization.string("profile_subtitle", selectedLanguageCode),
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        }

        // Section 1: Seleziona Lingua (Select Language)
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = ItalianGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.string("select_language", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BentoTextHeading
                        )
                    }

                    val languages = listOf(
                        AppLanguage.ITALIAN,
                        AppLanguage.ENGLISH,
                        AppLanguage.ARABIC,
                        AppLanguage.SPANISH,
                        AppLanguage.FRENCH
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { lang ->
                            val isSelected = selectedLanguageCode == lang.code
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectLanguage(lang.code) },
                                label = {
                                    Text("${lang.flag} ${lang.displayName}", fontSize = 13.sp)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ItalianGreenLight,
                                    selectedLabelColor = ItalianGreen,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = BentoTextSecondary
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) ItalianGreen else BentoCardBorder
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Edit Profile Data (Dati Personali)
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = ItalianGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.string("profile_title", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BentoTextHeading
                        )
                    }

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { viewModel.formFullName.value = it },
                        label = { Text(Localization.string("input_fullname", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_fullname_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = numeroAssicurata,
                        onValueChange = { viewModel.formNumeroAssicurata.value = it },
                        label = { Text(Localization.string("input_assicurata", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_assicurata_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = userId,
                        onValueChange = { viewModel.formUserId.value = it },
                        label = { Text(Localization.string("input_userid", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_userid_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.formPassword.value = it },
                        label = { Text(Localization.string("input_password", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_password_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = Localization.string("show_password_desc", selectedLanguageCode),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = numeroPratica,
                        onValueChange = { viewModel.formNumeroPratica.value = it },
                        label = { Text(Localization.string("input_pratica", selectedLanguageCode)) },
                        placeholder = { Text(Localization.string("input_pratica_placeholder", selectedLanguageCode)) },
                        leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            viewModel.saveCredentials()
                            android.widget.Toast.makeText(
                                context,
                                Localization.string("profile_edit_success", selectedLanguageCode),
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ItalianGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.string("button_save", selectedLanguageCode), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 3: App Settings (Informazioni)
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ItalianGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.string("about_section", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BentoTextHeading
                        )
                    }

                    Text(
                        text = Localization.string("about_text", selectedLanguageCode),
                        fontSize = 12.sp,
                        color = BentoTextSecondary,
                        lineHeight = 16.sp
                    )

                    HorizontalDivider(color = BentoCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.string("bg_sync", selectedLanguageCode),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoTextHeading
                        )
                        Switch(
                            checked = true,
                            onCheckedChange = { /* Always enabled for real-time checks */ },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ItalianGreen
                            )
                        )
                    }
                }
            }
        }
    }
}

