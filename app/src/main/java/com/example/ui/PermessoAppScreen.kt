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
import java.text.SimpleDateFormat
import java.util.*

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

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Request permissions for notifications on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "App Logo",
                            tint = BentoBadgeGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Soggiorno Track",
                            fontWeight = FontWeight.SemiBold,
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
                        label = { Text("Stato") },
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
                        label = { Text("Portale") },
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
                        label = { Text("Ricevuta") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoCardPrimaryText,
                            selectedTextColor = BentoCardPrimaryText,
                            indicatorColor = BentoAvatarBg,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_ricevuta")
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
                0 -> DashboardTab(
                    credentials = credentials,
                    history = historyList,
                    checkState = checkState,
                    onCheckNow = { viewModel.checkStatusNow() },
                    onResetCheckState = { viewModel.resetCheckState() },
                    onClearHistory = { viewModel.clearCheckHistory() },
                    onGoToCredentials = { selectedTabIndex = 2 }
                )
                1 -> PortalWebViewTab(
                    credentials = credentials
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
                        }
                    )
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
    onCheckNow: () -> Unit,
    onResetCheckState: () -> Unit,
    onClearHistory: () -> Unit,
    onGoToCredentials: () -> Unit
) {
    val hasCode = credentials != null && 
            (credentials.numeroAssicurata.isNotBlank() || credentials.numeroPratica.isNotBlank())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Welcome and Header Card
        item {
            val name = credentials?.fullName?.trim() ?: ""
            val greetName = if (name.isNotBlank()) name else "Amico"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ciao, $greetName! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextHeading
                    )
                    val lastCheckedStr = if (credentials?.lastCheckedTime != 0L) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        "Ultimo controllo: " + sdf.format(Date(credentials?.lastCheckedTime ?: 0L))
                    } else {
                        "Nessun controllo effettuato"
                    }
                    Text(
                        text = lastCheckedStr,
                        fontSize = 13.sp,
                        color = BentoTextSecondary
                    )
                }
            }
        }

        // BENTO CARD 1: Primary Status Card
        item {
            BentoPrimaryStatusCard(credentials = credentials)
        }

        // BENTO CARDS 2 & 3: Portale Status & Polizia Status side-by-side
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BentoPortalStatusCard(credentials = credentials)
                }
                Box(modifier = Modifier.weight(1f)) {
                    BentoPoliziaStatusCard(credentials = credentials)
                }
            }
        }

        // BENTO CARD 4: Credentials Card
        item {
            BentoCredentialsCard(credentials = credentials, onGoToCredentials = onGoToCredentials)
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
                            text = "Configurazione Richiesta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BentoAlertRed,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Inserisci il numero dell'assicurata per tracciare lo stato della tua pratica.",
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
                            Text("Configura Dati Ricevuta", fontWeight = FontWeight.Bold, color = Color.White)
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
                            text = "Controlla Ora",
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
                CheckStateCard(checkState = checkState, onDismiss = onResetCheckState)
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
                    text = "Cronologia Controlli",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BentoTextHeading
                )
                if (history.isNotEmpty()) {
                    TextButton(onClick = onClearHistory) {
                        Icon(Icons.Default.Delete, contentDescription = "Cancella", modifier = Modifier.size(16.dp), tint = BentoBadgeGreen)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancella tutto", fontSize = 13.sp, color = BentoBadgeGreen)
                    }
                }
            }
        }

        if (history.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            text = "Nessun controllo registrato",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = BentoTextSecondary
                        )
                        Text(
                            text = "I controlli automatici giornalieri e manuali appariranno qui.",
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
                HistoryRecordItem(record = record)
            }
        }
    }
}

@Composable
fun BentoPrimaryStatusCard(credentials: UserCredentials?) {
    val statusText = credentials?.lastPoliziaStatus ?: "In attesa di dati"
    val statusLabel = if (credentials == null || (credentials.numeroAssicurata.isBlank() && credentials.numeroPratica.isBlank())) {
        "Da configurare"
    } else {
        statusText
    }

    val isActiveTracking = credentials != null && 
            (credentials.numeroAssicurata.isNotBlank() || credentials.numeroPratica.isNotBlank())

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
                        text = if (isActiveTracking) "Tracciamento Attivo" else "Da Configurare",
                        color = BentoBadgeGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Column {
                Text(
                    text = "Stato Documento",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = BentoTextSecondary
                )
                Text(
                    text = statusLabel,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoCardPrimaryText
                )
            }
        }
    }
}

