package com.example.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import java.io.File
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.background.CheckInterval
import com.example.util.ReceiptScannerHelper
import kotlinx.coroutines.launch
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.data.SavedCredentialVaultEntry
import com.example.data.StatusHistory
import com.example.data.UserCredentials
import com.example.ui.theme.*
import com.example.util.BiometricAuthHelper
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
    val credentials by viewModel.credentials.collectAsStateWithLifecycle()
    val historyList by viewModel.history.collectAsStateWithLifecycle()
    val checkState by viewModel.checkState.collectAsStateWithLifecycle()
    val selectedLanguageCode by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isInstitutionalDemoMode.collectAsStateWithLifecycle()

    var currentRoute by remember { mutableStateOf(AppleScreenRoute.STATO_HOME) }
    var isBurgerDrawerOpen by remember { mutableStateOf(false) }

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
            currentRoute = AppleScreenRoute.STATO_HOME
            isBurgerDrawerOpen = false
        }
    }

    // Handle physical back button for drawer and sub-screens
    val isPrimaryTab = currentRoute == AppleScreenRoute.STATO_HOME ||
        currentRoute == AppleScreenRoute.STRUMENTI_HUB ||
        currentRoute == AppleScreenRoute.SOFIA_CHAT ||
        currentRoute == AppleScreenRoute.PROFILO_PASS

    androidx.activity.compose.BackHandler(enabled = isBurgerDrawerOpen || !isPrimaryTab || currentRoute != AppleScreenRoute.STATO_HOME) {
        when {
            isBurgerDrawerOpen -> isBurgerDrawerOpen = false
            !isPrimaryTab -> currentRoute = AppleScreenRoute.STRUMENTI_HUB
            currentRoute != AppleScreenRoute.STATO_HOME -> currentRoute = AppleScreenRoute.STATO_HOME
        }
    }

    val layoutDirection = if (selectedLanguageCode == "ar") {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        if (!onboardingCompleted) {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    viewModel.completeOnboarding()
                    currentRoute = AppleScreenRoute.STATO_HOME
                }
            )
        } else {
            Box(modifier = modifier.fillMaxSize()) {
                Scaffold(
                    topBar = {
                        AppleTopHeaderBar(
                            currentRoute = currentRoute,
                            selectedLanguageCode = selectedLanguageCode,
                            credentials = credentials,
                            onSelectLanguage = { langCode -> viewModel.selectLanguage(langCode) },
                            onBackClick = if (!isPrimaryTab) {
                                { currentRoute = AppleScreenRoute.STRUMENTI_HUB }
                            } else null,
                            onOpenBurgerMenu = { isBurgerDrawerOpen = true }
                        )
                    },
                    bottomBar = {
                        Column {
                            HorizontalDivider(color = AppleBorderHairline, thickness = 1.dp)
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == AppleScreenRoute.STATO_HOME,
                                    onClick = { currentRoute = AppleScreenRoute.STATO_HOME },
                                    icon = { Icon(Icons.Default.Verified, contentDescription = "Stato") },
                                    label = {
                                        Text(
                                            text = Localization.string("tab_status", selectedLanguageCode),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = VividEmerald,
                                        selectedTextColor = VividEmerald,
                                        indicatorColor = VividEmeraldTint,
                                        unselectedIconColor = AppleTextMuted,
                                        unselectedTextColor = AppleTextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_dashboard")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == AppleScreenRoute.STRUMENTI_HUB || !isPrimaryTab,
                                    onClick = { currentRoute = AppleScreenRoute.STRUMENTI_HUB },
                                    icon = { Icon(Icons.Default.GridView, contentDescription = "Strumenti") },
                                    label = {
                                        Text(
                                            text = when (selectedLanguageCode) {
                                                "en" -> "Tools"
                                                "ar" -> "الأدوات"
                                                "fr" -> "Outils"
                                                "es" -> "Herramientas"
                                                else -> "Strumenti"
                                            },
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricBlue,
                                        selectedTextColor = ElectricBlue,
                                        indicatorColor = ElectricBlueTint,
                                        unselectedIconColor = AppleTextMuted,
                                        unselectedTextColor = AppleTextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_strumenti")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == AppleScreenRoute.SOFIA_CHAT,
                                    onClick = { currentRoute = AppleScreenRoute.SOFIA_CHAT },
                                    icon = { Icon(Icons.Default.Gavel, contentDescription = "Consulente") },
                                    label = {
                                        Text(
                                            text = Localization.string("tab_sofia", selectedLanguageCode),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SiriViolet,
                                        selectedTextColor = SiriViolet,
                                        indicatorColor = SiriVioletTint,
                                        unselectedIconColor = AppleTextMuted,
                                        unselectedTextColor = AppleTextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_sofia")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == AppleScreenRoute.PROFILO_PASS,
                                    onClick = { currentRoute = AppleScreenRoute.PROFILO_PASS },
                                    icon = {
                                        if (!credentials?.fullName.isNullOrBlank()) {
                                            UserPersistentAvatar(
                                                fullName = credentials?.fullName,
                                                size = 26.dp,
                                                fontSize = 10.sp,
                                                isSelected = currentRoute == AppleScreenRoute.PROFILO_PASS,
                                                borderColor = if (currentRoute == AppleScreenRoute.PROFILO_PASS) VividEmerald else AppleBorderHairline,
                                                borderWidth = if (currentRoute == AppleScreenRoute.PROFILO_PASS) 2.dp else 1.dp,
                                                modifier = Modifier.testTag("navbar_profile_avatar")
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Badge,
                                                contentDescription = "Profilo",
                                                modifier = Modifier.testTag("navbar_profile_avatar")
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = Localization.string("tab_profile", selectedLanguageCode),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = VividEmerald,
                                        selectedTextColor = VividEmerald,
                                        indicatorColor = VividEmeraldTint,
                                        unselectedIconColor = AppleTextMuted,
                                        unselectedTextColor = AppleTextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_profile")
                                )
                            }
                        }
                    },
                    // Rule 2: NO duplicate Floating Action Button (FAB)
                    floatingActionButton = {},
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        val selectedProvince by viewModel.selectedProvince.collectAsStateWithLifecycle()
                        val appointmentDate by viewModel.appointmentDate.collectAsStateWithLifecycle()
                        val appointmentTime by viewModel.appointmentTime.collectAsStateWithLifecycle()
                        val appointmentLocation by viewModel.appointmentLocation.collectAsStateWithLifecycle()
                        val checkedDocs by viewModel.checkedDocs.collectAsStateWithLifecycle()

                        when (currentRoute) {
                            AppleScreenRoute.STATO_HOME -> {
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
                                    onGoToCredentials = { currentRoute = AppleScreenRoute.PROFILO_PASS },
                                    onOpenGuides = { currentRoute = AppleScreenRoute.GUIDE_IMMIGRAZIONE },
                                    onChooseQuestura = { currentRoute = AppleScreenRoute.ESPLORA_QUESTURE },
                                    onNavigateRoute = { currentRoute = it },
                                    selectedLanguageCode = selectedLanguageCode
                                )
                            }

                            AppleScreenRoute.STRUMENTI_HUB -> {
                                AppleStrumentiVividHubScreen(
                                    selectedLanguageCode = selectedLanguageCode,
                                    onOpenSofiaAi = { currentRoute = AppleScreenRoute.SOFIA_CHAT },
                                    onOpenPecGenerator = { currentRoute = AppleScreenRoute.SOLLECITO_PEC },
                                    onOpenTravelSimulator = { currentRoute = AppleScreenRoute.SIMULATORE_VIAGGI },
                                    onOpenQuestureMap = { currentRoute = AppleScreenRoute.ESPLORA_QUESTURE },
                                    onOpenChecklist = { currentRoute = AppleScreenRoute.CHECKLIST_FOTOSEGNALAMENTO },
                                    onOpenPortalWebView = { currentRoute = AppleScreenRoute.PORTALE_WEB },
                                    onOpenRedditoSimulator = { currentRoute = AppleScreenRoute.REDDITO_SIMULATOR },
                                    onOpenImmigrationGuides = { currentRoute = AppleScreenRoute.GUIDE_IMMIGRAZIONE }
                                )
                            }

                            AppleScreenRoute.PROFILO_PASS -> {
                                ProfileTab(
                                    viewModel = viewModel,
                                    selectedLanguageCode = selectedLanguageCode
                                )
                            }

                            AppleScreenRoute.SOFIA_CHAT -> {
                                val userFullName = credentials?.fullName?.takeIf { it.isNotBlank() }.orEmpty()
                                val sofiaViewModel: SofiaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                                SofiaScreen(
                                    viewModel = sofiaViewModel,
                                    userFullName = userFullName,
                                    selectedLanguageCode = selectedLanguageCode,
                                    onNavigateAction = { action ->
                                        when (action) {
                                            SofiaActionEvent.ExplainStatus,
                                            SofiaActionEvent.OpenTimeline,
                                            SofiaActionEvent.ShowAppointment -> {
                                                currentRoute = AppleScreenRoute.STATO_HOME
                                            }
                                            SofiaActionEvent.ShowRequiredDocuments -> {
                                                currentRoute = AppleScreenRoute.CHECKLIST_FOTOSEGNALAMENTO
                                            }
                                            SofiaActionEvent.ShowMyQuestura -> {
                                                currentRoute = AppleScreenRoute.ESPLORA_QUESTURE
                                            }
                                            SofiaActionEvent.OpenOfficialWebsite -> {
                                                currentRoute = AppleScreenRoute.PORTALE_WEB
                                            }
                                            SofiaActionEvent.OpenRenewalGuide,
                                            SofiaActionEvent.OpenBookmarks,
                                            SofiaActionEvent.OpenImmigrationGuides,
                                            SofiaActionEvent.OpenMyGuide -> {
                                                currentRoute = AppleScreenRoute.GUIDE_IMMIGRAZIONE
                                            }
                                            SofiaActionEvent.GoToProfile -> {
                                                currentRoute = AppleScreenRoute.PROFILO_PASS
                                            }
                                        }
                                    }
                                )
                            }

                            AppleScreenRoute.SOLLECITO_PEC -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(top = 14.dp, bottom = 88.dp)
                                ) {
                                    item {
                                        PecSollecitoGeneratorView(
                                            viewModel = viewModel,
                                            lang = selectedLanguageCode,
                                            onBack = { currentRoute = AppleScreenRoute.STRUMENTI_HUB }
                                        )
                                    }
                                }
                            }

                            AppleScreenRoute.SIMULATORE_VIAGGI -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(top = 14.dp, bottom = 88.dp)
                                ) {
                                    item {
                                        TravelRegulationsSimulatorView(
                                            lang = selectedLanguageCode,
                                            onBack = { currentRoute = AppleScreenRoute.STRUMENTI_HUB },
                                            enableInternalScroll = false
                                        )
                                    }
                                }
                            }

                            AppleScreenRoute.ESPLORA_QUESTURE -> {
                                QuestureExplorerScreen(
                                    onBack = { currentRoute = AppleScreenRoute.STRUMENTI_HUB },
                                    onSelectQuestura = { questura ->
                                        viewModel.selectQuesturaAndProvince(questura.id, questura.provinceCode, questura.province)
                                        currentRoute = AppleScreenRoute.STATO_HOME
                                    },
                                    selectedLanguageCode = selectedLanguageCode
                                )
                            }

                            AppleScreenRoute.CHECKLIST_FOTOSEGNALAMENTO -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp),
                                    contentPadding = PaddingValues(top = 14.dp, bottom = 88.dp)
                                ) {
                                    item {
                                        AppointmentVaultView(
                                            viewModel = viewModel,
                                            lang = selectedLanguageCode,
                                            onBack = { currentRoute = AppleScreenRoute.STRUMENTI_HUB },
                                            enableInternalScroll = false
                                        )
                                    }
                                    item {
                                        BentoAppointmentsAndVaultCard(
                                            appointmentDate = appointmentDate,
                                            appointmentTime = appointmentTime,
                                            appointmentLocation = appointmentLocation,
                                            onUpdateAppointment = { d, t, l -> viewModel.updateAppointment(d, t, l) },
                                            checkedDocs = checkedDocs,
                                            onToggleDocument = { viewModel.toggleDocument(it) },
                                            selectedLanguageCode = selectedLanguageCode
                                        )
                                    }
                                }
                            }

                            AppleScreenRoute.PORTALE_WEB -> {
                                PortalWebViewTab(
                                    credentials = credentials,
                                    onStatusDetected = { poliziaStatus, portaleStatus ->
                                        viewModel.updateLoggedStatus(poliziaStatus, portaleStatus)
                                    },
                                    selectedLanguageCode = selectedLanguageCode
                                )
                            }

                            AppleScreenRoute.REDDITO_SIMULATOR -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(top = 14.dp, bottom = 88.dp)
                                ) {
                                    item {
                                        RedditoCalculatorSimulatorView(
                                            lang = selectedLanguageCode,
                                            onBack = { currentRoute = AppleScreenRoute.STRUMENTI_HUB },
                                            enableInternalScroll = false
                                        )
                                    }
                                }
                            }

                            AppleScreenRoute.GUIDE_IMMIGRAZIONE -> {
                                ImmigrationGuidesScreen(
                                    viewModel = viewModel,
                                    onBack = {
                                        viewModel.activeGuideId.value = null
                                        currentRoute = AppleScreenRoute.STRUMENTI_HUB
                                    },
                                    selectedLanguageCode = selectedLanguageCode
                                )
                            }
                        }
                    }
                }

                // SCREEN 8: Global Slide-Over Burger Drawer (Slides from Right over any screen)
                SlideOverBurgerDrawer(
                    isDrawerOpen = isBurgerDrawerOpen,
                    onClose = { isBurgerDrawerOpen = false },
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute -> currentRoute = targetRoute },
                    credentials = credentials,
                    selectedLanguageCode = selectedLanguageCode,
                    onSelectLanguage = { langCode -> viewModel.selectLanguage(langCode) },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { enabled -> viewModel.setDarkMode(enabled) },
                    isDemoMode = isDemoMode,
                    onToggleDemoMode = { enabled -> viewModel.setInstitutionalDemoMode(enabled) }
                )
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
    onNavigateRoute: (AppleScreenRoute) -> Unit = {},
    onSyncVerifiedDossier: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    selectedLanguageCode: String
) {
    val hasCode = credentials != null &&
            (credentials.numeroAssicurata.isNotBlank() ||
             credentials.numeroPratica.isNotBlank() ||
             (credentials.userId.isNotBlank() && credentials.password.isNotBlank()))

    var showAllHistory by remember { mutableStateOf(false) }
    var showCommunityStats by remember { mutableStateOf(false) }
    val cooldownSeconds by viewModel.cooldownSecondsRemaining.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isInstitutionalDemoMode.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp)
    ) {
        // 1. Clean iOS 18 Greeting Header (Rule 3: No "Test Notifica" button)
        item {
            val name = credentials?.fullName?.trim().orEmpty()
            val greetingText = if (name.isNotBlank()) {
                Localization.string("greeting_user", selectedLanguageCode, name)
            } else {
                Localization.string("greeting_general", selectedLanguageCode)
            }
            val lastCheckedStr = if (credentials != null && credentials.lastCheckedTime != 0L) {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                Localization.string("last_checked", selectedLanguageCode, sdf.format(Date(credentials.lastCheckedTime)))
            } else {
                Localization.string("no_checks", selectedLanguageCode)
            }

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
                        size = 46.dp,
                        fontSize = 15.sp,
                        borderColor = VividEmerald,
                        borderWidth = 2.dp,
                        onClick = onGoToCredentials
                    )
                    Column {
                        Text(
                            text = greetingText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.30).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = lastCheckedStr,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Province Quick Badge
                Surface(
                    onClick = onChooseQuestura,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, AppleBorderHairline),
                    shadowElevation = 1.dp,
                    modifier = Modifier.appleBounceClick()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = SunsetTangerine,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = selectedProvince,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 2. SCREEN 1: HERO STATUS WIDGET (22dp White Squircle, Glowing Pill Badge, Dossier Number, Plain-Language Guidance)
        item {
            AppleHeroStatusWidget(
                credentials = credentials,
                isDemoMode = isDemoMode,
                selectedLanguageCode = selectedLanguageCode,
                onEditCredentials = onGoToCredentials
            )
        }

        // 3. SCREEN 1: SINGLE PRIMARY ACTION BUTTON
        item {
            val isCooldownActive = cooldownSeconds > 0
            val requiresSetup = !hasCode && !isDemoMode
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (requiresSetup) {
                            onResetCheckState()
                            onGoToCredentials()
                        } else {
                            onCheckNow()
                        }
                    },
                    enabled = !isCooldownActive && checkState !is CheckUiState.Loading,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (requiresSetup) ElectricBlue else VividEmerald,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp
                    ),
                    contentPadding = PaddingValues(vertical = 15.dp, horizontal = 22.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .appleBounceClick()
                        .testTag("check_status_btn")
                ) {
                    Icon(
                        imageVector = when {
                            isCooldownActive -> Icons.Default.HourglassBottom
                            requiresSetup -> Icons.Default.QrCodeScanner
                            else -> Icons.Default.Sync
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when {
                            isCooldownActive -> Localization.string("check_cooldown_button", selectedLanguageCode, cooldownSeconds)
                            requiresSetup -> Localization.string("config_receipt_btn", selectedLanguageCode)
                            else -> "Verifica Stato Live (Polizia & Portale)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = (-0.2).sp
                    )
                }

                if (isCooldownActive) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Localization.string("check_cooldown_subtitle", selectedLanguageCode),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("check_cooldown_subtitle")
                    )
                }
            }
        }

        // Live Check Feedback Card (when checking or completed)
        if (checkState !is CheckUiState.Idle) {
            item {
                CheckStateCard(
                    checkState = checkState,
                    onDismiss = onResetCheckState,
                    selectedLanguageCode = selectedLanguageCode
                )
            }
        }

        // 4. SCREEN 1: APPOINTMENT ALERT CARD (Sunset Tangerine #FF9500 Apple Wallet-style pass alert)
        item {
            AppleAppointmentAlertCard(
                appointmentDate = appointmentDate.ifBlank { credentials?.convocazioneDate.orEmpty() },
                appointmentTime = appointmentTime,
                appointmentLocation = appointmentLocation.ifBlank { credentials?.convocazioneLocation.orEmpty() },
                selectedProvince = selectedProvince,
                onUpdateAppointment = onUpdateAppointment
            )
        }

        // 5. SCREEN 1: 4-STAGE VERTICAL STEPPER WITH BREATHING EMERALD NODE
        item {
            AppleVerticalStepperCard(
                credentials = credentials,
                selectedLanguageCode = selectedLanguageCode
            )
        }

        // Real Portale Immigrazione Dettaglio Istanza Card (when live details exist)
        if (credentials != null && (credentials.codiceIstanza.isNotBlank() || credentials.convocazioneDate.isNotBlank() || credentials.dettaglioAnomalie.isNotBlank())) {
            item {
                BentoIstanzaDetailsCard(credentials = credentials)
            }
        }

        // Quick 1-Tap Action Row to Strumenti & Statistiche
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    onClick = { onNavigateRoute(AppleScreenRoute.STRUMENTI_HUB) },
                    shape = RoundedCornerShape(10.dp),
                    color = ElectricBlueTint,
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .appleBounceClick()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Strumenti & PEC",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    onClick = { showCommunityStats = !showCommunityStats },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, AppleBorderHairline),
                    modifier = Modifier
                        .weight(1f)
                        .appleBounceClick()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = null,
                            tint = VividEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showCommunityStats) "Nascondi Tempi" else "Tempi Questura",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        if (showCommunityStats) {
            item {
                BentoCommunityStatsCard(
                    selectedProvince = selectedProvince,
                    onProvinceSelected = onProvinceSelected,
                    selectedLanguageCode = selectedLanguageCode
                )
            }
        }

        // Collapsible Status Check History
        if (history.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.string("history_title", selectedLanguageCode),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (history.size > 2) {
                            TextButton(onClick = { showAllHistory = !showAllHistory }) {
                                Text(
                                    text = if (showAllHistory) "Mostra meno" else "Vedi tutti (${history.size})",
                                    fontSize = 12.sp,
                                    color = ElectricBlue
                                )
                            }
                        }
                        TextButton(onClick = onClearHistory) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Cancella",
                                modifier = Modifier.size(15.dp),
                                tint = AlertRed
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                Localization.string("history_clear", selectedLanguageCode),
                                fontSize = 12.sp,
                                color = AlertRed
                            )
                        }
                    }
                }
            }

            val visibleHistory = if (showAllHistory) history else history.take(2)
            items(visibleHistory) { record ->
                HistoryRecordItem(record = record, selectedLanguageCode = selectedLanguageCode)
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

    val isPoliziaReady = poliziaStatus.contains("pronto", ignoreCase = true) || poliziaStatus.contains("consegna", ignoreCase = true)
    val isPoliziaInProcessing = poliziaStatus.contains("trattazione", ignoreCase = true)
    val isPoliziaNotFound = poliziaStatus.contains("non presente", ignoreCase = true) || poliziaStatus.contains("non trovato", ignoreCase = true)

    val isPortaleReady = portaleStatus.contains("consegna", ignoreCase = true) || portaleStatus.contains("pronto", ignoreCase = true)
    val isPortalePreistruttoria = portaleStatus.contains("preistruttoria", ignoreCase = true)

    val isActiveTracking = credentials != null && (hasPolizia || hasPortale)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Live Status & Tracking Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BentoBadgeGreenBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = BentoCardPrimaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = Localization.string("document_status", selectedLanguageCode),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextHeading
                        )
                        if (credentials != null && credentials.fullName.isNotBlank()) {
                            Text(
                                text = credentials.fullName,
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isActiveTracking) ItalianGreenLight else BentoCardBorder.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = if (isActiveTracking) "• " + Localization.string("tracking_active", selectedLanguageCode) else Localization.string("tracking_to_configure", selectedLanguageCode),
                        color = if (isActiveTracking) ItalianGreen else BentoTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = BentoCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

            // Dual Portal Live Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Portal 1: Portale Immigrazione
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0F9FF),
                    border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isPortaleReady) ItalianGreen else Color(0xFF0284C7))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Portale Immigrazione",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0369A1),
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val portaleDisplay = when {
                            !hasPortale -> "Non configurato"
                            portaleStatus == "No checks yet" -> "In attesa verifica"
                            isPortalePreistruttoria -> "In Preistruttoria"
                            isPortaleReady -> "Pronto per ritiro"
                            else -> portaleStatus.substringBefore(" - ")
                        }
                        Text(
                            text = portaleDisplay,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0C4A6E),
                            maxLines = 2
                        )
                        if (credentials != null && credentials.codiceIstanza.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Istanza: ${credentials.codiceIstanza}",
                                fontSize = 10.sp,
                                color = Color(0xFF0284C7),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Portal 2: Polizia di Stato
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, ItalianGreen.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPoliziaReady -> ItalianGreen
                                            isPoliziaInProcessing -> Color(0xFFEAB308)
                                            isPoliziaNotFound -> Color(0xFFEF4444)
                                            else -> BentoTextSecondary
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Polizia di Stato",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ItalianGreen,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val poliziaDisplay = when {
                            !hasPolizia -> "Non configurato"
                            poliziaStatus == "No checks yet" -> "In attesa verifica"
                            isPoliziaReady -> "Pronto Consegna"
                            isPoliziaInProcessing -> "In Trattazione"
                            isPoliziaNotFound -> "Non in archivio"
                            else -> poliziaStatus
                        }
                        Text(
                            text = poliziaDisplay,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BentoTextHeading,
                            maxLines = 2
                        )
                        val codeShown = credentials?.numeroPratica?.takeIf { it.isNotBlank() }
                            ?: credentials?.numeroAssicurata?.takeIf { it.isNotBlank() }
                            ?: ""
                        if (codeShown.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Codice: $codeShown",
                                fontSize = 10.sp,
                                color = ItalianGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
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

@Composable
fun ReceiptAutoFillCard(
    viewModel: PermessoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }

    // Safe temporary URI creation for Camera capture via FileProvider
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val uri = tempCameraUri
        if (success && uri != null) {
            isScanning = true
            coroutineScope.launch {
                val result = ReceiptScannerHelper.scanReceipt(context, uri)
                isScanning = false
                result.onSuccess { data ->
                    viewModel.applyScannedReceipt(data)
                    val details = buildString {
                        if (data.fullName.isNotBlank()) append("• Nome: ${data.fullName}\n")
                        if (data.userId.isNotBlank()) append("• User ID: ${data.userId}\n")
                        if (data.password.isNotBlank()) append("• Password: ${data.password}\n")
                        if (data.numeroAssicurata.isNotBlank()) append("• Assicurata: ${data.numeroAssicurata}\n")
                        if (data.numeroPratica.isNotBlank()) append("• Polizia di Stato: ${data.numeroPratica}")
                    }.trim()
                    Toast.makeText(
                        context,
                        if (details.isNotBlank()) "Ricevuta elaborata con successo:\n$details" else "Ricevuta elaborata! Controlla i campi.",
                        Toast.LENGTH_LONG
                    ).show()
                }.onFailure { err ->
                    Toast.makeText(context, "Errore elaborazione: ${err.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isScanning = true
            coroutineScope.launch {
                val result = ReceiptScannerHelper.scanReceipt(context, uri)
                isScanning = false
                result.onSuccess { data ->
                    viewModel.applyScannedReceipt(data)
                    val details = buildString {
                        if (data.fullName.isNotBlank()) append("• Nome: ${data.fullName}\n")
                        if (data.userId.isNotBlank()) append("• User ID: ${data.userId}\n")
                        if (data.password.isNotBlank()) append("• Password: ${data.password}\n")
                        if (data.numeroAssicurata.isNotBlank()) append("• Assicurata: ${data.numeroAssicurata}\n")
                        if (data.numeroPratica.isNotBlank()) append("• Polizia di Stato: ${data.numeroPratica}")
                    }.trim()
                    Toast.makeText(
                        context,
                        if (details.isNotBlank()) "Ricevuta rilevata con successo:\n$details" else "Ricevuta elaborata! Controlla i campi.",
                        Toast.LENGTH_LONG
                    ).show()
                }.onFailure { err ->
                    Toast.makeText(context, "Errore scansione ricevuta: ${err.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF0284C7).copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFF0284C7).copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Scansiona o Carica Ricevuta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0C4A6E)
                    )
                    Text(
                        text = "Estrae automaticamente Nome, User ID e Password dalla tua ricevuta postale Mod. 22A",
                        fontSize = 11.sp,
                        color = Color(0xFF0369A1),
                        lineHeight = 15.sp
                    )
                }
            }

            if (isScanning) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0284C7),
                        trackColor = Color(0xFFBAE6FD)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Riconoscimento testo OCR in corso...",
                        fontSize = 11.sp,
                        color = Color(0xFF0369A1),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        try {
                            val cacheDir = File(context.cacheDir, "camera_images").apply { mkdirs() }
                            val tempFile = File.createTempFile("receipt_", ".jpg", cacheDir)
                            val authority = "${context.packageName}.fileprovider"
                            val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, tempFile)
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Impossibile avviare fotocamera: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF0284C7)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0284C7)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scatta Foto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Carica File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
    val context = LocalContext.current
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
                        // Accurately fill immigration practice field, strictly ignoring header search bar (#q)
                        const praticaField = document.querySelector('form[name="lapratica"] input[name="pratica"], input#pratica, input[name="pratica"]:not(#q)');
                        if (praticaField) {
                            praticaField.value = '$assicurataOrPratica';
                            praticaField.dispatchEvent(new Event('input', { bubbles: true }));
                            praticaField.dispatchEvent(new Event('change', { bubbles: true }));
                            praticaField.focus();
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
                        val isAr = selectedLanguageCode == "ar"
                        val isEn = selectedLanguageCode == "en"
                        BiometricAuthHelper.authenticateSingleTap(
                            context = context,
                            title = when {
                                isAr -> "التعبئة التلقائية بالبصمة"
                                isEn -> "Biometric 1-Tap Auto-Fill"
                                else -> "Auto-Fill Biometrico 1-Tap"
                            },
                            subtitle = when {
                                isAr -> "تحقق لإدخال بيانات إيصال Mod. 22A تلقائياً"
                                isEn -> "Verify identity to inject Mod. 22A credentials"
                                else -> "Verifica identità per inserire i codici Mod. 22A"
                            },
                            description = when {
                                isAr -> "قاعدة بيانات Room المشفرة محلياً"
                                isEn -> "Room Local Encrypted Credential Vault"
                                else -> "Cassaforte Credenziali Locale Room"
                            },
                            onAuthenticated = {
                                webViewInstance?.evaluateJavascript(buildAutofillJs, null)
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled = hasCredentials,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = VividEmerald, contentColor = Color.White),
                    modifier = Modifier.testTag("autofill_btn")
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("1-Tap Auto-fill", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                                        style.innerHTML = 'body { padding-bottom: 120px !important; }';
                                        if (document.head) document.head.appendChild(style);
                                        
                                        // 1. Polizia di Stato - auto-click cookie consent accept
                                        var pdsCookieBtn = document.querySelector('.cc-btn, .cc-dismiss, .cc-allow, a.cc-btn, button.cc-btn');
                                        if (pdsCookieBtn) {
                                            try { pdsCookieBtn.click(); } catch(e){}
                                        }

                                        // 2. Portale Immigrazione - accept cookie via __doPostBack so Conferma button is enabled
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

                                        // 3. Auto-dismiss any other cookie banners and clean overlays
                                        var banners = document.querySelectorAll('.cc-window, .cc-banner, #cookie-law-info-bar, div[class*="cookie"], div[id*="cookie"]');
                                        for (var i = 0; i < banners.length; i++) {
                                            banners[i].style.display = 'none';
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
                                            // Accurately fill immigration practice field, strictly ignoring header search bar (#q)
                                            const praticaField = document.querySelector('form[name="lapratica"] input[name="pratica"], input#pratica, input[name="pratica"]:not(#q)');
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
            ReceiptAutoFillCard(viewModel = viewModel)
        }

        item {
            SectionDividerHeader(
                title = if (isEn) "Official Receipt Credentials" else "Codici Ufficiali Ricevuta (Mod. 22A)",
                subtitle = if (isEn) "Applicant name, User ID (top-right) and Password (top-left barcode)" else "Nome, User ID (in alto a destra) e Password (codice a barre a sinistra)",
                badgeText = "DATI",
                icon = Icons.Default.Badge,
                accentColor = ItalianGreen
            )
        }

        item {
            var showAdvancedCreds by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoCardLightBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = ItalianGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEn) "Account Details" else "Credenziali di Accesso",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BentoTextHeading
                        )
                    }

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = onFullNameChange,
                        label = { Text("Nome e Cognome Intestatario") },
                        placeholder = { Text("es. Karim El Assali") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_fullname")
                    )

                    HorizontalDivider(color = BentoCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                    // Sub-section A: Portale Immigrazione
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Portale Immigrazione (Poste Italiane)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0369A1)
                        )
                    }

                    OutlinedTextField(
                        value = userId,
                        onValueChange = onUserIdChange,
                        label = { Text("User ID (12 Cifre)") },
                        supportingText = { Text("In alto a destra sulla ricevuta (es. 550000133317)", fontSize = 11.sp, color = ItalianGreen) },
                        placeholder = { Text("es. 550000133317") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_userid")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password (Codice a Barre)") },
                        supportingText = { Text("In alto a sinistra sotto il codice a barre (es. 05598747694-1)", fontSize = 11.sp, color = ItalianGreen) },
                        placeholder = { Text("es. 05598747694-1") },
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

                    HorizontalDivider(color = BentoCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                    // Sub-section B: Polizia di Stato
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = ItalianGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Polizia di Stato (Questura)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BentoTextHeading
                        )
                    }

                    OutlinedTextField(
                        value = numeroAssicurata,
                        onValueChange = onNumeroAssicurataChange,
                        label = { Text("Numero Assicurata (12 Cifre)") },
                        supportingText = { Text("Di solito coincide con la password senza trattino finale (es. 055987476941)", fontSize = 11.sp, color = BentoTextSecondary) },
                        placeholder = { Text("es. 055987476941") },
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_assicurata")
                    )

                    OutlinedTextField(
                        value = numeroPratica,
                        onValueChange = onNumeroPraticaChange,
                        label = { Text("Codice Pratica Questura (10 caratteri)") },
                        supportingText = { Text("Codice tracking assegnato dalla Questura (es. 25PC006466)", fontSize = 11.sp, color = BentoTextSecondary) },
                        placeholder = { Text("es. 25PC006466") },
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
    val coroutineScope = rememberCoroutineScope()
    val fullName by viewModel.formFullName.collectAsStateWithLifecycle()
    val userId by viewModel.formUserId.collectAsStateWithLifecycle()
    val password by viewModel.formPassword.collectAsStateWithLifecycle()
    val numeroAssicurata by viewModel.formNumeroAssicurata.collectAsStateWithLifecycle()
    val numeroPratica by viewModel.formNumeroPratica.collectAsStateWithLifecycle()
    val selectedProvince by viewModel.selectedProvince.collectAsStateWithLifecycle()
    val isBackgroundSyncEnabled by viewModel.isBackgroundSyncEnabled.collectAsStateWithLifecycle()
    val currentInterval by viewModel.syncInterval.collectAsStateWithLifecycle()
    val credentials by viewModel.credentials.collectAsStateWithLifecycle()
    val vaultEntries by viewModel.vaultEntries.collectAsStateWithLifecycle()
    val isBiometricSessionUnlocked by viewModel.isBiometricSessionUnlocked.collectAsStateWithLifecycle()
    val isEn = selectedLanguageCode == "en"
    val isAr = selectedLanguageCode == "ar"

    val isBiometricProtectionEnabled = credentials?.biometricProtectionEnabled == true
    val isBiometricLocked = isBiometricProtectionEnabled && !isBiometricSessionUnlocked
    val biometricHardwareStatus = remember(context) { BiometricAuthHelper.getHardwareStatus(context) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val triggerSingleTapBiometricUnlock: (() -> Unit) -> Unit = { onSuccessAction ->
        BiometricAuthHelper.authenticateSingleTap(
            context = context,
            title = when {
                isAr -> "فتح خزنة بيانات الإقامة (Mod. 22A)"
                isEn -> "Unlock Mod. 22A Credential Vault"
                else -> "Sblocca Cassaforte Credenziali Mod. 22A"
            },
            subtitle = when {
                isAr -> "الدخول السريع بلمسة واحدة عبر البصمة أو رمز الجهاز"
                isEn -> "Single-tap biometric access to your Room database credentials"
                else -> "Accesso rapido 1-Tap alle credenziali salvate nel database Room"
            },
            description = when {
                isAr -> "بياناتك محفوظة محلياً بأمان تام على جهازك"
                isEn -> "Your postal receipt & Questura credentials are stored locally in Room"
                else -> "I tuoi codici Assicurata, Ologramma e Pratica sono protetti localmente"
            },
            onAuthenticated = { usedHardware ->
                viewModel.unlockWithBiometrics()
                onSuccessAction()
                val msg = when {
                    isAr -> if (usedHardware) "تم فتح الخزنة بالبصمة بنجاح" else "تم فتح الخزنة بنجاح"
                    isEn -> if (usedHardware) "Biometric Vault Unlocked" else "Credential Vault Unlocked"
                    else -> if (usedHardware) "Cassaforte sbloccata con Biometria!" else "Cassaforte credenziali sbloccata!"
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            },
            onError = { errMsg ->
                Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val uri = tempCameraUri
        if (success && uri != null) {
            isScanning = true
            coroutineScope.launch {
                val result = ReceiptScannerHelper.scanReceipt(context, uri)
                isScanning = false
                result.onSuccess { data ->
                    viewModel.applyScannedReceipt(data)
                    viewModel.saveCredentials()
                    Toast.makeText(
                        context,
                        "Ricevuta Mod. 22A acquisita e sincronizzata nel Pass!",
                        Toast.LENGTH_LONG
                    ).show()
                }.onFailure { err ->
                    Toast.makeText(context, "Errore OCR: ${err.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isScanning = true
            coroutineScope.launch {
                val result = ReceiptScannerHelper.scanReceipt(context, uri)
                isScanning = false
                result.onSuccess { data ->
                    viewModel.applyScannedReceipt(data)
                    viewModel.saveCredentials()
                    Toast.makeText(
                        context,
                        "Ricevuta Mod. 22A acquisita da immagine!",
                        Toast.LENGTH_LONG
                    ).show()
                }.onFailure { err ->
                    Toast.makeText(context, "Errore scansione: ${err.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val launchCameraScan = {
        try {
            val cacheDir = File(context.cacheDir, "camera_images").apply { mkdirs() }
            val tempFile = File.createTempFile("receipt_", ".jpg", cacheDir)
            val authority = "${context.packageName}.fileprovider"
            val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, tempFile)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } catch (_: Exception) {
            galleryLauncher.launch("image/*")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp)
    ) {
        // 1. APPLE WALLET STYLE PASS CARD (Emerald Gradient, Side Ticket Notches, Barcode & 1-Tap Camera Scan)
        item {
            AppleWalletPassCard(
                fullName = fullName,
                userId = if (isBiometricLocked && userId.isNotBlank()) "•••• •••• ${userId.takeLast(3)}" else userId,
                assicurataCode = if (isBiometricLocked && (numeroAssicurata.isNotBlank() || password.isNotBlank())) {
                    "•••• •••• ${(numeroAssicurata.ifBlank { password }).takeLast(3)}"
                } else {
                    numeroAssicurata.ifBlank { password }
                },
                praticaCode = if (isBiometricLocked && numeroPratica.isNotBlank()) "••••••${numeroPratica.takeLast(3)}" else numeroPratica,
                selectedProvince = selectedProvince,
                onScanWithCamera = launchCameraScan
            )
        }

        // 1B. BIOMETRIC PROMPT & ROOM CREDENTIAL VAULT (SINGLE-TAP ACCESS & MULTI-DOSSIER AUTO-FILL)
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.dp,
                    if (isBiometricLocked) SunsetTangerine.copy(alpha = 0.45f) else AppleBorderHairline
                ),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("biometric_credential_vault_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isBiometricLocked) SunsetTangerineTint else VividEmeraldTint
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isBiometricLocked) Icons.Default.Lock else Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = if (isBiometricLocked) SunsetTangerine else VividEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = when {
                                        isAr -> "الدخول السريع بالبصمة وخزنة Room"
                                        isEn -> "Biometric 1-Tap & Room Vault"
                                        else -> "BiometricPrompt 1-Tap & Vault Room"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = BiometricAuthHelper.getStatusBadgeText(biometricHardwareStatus, selectedLanguageCode),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isBiometricProtectionEnabled,
                            onCheckedChange = { enable ->
                                BiometricAuthHelper.authenticateSingleTap(
                                    context = context,
                                    title = when {
                                        isAr -> "تأكيد إعداد الحماية بالبصمة"
                                        isEn -> "Confirm Biometric Vault Setting"
                                        else -> "Conferma Protezione Biometrica"
                                    },
                                    subtitle = when {
                                        isAr -> "تفعيل أو إيقاف قفل بيانات الإيصال بلمسة واحدة"
                                        isEn -> "Enable or disable single-tap biometric protection"
                                        else -> "Attiva o disattiva il blocco biometrico 1-Tap"
                                    },
                                    description = "Room Database Credential Vault",
                                    onAuthenticated = {
                                        viewModel.setBiometricProtectionEnabled(enable)
                                    },
                                    onError = { errMsg ->
                                        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VividEmerald
                            ),
                            modifier = Modifier.testTag("biometric_protection_switch")
                        )
                    }

                    // Stacked Full-Width Action Buttons (Never wraps text across lines)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isBiometricLocked) {
                                    triggerSingleTapBiometricUnlock {}
                                } else if (isBiometricProtectionEnabled) {
                                    viewModel.lockBiometricSession()
                                } else {
                                    triggerSingleTapBiometricUnlock {
                                        viewModel.setBiometricProtectionEnabled(true)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBiometricLocked) ElectricBlue else VividEmerald,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .appleBounceClick()
                                .testTag("biometric_single_tap_unlock_btn")
                        ) {
                            Icon(
                                imageVector = if (isBiometricLocked) Icons.Default.Fingerprint else Icons.Default.LockOpen,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isBiometricLocked -> when {
                                        isAr -> "فتح بالبصمة (لمسة واحدة)"
                                        isEn -> "1-Tap Biometric Unlock"
                                        else -> "Sblocca 1-Tap con Biometria"
                                    }
                                    isBiometricProtectionEnabled -> when {
                                        isAr -> "قفل الخزنة الآن"
                                        isEn -> "Lock Vault Session"
                                        else -> "Blocca Sessione Ora"
                                    }
                                    else -> when {
                                        isAr -> "تفعيل الدخول بالبصمة 1-Tap"
                                        isEn -> "Activate 1-Tap Biometric Entry"
                                        else -> "Attiva Accesso Biometrico 1-Tap"
                                    }
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                triggerSingleTapBiometricUnlock {
                                    val label = fullName.ifBlank {
                                        numeroPratica.ifBlank { "Mod. 22A" }
                                    }
                                    viewModel.saveCurrentCredentialsToVault(label)
                                    Toast.makeText(
                                        context,
                                        if (isEn) "Saved to Room Vault!" else "Salvato nella Cassaforte Room!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .appleBounceClick()
                                .testTag("save_to_room_vault_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EnhancedEncryption,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "Save Current Dossier to Room Vault" else if (isAr) "حفظ الملف الحالي بالخزنة" else "Salva Dossier Attuale nel Vault Room",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    val lastAuthTs = credentials?.lastBiometricAuthTimestamp ?: 0L
                    if (lastAuthTs > 0L) {
                        val formattedTime = remember(lastAuthTs) {
                            SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(lastAuthTs))
                        }
                        Text(
                            text = when {
                                isAr -> "آخر تحقق بالبصمة: $formattedTime • متكامل مع قاعدة بيانات Room"
                                isEn -> "Last biometric verification: $formattedTime • Backed by Room Database"
                                else -> "Ultima verifica biometrica: $formattedTime • Sincronizzato con Room DB"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Saved Room Credential Vault Profiles (Single-Tap Biometric Switch & Auto-Fill)
                    if (vaultEntries.isNotEmpty()) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 0.5.dp
                        )
                        Text(
                            text = when {
                                isAr -> "ملفات الإقامة المحفوظة في قاعدة بيانات ROOM (${vaultEntries.size})"
                                isEn -> "SAVED ROOM VAULT PROFILES (${vaultEntries.size})"
                                else -> "PROFILI SALVATI NEL VAULT ROOM (${vaultEntries.size})"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.6.sp
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            vaultEntries.forEach { entry ->
                                val maskedCode = remember(entry, isBiometricLocked) {
                                    val raw = entry.numeroAssicurata.ifBlank { entry.numeroPratica.ifBlank { entry.userId } }
                                    if (isBiometricLocked && raw.length > 3) {
                                        "•••• •••• ${raw.takeLast(3)}"
                                    } else {
                                        raw.ifBlank { "Mod. 22A" }
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    border = BorderStroke(1.dp, AppleBorderHairline),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = entry.fullName.ifBlank { entry.label },
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${entry.selectedProvince} • $maskedCode",
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                onClick = {
                                                    triggerSingleTapBiometricUnlock {
                                                        viewModel.applyVaultEntryToActiveCredentials(entry)
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                color = ElectricBlueTint,
                                                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f)),
                                                modifier = Modifier
                                                    .appleBounceClick()
                                                    .testTag("vault_autofill_btn_${entry.entryId}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Fingerprint,
                                                        contentDescription = null,
                                                        tint = ElectricBlue,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = if (isAr) "تعبئة 1-Tap" else "1-Tap Fill",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ElectricBlue,
                                                        maxLines = 1
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteVaultEntry(entry.entryId) },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .testTag("vault_delete_btn_${entry.entryId}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Delete vault entry",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(17.dp)
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

        if (isScanning) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ElectricBlueTint,
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = ElectricBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Scansione OCR Mod. 22A in corso...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElectricBlue
                        )
                    }
                }
            }
        }

        // Secondary Button to import from Gallery / Photo Picker
        item {
            Surface(
                onClick = { galleryLauncher.launch("image/*") },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, AppleBorderHairline),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .appleBounceClick()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Importa Foto Ricevuta da Galleria",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 2. INSET GROUPED SETTINGS LIST (Single Source of Truth for Credentials)
        item {
            Text(
                text = "CREDENZIALI RICEVUTA MOD. 22A (UNICA FONTE)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.7.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, AppleBorderHairline),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
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
                            UserPersistentAvatar(
                                fullName = fullName,
                                size = 40.dp,
                                fontSize = 14.sp,
                                borderColor = VividEmerald,
                                borderWidth = 1.5.dp,
                                modifier = Modifier.testTag("profile_persistent_avatar")
                            )
                            Column {
                                Text(
                                    text = "Dati Intestatario & Codici",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Sincronizzati con Portale Poste e Questura",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = VividEmeraldTint
                        ) {
                            Text(
                                text = "MOD. 22A",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007A33),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)

                    if (isBiometricLocked) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SunsetTangerineTint,
                            border = BorderStroke(1.dp, SunsetTangerine.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = SunsetTangerine,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = when {
                                        isAr -> "البيانات محمية بالبصمة (BiometricPrompt)"
                                        isEn -> "Credentials Protected by BiometricPrompt"
                                        else -> "Credenziali Protette da BiometricPrompt"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = when {
                                        isAr -> "اضغط بلمسة واحدة لفتح وتعديل أرقام الإيصال البريدي (Mod. 22A) المحفوظة في قاعدة بيانات Room."
                                        isEn -> "Authenticate with a single tap to view, edit, or auto-fill your Mod. 22A credentials stored in Room."
                                        else -> "Autenticati con un solo tocco per visualizzare, modificare o compilare i codici Mod. 22A salvati in Room."
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { triggerSingleTapBiometricUnlock {} },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VividEmerald,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .appleBounceClick()
                                        .testTag("unlock_credentials_form_btn")
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when {
                                            isAr -> "فتح البيانات بالبصمة (1-Tap)"
                                            isEn -> "1-Tap Biometric Unlock"
                                            else -> "Sblocca Credenziali (1-Tap)"
                                        },
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    } else {
                        // Row 1: Nome e Cognome
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { viewModel.formFullName.value = it },
                            label = { Text("Nome e Cognome Intestatario") },
                            placeholder = { Text("Come riportato sul passaporto") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = VividEmerald, modifier = Modifier.size(20.dp))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_fullname")
                        )

                        // Row 2: Password / Codice Assicurata
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                viewModel.formPassword.value = it
                                if (numeroAssicurata.isBlank() || numeroAssicurata == password) {
                                    viewModel.formNumeroAssicurata.value = it.replace("-", "")
                                }
                            },
                            label = { Text("Password / Codice Assicurata (12 cifre)") },
                            supportingText = {
                                Text("Sotto il codice a barre in alto a sinistra sulla ricevuta Mod. 22A", fontSize = 11.sp)
                            },
                            placeholder = { Text("12 cifre numeriche") },
                            leadingIcon = {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                            },
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_password")
                        )

                        // Row 3: User ID (Ologramma)
                        OutlinedTextField(
                            value = userId,
                            onValueChange = { viewModel.formUserId.value = it },
                            label = { Text("User ID / Codice Ologramma (12 cifre)") },
                            supportingText = {
                                Text("In alto a destra sopra l'ologramma Poste Italiane", fontSize = 11.sp)
                            },
                            placeholder = { Text("12 cifre ologramma") },
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_userid")
                        )

                        // Row 4: Numero Assicurata (Polizia di Stato)
                        OutlinedTextField(
                            value = numeroAssicurata,
                            onValueChange = { viewModel.formNumeroAssicurata.value = it },
                            label = { Text("Numero Assicurata Postale") },
                            placeholder = { Text("12 cifre senza trattini") },
                            leadingIcon = {
                                Icon(Icons.Default.Pin, contentDescription = null, tint = VividEmerald, modifier = Modifier.size(20.dp))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_assicurata")
                        )

                        // Row 5: Codice Pratica / Questura
                        OutlinedTextField(
                            value = numeroPratica,
                            onValueChange = { viewModel.formNumeroPratica.value = it },
                            label = { Text("Codice Pratica / Questura (10 caratteri)") },
                            supportingText = {
                                Text("Codice alfanumerico assegnato dalla Questura di $selectedProvince", fontSize = 11.sp)
                            },
                            placeholder = { Text("10 caratteri alfanumerici") },
                            leadingIcon = {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = SunsetTangerine, modifier = Modifier.size(20.dp))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_pratica")
                        )

                        // Save CTA
                        Button(
                            onClick = {
                                viewModel.saveCredentials()
                                Toast.makeText(
                                    context,
                                    Localization.string("profile_edit_success", selectedLanguageCode),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VividEmerald,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(vertical = 14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .appleBounceClick()
                                .testTag("save_creds_btn")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(19.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.string("button_save", selectedLanguageCode),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 3. EXPANDABLE LANGUAGE OPTIONS TOGGLE
        item {
            Text(
                text = "OPZIONI LINGUA INTERFACCIA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.7.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        item {
            var isLangOptionsOpen by remember { mutableStateOf(false) }
            val languages = listOf(
                AppLanguage.ITALIAN,
                AppLanguage.ENGLISH,
                AppLanguage.FRENCH,
                AppLanguage.SPANISH,
                AppLanguage.ARABIC
            )
            val activeLang = languages.find { it.code == selectedLanguageCode } ?: AppLanguage.ITALIAN

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, AppleBorderHairline),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isLangOptionsOpen = !isLangOptionsOpen }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .testTag("language_options_toggle_button"),
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
                                    .background(ElectricBlueTint, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Lingua Applicazione",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${activeLang.displayName} (${activeLang.code.uppercase()})",
                                    fontSize = 12.sp,
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, AppleBorderHairline)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = activeLang.code.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = if (isLangOptionsOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Cambia lingua",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (isLangOptionsOpen) {
                        HorizontalDivider(color = AppleBorderHairline, thickness = 1.dp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            languages.forEach { lang ->
                                val isSelected = selectedLanguageCode == lang.code
                                Surface(
                                    onClick = {
                                        viewModel.selectLanguage(lang.code)
                                        isLangOptionsOpen = false
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) ElectricBlueTint else Color.Transparent,
                                    border = if (isSelected) BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.35f)) else null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${lang.flag}  ${lang.displayName}",
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = ElectricBlue,
                                                modifier = Modifier.size(18.dp)
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

        // 4. TOGGLE SWITCH FOR CONTROLLO AUTOMATICO (24h) & FREQUENCY
        item {
            Text(
                text = "SINCRONIZZAZIONE AUTOMATICA IN BACKGROUND",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.7.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, AppleBorderHairline),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .background(VividEmeraldTint, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = VividEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Controllo Automatico (24h)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Notifica push istantanea appena il PSE è pronto",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isBackgroundSyncEnabled,
                            onCheckedChange = { viewModel.setBackgroundSyncEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VividEmerald
                            ),
                            modifier = Modifier.testTag("bg_sync_24h_switch")
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)

                    // Compact Segmented Options Row for Frequency
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CheckInterval.entries.forEach { interval ->
                            val isSelected = currentInterval == interval
                            Surface(
                                onClick = { viewModel.updateSyncInterval(interval) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .appleBounceClick()
                            ) {
                                Text(
                                    text = if (isEn) interval.titleEn else interval.titleIt,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. SECURE ENCLAVE PRIVACY BADGE & GDPR RIGHT TO ERASURE (Full-Width Button, Never Wraps)
        item {
            var showDeleteDialog by remember { mutableStateOf(false) }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(
                            text = Localization.string("delete_profile_confirm_title", selectedLanguageCode),
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                    },
                    text = {
                        Text(
                            text = Localization.string("delete_profile_confirm_msg", selectedLanguageCode),
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteProfileAndReset()
                                Toast.makeText(
                                    context,
                                    Localization.string("delete_profile_success", selectedLanguageCode),
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(Localization.string("confirm", selectedLanguageCode), color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(Localization.string("cancel", selectedLanguageCode))
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, AppleBorderHairline),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("secure_enclave_privacy_badge")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(VividEmeraldTint, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = VividEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Crittografia Locale AES-256",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "I codici Mod. 22A sono cifrati esclusivamente sul dispositivo e interrogati direttamente sui portali Polizia di Stato e Poste Italiane.",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = AppleBorderHairline, thickness = 1.dp)

                    Text(
                        text = "Diritto alla Cancellazione (Art. 17 GDPR)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.45f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.string("delete_profile", selectedLanguageCode),
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
}

