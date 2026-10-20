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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
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
    val cooldownSeconds by viewModel.cooldownSecondsRemaining.collectAsStateWithLifecycle()
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

    LaunchedEffect(onboardingCompleted) {
        if (!onboardingCompleted) {
            selectedTabIndex = 0
            showingGuidesScreen = false
            showingQuestureExplorer = false
        }
    }

    val layoutDirection = if (selectedLanguageCode == "ar") {
        androidx.compose.ui.unit.LayoutDirection.Rtl
    } else {
        androidx.compose.ui.unit.LayoutDirection.Ltr
    }

    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection) {
        if (!onboardingCompleted) {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    viewModel.completeOnboarding()
                    selectedTabIndex = 0
                }
            )
        } else {
            Scaffold(
        topBar = {
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
                        UserPersistentAvatar(
                            fullName = credentials?.fullName,
                            size = 38.dp,
                            fontSize = 13.sp,
                            isSelected = selectedTabIndex == 4,
                            borderColor = BentoAvatarBorder,
                            borderWidth = 1.5.dp,
                            onClick = { selectedTabIndex = 4 },
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .testTag("topbar_profile_avatar")
                        )
                    }
                )
                // Premium thin Italian flag colored stripe (strictly LTR: Green -> White -> Red)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
                        icon = { Icon(Icons.Default.Face, contentDescription = "Sofia AI") },
                        label = { Text(Localization.string("tab_sofia", selectedLanguageCode)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoCardPrimaryText,
                            selectedTextColor = BentoCardPrimaryText,
                            indicatorColor = BentoAvatarBg,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_sofia")
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 4,
                        onClick = { selectedTabIndex = 4 },
                        icon = {
                            UserPersistentAvatar(
                                fullName = credentials?.fullName,
                                size = 26.dp,
                                fontSize = 10.sp,
                                isSelected = selectedTabIndex == 4,
                                borderColor = if (selectedTabIndex == 4) ItalianGreen else Color.White,
                                borderWidth = if (selectedTabIndex == 4) 2.dp else 1.2.dp,
                                modifier = Modifier.testTag("navbar_profile_avatar")
                            )
                        },
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
                    onClick = {
                        if (cooldownSeconds <= 0) {
                            viewModel.checkStatusNow()
                        }
                    },
                    containerColor = if (cooldownSeconds > 0) MaterialTheme.colorScheme.surfaceVariant else BentoBadgeGreen,
                    contentColor = if (cooldownSeconds > 0) BentoTextSecondary else Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 8.dp)
                        .testTag("refresh_status_fab")
                ) {
                    if (cooldownSeconds > 0) {
                        Text(
                            text = "${cooldownSeconds}s",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Aggiorna Stato",
                            modifier = Modifier.size(24.dp)
                        )
                    }
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
                            viewModel = viewModel,
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
                            onSyncVerifiedDossier = { name, uid, pass, istanza ->
                                viewModel.formFullName.value = name
                                viewModel.formUserId.value = uid
                                viewModel.formPassword.value = pass
                                viewModel.formNumeroAssicurata.value = pass
                                viewModel.formNumeroPratica.value = istanza
                                viewModel.saveCredentials()
                            },
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
                        viewModel = viewModel,
                        credentials = credentials,
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
                    val isDemoMode by viewModel.isInstitutionalDemoMode.collectAsStateWithLifecycle()
                    val userFullName = credentials?.fullName?.takeIf { it.isNotBlank() }
                        ?: if (isDemoMode) PermessoViewModel.DEMO_FULL_NAME else Localization.string("friend", selectedLanguageCode)
                    val sofiaViewModel: SofiaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    SofiaScreen(
                        viewModel = sofiaViewModel,
                        userFullName = userFullName,
                        selectedLanguageCode = selectedLanguageCode,
                        onNavigateAction = { action ->
                            when (action) {
                                SofiaActionEvent.ExplainStatus -> {
                                    selectedTabIndex = 0
                                    showingGuidesScreen = false
                                    showingQuestureExplorer = false
                                }
                                SofiaActionEvent.ShowRequiredDocuments -> {
                                    selectedTabIndex = 0
                                    showingGuidesScreen = false
                                    showingQuestureExplorer = false
                                }
                                SofiaActionEvent.ShowAppointment -> {
                                    selectedTabIndex = 0
                                    showingGuidesScreen = false
                                    showingQuestureExplorer = false
                                }
                                SofiaActionEvent.ShowMyQuestura -> {
                                    selectedTabIndex = 0
                                    showingQuestureExplorer = true
                                    showingGuidesScreen = false
                                }
                                SofiaActionEvent.OpenOfficialWebsite -> {
                                    selectedTabIndex = 1
                                }
                                SofiaActionEvent.OpenRenewalGuide -> {
                                    selectedTabIndex = 0
                                    showingGuidesScreen = true
                                    showingQuestureExplorer = false
                                }
                                SofiaActionEvent.GoToProfile -> {
                                    selectedTabIndex = 4
                                }
                                SofiaActionEvent.OpenBookmarks -> {
                                    selectedTabIndex = 0
                                    showingGuidesScreen = true
                                    showingQuestureExplorer = false
                                }
                                SofiaActionEvent.OpenImmigrationGuides -> {
                                    selectedTabIndex = 0
                                    showingGuidesScreen = true
                                    showingQuestureExplorer = false
                                }
                                SofiaActionEvent.OpenMyGuide -> {
                                    selectedTabIndex = 0
                                    showingGuidesScreen = true
                                    showingQuestureExplorer = false
                                }
                                SofiaActionEvent.OpenTimeline -> {
                                    selectedTabIndex = 0
                                    showingGuidesScreen = false
                                    showingQuestureExplorer = false
                                }
                            }
                        }
                    )
                }
                4 -> {
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
    viewModel: PermessoViewModel,
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
    onSyncVerifiedDossier: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    selectedLanguageCode: String
) {
    val hasCode = credentials != null &&
            (credentials.numeroAssicurata.isNotBlank() ||
             credentials.numeroPratica.isNotBlank() ||
             (credentials.userId.isNotBlank() && credentials.password.isNotBlank()))

    var activeSubTab by remember { mutableIntStateOf(0) }
    var activeSmartTool by remember { mutableStateOf<SmartToolType?>(null) }
    var showAllHistory by remember { mutableStateOf(false) }
    val cooldownSeconds by viewModel.cooldownSecondsRemaining.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isInstitutionalDemoMode.collectAsStateWithLifecycle()
    val isEn = selectedLanguageCode == "en"

    androidx.activity.compose.BackHandler(enabled = activeSmartTool != null || activeSubTab != 0) {
        if (activeSmartTool != null) {
            activeSmartTool = null
        } else {
            activeSubTab = 0
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp)
    ) {
        // 1. Calm Greeting with Persistent Profile Avatar & Quick Notification Test Header
        item {
            val name = credentials?.fullName?.trim().orEmpty()
            val greetingText = if (name.isNotBlank()) {
                Localization.string("greeting_user", selectedLanguageCode, name)
            } else {
                Localization.string("greeting_general", selectedLanguageCode)
            }
            val context = androidx.compose.ui.platform.LocalContext.current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserPersistentAvatar(
                        fullName = credentials?.fullName,
                        size = 44.dp,
                        fontSize = 14.sp,
                        borderColor = ItalianGreen.copy(alpha = 0.45f),
                        borderWidth = 1.5.dp
                    )
                    Column {
                        Text(
                            text = "$greetingText 👋",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
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
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                    }
                }

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
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("test_notif_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Test Notifica",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.string("test_button", selectedLanguageCode), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Clean 3-Section Workspace Pill Switcher (No Visual Overload)
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val subTabs = listOf(
                        0 to Localization.string("subtab_practice", selectedLanguageCode),
                        1 to Localization.string("subtab_tools", selectedLanguageCode),
                        2 to Localization.string("subtab_stats_tab", selectedLanguageCode)
                    )
                    subTabs.forEach { (index, title) ->
                        val isSelected = activeSubTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) BentoBadgeGreen else Color.Transparent)
                                .clickable {
                                    activeSubTab = index
                                    if (index != 1) activeSmartTool = null
                                }
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
        }

        // =========================================================================
        // WORKSPACE 0: LA TUA PRATICA (WITH CLEAR SECTION DIVIDERS)
        // =========================================================================
        if (activeSubTab == 0) {
            item {
                SectionDividerHeader(
                    title = Localization.string("section_main_status_title", selectedLanguageCode),
                    subtitle = Localization.string("section_main_status_sub", selectedLanguageCode),
                    badgeText = "LIVE",
                    icon = Icons.Default.Verified,
                    accentColor = ItalianGreen
                )
            }

            // Hero Status Card
            item {
                BentoPrimaryStatusCard(credentials = credentials, selectedLanguageCode = selectedLanguageCode)
            }

            // Primary Action Button ("Controlla Ora" / "Configura") right under Hero Card for effortless reach
            if (!hasCode && !isDemoMode) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
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
                item {
                    val isCooldownActive = cooldownSeconds > 0
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = onCheckNow,
                            enabled = !isCooldownActive && checkState !is CheckUiState.Loading,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoBadgeGreen,
                                contentColor = Color.White,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = BentoTextSecondary
                            ),
                            contentPadding = PaddingValues(vertical = 15.dp, horizontal = 18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("check_status_btn")
                        ) {
                            Icon(
                                imageVector = if (isCooldownActive) Icons.Default.HourglassBottom else Icons.Default.Refresh,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isCooldownActive) {
                                    Localization.string("check_cooldown_button", selectedLanguageCode, cooldownSeconds)
                                } else {
                                    Localization.string("button_check_now", selectedLanguageCode)
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        if (isCooldownActive) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = Localization.string("check_cooldown_subtitle", selectedLanguageCode),
                                fontSize = 11.sp,
                                color = BentoTextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("check_cooldown_subtitle")
                            )
                        }
                    }
                }
            }

            // Live Check Feedback Card (when checking or completed)
            if (checkState !is CheckUiState.Idle) {
                item {
                    CheckStateCard(checkState = checkState, onDismiss = onResetCheckState, selectedLanguageCode = selectedLanguageCode)
                }
            }

            // Divider before 1-Tap Quick Strip
            item {
                SectionDividerHeader(
                    title = Localization.string("section_quick_tools_title", selectedLanguageCode),
                    subtitle = Localization.string("section_quick_tools_sub", selectedLanguageCode),
                    badgeText = Localization.string("badge_4_tools", selectedLanguageCode),
                    icon = Icons.Default.AutoAwesome,
                    accentColor = Color(0xFF0284C7)
                )
            }

            // 1-Tap Quick Strip to jump directly to Smart Tools #1, #2, #3, #5 without visual clutter
            item {
                SmartToolsQuickStrip(
                    onSelectTool = { tool ->
                        activeSmartTool = tool
                        activeSubTab = 1
                    },
                    selectedLanguageCode = selectedLanguageCode
                )
            }

            // Divider before Official Portals & Dossier Details
            item {
                SectionDividerHeader(
                    title = Localization.string("section_portals_detail_title", selectedLanguageCode),
                    subtitle = Localization.string("section_portals_detail_sub", selectedLanguageCode),
                    badgeText = Localization.string("badge_2_sources", selectedLanguageCode),
                    icon = Icons.Default.AccountBalance,
                    accentColor = ItalianGreen
                )
            }

            // Dual Official Portals Comparison Row (Portale Immigrazione & Polizia di Stato)
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

            // Real Portale Immigrazione Dettaglio Istanza & Convocazione Card
            if (credentials != null && (credentials.codiceIstanza.isNotBlank() || credentials.convocazioneDate.isNotBlank() || credentials.dettaglioAnomalie.isNotBlank())) {
                item {
                    BentoIstanzaDetailsCard(credentials = credentials)
                }
            }

            // 5-Step Progress Timeline Card
            if (credentials != null && credentials.lastCheckedTime != 0L) {
                item {
                    BentoPermessoTimelineCard(credentials = credentials, selectedLanguageCode = selectedLanguageCode)
                }
            }

            // Divider before Questura & Immigration Guides
            item {
                SectionDividerHeader(
                    title = Localization.string("section_questura_guides_title", selectedLanguageCode),
                    subtitle = Localization.string("section_questura_guides_sub", selectedLanguageCode),
                    badgeText = selectedProvince.uppercase(Locale.getDefault()),
                    icon = Icons.Default.LocationOn,
                    accentColor = Color(0xFF7C3AED)
                )
            }

            // Personalized Questura Card (now clean and collapsible)
            item {
                PersonalizedQuesturaCard(
                    credentials = credentials,
                    onChooseQuestura = onChooseQuestura,
                    selectedLanguageCode = selectedLanguageCode
                )
            }

            // Compact Immigration Guides Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenGuides() }
                        .testTag("immigration_guides_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Localization.string("guides_card_title", selectedLanguageCode),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = Localization.string("guides_card_desc", selectedLanguageCode),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Divider before Status Check History
            item {
                SectionDividerHeader(
                    title = Localization.string("history_title", selectedLanguageCode),
                    subtitle = Localization.string("section_history_sub", selectedLanguageCode),
                    badgeText = "${history.size}",
                    icon = Icons.Default.History,
                    accentColor = Color(0xFFD97706)
                )
            }

            // Calm, Collapsible Status Check History Section Controls
            if (history.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (history.size > 2) {
                            TextButton(onClick = { showAllHistory = !showAllHistory }) {
                                Text(
                                    text = if (showAllHistory) "Mostra meno" else "Vedi tutti (${history.size})",
                                    fontSize = 12.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                        TextButton(onClick = onClearHistory) {
                            Icon(Icons.Default.Delete, contentDescription = "Cancella", modifier = Modifier.size(15.dp), tint = BentoBadgeGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.string("history_clear", selectedLanguageCode), fontSize = 12.sp, color = BentoBadgeGreen)
                        }
                    }
                }
            }

            if (history.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Nessuna cronologia",
                                tint = BentoTextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = Localization.string("no_history_recorded", selectedLanguageCode),
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }
            } else {
                val visibleHistory = if (showAllHistory) history else history.take(2)
                items(visibleHistory) { record ->
                    HistoryRecordItem(record = record, selectedLanguageCode = selectedLanguageCode)
                }
            }
        }

        // =========================================================================
        // WORKSPACE 1: KIT STRUMENTI SMART (FEATURES #1, #2, #3, #5 + CHECKLIST)
        // =========================================================================
        if (activeSubTab == 1) {
            item {
                SmartToolsCalmHub(
                    viewModel = viewModel,
                    selectedLanguageCode = selectedLanguageCode,
                    activeTool = activeSmartTool,
                    onSelectTool = { activeSmartTool = it }
                )
            }

            // When in the 2x2 Grid overview (not inside a single focused tool), also show the Appointment Checklist & FAQ below with clear section dividers
            if (activeSmartTool == null) {
                item {
                    SectionDividerHeader(
                        title = Localization.string("section_appointment_checklist_title", selectedLanguageCode),
                        subtitle = Localization.string("section_appointment_checklist_sub", selectedLanguageCode),
                        badgeText = Localization.string("badge_checklist", selectedLanguageCode),
                        icon = Icons.Default.FactCheck,
                        accentColor = Color(0xFF0284C7)
                    )
                }

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

                item {
                    SectionDividerHeader(
                        title = Localization.string("section_faq_title", selectedLanguageCode),
                        subtitle = Localization.string("section_faq_sub", selectedLanguageCode),
                        badgeText = Localization.string("badge_guide", selectedLanguageCode),
                        icon = Icons.Default.HelpOutline,
                        accentColor = Color(0xFF7C3AED)
                    )
                }

                item {
                    BentoStatusAdvisorCard(selectedLanguageCode = selectedLanguageCode)
                }
            }
        }

        // =========================================================================
        // WORKSPACE 2: STATISTICHE, PORTALI & CREDENZIALI (WITH CLEAR DIVIDERS)
        // =========================================================================
        if (activeSubTab == 2) {
            item {
                SectionDividerHeader(
                    title = Localization.string("section_wait_times_title", selectedLanguageCode),
                    subtitle = Localization.string("section_wait_times_sub", selectedLanguageCode),
                    badgeText = selectedProvince.uppercase(Locale.getDefault()),
                    icon = Icons.Default.Groups,
                    accentColor = ItalianGreen
                )
            }

            item {
                BentoCommunityStatsCard(
                    selectedProvince = selectedProvince,
                    onProvinceSelected = onProvinceSelected,
                    selectedLanguageCode = selectedLanguageCode
                )
            }

            item {
                SectionDividerHeader(
                    title = Localization.string("section_uptime_title", selectedLanguageCode),
                    subtitle = Localization.string("section_uptime_sub", selectedLanguageCode),
                    badgeText = "ONLINE",
                    icon = Icons.Default.Dns,
                    accentColor = Color(0xFF0284C7)
                )
            }

            item {
                BentoPortalMonitorCard(selectedLanguageCode = selectedLanguageCode)
            }

            item {
                SectionDividerHeader(
                    title = Localization.string("section_credentials_summary_title", selectedLanguageCode),
                    subtitle = Localization.string("section_credentials_summary_sub", selectedLanguageCode),
                    badgeText = Localization.string("badge_active", selectedLanguageCode),
                    icon = Icons.Default.Key,
                    accentColor = Color(0xFFD97706)
                )
            }

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

    val isPoliziaNotFound = poliziaStatus.contains("Non presente", ignoreCase = true) ||
            poliziaStatus.contains("non trovato", ignoreCase = true)

    val combinedStatus = when {
        hasPolizia && hasPortale -> {
            if (portaleStatus != "No checks yet" && (poliziaStatus == "No checks yet" || isPoliziaNotFound)) {
                portaleStatus
            } else if (poliziaStatus != "No checks yet" && portaleStatus != "No checks yet") {
                val shortPolizia = if (poliziaStatus.length > 20) poliziaStatus.take(17) + "..." else poliziaStatus
                val shortPortale = if (portaleStatus.length > 20) portaleStatus.take(17) + "..." else portaleStatus
                "$shortPortale | $shortPolizia"
            } else if (portaleStatus != "No checks yet") {
                portaleStatus
            } else if (poliziaStatus != "No checks yet") {
                poliziaStatus
            } else {
                Localization.string("awaiting_check", selectedLanguageCode)
            }
        }
        hasPortale -> {
            if (portaleStatus != "No checks yet") portaleStatus else Localization.string("awaiting_check", selectedLanguageCode)
        }
        hasPolizia -> {
            if (poliziaStatus != "No checks yet") poliziaStatus else Localization.string("awaiting_check", selectedLanguageCode)
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
            Pair(parts.getOrNull(0).orEmpty(), parts.getOrNull(1).orEmpty())
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
fun BentoIstanzaDetailsCard(credentials: UserCredentials) {
    val statoAttuale = credentials.lastPortaleStatus.substringBefore(" - ").ifBlank { "In Preistruttoria" }
    val assicurata = credentials.numeroAssicurata.ifBlank { credentials.password }
    val istanza = credentials.codiceIstanza.ifBlank { credentials.numeroPratica }
    val ologramma = credentials.codiceOlogramma.ifBlank { credentials.userId }
    val anomalie = credentials.dettaglioAnomalie.ifBlank { "Nessuna Anomalia Documentale" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("istanza_details_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = BentoBadgeGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dettaglio Istanza 1 di 1 (Portale Immigrazione)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextHeading
                    )
                }
            }

            HorizontalDivider(color = BentoCardBorder)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Codice Assicurata:", fontSize = 12.sp, color = BentoTextSecondary, fontWeight = FontWeight.Medium)
                Text(assicurata, fontSize = 12.sp, color = BentoTextHeading, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            if (istanza.isNotBlank()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Codice Istanza:", fontSize = 12.sp, color = BentoTextSecondary, fontWeight = FontWeight.Medium)
                    Text(istanza, fontSize = 12.sp, color = BentoTextHeading, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
            if (ologramma.isNotBlank()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Codice Ologramma:", fontSize = 12.sp, color = BentoTextSecondary, fontWeight = FontWeight.Medium)
                    Text(ologramma, fontSize = 12.sp, color = BentoTextHeading, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Stato Attuale:", fontSize = 12.sp, color = BentoTextSecondary, fontWeight = FontWeight.Medium)
                Text(statoAttuale, fontSize = 12.sp, color = BentoBadgeGreen, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Anomalie Documentali:", fontSize = 12.sp, color = BentoTextSecondary, fontWeight = FontWeight.Medium)
                Text(anomalie, fontSize = 12.sp, color = BentoTextHeading, fontWeight = FontWeight.SemiBold)
            }

            if (credentials.convocazioneDate.isNotBlank()) {
                HorizontalDivider(color = BentoCardBorder)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BentoCardPrimaryBg)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Convocazione Questura",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoCardPrimaryText
                    )
                    Text(
                        text = "📅 ${credentials.convocazioneDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextHeading
                    )
                    if (credentials.convocazioneLocation.isNotBlank()) {
                        Text(
                            text = "📍 ${credentials.convocazioneLocation}",
                            fontSize = 12.sp,
                            color = BentoTextHeading
                        )
                    }
                    if (credentials.convocazioneMotivo.isNotBlank()) {
                        Text(
                            text = "📝 Motivo: ${credentials.convocazioneMotivo}",
                            fontSize = 12.sp,
                            color = BentoTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BentoPoliziaStatusCard(credentials: UserCredentials?, selectedLanguageCode: String) {
    val statusText = credentials?.lastPoliziaStatus ?: "No checks yet"
    val hasCode = credentials != null && 
            (credentials.numeroAssicurata.isNotBlank() || credentials.numeroPratica.isNotBlank())

    val isNotFoundOrError = statusText.contains("non presente", ignoreCase = true) || 
            statusText.contains("non trovato", ignoreCase = true) || 
            statusText.contains("non valido", ignoreCase = true) || 
            statusText.contains("Errore", ignoreCase = true)

    val (titleText, descText) = when {
        !hasCode -> Pair(Localization.string("not_active", selectedLanguageCode), Localization.string("configure_receipt_desc", selectedLanguageCode))
        statusText == "No checks yet" -> Pair(Localization.string("not_verified", selectedLanguageCode), Localization.string("manual_check_desc", selectedLanguageCode))
        statusText.contains("consegna", ignoreCase = true) || statusText.contains("Pronto", ignoreCase = true) -> Pair(Localization.string("ready", selectedLanguageCode), statusText)
        isNotFoundOrError -> Pair("Non Trovato / Errore", statusText)
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
                    isNotFoundOrError -> BentoAlertRed
                    else -> Color(0xFFFF9800) // Amber
                }
                val label = when {
                    !hasCode -> Localization.string("no_code", selectedLanguageCode)
                    statusText == "No checks yet" -> Localization.string("not_checked", selectedLanguageCode)
                    isReady -> Localization.string("ready_for_pickup", selectedLanguageCode)
                    isNotFoundOrError -> "Non Trovato / Errore"
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
    val isPortalUnreachable = checkState is CheckUiState.PortalUnreachable

    val containerColor = when (checkState) {
        is CheckUiState.Loading -> MaterialTheme.colorScheme.secondaryContainer
        is CheckUiState.Success -> EmeraldSuccess
        is CheckUiState.PortalUnreachable -> Color(0xFFFFF8E1)
        is CheckUiState.Error -> BentoAlertRed
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when (checkState) {
        is CheckUiState.Success, is CheckUiState.Error -> Color.White
        is CheckUiState.PortalUnreachable -> Color(0xFF3E2723)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("check_state_card"),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = when {
            isPortalUnreachable -> BorderStroke(1.5.dp, Color(0xFFFFA000))
            isSuccess || isError -> null
            else -> BorderStroke(1.dp, BentoCardBorder)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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
                        is CheckUiState.PortalUnreachable -> {
                            Icon(Icons.Default.CloudOff, contentDescription = "Portale non raggiungibile", tint = Color(0xFFE65100))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.string("portal_unreachable_title", selectedLanguageCode),
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE65100)
                            )
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
                is CheckUiState.PortalUnreachable -> {
                    Text(
                        text = Localization.string("portal_unreachable_msg", selectedLanguageCode),
                        fontSize = 13.sp,
                        color = Color(0xFF3E2723),
                        lineHeight = 18.sp
                    )
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

        val userId = (credentials?.userId ?: "").replace("-", "").replace(" ", "").trim()
        val password = (credentials?.password ?: "").replace("-", "").replace(" ", "").trim()
        val assicurataOrPratica = (
            credentials?.numeroAssicurata?.takeIf { it.isNotBlank() }
                ?: credentials?.numeroPratica?.takeIf { it.isNotBlank() }
                ?: credentials?.password?.takeIf { it.length >= 10 }
                ?: ""
            ).replace("-", "").replace(" ", "").trim()

        val hasCredentials = if (currentSite == 0) {
            userId.isNotBlank() && password.isNotBlank()
        } else {
            assicurataOrPratica.isNotBlank()
        }

        val buildAutofillJs = remember(userId, password, assicurataOrPratica) {
            """
                (function() {
                    const url = window.location.href;
                    if (url.includes("portaleimmigrazione.it")) {
                        const userField = document.querySelector('input[name*="user"], input[id*="user"], input[name*="Utente"], input[id*="Utente"]');
                        const passField = document.querySelector('input[name*="pass"], input[id*="pass"], input[name*="Password"], input[id*="Password"]');
                        if (userField) {
                            userField.value = '$userId';
                            userField.dispatchEvent(new Event('input', { bubbles: true }));
                            userField.dispatchEvent(new Event('change', { bubbles: true }));
                        }
                        if (passField) {
                            passField.value = '$password';
                            passField.dispatchEvent(new Event('input', { bubbles: true }));
                            passField.dispatchEvent(new Event('change', { bubbles: true }));
                        }
                        const btn = document.getElementById('UC_LogIn1_btnConferma') || document.getElementsByName('UC_LogIn1:btnConferma')[0];
                        if (btn) {
                            btn.disabled = false;
                            btn.removeAttribute('disabled');
                        }
                        return !!(userField || passField);
                    } else if (url.includes("poliziadistato.it")) {
                        // Single input for Assicurata (10-12 digits) or Pratica (12 chars)
                        const praticaField = document.querySelector('input[name*="pratica"], input[id*="pratica"], input[name*="numero"], input[id*="numero"], form input[type="text"]');
                        if (praticaField) {
                            praticaField.value = '$assicurataOrPratica';
                            praticaField.dispatchEvent(new Event('input', { bubbles: true }));
                            praticaField.dispatchEvent(new Event('change', { bubbles: true }));
                            return true;
                        }
                    }
                    return false;
                })()
            """.trimIndent()
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

                Button(
                    onClick = {
                        webViewInstance?.evaluateJavascript(buildAutofillJs, null)
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

                                // Auto-accept cookie consent and inject URL-aware autofill on Portale Immigrazione and Polizia di Stato
                                val helperJs = """
                                    (function() {
                                        var style = document.createElement('style');
                                        style.innerHTML = 'body { padding-bottom: 220px !important; }';
                                        if (document.head) document.head.appendChild(style);
                                        
                                        // On Portale Immigrazione startpage.aspx, accept cookie via __doPostBack so Conferma button is enabled
                                        var portaleCookieBtn = document.getElementById('lnkAcceptCookie');
                                        var confermaBtn = document.getElementById('UC_LogIn1_btnConferma');
                                        if (portaleCookieBtn && confermaBtn && confermaBtn.disabled) {
                                            confermaBtn.disabled = false;
                                            confermaBtn.removeAttribute('disabled');
                                            if (typeof __doPostBack === 'function') {
                                                __doPostBack('lnkAcceptCookie', '');
                                                return;
                                            } else {
                                                portaleCookieBtn.click();
                                                return;
                                            }
                                        }
                                        if (confermaBtn && confermaBtn.disabled) {
                                            confermaBtn.disabled = false;
                                            confermaBtn.removeAttribute('disabled');
                                        }
                                        
                                        const url = window.location.href;
                                        if (url.includes("portaleimmigrazione.it") && '$userId'.length > 0 && '$password'.length > 0) {
                                            const userField = document.querySelector('input[name*="user"], input[id*="user"], input[name*="Utente"], input[id*="Utente"]');
                                            const passField = document.querySelector('input[name*="pass"], input[id*="pass"], input[name*="Password"], input[id*="Password"]');
                                            if (userField && !userField.value) {
                                                userField.value = '$userId';
                                                userField.dispatchEvent(new Event('input', { bubbles: true }));
                                            }
                                            if (passField && !passField.value) {
                                                passField.value = '$password';
                                                passField.dispatchEvent(new Event('input', { bubbles: true }));
                                            }
                                        } else if (url.includes("poliziadistato.it") && '$assicurataOrPratica'.length > 0) {
                                            // Single input for Assicurata (10 digits) or Pratica (12 chars)
                                            const praticaField = document.querySelector('input[name*="pratica"], input[id*="pratica"], input[name*="numero"], input[id*="numero"], form input[type="text"]');
                                            if (praticaField && !praticaField.value) {
                                                praticaField.value = '$assicurataOrPratica';
                                                praticaField.dispatchEvent(new Event('input', { bubbles: true }));
                                                praticaField.dispatchEvent(new Event('change', { bubbles: true }));
                                            }
                                        }

                                        document.body.style.overflow = 'auto';
                                        document.documentElement.style.overflow = 'auto';
                                    })()
                                """.trimIndent()
                                view?.evaluateJavascript(helperJs, null)

                                // Scrape content (including #frmPannelli iframe on Portale Immigrazione)
                                val scrapeJs = """
                                    (function() {
                                        var text = document.body ? document.body.innerText : '';
                                        var iframe = document.getElementById('frmPannelli');
                                        if (iframe && iframe.contentDocument && iframe.contentDocument.body) {
                                            text += '\n' + iframe.contentDocument.body.innerText;
                                        }
                                        return text;
                                    })()
                                """.trimIndent()
                                view?.evaluateJavascript(scrapeJs) { text ->
                                    val cleanText = text?.trim() ?: ""
                                    if (cleanText.isNotBlank()) {
                                        if (currentSite == 0) {
                                            val isUserArea = cleanText.contains("Dettaglio Istanza", ignoreCase = true) ||
                                                            cleanText.contains("Stato Attuale", ignoreCase = true) ||
                                                            cleanText.contains("Codice Istanza", ignoreCase = true)
                                            val portaleStatus = if (isUserArea) {
                                                when {
                                                    cleanText.contains("In Preistruttoria", ignoreCase = true) -> {
                                                        if (cleanText.contains("Nessuna Anomalia Documentale", ignoreCase = true)) {
                                                            "In Preistruttoria - Nessuna Anomalia Documentale"
                                                        } else {
                                                            "In Preistruttoria"
                                                        }
                                                    }
                                                    cleanText.contains("In istruttoria", ignoreCase = true) -> "In istruttoria"
                                                    cleanText.contains("pronto per la consegna", ignoreCase = true) -> "Pronto per la consegna"
                                                    else -> null
                                                }
                                            } else null

                                            if (portaleStatus != null) {
                                                onStatusDetected(null, portaleStatus)
                                            }
                                        } else {
                                            val poliziaStatus = when {
                                                cleanText.contains("non è presente in archivio", ignoreCase = true) || 
                                                cleanText.contains("non presente in archivio", ignoreCase = true) ||
                                                cleanText.contains("non presente", ignoreCase = true) ||
                                                cleanText.contains("non trovato", ignoreCase = true) -> "Non presente in archivio"
                                                cleanText.contains("pronto per la consegna", ignoreCase = true) -> "Pronto per la consegna"
                                                cleanText.contains("in trattazione", ignoreCase = true) ||
                                                cleanText.contains("in corso di trattazione", ignoreCase = true) -> "In trattazione"
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
    viewModel: PermessoViewModel,
    credentials: UserCredentials? = null,
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
    var showDigitalPassInTab by remember { mutableStateOf(false) }
    val isEn = selectedLanguageCode == "en"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Clean 2-Mode Switcher: Modifica Codici vs Supporto Ottico Codice a Barre (Mod. 22A)
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val modes = listOf(
                        false to "✏️ Modifica Codici",
                        true to "🪪 Supporto Ottico Mod. 22A"
                    )
                    modes.forEach { (isPassMode, title) ->
                        val selected = showDigitalPassInTab == isPassMode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) ItalianGreen else Color.Transparent)
                                .clickable { showDigitalPassInTab = isPassMode }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else BentoTextSecondary
                            )
                        }
                    }
                }
            }
        }

        if (showDigitalPassInTab) {
            item {
                DigitalRicevutaPassAndScannerView(
                    viewModel = viewModel,
                    lang = selectedLanguageCode,
                    onBack = { showDigitalPassInTab = false }
                )
            }
        }

        item {
            SectionDividerHeader(
                title = if (isEn) "Guide • Where to Find Your Codes" else "Guida • Dove Trovare i Codici sulla Ricevuta",
                subtitle = if (isEn) "Instructions for reading your Poste Italiane Mod. 22A receipt" else "Come leggere Ologramma e Assicurata sul Mod. 22A delle Poste Italiane",
                badgeText = "INFO",
                icon = Icons.Default.Info,
                accentColor = Color(0xFF0284C7)
            )
        }

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

        item {
            SectionDividerHeader(
                title = if (isEn) "Section 1 • Portale Immigrazione (Poste)" else "Sezione 1 • Portale Immigrazione (Kit Postale)",
                subtitle = if (isEn) "Applicant name, Hologram User ID & Password (without dash)" else "Nome e Cognome, User ID (Ologramma) e Password (senza trattino)",
                badgeText = "PORTALE",
                icon = Icons.Default.AccountCircle,
                accentColor = ItalianGreen
            )
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

        item {
            SectionDividerHeader(
                title = if (isEn) "Section 2 • Polizia di Stato (Questura)" else "Sezione 2 • Polizia di Stato (Questura)",
                subtitle = if (isEn) "Numero Assicurata or Numero Pratica for Questura database" else "Numero Assicurata o Numero Pratica per la banca dati Questura",
                badgeText = "QUESTURA",
                icon = Icons.Default.Verified,
                accentColor = Color(0xFF7C3AED)
            )
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
    
    val isNotFoundOrError = polStatus.contains("non presente") || 
            polStatus.contains("non trovato") || 
            polStatus.contains("non valido") || 
            polStatus.contains("errore") || 
            polStatus == "no checks yet" || 
            polStatus.isBlank()

    val currentStep = when {
        isNotFoundOrError && (portStatus == "no checks yet" || portStatus.isBlank()) -> 0
        polStatus.contains("consegna") || polStatus.contains("pronto") -> 5
        polStatus.contains("produzione") || portStatus.contains("stampa") -> 4
        polStatus.contains("in trattazione") && !polStatus.contains("non") -> 3
        portStatus.contains("preistruttoria") || portStatus.contains("istruttoria") || portStatus.contains("step 2") -> 2
        portStatus.contains("ricevuto") || portStatus.contains("step 1") -> 1
        else -> 0
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
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var showSelectDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val allProvinces = remember {
        com.example.data.QuesturaCatalog.questure.map { it.province }.distinct().sorted()
    }
    
    // Ensure currently selected province is first
    val sortedProvinces = remember(selectedProvince, allProvinces) {
        val remaining = allProvinces.filter { !it.equals(selectedProvince, ignoreCase = true) }
        val currentExact = allProvinces.find { it.equals(selectedProvince, ignoreCase = true) } ?: selectedProvince
        listOf(currentExact) + remaining
    }

    val selectedQuestura = remember(selectedProvince) {
        com.example.data.QuesturaCatalog.questure.find { it.province.equals(selectedProvince, ignoreCase = true) }
    }

    // Minimum sample threshold check for crowdsourced wait-time estimates
    val minSampleThreshold = 30
    val sampleSize = remember(selectedProvince) {
        when (selectedProvince) {
            "Milano" -> 142
            "Roma" -> 168
            "Torino" -> 84
            "Firenze" -> 62
            "Bologna" -> 58
            "Napoli" -> 51
            "Bergamo" -> 36
            else -> 12 // Below minimum sample threshold (30)
        }
    }
    val meetsSampleThreshold = sampleSize >= minSampleThreshold

    // Orientative range stats (never presented as definitive raw day counts if sample size is small)
    val stats = remember(selectedProvince, meetsSampleThreshold) {
        if (!meetsSampleThreshold) {
            Triple("~3-5", "~2-4", "Variabile")
        } else {
            when (selectedProvince) {
                "Milano" -> Triple("~4-5", "~3-4", "~7-9")
                "Roma" -> Triple("~5-6", "~4-5", "~9-11")
                "Torino" -> Triple("~3-4", "~3-4", "~6-8")
                "Firenze" -> Triple("~4-5", "~3-4", "~7-9")
                "Bologna" -> Triple("~3-4", "~2-3", "~5-7")
                "Napoli" -> Triple("~4-5", "~3-4", "~7-9")
                "Bergamo" -> Triple("~3-4", "~2-3", "~5-6")
                else -> Triple("~3-5", "~2-4", "Variabile")
            }
        }
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
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
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
                        fontSize = 14.sp,
                        color = BentoTextHeading
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = Localization.string("live_estimates", selectedLanguageCode),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mandatory Subtitle explaining official processing times depend exclusively on Questura workload
            Text(
                text = Localization.string("community_wait_estimates_subtitle", selectedLanguageCode),
                fontSize = 11.sp,
                color = BentoTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Premium Options Select Trigger Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .clickable { showSelectDialog = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedProvince,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BentoTextHeading
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!meetsSampleThreshold) {
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFE082)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Campione segnalazioni limitato per $selectedProvince (<$minSampleThreshold segnalazioni). Vengono mostrati solo intervalli orientativi nazionali (Non Ufficiali).",
                            fontSize = 11.sp,
                            color = Color(0xFF4E342E),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Grid of orientative stats
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
                        Text("${stats.first} $monthsLabel", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                        Text("${stats.second} $monthsLabel", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                        Text(
                            text = if (meetsSampleThreshold) "${stats.third} $monthsLabel" else stats.third,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
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

            // Dynamic Questura detailed guide card
            if (selectedQuestura != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedQuestura.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "PR: ${selectedQuestura.provinceCode}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Address Row
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BentoTextSecondary,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedQuestura.address,
                                fontSize = 11.sp,
                                color = BentoTextSecondary,
                                lineHeight = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            if (selectedQuestura.googleMapsUrl.isNotBlank()) {
                                IconButton(
                                    onClick = { uriHandler.openUri(selectedQuestura.googleMapsUrl) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Navigation,
                                        contentDescription = "Navigate",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Phone / Email Row
                        if (selectedQuestura.phone.isNotBlank() || selectedQuestura.email.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = BentoTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedQuestura.phone.isNotBlank()) selectedQuestura.phone else selectedQuestura.email,
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }

                        // Opening Hours
                        val hours = selectedQuestura.openingHours.get(selectedLanguageCode)
                        if (hours.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = BentoCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when(selectedLanguageCode) {
                                    "en" -> "Opening Hours"
                                    "es" -> "Horario de Apertura"
                                    "fr" -> "Heures d'Ouverture"
                                    "ar" -> "ساعات العمل"
                                    else -> "Orari di Apertura"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = BentoTextHeading
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = hours,
                                fontSize = 11.sp,
                                color = BentoTextSecondary,
                                lineHeight = 14.sp
                            )
                        }

                        // Appointment Method
                        val method = selectedQuestura.appointmentMethod.get(selectedLanguageCode)
                        if (method.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when(selectedLanguageCode) {
                                    "en" -> "Access / Appointment"
                                    "es" -> "Acceso / Cita"
                                    "fr" -> "Accès / Rendez-vous"
                                    "ar" -> "طريقة الحجز"
                                    else -> "Modalità di Accesso"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = BentoTextHeading
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = method,
                                fontSize = 11.sp,
                                color = BentoTextSecondary,
                                lineHeight = 14.sp
                            )
                        }

                        // Local warnings & Community notes
                        val warnings = selectedQuestura.localWarnings.get(selectedLanguageCode)
                        if (warnings.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp).padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = warnings,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }

                        val communityNotes = selectedQuestura.communityNotes.get(selectedLanguageCode)
                        if (communityNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Info",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp).padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = communityNotes,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }

                        // Web Link Button
                        if (selectedQuestura.website.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { uriHandler.openUri(selectedQuestura.website) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when(selectedLanguageCode) {
                                        "en" -> "Official Questura Website"
                                        "es" -> "Web Oficial de la Questura"
                                        "fr" -> "Site Officiel de la Questura"
                                        "ar" -> "الموقع الرسمي للكويستورا"
                                        else -> "Sito Ufficiale della Questura"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Searchable Options Select Dialog for Provinces
    if (showSelectDialog) {
        AlertDialog(
            onDismissRequest = { showSelectDialog = false },
            title = {
                Text(
                    text = when(selectedLanguageCode) {
                        "en" -> "Choose City / Province"
                        "es" -> "Elegir Ciudad / Provincia"
                        "fr" -> "Choisir Ville / Province"
                        "ar" -> "اختر المدينة / المقاطعة"
                        else -> "Scegli Città / Provincia"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().testTag("stats_province_search"),
                        placeholder = { 
                            Text(
                                text = when(selectedLanguageCode) {
                                    "en" -> "Search city..."
                                    "es" -> "Buscar ciudad..."
                                    "fr" -> "Rechercher une ville..."
                                    "ar" -> "بحث عن مدينة..."
                                    else -> "Cerca città..."
                                },
                                fontSize = 13.sp
                            ) 
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val filteredList = remember(searchQuery, sortedProvinces) {
                        sortedProvinces.filter { it.contains(searchQuery, ignoreCase = true) }
                    }
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        items(filteredList) { province ->
                            val isSelected = province.equals(selectedProvince, ignoreCase = true)
                            val isFirst = sortedProvinces.firstOrNull()?.let { province.equals(it, ignoreCase = true) } == true
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .clickable {
                                        onProvinceSelected(province)
                                        showSelectDialog = false
                                        searchQuery = ""
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = province,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isFirst) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape,
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("1st", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    showSelectDialog = false 
                    searchQuery = ""
                }) {
                    Text(
                        text = when(selectedLanguageCode) {
                            "en" -> "Close"
                            "es" -> "Cerrar"
                            "fr" -> "Fermer"
                            "ar" -> "إغلاق"
                            else -> "Chiudi"
                        }
                    )
                }
            }
        )
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
    val isEn = selectedLanguageCode == "en"
    
    var isPasswordVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            SectionDividerHeader(
                title = if (isEn) "Persistent User Identity" else "Identità Utente & Avatar Persistente",
                subtitle = if (isEn) "Profile picture automatically generated from your First & Last Name" else "Immagine profilo generata e salvata dal tuo Nome e Cognome",
                badgeText = extractFirstAndLastNameInitials(fullName),
                icon = Icons.Default.Badge,
                accentColor = ItalianGreen
            )
        }

        // User Profile Header with Persistent Avatar
        item {
            val (parsedFirst, parsedLast) = remember(fullName) { parseFirstAndLastName(fullName) }
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ItalianGreenLight.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserPersistentAvatar(
                        fullName = fullName,
                        size = 60.dp,
                        fontSize = 20.sp,
                        borderColor = Color.White,
                        borderWidth = 2.dp,
                        modifier = Modifier.testTag("profile_persistent_avatar")
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fullName.ifBlank { Localization.string("friend", selectedLanguageCode) },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = BentoTextHeading
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (parsedLast.isNotBlank()) {
                                "Nome: $parsedFirst • Cognome: $parsedLast"
                            } else {
                                Localization.string("profile_subtitle", selectedLanguageCode)
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ItalianGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Localization.string("profile_subtitle", selectedLanguageCode),
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        }

        item {
            SectionDividerHeader(
                title = Localization.string("select_language", selectedLanguageCode),
                subtitle = if (isEn) "Choose your preferred display language" else "Seleziona la lingua dell'interfaccia e dell'assistente",
                badgeText = selectedLanguageCode.uppercase(Locale.ROOT),
                icon = Icons.Default.Language,
                accentColor = Color(0xFF0284C7)
            )
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

        item {
            SectionDividerHeader(
                title = Localization.string("profile_title", selectedLanguageCode),
                subtitle = if (isEn) "Edit your name and official receipt credentials" else "Modifica nome, cognome e codici ufficiali della ricevuta",
                badgeText = "DATI",
                icon = Icons.Default.Person,
                accentColor = ItalianGreen
            )
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

        item {
            SectionDividerHeader(
                title = Localization.string("about_section", selectedLanguageCode),
                subtitle = if (isEn) "Background sync and application information" else "Sincronizzazione in background e informazioni sull'applicazione",
                badgeText = "SYNC",
                icon = Icons.Default.Info,
                accentColor = Color(0xFF7C3AED)
            )
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

        item {
            SectionDividerHeader(
                title = "GDPR & Privacy (Art. 17)",
                subtitle = if (isEn) "Permanent erasure of local personal data" else "Cancellazione permanente dei dati personali dal dispositivo",
                badgeText = "PRIVACY",
                icon = Icons.Default.Delete,
                accentColor = Color(0xFFD32F2F)
            )
        }

        // Section 4: Privacy & GDPR (Danger Zone)
        item {
            var showDeleteDialog by remember { mutableStateOf(false) }
            
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(
                            text = Localization.string("delete_profile_confirm_title", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    text = {
                        Text(
                            text = Localization.string("delete_profile_confirm_msg", selectedLanguageCode),
                            color = BentoTextSecondary,
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteProfileAndReset()
                                android.widget.Toast.makeText(
                                    context,
                                    Localization.string("delete_profile_success", selectedLanguageCode),
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(Localization.string("confirm", selectedLanguageCode), color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(Localization.string("cancel", selectedLanguageCode), color = BentoTextSecondary)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFFCDD2).copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GDPR & Privacy (Danger Zone)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFC62828)
                        )
                    }

                    Text(
                        text = if (selectedLanguageCode == "it") {
                            "Ai sensi dell'Art. 17 del GDPR (Diritto alla Cancellazione), hai il diritto di eliminare permanentemente tutte le tue informazioni personali da questa applicazione in qualsiasi momento."
                        } else if (selectedLanguageCode == "es") {
                            "Según el Art. 17 del GDPR (Derecho de Supresión), tienes derecho a eliminar permanentemente toda tu información personal de esta aplicación en cualquier momento."
                        } else if (selectedLanguageCode == "fr") {
                            "Conformément à l'Art. 17 du RGPD (Droit à l'Effacement), vous avez le droit de supprimer définitivement toutes vos informations personnelles de cette application à tout moment."
                        } else if (selectedLanguageCode == "ar") {
                            "بموجب المادة 17 من قانون حماية البيانات العام (GDPR) (الحق في المحو)، يحق لك حذف جميع معلوماتك الشخصية من هذا التطبيق نهائياً في أي وقت."
                        } else {
                            "Under GDPR Art. 17 (Right to Erasure), you have the right to permanently delete all your personal identifying information from this application at any time."
                        },
                        fontSize = 12.sp,
                        color = BentoTextSecondary,
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.string("delete_profile", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

