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
            "sofia_title" to "Sofia AI",
            "sofia_subtitle" to "Esperta di Immigrazione Italiana",
            "sofia_status_online" to "● Online",
            "hello_user" to "Ciao %s 👋",
            "intro_text" to "Sono Sofia. La tua consulente personale per l'immigrazione italiana. Sono qui per aiutarti a navigare la burocrazia 24/7.",
            "ask_me_about" to "Chiedimi qualsiasi cosa su:",
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
            "my_journey_title" to "Il Mio Percorso",
            "what_users_ask" to "Cosa chiedono spesso gli utenti",
            "source_title" to "Fonti delle informazioni",
            "source_ai" to "IA",
            "source_official" to "Ufficiale",
            "source_community" to "Comunità",
            "disclaimer" to "Le risposte dell'IA sono solo informative. Verifica sempre i requisiti legali tramite le autorità italiane ufficiali.",
            "placeholder_chat" to "Chiedi a Sofia...",
            "empty_convs" to "Nessuna conversazione precedente. Fai una domanda per iniziare!",
            "recent_conversations" to "Conversazioni recenti",
            "start_chat" to "Nuova Conversazione",
            "copy_success" to "Copiato negli appunti",
            "liked_success" to "Feedback registrato, grazie!",
            "search_conv_placeholder" to "Cerca conversazioni...",
            "rename_dialog_title" to "Rinomina Conversazione",
            "delete_dialog_title" to "Elimina Conversazione",
            "delete_dialog_msg" to "Sei sicuro di voler eliminare questa conversazione?",
            "confirm" to "Conferma",
            "cancel" to "Annulla",
            "conv_starters" to "Spunti di conversazione",
            "suggestions" to "Suggerimenti",
            "starter_initial_query" to "Parlami dei requisiti per: %s"
        ),
        "en" to mapOf(
            "sofia_title" to "Sofia AI",
            "sofia_subtitle" to "Italian Immigration Expert",
            "sofia_status_online" to "● Online",
            "hello_user" to "Hello %s 👋",
            "intro_text" to "I'm Sofia. Your personal Italian immigration advisor. I am here to help you navigate bureaucracy 24/7.",
            "ask_me_about" to "Ask me anything about:",
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
            "my_journey_title" to "My Journey",
            "what_users_ask" to "What users usually ask",
            "source_title" to "Information sources",
            "source_ai" to "AI",
            "source_official" to "Official",
            "source_community" to "Community",
            "disclaimer" to "AI responses are informational only. Always verify legal requirements through official Italian authorities.",
            "placeholder_chat" to "Ask Sofia...",
            "empty_convs" to "No previous conversations. Ask a question to start!",
            "recent_conversations" to "Recent conversations",
            "start_chat" to "New Conversation",
            "copy_success" to "Copied to clipboard",
            "liked_success" to "Feedback recorded, thank you!",
            "search_conv_placeholder" to "Search conversations...",
            "rename_dialog_title" to "Rename Conversation",
            "delete_dialog_title" to "Delete Conversation",
            "delete_dialog_msg" to "Are you sure you want to delete this conversation?",
            "confirm" to "Confirm",
            "cancel" to "Cancel",
            "conv_starters" to "Conversation Starters",
            "suggestions" to "Suggestions",
            "starter_initial_query" to "Tell me about the requirements for: %s"
        ),
        "ar" to mapOf(
            "sofia_title" to "صوفيا الذكية",
            "sofia_subtitle" to "خبير الهجرة الإيطالية",
            "sofia_status_online" to "● متصل الآن",
            "hello_user" to "أهلاً %s 👋",
            "intro_text" to "أنا صوفيا. مستشارتك الشخصية للهجرة الإيطالية. أنا هنا لمساعدتك في التغلب على المعاملات الورقية والروتين 24/7.",
            "ask_me_about" to "اسألني عن أي شيء يخص:",
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
            "my_journey_title" to "مساري الخاص",
            "what_users_ask" to "ما يسأله المستخدمون عادةً",
            "source_title" to "مصادر المعلومات",
            "source_ai" to "ذكاء اصطناعي",
            "source_official" to "رسمي",
            "source_community" to "مجتمعي",
            "disclaimer" to "إجابات الذكاء الاصطناعي هي لأغراض إعلامية فقط. تأكد دائمًا من المتطلبات القانونية من خلال السلطات الإيطالية الرسمية.",
            "placeholder_chat" to "اسأل صوفيا...",
            "empty_convs" to "لا توجد محادثات سابقة. اسأل سؤالاً للبدء!",
            "recent_conversations" to "المحادثات الأخيرة",
            "start_chat" to "محادثة جديدة",
            "copy_success" to "تم النسخ إلى الحافظة",
            "liked_success" to "تم تسجيل رأيك، شكراً لك!",
            "search_conv_placeholder" to "البحث في المحادثات...",
            "rename_dialog_title" to "إعادة تسمية المحادثة",
            "delete_dialog_title" to "حذف المحادثة",
            "delete_dialog_msg" to "هل أنت متأكد من رغبتك في حذف هذه المحادثة؟",
            "confirm" to "تأكيد",
            "cancel" to "إلغاء",
            "conv_starters" to "مواضيع لبدء المحادثة",
            "suggestions" to "اقتراحات",
            "starter_initial_query" to "أخبرني عن متطلبات: %s"
        ),
        "es" to mapOf(
            "sofia_title" to "Sofía AI",
            "sofia_subtitle" to "Experta en Inmigración Italiana",
            "sofia_status_online" to "● En línea",
            "hello_user" to "Hola %s 👋",
            "intro_text" to "Soy Sofía. Tu asesora personal de inmigración italiana. Estoy aquí para ayudarte a navegar la burocracia 24/7.",
            "ask_me_about" to "Pregúntame cualquier cosa sobre:",
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
            "my_journey_title" to "Mi Camino",
            "what_users_ask" to "Lo que la gente suele preguntar",
            "source_title" to "Fuentes de información",
            "source_ai" to "IA",
            "source_official" to "Oficial",
            "source_community" to "Comunidad",
            "disclaimer" to "Las respuestas de la IA son informativas. Siempre verifique los requisitos legales a través de las autoridades italianas oficiales.",
            "placeholder_chat" to "Pregúntale a Sofía...",
            "empty_convs" to "Sin conversaciones previas. ¡Haz una pregunta para comenzar!",
            "recent_conversations" to "Conversaciones recientes",
            "start_chat" to "Nueva Conversación",
            "copy_success" to "Copiado al portapapeles",
            "liked_success" to "¡Comentario registrado, gracias!",
            "search_conv_placeholder" to "Buscar conversaciones...",
            "rename_dialog_title" to "Renombrar Conversación",
            "delete_dialog_title" to "Eliminar Conversación",
            "delete_dialog_msg" to "¿Estás seguro de que deseas eliminar esta conversación?",
            "confirm" to "Confirmar",
            "cancel" to "Cancelar",
            "conv_starters" to "Temas de conversación",
            "suggestions" to "Sugerencias",
            "starter_initial_query" to "Cuéntame sobre los requisitos para: %s"
        ),
        "fr" to mapOf(
            "sofia_title" to "Sofia AI",
            "sofia_subtitle" to "Experte en Immigration Italienne",
            "sofia_status_online" to "● En ligne",
            "hello_user" to "Bonjour %s 👋",
            "intro_text" to "Je suis Sofia. Votre conseillère personnelle en immigration italienne. Je suis là pour vous aider à naviguer la bureaucratie 24/7.",
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
            "disclaimer" to "Les réponses de l'IA sont informatives. Veuillez toujours vérifier auprès des autorités italiennes officielles.",
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

// Sofia Theme Premium Colors - Styled for Clean Light Green-White-Red Theme
private val SofiaGradientStart = Color(0xFF008C45) // Italian Flag Green
private val SofiaGradientEnd = Color(0xFF1B5E20)   // Deep Green
private val SofiaGlowColor = Color(0xFF008C45)     // Bright Green
private val GlassBackground = Color(0xFFEAF5EF)    // Light Green Pastel
private val GlassBorder = Color(0xFFE2E8F0)        // Slate border
private val SofiaTextPrimary = Color(0xFF1E293B)   // Dark slate text
private val SofiaTextSecondary = Color(0xFF64748B) // Medium slate text
private val BentoCardBackground = Color.White       // Pure white cards
private val BentoCardBorder = Color(0xFFE2E8F0)     // Clean border
private val SofiaGreen = Color(0xFF008C45)
private val SofiaWhite = Color(0xFFFFFFFF)
private val SofiaRed = Color(0xFFCD212A)
private val SofiaLightBorder = Color(0xFFE2E8F0)
private val SofiaLightGreen = Color(0xFFEAF5EF)

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
            Text(
                text = SofiaLocalization.string("hello_user", selectedLanguageCode).format(userFullName),
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
                    imageVector = Icons.Default.DirectionsRun,
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
            
            // Custom summary items dynamically loaded in text style
            Text(
                text = "Hello Karim.",
                color = SofiaTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You are currently tracking: Work Permit Renewal",
                color = SofiaTextPrimary,
                fontSize = 13.sp
            )
            Text(
                text = "Questura: Questura di Piacenza",
                color = SofiaTextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(BentoBadgeOrange.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "In trattazione", color = BentoBadgeOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Next Appointment: 21 August", color = SofiaTextPrimary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Missing documents: Passport copy, Marca da Bollo",
                color = SofiaTextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Recommended action: Prepare your documents before your fingerprint appointment.",
                color = SofiaGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SofiaConversationStarters(
    viewModel: SofiaViewModel,
    selectedLanguageCode: String
) {
    val starters = listOf(
        Triple("📄", "documents", "documents"),
        Triple("✈️", "travel", "travel"),
        Triple("💼", "work", "work"),
        Triple("👨\u200D👩\u200D👧", "family_reunification", "family"),
        Triple("🏢", "questure", "offices"),
        Triple("📅", "appointments", "appointments"),
        Triple("🛂", "residence_permits", "permits"),
        Triple("⚖️", "bureaucracy", "legal")
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
                    val cardTitle = "${item.first} $localizedTitle"
                    SofiaStarterCard(title = cardTitle, onClick = {
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
                    val cardTitle = "${item.first} $localizedTitle"
                    SofiaStarterCard(title = cardTitle, onClick = {
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

        // CHAT INPUT BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 1.dp, color = SofiaLightBorder)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text(SofiaLocalization.string("placeholder_chat", selectedLanguageCode), color = SofiaTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SofiaTextPrimary,
                    unfocusedTextColor = SofiaTextPrimary,
                    focusedBorderColor = SofiaGreen,
                    unfocusedBorderColor = SofiaLightBorder,
                    focusedContainerColor = Color(0xFFF1F5F3),
                    unfocusedContainerColor = Color(0xFFF1F5F3)
                ),
                shape = RoundedCornerShape(16.dp),
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

            Spacer(modifier = Modifier.width(8.dp))

            // Animated Send Button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText, selectedLanguageCode)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(SofiaGradientStart, SofiaGradientEnd)
                        )
                    )
                    .testTag("sofia_send_button")
            ) {
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

@Composable
fun SofiaQuickToolsRow(
    viewModel: SofiaViewModel,
    selectedLanguageCode: String
) {
    val tools = listOf(
        Pair("Explain Status", SofiaActionEvent.ExplainStatus),
        Pair("Check Documents", SofiaActionEvent.ShowRequiredDocuments),
        Pair("Appointment Help", SofiaActionEvent.ShowAppointment),
        Pair("Find Office", SofiaActionEvent.ShowMyQuestura),
        Pair("Official Links", SofiaActionEvent.OpenOfficialWebsite),
        Pair("Renewal Help", SofiaActionEvent.OpenRenewalGuide),
        Pair("Guide Me Step by Step", SofiaActionEvent.OpenImmigrationGuides)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tools.forEach { tool ->
            Card(
                onClick = { viewModel.triggerSmartAction(tool.second) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SofiaLightBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(text = tool.first, color = SofiaTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    val containerColor = if (isUser) SofiaGreen else Color.White
    val borderStroke = if (isUser) null else BorderStroke(1.dp, SofiaLightBorder)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = borderStroke,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Message Text
                    Text(
                        text = message.text,
                        color = if (isUser) Color.White else SofiaTextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
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
                    .fillMaxWidth(0.85f)
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isUser) {
                    // Source color badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val sourceColor = when (message.sourceType) {
                            "official" -> SofiaGreen
                            "community" -> SofiaRed
                            else -> SofiaGreen
                        }
                        val sourceLabel = when (message.sourceType) {
                            "official" -> SofiaLocalization.string("source_official", selectedLanguageCode)
                            "community" -> SofiaLocalization.string("source_community", selectedLanguageCode)
                            else -> SofiaLocalization.string("source_ai", selectedLanguageCode)
                        }
                        Box(
                            modifier = Modifier
                                .background(sourceColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = sourceLabel, color = sourceColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SofiaTextSecondary, modifier = Modifier.size(12.dp))
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
