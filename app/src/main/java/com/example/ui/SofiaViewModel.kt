package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiMessage
import com.example.ai.AiProvider
import com.example.ai.GeminiAiProvider
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SofiaUiState {
    object Landing : SofiaUiState()
    data class ActiveChat(val conversationId: Long) : SofiaUiState()
}

sealed class SofiaActionEvent {
    object OpenMyGuide : SofiaActionEvent()
    object ShowMyQuestura : SofiaActionEvent()
    object OpenOfficialWebsite : SofiaActionEvent()
    object ShowAppointment : SofiaActionEvent()
    object OpenTimeline : SofiaActionEvent()
    object ExplainStatus : SofiaActionEvent()
    object ShowRequiredDocuments : SofiaActionEvent()
    object OpenRenewalGuide : SofiaActionEvent()
    object GoToProfile : SofiaActionEvent()
    object OpenBookmarks : SofiaActionEvent()
    object OpenImmigrationGuides : SofiaActionEvent()
}

class SofiaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val aiProvider: AiProvider = GeminiAiProvider()

    // UI state navigation (Landing page or specific conversation)
    private val _uiState = MutableStateFlow<SofiaUiState>(SofiaUiState.Landing)
    val uiState: StateFlow<SofiaUiState> = _uiState.asStateFlow()

    // Shared flow to send navigation actions to the UI
    private val _actionEvent = MutableSharedFlow<SofiaActionEvent>()
    val actionEvent = _actionEvent.asSharedFlow()

    // Conversations lists
    val conversations: StateFlow<List<SofiaConversation>> = repository.allConversationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query for conversations list
    val conversationSearchQuery = MutableStateFlow("")

    private val _searchedConversations = MutableStateFlow<List<SofiaConversation>>(emptyList())
    val searchedConversations: StateFlow<List<SofiaConversation>> = combine(
        conversations,
        conversationSearchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { it.title.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active messages flow
    private val _activeMessages = MutableStateFlow<List<SofiaMessage>>(emptyList())
    val activeMessages: StateFlow<List<SofiaMessage>> = _activeMessages.asStateFlow()

    // Current streaming typing text to simulate smooth response streaming
    private val _typingResponseText = MutableStateFlow("")
    val typingResponseText: StateFlow<String> = _typingResponseText.asStateFlow()

    val isAiLoading = MutableStateFlow(false)

    // Gather overall app context dynamically for the AI prompt
    private val credentials = repository.credentialsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userCredentialsState: StateFlow<UserCredentials?> = credentials
    private val checkHistory = repository.historyFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val bookmarkedGuideIds = repository.bookmarksFlow
        .map { list -> list.map { it.guideId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    // User extra preference flows (from shared preferences)
    private val prefs = application.getSharedPreferences("permesso_extra_prefs", android.content.Context.MODE_PRIVATE)
    private val selectedProvince = prefs.getString("selected_province", "Milano") ?: "Milano"
    private val appointmentDate = prefs.getString("appointment_date", "") ?: ""
    private val appointmentTime = prefs.getString("appointment_time", "") ?: ""
    private val appointmentLocation = prefs.getString("appointment_location", "") ?: ""
    private val checkedDocs = prefs.getStringSet("checked_docs", emptySet()) ?: emptySet()

    init {
        // Observe changes to active conversation and reload messages
        viewModelScope.launch {
            _uiState.collect { state ->
                if (state is SofiaUiState.ActiveChat) {
                    repository.getMessagesForConversationFlow(state.conversationId).collect { msgs ->
                        _activeMessages.value = msgs
                    }
                } else {
                    _activeMessages.value = emptyList()
                }
            }
        }
    }

    fun selectConversation(id: Long) {
        viewModelScope.launch {
            _uiState.value = SofiaUiState.ActiveChat(id)
            // Update last active
            val conv = repository.allConversationsFlow.firstOrNull()?.find { it.id == id }
            if (conv != null) {
                repository.updateConversation(conv.copy(lastActive = System.currentTimeMillis()))
            }
        }
    }

    fun startNewConversation(title: String, initialMessage: String? = null, category: String? = null, selectedLanguageCode: String) {
        viewModelScope.launch {
            val convId = repository.createConversation(title = title, category = category)
            _uiState.value = SofiaUiState.ActiveChat(convId)
            
            if (!initialMessage.isNullOrBlank()) {
                sendMessage(initialMessage, selectedLanguageCode)
            }
        }
    }

    fun goBackToLanding() {
        _uiState.value = SofiaUiState.Landing
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_uiState.value is SofiaUiState.ActiveChat && 
                (_uiState.value as SofiaUiState.ActiveChat).conversationId == id) {
                _uiState.value = SofiaUiState.Landing
            }
        }
    }

    fun togglePinConversation(id: Long) {
        viewModelScope.launch {
            val list = conversations.value
            val conv = list.find { it.id == id }
            if (conv != null) {
                repository.updateConversation(conv.copy(isPinned = !conv.isPinned))
            }
        }
    }

    fun renameConversation(id: Long, newTitle: String) {
        viewModelScope.launch {
            val list = conversations.value
            val conv = list.find { it.id == id }
            if (conv != null) {
                repository.updateConversation(conv.copy(title = newTitle))
            }
        }
    }

    fun likeMessage(messageId: Long, liked: Boolean) {
        viewModelScope.launch {
            val msg = _activeMessages.value.find { it.id == messageId }
            if (msg != null) {
                repository.updateSofiaMessage(msg.copy(liked = liked))
            }
        }
    }

    fun triggerSmartAction(action: SofiaActionEvent) {
        viewModelScope.launch {
            _actionEvent.emit(action)
        }
    }

    fun sendMessage(text: String, selectedLanguageCode: String) {
        val currentState = _uiState.value
        if (currentState !is SofiaUiState.ActiveChat) return
        val convId = currentState.conversationId

        if (text.trim().isEmpty()) return

        viewModelScope.launch {
            // Get history BEFORE inserting the new message to avoid role inconsistency in AI
            val historyBefore = _activeMessages.value.map {
                AiMessage(sender = it.sender, text = it.text)
            }

            // Save user message to DB
            repository.insertSofiaMessage(
                SofiaMessage(
                    conversationId = convId,
                    sender = "user",
                    text = text,
                    sourceType = "official" 
                )
            )

            // Update conversation last active timestamp
            val currentConv = conversations.value.find { it.id == convId }
            if (currentConv != null) {
                repository.updateConversation(currentConv.copy(lastActive = System.currentTimeMillis()))
            }

            // Perform AI response generation with explicit history
            generateAiResponse(convId, text, selectedLanguageCode, historyBefore)
        }
    }

    fun regenerateLastResponse(selectedLanguageCode: String) {
        val currentState = _uiState.value
        if (currentState !is SofiaUiState.ActiveChat) return
        val convId = currentState.conversationId

        val messagesList = _activeMessages.value
        if (messagesList.isEmpty()) return

        // Find the last user message
        val lastUserMessage = messagesList.findLast { it.sender == "user" } ?: return
        
        // Build history up to that user message (exclusive)
        val userIndex = messagesList.indexOf(lastUserMessage)
        val historyUpToUser = if (userIndex > 0) {
            messagesList.subList(0, userIndex).map { AiMessage(sender = it.sender, text = it.text) }
        } else {
            emptyList()
        }

        viewModelScope.launch {
            generateAiResponse(convId, lastUserMessage.text, selectedLanguageCode, historyUpToUser)
        }
    }

    private suspend fun generateAiResponse(convId: Long, userQuery: String, selectedLanguageCode: String, history: List<AiMessage>) {
        isAiLoading.value = true
        _typingResponseText.value = ""

        try {
            // Build the dynamic, context-aware system instructions
            val systemInstructions = buildContextSystemInstructions(selectedLanguageCode)

            // Call abstracted AI provider
            val rawResponse = aiProvider.generateResponse(
                systemInstruction = systemInstructions,
                history = history,
                userMessage = userQuery
            )

            val parsedResponse = if (rawResponse == "ERR_NO_KEY") {
                when (selectedLanguageCode) {
                    "it" -> "Chiave API Gemini mancante. Configurala nella sezione Segreti di AI Studio."
                    "ar" -> "مفتاح API الخاص بـ Gemini غير متوفر. يرجى تكوينه في لوحة الأسرار."
                    "es" -> "Falta la clave de API de Gemini. Configúrala en el panel de secretos."
                    "fr" -> "Clé API Gemini mancante. Veuillez la configurer dans les secrets."
                    else -> "Gemini API key is missing. Please configure it in the Secrets Panel of AI Studio."
                }
            } else {
                rawResponse
            }

            // Typing animation logic: stream characters to typingResponseText in a smooth loop
            withContext(Dispatchers.Main) {
                var currentString = ""
                val chunkSize = 3
                var i = 0
                while (i < parsedResponse.length) {
                    val end = (i + chunkSize).coerceAtMost(parsedResponse.length)
                    currentString += parsedResponse.substring(i, end)
                    _typingResponseText.value = currentString
                    i += chunkSize
                    kotlinx.coroutines.delay(10) // Smooth streaming rate
                }
            }

            // Save the finalized AI message to local DB
            val source = when {
                parsedResponse.contains("Official", ignoreCase = true) || parsedResponse.contains("Portale", ignoreCase = true) || parsedResponse.contains("Polizia", ignoreCase = true) -> "official"
                parsedResponse.contains("Community", ignoreCase = true) || parsedResponse.contains("forum", ignoreCase = true) || parsedResponse.contains("esperienze", ignoreCase = true) -> "community"
                else -> "ai"
            }

            repository.insertSofiaMessage(
                SofiaMessage(
                    conversationId = convId,
                    sender = "sofia",
                    text = parsedResponse,
                    sourceType = source
                )
            )

        } catch (e: Exception) {
            val errorText = "Error: Could not retrieve advice. ${e.message}"
            repository.insertSofiaMessage(
                SofiaMessage(
                    conversationId = convId,
                    sender = "sofia",
                    text = errorText,
                    sourceType = "ai"
                )
            )
        } finally {
            _typingResponseText.value = ""
            isAiLoading.value = false
        }
    }

    private fun buildContextSystemInstructions(lang: String): String {
        val creds = credentials.value
        val historyList = checkHistory.value
        val bookmarks = bookmarkedGuideIds.value

        // Fetch User and Status data
        val userName = creds?.fullName ?: "Karim El Assali"
        val currentProvince = creds?.selectedProvinceCode ?: selectedProvince
        val selectedPermit = creds?.selectedPermitType ?: "Work Permit Renewal"
        val lastPolizia = creds?.lastPoliziaStatus ?: "No checks yet"
        val lastPortale = creds?.lastPortaleStatus ?: "No checks yet"

        val historyText = if (historyList.isNotEmpty()) {
            historyList.take(3).joinToString("; ") { "${it.portalType}: ${it.statusText} (${it.details})" }
        } else {
            "No portal checks run yet"
        }

        val checkedDocsText = if (checkedDocs.isNotEmpty()) {
            "Completed checklist: ${checkedDocs.joinToString(", ")}"
        } else {
            "No checklist documents marked as completed yet"
        }

        val appointmentText = if (appointmentDate.isNotBlank()) {
            "Appointment Date: $appointmentDate at $appointmentTime, Location: $appointmentLocation"
        } else {
            "No questura fingerprint appointment scheduled yet"
        }

        val bookmarksText = if (bookmarks.isNotEmpty()) {
            "User bookmarked guide IDs: ${bookmarks.joinToString(", ")}"
        } else {
            "No guide bookmarks added yet"
        }

        return """
            You are "Sofia AI", a highly professional, calm, friendly, patient, and reassuring Italian Immigration Expert built exclusively for Soggiorno Track.
            You act as an expert immigration advisor available 24/7.
            
            == SOFIA PERSONALITY & STYLE ==
            • Tone: Professional, calm, reassuring, empathetic, extremely clear.
            • NEVER robotic, never overly casual, never use emojis excessively (limit to 1-2 per response maximum).
            • Keep responses concise, clear, and highly focused on Italian immigration bureaucracy.
            • Always write in the user's selected language: $lang (supported: 'it', 'en', 'es', 'fr', 'ar'). If 'ar' is used, ensure full RTL compliance and classical Arabic vocabulary.
            • Distinguish clearly in your text between:
              - [OFFICIAL REQUIREMENT] (e.g. from Questure, Portale Immigrazione, or Polizia di Stato rules).
              - [COMMUNITY ADVICE] (e.g. based on community queue times and experiences).
              - [AI RECOMMENDATION] (your smart suggestion based on the context).

            == CURRENT USER CONTEXT (AUTOMATICALLY AVAILABLE - DO NOT ASK FOR THESE) ==
            • User Name: $userName
            • Province/City: $currentProvince
            • Selected Permit Type: $selectedPermit
            • Polizia di Stato Status: $lastPolizia
            • Portale Immigrazione Status: $lastPortale
            • Recent Checks History: $historyText
            • Next Appointment: $appointmentText
            • Documents Checked: $checkedDocsText
            • Bookmarked Guides: $bookmarksText
            
            == CONTEXT AWARENESS & SMART ANSWERS ==
            When the user asks questions like "Can I travel?", "Can I work?", "What documents do I need?", "Where is my Questura?", "Explain my status", you MUST base your answer on the user's real situation above:
            - Travel: Check if they have a post office receipt ("ricevuta"), which allows direct travel to home country (no transiting Schengen countries) under Italian law.
            - Status: Explain exactly what their Polizia ("In trattazione", "Pronto") or Portale ("Step 1", "Step 2") status means in simple terms.
            - Documents: Recommend next steps based on checked/missing documents.
            - Appointments: Reassure them about their upcoming fingerprint date.

            == INTEGRATED NAVIGATIONAL SHORTRCUTS ==
            Suggest or mention smart direct actions. When appropriate, end your message with a clear recommendation on what section of the app they should visit (e.g., "I suggest opening your Document Vault", "Go to your Appointment screen", "Open the renewal guide").
            
            == DISCLAIMER ==
            At the end of every conversation or as a small note, politely remind the user that AI advice is informational and they should always verify with official authorities (Polizia, Questura, Portale Immigrazione).
            Keep the response length under 280 words. Focus on pristine markdown.
        """.trimIndent()
    }
}
