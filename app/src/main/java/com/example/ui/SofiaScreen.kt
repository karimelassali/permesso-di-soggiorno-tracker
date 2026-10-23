package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SofiaConversation
import com.example.data.SofiaMessage
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

val BentoBadgeOrange = Color(0xFFD84315)

// Localized translation helper specifically designed for Sofia to avoid large file modifications
object SofiaLocalization {
    private val strings = mapOf(
        "it" to mapOf(
            "sofia_title" to "Consulente D.Lgs. 286/98",
            "sofia_subtitle" to "Prontuario Normativo Immigrazione",
            "sofia_status_online" to "● Attivo",
            "hello_user" to "Benvenuto, %s",
            "intro_text" to "Sportello di orientamento normativo sull'immigrazione italiana (T.U. D.Lgs. 286/98 e D.P.R. 394/99). Consulta procedure, requisiti documentali e riferimenti ufficiali.",
            "ask_me_about" to "Aree normative disponibili:",
            "residence_permits" to "Permessi di soggiorno",
            "questure" to "Questure e uffici",
            "appointments" to "Appuntamenti e impronte",
            "documents" to "Documenti necessari",
            "renewals" to "Rinnovi e scadenze",
            "citizenship" to "Cittadinanza italiana",
            "family_reunification" to "Ricongiungimento familiare",
            "travel" to "Viaggiare con ricevuta",
            "work" to "Lavoro e contratti",
            "bureaucracy" to "Burocrazia e leggi",
            "my_journey_title" to "Riepilogo Fascicolo",
            "what_users_ask" to "Quesiti frequenti",
            "source_title" to "Fonti delle informazioni",
            "source_ai" to "Normativa",
            "source_official" to "Ufficiale",
            "source_community" to "Comunità",
            "disclaimer" to "Il prontuario fornisce indicazioni basate su fonti pubbliche (D.Lgs. 286/98) e non sostituisce il parere di un avvocato o Patronato accreditato.",
            "placeholder_chat" to "Inserisci un quesito normativo...",
            "empty_convs" to "Nessuna consultazione archiviata. Seleziona un tema o inserisci un quesito.",
            "recent_conversations" to "Consultazioni recenti",
            "start_chat" to "Nuova Consultazione",
            "copy_success" to "Copiato negli appunti",
            "liked_success" to "Feedback registrato",
            "search_conv_placeholder" to "Cerca consultazioni...",
            "rename_dialog_title" to "Rinomina Consultazione",
            "delete_dialog_title" to "Elimina Consultazione",
            "delete_dialog_msg" to "Sei sicuro di voler eliminare questa consultazione?",
            "confirm" to "Conferma",
            "cancel" to "Annulla",
            "conv_starters" to "Temi normativi principali",
            "suggestions" to "Approfondimenti",
            "starter_initial_query" to "Quali sono i riferimenti normativi e i requisiti per: %s"
        ),
        "en" to mapOf(
            "sofia_title" to "Legal Reference Desk",
            "sofia_subtitle" to "Italian Immigration Law (D.Lgs. 286/98)",
            "sofia_status_online" to "● Active",
            "hello_user" to "Welcome, %s",
            "intro_text" to "Regulatory reference desk for Italian immigration procedures (Consolidated Act D.Lgs. 286/98). Verify document requirements, deadlines, and statutory rules.",
            "ask_me_about" to "Regulatory topics:",
            "residence_permits" to "Residence permits",
            "questure" to "Questure & offices",
            "appointments" to "Appointments & fingerprints",
            "documents" to "Required documents",
            "renewals" to "Renewals & deadlines",
            "citizenship" to "Italian citizenship",
            "family_reunification" to "Family reunification",
            "travel" to "Traveling with receipt",
            "work" to "Work & contracts",
            "bureaucracy" to "Bureaucracy & laws",
            "my_journey_title" to "Dossier Summary",
            "what_users_ask" to "Frequent inquiries",
            "source_title" to "Information sources",
            "source_ai" to "Statutory",
            "source_official" to "Official",
            "source_community" to "Community",
            "disclaimer" to "This desk provides regulatory guidance based on public statutes (D.Lgs. 286/98) and does not constitute formal legal representation.",
            "placeholder_chat" to "Enter a procedural question...",
            "empty_convs" to "No archived consultations. Select a topic or enter a question below.",
            "recent_conversations" to "Recent consultations",
            "start_chat" to "New Consultation",
            "copy_success" to "Copied to clipboard",
            "liked_success" to "Feedback recorded",
            "search_conv_placeholder" to "Search consultations...",
            "rename_dialog_title" to "Rename Consultation",
            "delete_dialog_title" to "Delete Consultation",
            "delete_dialog_msg" to "Are you sure you want to delete this consultation?",
            "confirm" to "Confirm",
            "cancel" to "Cancel",
            "conv_starters" to "Core Regulatory Topics",
            "suggestions" to "Suggestions",
            "starter_initial_query" to "What are the statutory requirements for: %s"
        ),
        "ar" to mapOf(
            "sofia_title" to "المرجع القانوني للهجرة",
            "sofia_subtitle" to "قانون الهجرة الإيطالي (D.Lgs. 286/98)",
            "sofia_status_online" to "● متصل",
            "hello_user" to "مرحباً، %s",
            "intro_text" to "مرجع إرشادي لإجراءات الهجرة الإيطالية وفق المرسوم التشريعي 286/98.",
            "ask_me_about" to "المواضيع القانونية المتاحة:",
            "residence_permits" to "تصاريح الإقامة",
            "questure" to "مراكز الشرطة والمكاتب",
            "appointments" to "المواعيد والبصمات",
            "documents" to "المستندات المطلوبة",
            "renewals" to "التجديد والمواعيد النهائية",
            "citizenship" to "الجنسية الإيطالية",
            "family_reunification" to "لم شمل الأسرة",
            "travel" to "السفر مع الإيصال",
            "work" to "العمل والعقود",
            "bureaucracy" to "البيروقراطية والقوانين",
            "my_journey_title" to "ملخص الملف",
            "what_users_ask" to "الأسئلة الشائعة",
            "source_title" to "مصادر المعلومات",
            "source_ai" to "قانوني",
            "source_official" to "رسمي",
            "source_community" to "مجتمعي",
            "disclaimer" to "إشعار قانوني: يقدم هذا القسم معلومات توجيهية بناءً على المصادر الرسمية ولا يشكل استشارة قانونية مهنية. للمساعدة في ملفك، يرجى مراجعة مكتب باتروناتو (Patronato) معتمد.",
            "placeholder_chat" to "اكتب استفسارك القانوني...",
            "empty_convs" to "لا توجد استشارات سابقة.",
            "recent_conversations" to "الاستشارات الأخيرة",
            "start_chat" to "استشارة جديدة",
            "copy_success" to "تم النسخ إلى الحافظة",
            "liked_success" to "تم تسجيل رأيك",
            "search_conv_placeholder" to "البحث في الاستشارات...",
            "rename_dialog_title" to "إعادة تسمية المحادثة",
            "delete_dialog_title" to "حذف المحادثة",
            "delete_dialog_msg" to "هل أنت متأكد من رغبتك في حذف هذه المحادثة؟",
            "confirm" to "تأكيد",
            "cancel" to "إلغاء",
            "conv_starters" to "المواضيع القانونية الرئيسية",
            "suggestions" to "اقتراحات",
            "starter_initial_query" to "أخبرني عن المتطلبات القانونية لـ: %s"
        ),
        "es" to mapOf(
            "sofia_title" to "Consultor D.Lgs. 286/98",
            "sofia_subtitle" to "Normativa de Inmigración Italiana",
            "sofia_status_online" to "● Activo",
            "hello_user" to "Bienvenido, %s",
            "intro_text" to "Servicio de orientación normativa sobre inmigración italiana (D.Lgs. 286/98).",
            "ask_me_about" to "Temas normativos:",
            "residence_permits" to "Permisos de residencia",
            "questure" to "Comisarías y oficinas",
            "appointments" to "Citas y huellas",
            "documents" to "Documentos necesarios",
            "renewals" to "Renovaciones y plazos",
            "citizenship" to "Ciudadanía italiana",
            "family_reunification" to "Reagrupación familiar",
            "travel" to "Viajar con recibo",
            "work" to "Trabajo y contratos",
            "bureaucracy" to "Burocracia y leyes",
            "my_journey_title" to "Resumen del Expediente",
            "what_users_ask" to "Consultas frecuentes",
            "source_title" to "Fuentes de información",
            "source_ai" to "Normativa",
            "source_official" to "Oficial",
            "source_community" to "Comunidad",
            "disclaimer" to "Información orientativa basada en fuentes públicas (D.Lgs. 286/98). Para asistencia con su trámite, acuda a un Patronato.",
            "placeholder_chat" to "Escribe tu consulta normativa...",
            "empty_convs" to "Sin consultas previas.",
            "recent_conversations" to "Consultas recientes",
            "start_chat" to "Nueva Consulta",
            "copy_success" to "Copiado al portapapeles",
            "liked_success" to "Comentario registrado",
            "search_conv_placeholder" to "Buscar consultas...",
            "rename_dialog_title" to "Renombrar Consulta",
            "delete_dialog_title" to "Eliminar Consulta",
            "delete_dialog_msg" to "¿Estás seguro de que deseas eliminar esta consulta?",
            "confirm" to "Confirmar",
            "cancel" to "Cancelar",
            "conv_starters" to "Temas normativos",
            "suggestions" to "Sugerencias",
            "starter_initial_query" to "Cuéntame sobre los requisitos legales para: %s"
        ),
        "fr" to mapOf(
            "sofia_title" to "Référentiel D.Lgs. 286/98",
            "sofia_subtitle" to "Droit de l'Immigration Italienne",
            "sofia_status_online" to "● Actif",
            "hello_user" to "Bienvenue, %s",
            "intro_text" to "Service d'orientation réglementaire sur l'immigration italienne (D.Lgs. 286/98).",
            "ask_me_about" to "Demandez-moi n'importe quoi sur :",
            "residence_permits" to "Titres de séjour",
            "questure" to "Préfectures et bureaux",
            "appointments" to "Rendez-vous et empreintes",
            "documents" to "Documents requis",
            "renewals" to "Renouvellements et délais",
            "citizenship" to "Citoyenneté italienne",
            "family_reunification" to "Regroupement familial",
            "travel" to "Voyager avec récépissé",
            "work" to "Travail et contrats",
            "bureaucracy" to "Bureaucratie et lois",
            "my_journey_title" to "Mon Parcours",
            "what_users_ask" to "Ce que les utilisateurs demandent souvent",
            "source_title" to "Sources d'information",
            "source_ai" to "IA",
            "source_official" to "Officiel",
            "source_community" to "Communauté",
            "disclaimer" to "Sofia fournit des informations indicatives basées sur des sources publiques et ne constitue pas un conseil juridique professionnel. Pour toute assistance, adressez-vous à un Patronato.",
            "placeholder_chat" to "Demander à Sofia...",
            "empty_convs" to "Aucune conversation précédente. Posez une question pour commencer!",
            "recent_conversations" to "Conversations récentes",
            "start_chat" to "Nouvelle Conversation",
            "copy_success" to "Copié dans le presse-papiers",
            "liked_success" to "Avis enregistré, merci!",
            "search_conv_placeholder" to "Rechercher...",
            "rename_dialog_title" to "Renommer la Conversation",
            "delete_dialog_title" to "Supprimer la Conversation",
            "delete_dialog_msg" to "Êtes-vous sûr de vouloir supprimer cette conversation ?",
            "confirm" to "Confirmer",
            "cancel" to "Annuler",
            "conv_starters" to "Sujets de conversation",
            "suggestions" to "Suggestions",
            "starter_initial_query" to "Parlez-moi des exigences pour : %s"
        )
    )