@Composable
fun BentoPortalStatusCard(credentials: UserCredentials?) {
    val hasCredentials = credentials != null && 
            credentials.userId.isNotBlank() && credentials.password.isNotBlank()

    val statusText = credentials?.lastPortaleStatus ?: "No checks yet"
    val (titleText, descText) = when {
        !hasCredentials -> Pair("Non Attivo", "Configura credenziali")
        statusText == "No checks yet" -> Pair("Pronto", "Verifica la tua pratica")
        statusText.contains(" - ") -> {
            val parts = statusText.split(" - ", limit = 2)
            Pair(parts[0], parts[1])
        }
        else -> Pair("Stato", statusText)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "STATO PORTALE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoAvatarBorder,
                letterSpacing = 0.5.sp
            )

            Column(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Text(
                    text = titleText,
                    fontSize = 18.sp,
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
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (hasCredentials) BentoBadgeGreen else BentoTextSecondary.copy(alpha = 0.4f))
                )
                Text(
                    text = "Portale Immigrazione",
                    fontSize = 10.sp,
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BentoPoliziaStatusCard(credentials: UserCredentials?) {
    val statusText = credentials?.lastPoliziaStatus ?: ""
    val isReady = statusText.contains("Pronto", ignoreCase = true)
    val hasCode = credentials != null && 
            (credentials.numeroAssicurata.isNotBlank() || credentials.numeroPratica.isNotBlank())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "POLIZIA STATO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoAvatarBorder,
                letterSpacing = 0.5.sp
            )

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isReady) Icons.Default.CheckCircle else Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (!hasCode) BentoTextSecondary.copy(alpha = 0.2f) else if (isReady) BentoBadgeGreen else BentoCardBorder,
                    modifier = Modifier.size(44.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val dotColor = when {
                    !hasCode -> BentoTextSecondary.copy(alpha = 0.4f)
                    isReady -> BentoBadgeGreen
                    else -> BentoAlertRed
                }
                val label = when {
                    !hasCode -> "Nessun codice"
                    isReady -> "Pronto per il ritiro"
                    else -> "Non ancora pronto"
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
    onGoToCredentials: () -> Unit
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

    val passLabel = if (credentials?.password?.isNotBlank() == true) "••••••••" else "Non inserita"

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
                    text = "Account Sicuro",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextHeading
                )
                Text(
                    text = "User: $maskedUser • Pass: $passLabel",
                    fontSize = 12.sp,
                    color = BentoTextSecondary
                )
            }

            // Edit button (p-3 rounded-full bg-white)
            IconButton(
                onClick = onGoToCredentials,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White),
                modifier = Modifier
                    .size(40.dp)
                    .testTag("edit_credentials_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifica credenziali",
                    tint = BentoTextHeading,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BiometricPermessoCard(credentials: UserCredentials?) {
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
                            text = "PERMESSO DI SOGGIORNO",
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
                        text = credentials?.fullName?.uppercase() ?: "INTESTATARIO DA CONFIGURARE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    val postCode = credentials?.numeroAssicurata ?: ""
                    val questuraCode = credentials?.numeroPratica ?: ""
                    val displayCode = when {
                        postCode.isNotBlank() && questuraCode.isNotBlank() -> "Assicurata: $postCode | Pratica: $questuraCode"
                        postCode.isNotBlank() -> "Assicurata: $postCode"
                        questuraCode.isNotBlank() -> "Numero Pratica: $questuraCode"
                        else -> "Nessun codice registrato"
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
                            text = "ULTIMO CONTROLLO",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        val lastCheckedStr = if (credentials?.lastCheckedTime != 0L) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            sdf.format(Date(credentials?.lastCheckedTime ?: 0L))
                        } else {
                            "Mai controllato"
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
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (checkState) {
                is CheckUiState.Loading -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                is CheckUiState.Success -> EmeraldSuccess.copy(alpha = 0.1f)
                is CheckUiState.Error -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
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
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Interrogazione portale...", fontWeight = FontWeight.Bold)
                        }
                        is CheckUiState.Success -> {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = EmeraldSuccess)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Risposta Ricevuta!", fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                        is CheckUiState.Error -> {
                            Icon(Icons.Default.Error, contentDescription = "Errore", tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Errore Controllo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                        else -> {}
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Chiudi", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (checkState) {
                is CheckUiState.Success -> {
                    Text(
                        text = checkState.statusText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (checkState.detailsText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = checkState.detailsText,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
                is CheckUiState.Error -> {
                    Text(
                        text = checkState.errorMessage,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                is CheckUiState.Loading -> {
                    Text(
                        text = "Il sistema si sta connettendo a questure.poliziadistato.it per analizzare lo stato della raccomandata...",
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
fun HistoryRecordItem(record: StatusHistory) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(record.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
    credentials: UserCredentials?
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var webProgress by remember { mutableIntStateOf(0) }
    val portalUrl = "https://www.portaleimmigrazione.it/ELI_Stranieri/Informativa.aspx"

    Column(modifier = Modifier.fillMaxSize()) {
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
                        text = "Portale Immigrazione (Sportello Amico)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "La password di accesso si trova sulla ricevuta.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }

                val hasCredentials = credentials != null && 
                        credentials.userId.isNotBlank() && credentials.password.isNotBlank()

                Button(
                    onClick = {
                        webViewInstance?.let { wv ->
                            val user = credentials?.userId ?: ""
                            val pass = credentials?.password ?: ""
                            
                            // Highly robust Autofill script injection
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
                                            filledUser = true;
                                        }
                                        // Fill password field
                                        if (!filledPass && (type === 'password' || id.indexOf('pass') !== -1 || id.indexOf('pwd') !== -1 || name.indexOf('pass') !== -1 || name.indexOf('pwd') !== -1)) {
                                            input.value = '$pass';
                                            filledPass = true;
                                        }
                                    }
                                    return filledUser && filledPass;
                                })()
                            """.trimIndent()
                            wv.evaluateJavascript(jsCode, null)
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
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            webProgress = 100
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
            modifier = Modifier.fillMaxSize().weight(1f)
        )
    }
}

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
    onSave: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
    ) {
        // Info card explaining Italian permit receipts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Guida alla ricevuta (Cedolino)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Il Numero Assicurata (12 cifre, es: 0608...) serve per controllare lo stato su Poste Italiane e Polizia di Stato.\n" +
                                   "2. L'Area Riservata Stranieri usa l'ID utente (12 cifre, spesso l'assicurata senza l'ultima cifra o come stampato sulla ricevuta) e la Password (il codice di sicurezza sulla ricevuta dell'assicurata poste).",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Section header
        item {
            Text(
                text = "Dati Personali",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Intestatario / Full Name
        item {
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Nome Completo dell'Intestatario") },
                placeholder = { Text("Es: Mario Rossi") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_fullname")
            )
        }

        // Section header
        item {
            Text(
                text = "Credenziali Portale Immigrazione",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // User ID / Login (12 cifre)
        item {
            OutlinedTextField(
                value = userId,
                onValueChange = onUserIdChange,
                label = { Text("User ID (12 Cifre ricevuta)") },
                placeholder = { Text("Es: 061234567890") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_userid")
            )
        }

        // Password (da ricevuta)
        item {
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password Portale") },
                placeholder = { Text("Codice di sicurezza (es: 12ab34...)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Mostra password"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_password")
            )
        }

        // Section header
        item {
            Text(
                text = "Codici per Verifica Polizia di Stato",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Numero Assicurata (Poste)
        item {
            OutlinedTextField(
                value = numeroAssicurata,
                onValueChange = onNumeroAssicurataChange,
                label = { Text("Numero Assicurata (12 cifre)") },
                placeholder = { Text("Es: 060812345678") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_assicurata")
            )
        }

        // Numero Pratica (Questura)
        item {
            OutlinedTextField(
                value = numeroPratica,
                onValueChange = onNumeroPraticaChange,
                label = { Text("Numero Pratica Questura (Opzionale)") },
                placeholder = { Text("Es: MI01234567") },
                leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_pratica")
            )
        }

        // Save Button
        item {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(50.dp)
                    .testTag("save_creds_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salva e Attiva Notifiche", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