    fun string(key: String, lang: String): String {
        return strings[lang]?.get(key) ?: strings["en"]?.get(key) ?: key
    }
}

// Sofia Theme Apple iOS 18 Vivid Colors (Siri Violet + Vivid Emerald + Electric Blue)
private val SofiaGradientStart = SiriViolet
private val SofiaGradientEnd = ElectricBlue
private val SofiaGlowColor = SiriViolet
private val GlassBackground = SiriVioletTint
private val GlassBorder = Color(0xFFE5E5EA)
private val SofiaTextPrimary = AppleInk
private val SofiaTextSecondary = AppleTextMuted
private val BentoCardBackground = AppleCardSurface
private val BentoCardBorder = Color(0xFFE5E5EA)
private val SofiaGreen = VividEmerald
private val SofiaWhite = Color(0xFFFFFFFF)
private val SofiaRed = AlertRed
private val SofiaLightBorder = Color(0xFFE5E5EA)
private val SofiaLightGreen = VividEmeraldTint

@Composable
fun SofiaScreen(
    viewModel: SofiaViewModel,
    userFullName: String,
    selectedLanguageCode: String,
    onNavigateAction: (SofiaActionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val conversationsList by viewModel.searchedConversations.collectAsStateWithLifecycle()
    val activeMessagesList by viewModel.activeMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val typingText by viewModel.typingResponseText.collectAsStateWithLifecycle()
    val searchQuery by viewModel.conversationSearchQuery.collectAsStateWithLifecycle()

    val layoutDirection = if (selectedLanguageCode == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    val resolvedBentoBackground = BentoBackground

    // Handle Smart Action Events triggered from inside ViewModel
    LaunchedEffect(Unit) {
        viewModel.actionEvent.collectLatest { event ->
            onNavigateAction(event)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8F7))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // PREMIUM HEADER
                SofiaHeader(
                    onBack = {
                        if (uiState is SofiaUiState.ActiveChat) {
                            viewModel.goBackToLanding()
                        }
                    },
                    showBackButton = uiState is SofiaUiState.ActiveChat,
                    selectedLanguageCode = selectedLanguageCode
                )

                // MAIN CONTENT CONTAINER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (val state = uiState) {
                        is SofiaUiState.Landing -> {
                             SofiaLandingPage(
                                 viewModel = viewModel,
                                 userFullName = userFullName,
                                 conversations = conversationsList,
                                 searchQuery = searchQuery,
                                 selectedLanguageCode = selectedLanguageCode
                             )
                        }
                        is SofiaUiState.ActiveChat -> {
                            SofiaActiveChatPage(
                                viewModel = viewModel,
                                messages = activeMessagesList,
                                isAiLoading = isAiLoading,
                                typingText = typingText,
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
fun SofiaHeader(
    onBack: () -> Unit,
    showBackButton: Boolean,
    selectedLanguageCode: String
) {
    var showDisclaimerDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = SofiaLightBorder, shape = RoundedCornerShape(0.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (showBackButton) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("sofia_header_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SofiaTextPrimary
                    )
                }
            }

            // Subtly glowing avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SofiaGradientStart, SofiaGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Sofia",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = SofiaLocalization.string("sofia_title", selectedLanguageCode),
                        color = SofiaTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = SofiaLocalization.string("sofia_status_online", selectedLanguageCode),
                        color = SofiaGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = SofiaLocalization.string("sofia_subtitle", selectedLanguageCode),
                    color = SofiaTextSecondary,
                    fontSize = 12.sp
                )
            }

            // Tiny info icon for disclaimer & sources
            IconButton(
                onClick = { showDisclaimerDialog = true },
                modifier = Modifier.testTag("sofia_header_info")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Disclaimer and Info",
                    tint = SofiaGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }

    // Compact sources and disclaimer alert dialog
    if (showDisclaimerDialog) {
        AlertDialog(
            onDismissRequest = { showDisclaimerDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = SofiaGreen,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = SofiaLocalization.string("source_title", selectedLanguageCode),
                    color = SofiaTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        SofiaBadgeItem(label = SofiaLocalization.string("source_ai", selectedLanguageCode), color = SofiaGreen)
                        SofiaBadgeItem(label = SofiaLocalization.string("source_official", selectedLanguageCode), color = Color(0xFF1B5E20))
                        SofiaBadgeItem(label = SofiaLocalization.string("source_community", selectedLanguageCode), color = SofiaRed)
                    }
                    Text(
                        text = SofiaLocalization.string("disclaimer", selectedLanguageCode),
                        color = SofiaTextPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDisclaimerDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = SofiaGreen)
                ) {
                    Text(text = "OK", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun SofiaLandingPage(
    viewModel: SofiaViewModel,
    userFullName: String,
    conversations: List<SofiaConversation>,
    searchQuery: String,
    selectedLanguageCode: String
) {
    var showRenameDialog by remember { mutableStateOf<Long?>(null) }
    var renameText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 1. Memory: Recent Conversations & New Chat Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SofiaLocalization.string("recent_conversations", selectedLanguageCode),
                    color = SofiaTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = {
                        val title = "Chat ${SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date())}"
                        viewModel.startNewConversation(title = title, selectedLanguageCode = selectedLanguageCode)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SofiaGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = SofiaLocalization.string("start_chat", selectedLanguageCode),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. Search Bar for conversations
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.conversationSearchQuery.value = it },
                placeholder = { Text(SofiaLocalization.string("search_conv_placeholder", selectedLanguageCode), color = SofiaTextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SofiaTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SofiaTextPrimary,
                    unfocusedTextColor = SofiaTextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = SofiaGreen,
                    unfocusedBorderColor = BentoCardBorder
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        // 3. Conversation Items List
        if (conversations.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                             imageVector = Icons.Default.Forum,
                             contentDescription = null,
                             tint = SofiaTextSecondary,
                             modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                             text = SofiaLocalization.string("empty_convs", selectedLanguageCode),
                             color = SofiaTextSecondary,
                             fontSize = 14.sp,
                             textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(conversations) { conv ->
                SofiaConversationItem(
                    conversation = conv,
                    onClick = { viewModel.selectConversation(conv.id) },
                    onPin = { viewModel.togglePinConversation(conv.id) },
                    onRename = {
                        renameText = conv.title
                        showRenameDialog = conv.id
                    },
                    onDelete = { showDeleteDialog = conv.id }
                )
            }
        }

        // 4. Divider showing Knowledge Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = SofiaLightBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Hello Greeting Card (Knowledge Section)
        item {
            SofiaGreetingCard(
                userFullName = userFullName,
                selectedLanguageCode = selectedLanguageCode
            )
        }

        // 6. SPECIAL FEATURE: My Journey (Knowledge Section)
        item {
            SofiaMyJourneyCard(
                viewModel = viewModel,
                selectedLanguageCode = selectedLanguageCode
            )
        }

        // 7. Conversation Starters Quick Topics (Knowledge Section)
        item {
            SofiaConversationStarters(
                viewModel = viewModel,
                selectedLanguageCode = selectedLanguageCode
            )
        }

        // 8. Suggestions Chips Grid (Knowledge Section)
        item {
            SofiaSuggestionChips(
                viewModel = viewModel,
                selectedLanguageCode = selectedLanguageCode
            )
        }

        // 9. SPECIAL FEATURE: What users usually ask (Knowledge Section)
        item {
            SofiaPopularQuestions(
                viewModel = viewModel,
                selectedLanguageCode = selectedLanguageCode
            )
        }
    }

    // Rename Dialog
    if (showRenameDialog != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text(SofiaLocalization.string("rename_dialog_title", selectedLanguageCode)) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = showRenameDialog
                        if (id != null && renameText.isNotBlank()) {
                            viewModel.renameConversation(id, renameText)
                        }
                        showRenameDialog = null
                    }
                ) {
                    Text(SofiaLocalization.string("confirm", selectedLanguageCode))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text(SofiaLocalization.string("cancel", selectedLanguageCode))
                }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(SofiaLocalization.string("delete_dialog_title", selectedLanguageCode)) },
            text = { Text(SofiaLocalization.string("delete_dialog_msg", selectedLanguageCode)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = showDeleteDialog
                        if (id != null) {
                            viewModel.deleteConversation(id)
                        }
                        showDeleteDialog = null
                    }
                ) {
                    Text(SofiaLocalization.string("confirm", selectedLanguageCode), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(SofiaLocalization.string("cancel", selectedLanguageCode))
                }
            }
        )
    }
}

@Composable
fun SofiaGreetingCard(
    userFullName: String,
    selectedLanguageCode: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, SofiaLightBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column {
            val cleanName = userFullName.trim()
            val headerTitle = if (cleanName.isNotBlank()) {
                SofiaLocalization.string("hello_user", selectedLanguageCode).format(cleanName)
            } else {
                SofiaLocalization.string("sofia_title", selectedLanguageCode)
            }
            Text(
                text = headerTitle,
                color = SofiaTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = SofiaLocalization.string("intro_text", selectedLanguageCode),
                color = SofiaTextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = SofiaLocalization.string("ask_me_about", selectedLanguageCode),
                color = SofiaTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Topic bullets
            val topics = listOf(
                SofiaLocalization.string("residence_permits", selectedLanguageCode),
                SofiaLocalization.string("questure", selectedLanguageCode),
                SofiaLocalization.string("appointments", selectedLanguageCode),
                SofiaLocalization.string("documents", selectedLanguageCode),
                SofiaLocalization.string("renewals", selectedLanguageCode),
                SofiaLocalization.string("citizenship", selectedLanguageCode),
                SofiaLocalization.string("family_reunification", selectedLanguageCode),
                SofiaLocalization.string("travel", selectedLanguageCode),
                SofiaLocalization.string("work", selectedLanguageCode),
                SofiaLocalization.string("bureaucracy", selectedLanguageCode)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                topics.forEach { topic ->
                    Box(
                        modifier = Modifier
                            .background(SofiaLightGreen, RoundedCornerShape(8.dp))
                            .border(0.5.dp, SofiaGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = topic,
                            color = SofiaGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SofiaMyJourneyCard(
    viewModel: SofiaViewModel,
    selectedLanguageCode: String
) {
    val credentials by viewModel.userCredentialsState.collectAsStateWithLifecycle()
    val fullName = credentials?.fullName?.trim().orEmpty()
    val hasDossierCode = !credentials?.numeroAssicurata.isNullOrBlank() ||
        !credentials?.numeroPratica.isNullOrBlank() ||
        !credentials?.codiceIstanza.isNullOrBlank()

    val permitType = credentials?.selectedPermitType?.takeIf { it.isNotBlank() }
        ?: if (selectedLanguageCode == "it") "Non specificato" else "Not specified"
    val questuraName = credentials?.selectedQuesturaId?.takeIf { it.isNotBlank() }
        ?: if (selectedLanguageCode == "it") "Non selezionata" else "Not selected"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, SofiaLightBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FolderShared,
                    contentDescription = null,
                    tint = SofiaGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = SofiaLocalization.string("my_journey_title", selectedLanguageCode),
                    color = SofiaTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (fullName.isNotBlank()) {
                Text(
                    text = if (selectedLanguageCode == "it") "Intestatario: $fullName"
                           else "Holder: $fullName",
                    color = SofiaTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = if (selectedLanguageCode == "it") "Tipologia pratica: $permitType"
                       else if (selectedLanguageCode == "es") "Tipo de trámite: $permitType"
                       else if (selectedLanguageCode == "fr") "Type de titre : $permitType"
                       else if (selectedLanguageCode == "ar") "نوع التصريح: $permitType"
                       else "Permit category: $permitType",
                color = SofiaTextPrimary,
                fontSize = 13.sp
            )
            Text(
                text = if (selectedLanguageCode == "it") "Sede competente: $questuraName"
                       else if (selectedLanguageCode == "es") "Oficina: $questuraName"
                       else if (selectedLanguageCode == "fr") "Préfecture : $questuraName"
                       else if (selectedLanguageCode == "ar") "المديرية: $questuraName"
                       else "Competent office: $questuraName",
                color = SofiaTextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val badgeColor = if (hasDossierCode) SofiaGreen else SofiaTextSecondary
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (hasDossierCode) {
                            if (selectedLanguageCode == "it") "Codici registrati" else "Codes configured"
                        } else {
                            if (selectedLanguageCode == "it") "Codici non inseriti" else "No codes configured"
                        },
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SofiaConversationStarters(
    viewModel: SofiaViewModel,
    selectedLanguageCode: String
) {
    val starters = listOf(
        Triple("", "documents", "documents"),
        Triple("", "travel", "travel"),
        Triple("", "work", "work"),
        Triple("", "family_reunification", "family"),
        Triple("", "questure", "offices"),
        Triple("", "appointments", "appointments"),
        Triple("", "residence_permits", "permits"),
        Triple("", "bureaucracy", "legal")
    )

    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = SofiaLocalization.string("conv_starters", selectedLanguageCode),
            color = SofiaTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        
        // Grid display
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                starters.take(4).forEach { item ->
                    val localizedTitle = SofiaLocalization.string(item.second, selectedLanguageCode)
                    SofiaStarterCard(title = localizedTitle, onClick = {
                        val initialMsgPattern = SofiaLocalization.string("starter_initial_query", selectedLanguageCode)
                        viewModel.startNewConversation(
                            title = localizedTitle,
                            initialMessage = String.format(initialMsgPattern, localizedTitle),
                            category = item.third,
                            selectedLanguageCode = selectedLanguageCode
                        )
                    })
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                starters.drop(4).forEach { item ->
                    val localizedTitle = SofiaLocalization.string(item.second, selectedLanguageCode)
                    SofiaStarterCard(title = localizedTitle, onClick = {
                        val initialMsgPattern = SofiaLocalization.string("starter_initial_query", selectedLanguageCode)
                        viewModel.startNewConversation(
                            title = localizedTitle,
                            initialMessage = String.format(initialMsgPattern, localizedTitle),
                            category = item.third,
                            selectedLanguageCode = selectedLanguageCode
                        )
                    })
                }
            }
        }
    }
}

@Composable
fun SofiaStarterCard(title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SofiaLightBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 14.dp)
        ) {
            Text(
                text = title,
                color = SofiaTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SofiaSuggestionChips(
    viewModel: SofiaViewModel,
    selectedLanguageCode: String
) {
    val chips = when (selectedLanguageCode) {
        "it" -> listOf(
            "Posso viaggiare?",
            "Posso lavorare?",
            "Di quali documenti ho bisogno?",
            "Spiega il mio stato attuale",
            "Quando devo rinnovare?",
            "Dov'è la mia Questura?",
            "Cosa portare all'appuntamento?",
            "Quanto tempo dura il processo?",
            "Cosa significa Step 2?",
            "Cosa succede ora?",
            "Ho perso la ricevuta",
            "Ricongiungimento familiare",
            "Articolo 31",
            "Asilo politico",
            "Soggiorno lungo termine",
            "Cittadinanza"
        )
        "ar" -> listOf(
            "هل يمكنني السفر؟",
            "هل يمكنني العمل؟",
            "ما هي المستندات المطلوبة؟",
            "شرح حالتي الحالية",
            "متى يجب علي التجديد؟",
            "أين هو مركز الكويستورا الخاص بي؟",
            "ما الذي يجب أن أحضره للموعد؟",
            "كم من الوقت تستغرق هذه العملية؟",
            "ماذا تعني الخطوة الثانية؟",
            "ماذا يحدث بعد ذلك؟",
            "فقدت الإيصال الخاص بي",
            "لم شمل الأسرة",
            "المادة 31",
            "اللجوء السياسي",
            "الإقامة طويلة الأجل",
            "الجنسية"
        )
        "es" -> listOf(
            "¿Puedo viajar?",
            "¿Puedo trabajar?",
            "¿Qué documentos necesito?",
            "Explicar mi estado actual",
            "¿Cuándo debo renovar?",
            "¿Dónde está mi Questura?",
            "¿Qué debo llevar a mi cita?",
            "¿Cuánto dura este proceso?",
            "¿Qué significa el Paso 2?",
            "¿Qué pasa ahora?",
            "Perdí mi recibo",
            "Reagrupación familiar",
            "Artículo 31",
            "Asilo político",
            "Residencia de larga duración",
            "Ciudadanía"
        )
        "fr" -> listOf(
            "Puis-je voyager ?",
            "Puis-je travailler ?",
            "Quels documents faut-il ?",
            "Expliquer mon statut actuel",
            "Quand dois-je renouveler ?",
            "Où est ma Questura ?",
            "Que dois-je apporter au RDV ?",
            "Combien de temps ça prend ?",
            "Que signifie l'Étape 2 ?",
            "Que se passe-t-il ensuite ?",
            "J'ai perdu mon récépissé",
            "Regroupement familial",
            "Article 31",
            "Asile politique",
            "Résidence longue durée",
            "Citoyenneté"
        )
        else -> listOf(
            "Can I travel?",
            "Can I work?",
            "What documents do I need?",
            "Explain my current status",
            "When should I renew?",
            "Where is my Questura?",
            "What should I bring to my appointment?",
            "How long does this process take?",
            "What does Step 2 mean?",
            "What happens next?",
            "Lost my receipt",
            "Family reunification",
            "Article 31",
            "Political asylum",
            "Long-term residence",
            "Citizenship"
        )
    }

    Column(modifier = Modifier.padding(top = 18.dp)) {
        Text(
            text = SofiaLocalization.string("suggestions", selectedLanguageCode),
            color = SofiaTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEach { chip ->
                SuggestionChip(
                    onClick = {
                        viewModel.startNewConversation(
                            title = chip,
                            initialMessage = chip,
                            selectedLanguageCode = selectedLanguageCode
                        )
                    },
                    label = { Text(text = chip, color = SofiaTextPrimary, fontSize = 12.sp) },
                    border = BorderStroke(1.dp, SofiaLightBorder),
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun SofiaPopularQuestions(
    viewModel: SofiaViewModel,
    selectedLanguageCode: String
) {
    val popular = when (selectedLanguageCode) {
        "it" -> listOf(
            "Come posso richiedere il ricongiungimento familiare in Italia?" to "Ricongiungimento Familiare",
            "Posso viaggiare mentre il mio rinnovo del permesso è in corso?" to "Regole di Viaggio",
            "Cos'è l'Articolo 31 e chi ne ha diritto?" to "Guida Articolo 31"
        )
        "ar" -> listOf(
            "كيف يمكنني التقدم بطلب لم شمل الأسرة في إيطاليا؟" to "لم شمل الأسرة",
            "هل يمكنني السفر أثناء فترة انتظار تجديد تصريح الإقامة؟" to "قواعد السفر",
            "ما هي المادة 31 ومن هو المؤهل لها؟" to "دليل المادة 31"
        )
        "es" -> listOf(
            "¿Cómo solicito la reagrupación familiar en Italia?" to "Reagrupación Familiar",
            "¿Puedo viajar mientras mi renovación de permesso está pendiente?" to "Reglas de Viaje",
            "¿Qué es el Artículo 31 y quién es elegible?" to "Guía del Artículo 31"
        )
        "fr" -> listOf(
            "Comment demander un regroupement familial en Italie ?" to "Regroupement Familial",
            "Puis-je voyager pendant que le renouvellement de mon titre de séjour est en cours ?" to "Règles de Voyage",
            "Qu'est-ce que l'Article 31 et qui est éligible ?" to "Guide Article 31"
        )
        else -> listOf(
            "How do I apply for family reunification in Italy?" to "Family Reunification",
            "Can I travel while my permesso renewal is pending?" to "Travel Rules",
            "What is Article 31 and who is eligible?" to "Article 31 Guide"
        )
    }

    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = SofiaLocalization.string("what_users_ask", selectedLanguageCode),
            color = SofiaTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        popular.forEach { q ->
            Card(
                onClick = {
                    viewModel.startNewConversation(
                        title = q.second,
                        initialMessage = q.first,
                        selectedLanguageCode = selectedLanguageCode
                    )
                },
                colors = CardDefaults.cardColors(containerColor = SofiaLightGreen),
                border = BorderStroke(0.5.dp, SofiaGreen.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = SofiaGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = q.first,
                        color = SofiaTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SofiaConversationItem(
    conversation: SofiaConversation,
    onClick: () -> Unit,
    onPin: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (conversation.isPinned) SofiaGreen else SofiaLightBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = if (conversation.isPinned) Icons.Default.PushPin else Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = if (conversation.isPinned) SofiaGreen else SofiaTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = conversation.title,
                    color = SofiaTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
            
            // Actions menu
            Row {
                IconButton(onClick = onPin, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pin",
                        tint = if (conversation.isPinned) SofiaGreen else SofiaTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onRename, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Rename",
                        tint = SofiaTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SofiaActiveChatPage(
    viewModel: SofiaViewModel,
    messages: List<SofiaMessage>,
    isAiLoading: Boolean,
    typingText: String,
    selectedLanguageCode: String
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Scroll to bottom on load/new message
    LaunchedEffect(messages.size, typingText.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // QUICK TOOLS GRID ABOVE CHAT INPUT
        SofiaQuickToolsRow(viewModel = viewModel, selectedLanguageCode = selectedLanguageCode)

        // CHAT MESSAGE LIST
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
        ) {
            items(messages) { msg ->
                SofiaMessageBubble(
                    message = msg,
                    onLike = { liked -> viewModel.likeMessage(msg.id, liked) },
                    selectedLanguageCode = selectedLanguageCode
                )
            }

            // Typing animation / loading indicator
            if (isAiLoading) {
                item {
                    if (typingText.isNotEmpty()) {
                        SofiaMessageBubble(
                            message = SofiaMessage(
                                conversationId = 0L,
                                sender = "sofia",
                                text = typingText,
                                sourceType = "ai"
                            ),
                            onLike = {},
                            isStreaming = true,
                            selectedLanguageCode = selectedLanguageCode
                        )
                    } else {
                        SofiaTypingSkeleton()
                    }
                }
            }
        }

        // PERSISTENT LEGAL SAFEGUARD CAPTION (Art. 348 c.p.) ABOVE CHAT INPUT BAR
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sofia_persistent_disclaimer_banner"),
            color = Color(0xFFFFF8E1),
            border = BorderStroke(1.dp, Color(0xFFFFE082))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = com.example.util.Strings.get("sofia_persistent_disclaimer", selectedLanguageCode),
                    color = Color(0xFF4E342E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 15.sp
                )
            }
        }

        // DOCKED INPUT BAR WITH REAL SPEECH RECOGNIZER & SEND ACTION
        val context = LocalContext.current
        val speechLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val spoken = result.data
                ?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputText = spoken
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            border = BorderStroke(1.dp, AppleBorderHairline),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Real Android Speech Recognition Button
                Surface(
                    onClick = {
                        try {
                            val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, selectedLanguageCode)
                            }
                            speechLauncher.launch(intent)
                        } catch (_: Exception) {
                            Toast.makeText(
                                context,
                                "Riconoscimento vocale di sistema non disponibile su questo dispositivo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    shape = CircleShape,
                    color = SiriVioletTint,
                    border = BorderStroke(1.dp, SiriViolet.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .size(44.dp)
                        .appleBounceClick()
                        .testTag("sofia_voice_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Input Vocale",
                            tint = SiriViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Full-Capsule Input Pill
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = SofiaLocalization.string("placeholder_chat", selectedLanguageCode),
                            color = SofiaTextSecondary,
                            fontSize = 14.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = SiriViolet,
                        unfocusedBorderColor = AppleBorderHairline,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = CircleShape,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText, selectedLanguageCode)
                            inputText = ""
                        }
                    }),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sofia_chat_input")
                )

                // Send Button
                Surface(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText, selectedLanguageCode)
                            inputText = ""
                        }
                    },
                    shape = CircleShape,
                    color = VividEmerald,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(46.dp)
                        .appleBounceClick()
                        .testTag("sofia_send_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SofiaQuickToolsRow(
    viewModel: SofiaViewModel,
    selectedLanguageCode: String
) {
    val tools = listOf(
        Pair("Analisi Stato Pratica", SofiaActionEvent.ExplainStatus),
        Pair("Documenti Obbligatori", SofiaActionEvent.ShowRequiredDocuments),
        Pair("Convocazione Questura", SofiaActionEvent.ShowAppointment),
        Pair("Sedi & Uffici Questura", SofiaActionEvent.ShowMyQuestura),
        Pair("Portali Ufficiali", SofiaActionEvent.OpenOfficialWebsite),
        Pair("Procedure di Rinnovo", SofiaActionEvent.OpenRenewalGuide)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tools.forEach { tool ->
            Surface(
                onClick = { viewModel.triggerSmartAction(tool.second) },
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.appleBounceClick()
            ) {
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = tool.first,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SofiaMessageBubble(
    message: SofiaMessage,
    onLike: (Boolean) -> Unit,
    isStreaming: Boolean = false,
    selectedLanguageCode: String
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val isUser = message.sender == "user"

    val alignment = if (isUser) Alignment.End else Alignment.Start
    val siriBorder = Brush.linearGradient(
        colors = listOf(SiriViolet, ElectricBlue, VividEmerald)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            Surface(
                color = if (isUser) ElectricBlue else MaterialTheme.colorScheme.surface,
                border = if (isUser) null else BorderStroke(1.5.dp, siriBorder),
                shadowElevation = if (isUser) 2.dp else 4.dp,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 6.dp,
                    bottomEnd = if (isUser) 6.dp else 20.dp
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = SiriViolet,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "PRONTUARIO NORMATIVO • D.LGS. 286/98",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SiriViolet,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }

                    // Message Text
                    Text(
                        text = message.text,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )

                    // Smart direct navigation shortcuts if detected in AI responses
                    if (!isUser && !isStreaming) {
                        SofiaResponseShortcuts(messageText = message.text, onLike = onLike, messageLiked = message.liked)
                    }
                }
            }
        }

        // Action indicators: timestamp, copy, like/dislike, source badges
        if (!isStreaming) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isUser) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val sourceColor = when (message.sourceType) {
                            "official" -> VividEmerald
                            "community" -> SunsetTangerine
                            else -> SiriViolet
                        }
                        val sourceLabel = when (message.sourceType) {
                            "official" -> "Fonte Ufficiale"
                            "community" -> "Segnalazione Utenti"
                            else -> "Rif. D.Lgs. 286/98"
                        }
                        Surface(
                            shape = CircleShape,
                            color = sourceColor.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = sourceLabel,
                                color = if (sourceColor == VividEmerald) Color(0xFF007A33) else sourceColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Copy Button
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(message.text))
                                Toast.makeText(context, SofiaLocalization.string("copy_success", selectedLanguageCode), Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SofiaTextSecondary, modifier = Modifier.size(13.dp))
                        }
                    }
                }

                // Timestamp
                val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                Text(
                    text = timeStr,
                    color = SofiaTextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun SofiaResponseShortcuts(
    messageText: String,
    onLike: (Boolean) -> Unit,
    messageLiked: Boolean?
) {
    // Detect keywords to suggest buttons dynamically
    val showGuideButton = messageText.contains("guide", ignoreCase = true) || messageText.contains("manual", ignoreCase = true)
    val showQuesturaButton = messageText.contains("questura", ignoreCase = true) || messageText.contains("office", ignoreCase = true)

    if (showGuideButton || showQuesturaButton || messageLiked != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like/Dislike responses
            IconButton(onClick = { onLike(true) }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Like",
                    tint = if (messageLiked == true) SofiaGreen else SofiaTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
            IconButton(onClick = { onLike(false) }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.ThumbDown,
                    contentDescription = "Dislike",
                    tint = if (messageLiked == false) SofiaRed else SofiaTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun SofiaTypingSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, SofiaLightBorder),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            modifier = Modifier.width(100.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Smooth blinking indicator dots
                val infiniteTransition = rememberInfiniteTransition(label = "typing")
                val alpha1 by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes { durationMillis = 600 },
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot1"
                )
                val alpha2 by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes { durationMillis = 600; delayMillis = 200 },
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot2"
                )
                val alpha3 by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes { durationMillis = 600; delayMillis = 400 },
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot3"
                )

                Box(modifier = Modifier.size(6.dp).graphicsLayer(alpha = alpha1).background(SofiaGreen, CircleShape))
                Box(modifier = Modifier.size(6.dp).graphicsLayer(alpha = alpha2).background(SofiaGreen, CircleShape))
                Box(modifier = Modifier.size(6.dp).graphicsLayer(alpha = alpha3).background(SofiaGreen, CircleShape))
            }
        }
    }
}

@Composable
fun SofiaTrustFooter(
    selectedLanguageCode: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BentoCardBackground)
            .border(width = 1.dp, color = BentoCardBorder)
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SofiaLocalization.string("source_title", selectedLanguageCode) + ":",
                    color = SofiaTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Color-coded badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SofiaBadgeItem(label = SofiaLocalization.string("source_ai", selectedLanguageCode), color = SofiaGlowColor)
                    SofiaBadgeItem(label = SofiaLocalization.string("source_official", selectedLanguageCode), color = BentoBadgeGreen)
                    SofiaBadgeItem(label = SofiaLocalization.string("source_community", selectedLanguageCode), color = BentoBadgeOrange)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SofiaLocalization.string("disclaimer", selectedLanguageCode),
                color = SofiaTextSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun SofiaBadgeItem(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
