package com.example.util

import androidx.compose.runtime.Composable

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ITALIAN("it", "Italiano", "🇮🇹"),
    ENGLISH("en", "English", "🇬🇧"),
    ARABIC("ar", "العربية", "🇸🇦"),
    SPANISH("es", "Español", "🇪🇸"),
    FRENCH("fr", "Français", "🇫🇷");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: ITALIAN
        }
    }
}

object Localization {
    private val translations = mapOf(
        "it" to mapOf(
            "app_name" to "Permesso Status Tracker",
            "tab_status" to "Stato",
            "tab_portal" to "Portale",
            "tab_receipt" to "Ricevuta",
            "tab_sofia" to "Sofia AI",
            "tab_profile" to "Profilo",
            "dashboard_title" to "Stato del Permesso",
            "button_check_now" to "Controlla Ora",
            "checked_on" to "Verificato il",
            "no_history" to "Nessuna verifica effettuata",
            "card_postal_title" to "Poste Italiane",
            "card_polizia_title" to "Polizia di Stato",
            "card_portale_title" to "Portale Immigrazione",
            "credentials_card_title" to "Credenziali",
            "credentials_missing" to "Mancanti",
            "credentials_configure" to "Configura",
            "timeline_title" to "Cronologia Avanzamento",
            "status_approved" to "Approvato / Pronto per il Ritiro",
            "status_pending" to "In Lavorazione / In Istruttoria",
            "status_missing_docs" to "Documenti Mancanti / Anomalie",
            "status_unknown" to "Nessuno stato (Configura i codici)",
            "province_selector" to "Seleziona Provincia",
            "appointment_title" to "Appuntamento in Questura",
            "appointment_not_set" to "Nessun appuntamento salvato",
            "appointment_set" to "Appuntamento salvato",
            "appointment_date" to "Data",
            "appointment_time" to "Ora",
            "appointment_location" to "Luogo",
            "documents_checklist" to "Lista Documenti Necessari",
            "history_title" to "Cronologia Controlli",
            "history_clear" to "Cancella Cronologia",
            "reset_state" to "Reimposta Stato",
            "save_success" to "Credenziali salvate con successo!",
            "webview_loading" to "Caricamento Portale...",
            "webview_offline" to "Sei offline. Verifica la tua connessione.",
            "guide_title" to "Guida ai Codici della Ricevuta",
            "guide_body" to "• Il Numero Assicurata (12 cifre, es: 0500...) è stampato in alto sulla ricevuta e serve per Poste Italiane e Polizia di Stato.\n\n• L'Area Riservata usa lo User ID (12 cifre dell'ologramma) e la Password (il codice assicurata sulla ricevuta poste senza trattino).",
            "input_fullname" to "Nome Completo Intestatario",
            "input_assicurata" to "Numero Assicurata (12 cifre)",
            "input_userid" to "User ID Portale (12 Cifre)",
            "input_password" to "Password Portale",
            "input_pratica" to "Numero Pratica Questura (Opzionale)",
            "button_save" to "Salva e Attiva Notifiche",
            "profile_title" to "Profilo Utente",
            "profile_subtitle" to "Gestisci le tue credenziali e impostazioni dell'applicazione.",
            "profile_edit_success" to "Profilo aggiornato!",
            "select_language" to "Seleziona Lingua",
            "app_settings" to "Impostazioni Applicazione",
            "bg_sync" to "Sincronizzazione in Background",
            "notif_enabled" to "Notifiche Abilitate",
            "about_section" to "Informazioni",
            "about_text" to "Permesso Status Tracker ti aiuta a monitorare lo stato delle tue pratiche di immigrazione in Italia direttamente dai portali ufficiali in totale sicurezza.",
            "onboarding_lang_title" to "Scegli la tua Lingua",
            "onboarding_lang_subtitle" to "Scegli come visualizzare l'applicazione",
            "button_skip" to "Salta",
            "button_continue" to "Continua",
            "button_start" to "Configura e Inizia",
            "welcome_title" to "Traccia il tuo Permesso",
            "welcome_body" to "Benvenuto! Questa applicazione ti aiuta a monitorare lo stato del tuo permesso di soggiorno in tempo reale, interrogando in modo sicuro e diretto i portali ufficiali italiani.",
            "privacy_title" to "Sicurezza & Privacy",
            "privacy_body" to "I tuoi dati sensibili, codici e password sono crittografati e salvati esclusivamente sul tuo telefono nel database locale SQLite (Room).\n\nNessun server di terze parti memorizza o analizza le tue credenziali.",
            
            // NEW Italian strings for thorough translation
            "friend" to "Amico",
            "last_checked" to "Ultimo controllo: %s",
            "no_checks" to "Nessun controllo effettuato",
            "notif_active" to "Notifiche Attive 🔔",
            "notif_active_desc" to "Il sistema di monitoraggio automatico di PermessoSoggiorno è attivo!",
            "test_button" to "Test",
            "config_required" to "Configurazione Richiesta",
            "config_required_desc" to "Inserisci il numero dell'assicurata per tracciare lo stato della tua pratica.",
            "config_receipt_btn" to "Configura Dati Ricevuta",
            "no_history_recorded" to "Nessun controllo registrato",
            "history_help_desc" to "I controlli automatici giornalieri e manuali appariranno qui.",
            "tracking_active" to "Tracciamento Attivo",
            "tracking_to_configure" to "Da Configurare",
            "document_status" to "Stato Documento",
            "awaiting_check" to "In attesa di controllo",
            "not_active" to "Non Attivo",
            "configure_credentials_desc" to "Configura credenziali",
            "not_verified" to "Non Verificato",
            "manual_check_desc" to "Fai un controllo manuale",
            "status" to "Stato",
            "portal_status_header" to "STATO PORTALE",
            "immigration_portal" to "Portale Immigrazione",
            "state_police_header" to "POLIZIA STATO",
            "ready" to "Pronto",
            "in_processing" to "In Trattazione",
            "no_code" to "Nessun codice",
            "not_checked" to "Non controllato",
            "ready_for_pickup" to "Pronto per il ritiro",
            "secure_account" to "Account Sicuro",
            "credentials_summary" to "User: %1\$s • Pass: %2\$s",
            "edit_credentials_desc" to "Modifica credenziali",
            "not_entered" to "Non inserito",
            "not_entered_f" to "Non inserita",
            "italian_republic" to "REPUBBLICA ITALIANA",
            "residency_permit" to "PERMESSO DI SOGGIORNO",
            "holder_not_configured" to "INTESTATARIO DA CONFIGURARE",
            "code_both" to "Assicurata: %1\$s | Pratica: %2\$s",
            "code_assicurata" to "Assicurata: %s",
            "code_pratica" to "Numero Pratica: %s",
            "no_code_registered" to "Nessun codice registrato",
            "last_checked_header" to "ULTIMO CONTROLLO",
            "never_checked" to "Mai controllato",
            "querying_portal" to "Interrogazione portale...",
            "response_received" to "Risposta Ricevuta!",
            "check_error" to "Errore Controllo",
            "close" to "Chiudi",
            "querying_portal_desc" to "Il sistema si sta connettendo ai portali ufficiali per analizzare lo stato della pratica...",
            "portal_banner_immigrazione" to "Portale Immigrazione (Sportello Amico)",
            "portal_banner_polizia" to "Polizia di Stato (Questura)",
            "portal_autofill_help_portale" to "Premi Auto-fill per inserire automaticamente User ID e Password.",
            "portal_autofill_help_polizia" to "Premi Auto-fill per inserire automaticamente il Numero Assicurata.",
            "portal_data_title" to "Dati Portale Immigrazione",
            "portal_data_desc" to "Configura il tuo nome e le credenziali per monitorare l'Area Riservata del Portale Immigrazione.",
            "input_fullname_placeholder" to "Nome e Cognome (come da passaporto)",
            "input_userid_placeholder" to "12 cifre ologramma (es. 0500...)",
            "input_password_placeholder" to "Codice di sicurezza (es: 050000000000)",
            "show_password_desc" to "Mostra password",
            "polizia_tracking_title" to "Tracciamento Polizia di Stato",
            "polizia_tracking_desc" to "Configura il numero di assicurata o della pratica per interrogare il sito ufficiale della Polizia di Stato.",
            "input_assicurata_placeholder" to "Es: 050000000000",
            "input_pratica_placeholder" to "Es: 00XX00000000",
            "step1_title" to "Poste",
            "step1_desc" to "Ricevuto",
            "step2_title" to "Questura",
            "step2_desc" to "Fotosegnalamento",
            "step3_title" to "Trattazione",
            "step3_desc" to "In Istruttoria",
            "step4_title" to "Stampa",
            "step4_desc" to "In Produzione",
            "step5_title" to "Consegna",
            "step5_desc" to "Pronto!",
            "timeline_exp_1" to "In attesa dell'invio del kit postale o inserimento dati nel sistema.",
            "timeline_exp_2" to "Kit inviato con successo. Prossimo passo: convocazione in Questura per il rilievo delle impronte digitali (fotosegnalamento).",
            "timeline_exp_3" to "La tua pratica è in fase di istruttoria/trattazione presso la Questura. Questo processo richiede normalmente diversi mesi. Nessuna anomalia riscontrata.",
            "timeline_exp_4" to "L'istruttoria è completata con successo! Il tuo titolo di soggiorno elettronico è in fase di stampa presso il Poligrafico dello Stato.",
            "timeline_exp_5" to "Eccezionale! Il tuo Permesso di Soggiorno è pronto per la consegna. Verifica gli orari di apertura della tua Questura per il ritiro.",
            "timeline_exp_none" to "Nessuna pratica attiva rilevata.",
            "timeline_advance_title" to "Linea Temporale di Avanzamento",
            "timeline_current_exp_title" to "Spiegazione dello Stato Corrente:",
            "community_wait_estimates_title" to "Stime Orientative della Community (Non Ufficiali)",
            "live_estimates" to "Orientativo 📊",
            "select_province_stats_desc" to "I tempi ufficiali di trattazione dipendono esclusivamente dal carico di lavoro della Questura competente:",
            "wait_fingerprints" to "Attesa Impronte",
            "wait_post_to_fingerprints" to "Poste → Impronte",
            "wait_printing" to "Attesa Stampa",
            "wait_fingerprints_to_ready" to "Impronte → Pronto",
            "wait_total" to "Attesa Totale",
            "global_average" to "Fascia Stimata",
            "people_like_you" to "Dati Aggregati Community (Questura di %s):",
            "people_like_you_desc" to "Valori puramente indicativi calcolati solo al superamento della soglia minima statistica di segnalazioni anonime. I tempi effettivi dipendono dal carico di lavoro dell'Ufficio Immigrazione competente.",
            "ai_advisor_title" to "Prontuario Procedure Burocratiche",
            "ai_advisor_desc" to "Tocca una domanda comune per ricevere indicazioni immediate e consigli pratici:",
            "advisor_q1" to "Cosa significa lo stato 'In Istruttoria'?",
            "advisor_a1" to "Indica che l'ufficio immigrazione sta esaminando i requisiti (contratto di lavoro, reddito, alloggio). È la fase più lunga e può richiedere diversi mesi. Non ti allarmare!",
            "advisor_q2" to "Cosa fare se aspetto da più di 12 mesi?",
            "advisor_a2" to "Puoi inviare un sollecito formale tramite PEC (Posta Elettronica Certificata) alla tua Questura allegando copia della ricevuta. È un tuo diritto legale ricevere risposta.",
            "advisor_q3" to "Quali documenti portare per il ritiro?",
            "advisor_a3" to "Porta con te: 1) Passaporto originale, 2) Il vecchio permesso scaduto (se applicabile), 3) La ricevuta postale (cedolino), 4) Lettera o SMS di appuntamento (se ricevuti).",
            "advisor_q4" to "Ho perso il cedolino delle Poste! Cosa faccio?",
            "advisor_a4" to "Devi sporgere denuncia di smarrimento presso i Carabinieri o Polizia. Porta la denuncia originale il giorno del ritiro o del fotosegnalamento per sostituire il cedolino.",
            "doc_passport" to "Passaporto in corso di validità 🛂",
            "doc_receipt" to "Ricevuta delle Poste (Cedolino) 🧾",
            "doc_revenue_stamp" to "Marca da Bollo da €16.00 🎟️",
            "doc_photos" to "4 Fototessere uguali e recenti 📸",
            "doc_accommodation" to "Contratto d'affitto / Ospitalità 🏠",
            "doc_work_contract" to "Contratto di lavoro / Cedolino reddito 💼",
            "questura_appointments_title" to "Appuntamenti Questura",
            "edit_appointment_desc" to "Modifica appuntamento",
            "next_appointment" to "Prossimo Appuntamento:",
            "appointment_formatted" to "%1\$s alle ore %2\$s",
            "no_appointment_desc" to "Nessun appuntamento inserito. Tocca per inserire data e ora della convocazione.",
            "doc_vault_title" to "Cassaforte Documenti Pronti",
            "doc_vault_desc" to "Spunta i documenti che hai già preparato per non dimenticare nulla:",
            "appointment_details_title" to "Dettagli Convocazione",
            "appointment_date_label" to "Data Appuntamento",
            "appointment_date_placeholder" to "Es: 15/10/2026",
            "appointment_time_label" to "Ora Appuntamento",
            "appointment_time_placeholder" to "Es: 09:30",
            "appointment_location_label" to "Questura o Commissariato",
            "appointment_location_placeholder" to "Es: Questura di Milano, via Fatebenefratelli",
            "save" to "Salva",
            "cancel" to "Annulla",
            "portal_status_monitor_title" to "Stato Portali Istituzionali 🌐",
            "online_status" to "Online 🟢",
            "anti_scam_warning_title" to "Avviso Sicurezza Anti-Truffa:",
            "anti_scam_warning_desc" to "Fai attenzione a siti web non ufficiali o messaggi SMS che richiedono pagamenti per il tracciamento del permesso. Questa applicazione contatta SOLO i server istituzionali ufficiali e conserva le tue credenziali in modo sicuro al 100% all'interno del tuo telefono.",
            "timeline_card_title" to "Cronologia Avanzamento Pratica",
            "step_1_poste" to "Poste Italiane",
            "step_1_received" to "Ricevuto",
            "step_2_questura" to "Questura",
            "step_2_fingerprinting" to "Impronte",
            "step_3_processing" to "Trattazione",
            "step_3_under_investigation" to "In Istruttoria",
            "step_4_printing" to "Stampa",
            "step_4_in_production" to "In Produzione",
            "step_5_delivery" to "Consegna",
            "step_5_ready" to "Pronto",
            "timeline_desc_1" to "Il kit postale è stato spedito. In attesa di convocazione per il fotosegnalamento.",
            "timeline_desc_2" to "Rilievo delle impronte digitali e verifica dei documenti presso la Questura.",
            "timeline_desc_3" to "La tua pratica è in fase di istruttoria e valutazione da parte degli uffici della Questura.",
            "timeline_desc_4" to "Il tuo permesso di soggiorno è stato approvato ed è in fase di stampa/produzione.",
            "timeline_desc_5" to "Il permesso di soggiorno è pronto per il ritiro presso l'ufficio competente.",
            "timeline_desc_inactive" to "Nessun dato di avanzamento disponibile. Completa la configurazione del profilo.",
            "current_status_explanation" to "Spiegazione Stato Corrente:",
            "subtab_status" to "Stato",
            "subtab_appointments" to "Appuntamenti & AI",
            "subtab_stats" to "Analisi & Portali",
            "guides_card_title" to "Guide Immigrazione",
            "guides_card_desc" to "Tutto ciò che serve sapere prima e durante la domanda di soggiorno.",
            "guides_screen_title" to "Guide Immigrazione",
            "guides_search_placeholder" to "Cerca permessi, documenti, uffici...",
            "guides_bookmarks_title" to "I Miei Segnalibri",
            "guides_all_title" to "Tutte le Guide",
            "guides_difficulty" to "Difficoltà:",
            "guides_duration" to "Durata Stimata:",
            "guides_cost" to "Costo Stimato:",
            "guides_validity" to "Validità:",
            "guides_offices" to "Uffici Richiesti:",
            "guides_sec_overview" to "Panoramica",
            "guides_sec_timeline" to "Linea Temporale",
            "guides_sec_documents" to "Lista Documenti",
            "guides_sec_deadlines" to "Scadenze Importanti",
            "guides_sec_where_start" to "Da Dove Iniziare",
            "guides_sec_costs" to "Dettaglio Costi",
            "guides_sec_mistakes" to "Errori Comuni",
            "guides_sec_faqs" to "Domande Frequenti",
            "guides_sec_contacts" to "Contatti Utili",
            "guides_sec_resources" to "Siti Ufficiali",
            "guides_suggestions" to "Suggerimenti Contestuali",
            "guides_no_bookmarks" to "Nessun segnalibro salvato. Clicca sull'icona in alto a destra di una guida per salvarla.",
            "guides_no_results" to "Nessun risultato trovato. Prova un'altra parola chiave.",
            "guides_disclaimer" to "DISCLAIMER: Le presenti guide sono solo a scopo informativo e non sostituiscono i canali ufficiali della Pubblica Amministrazione. Verifica sempre prima di procedere.",
            "guides_last_reviewed" to "Ultima revisione",
            "guides_version" to "Versione",
            "guides_last_updated" to "Ultimo aggiornamento",
            "guides_next_action" to "Prossimo Passo",
            "guides_what_to_do" to "Cosa fare",
            "guides_local_notice" to "Avviso Locale",
            "guides_standard_procedure" to "Procedura standard applicabile in questa provincia.",
            "guides_community_tips" to "Consigli della Community",
            "guides_tip_by_anonymous" to "Consiglio da Utente Anonimo",
            "guides_helpful_votes" to "Utile (%d)",
            "guides_ask_ai" to "Verifica Normativa sulla Procedura",
            "guides_ask_ai_desc" to "Ricevi chiarimenti normativi sui requisiti e le procedure di questa guida.",
            "guides_ask_ai_placeholder" to "Inserisci un quesito su questa procedura...",
            "guides_ask_ai_loading" to "Consultazione riferimenti normativi...",
            "guides_legal_references" to "Riferimenti Legislativi",
            "guides_related_guides" to "Guide Correlate",
            "guides_trust_verified" to "Info Verificata",
            "guides_trust_official" to "Fonte Ufficiale",
            "guides_trust_updated" to "Aggiornato",
            "guides_select_province_guide" to "Seleziona la tua provincia per informazioni locali:",
            "guides_timeline_average" to "Media di Elaborazione",
            "guides_timeline_fast" to "Opzioni Fast-Track",
            "guides_timeline_delays" to "Cause Comuni di Ritardo",
            "guides_timeline_relative" to "Velocità Relativa ad Altri Permessi",
            "guides_success_completed" to "Congratulazioni! Hai completato tutti i passi di questa procedura.",
            "onboarding_personalize_title" to "Personalizza Soggiorno Track",
            "onboarding_personalize_desc" to "Soggiorno Track può personalizzare le informazioni in base alla tua Provincia e Questura. Seleziona dove viene gestita la tua pratica per ricevere tempi di attesa e guide locali su misura.",
            "onboarding_questura_title" to "Dove viene gestita la tua pratica?",
            "onboarding_search_placeholder" to "Cerca per Provincia, Questura, Città o Regione...",
            "onboarding_choose_later" to "Sceglierò più tardi",
            "onboarding_permit_type_title" to "Che tipo di permesso stai richiedendo?",
            "onboarding_permit_type_desc" to "Questo ci permette di personalizzare la tua dashboard, le guide e le risposte dell'AI.",
            "your_questura_title" to "La tua Questura",
            "change_questura" to "Cambia la mia Questura",
            "explore_questure" to "Esplora Questure",
            "explore_questure_desc" to "Cerca e naviga tra tutte le Questure in Italia.",
            "office_status" to "Stato Ufficio",
            "community_estimate" to "Stima attesa community",
            "quick_actions" to "Azioni Rapide",
            "call" to "Chiama",
            "open_maps" to "Mappa",
            "visit_website" to "Sito Web",
            "opening_hours" to "Orari di Apertura",
            "appointment_method" to "Metodo di Appuntamento",
            "local_warnings" to "Avvisi Locali",
            "pec" to "PEC (Posta Certificata)",
            "address" to "Indirizzo",
            "phone" to "Telefono",
            "email" to "Email",
            "website" to "Sito Web",
            "official_notes" to "Note Ufficiali",
            "community_notes" to "Note della Community",
            "guides_available" to "Guide Disponibili",
            "average_waiting_time" to "Tempo Medio di Attesa",
            "related_permit_guides" to "Guide ai Permessi Correlati",
            "permit_work" to "Lavoro",
            "permit_study" to "Studio",
            "permit_family_reunification" to "Ricongiungimento Familiare",
            "permit_family_cohesion" to "Coesione Familiare",
            "permit_asylum" to "Asilo",
            "permit_eu_family" to "Familiare UE",
            "permit_long_term" to "Lungo Periodo",
            "permit_renewal" to "Rinnovo",
            "permit_not_sure" to "Non sono sicuro"
        ),
        "en" to mapOf(
            "app_name" to "Permesso Status Tracker",
            "tab_status" to "Status",
            "tab_portal" to "Portal",
            "tab_receipt" to "Receipt",
            "tab_sofia" to "Sofia AI",
            "tab_profile" to "Profile",
            "dashboard_title" to "Permit Status",
            "button_check_now" to "Check Now",
            "checked_on" to "Checked on",
            "no_history" to "No checks performed yet",
            "card_postal_title" to "Italian Post",
            "card_polizia_title" to "State Police",
            "card_portale_title" to "Immigration Portal",
            "credentials_card_title" to "Credentials",
            "credentials_missing" to "Missing",
            "credentials_configure" to "Configure",
            "timeline_title" to "Progress Timeline",
            "status_approved" to "Approved / Ready for Pickup",
            "status_pending" to "In Progress / Under Review",
            "status_missing_docs" to "Missing Documents / Discrepancies",
            "status_unknown" to "No status (Configure your codes)",
            "province_selector" to "Select Province",
            "appointment_title" to "Questura Appointment",
            "appointment_not_set" to "No appointment saved",
            "appointment_set" to "Appointment saved",
            "appointment_date" to "Date",
            "appointment_time" to "Time",
            "appointment_location" to "Location",
            "documents_checklist" to "Required Documents Checklist",
            "history_title" to "Check History",
            "history_clear" to "Clear History",
            "reset_state" to "Reset Status",
            "save_success" to "Credentials saved successfully!",
            "webview_loading" to "Loading Portal...",
            "webview_offline" to "You are offline. Please check your connection.",
            "guide_title" to "Receipt Codes Guide",
            "guide_body" to "• The Registered Code (12 digits, e.g.: 0500...) is printed at the top of the receipt and is used for Italian Post and State Police.\n\n• The Reserved Area uses the User ID (12-digit hologram code) and the Password (the security code on the post receipt without dashes).",
            "input_fullname" to "Holder Full Name",
            "input_assicurata" to "Registered Code (12 digits)",
            "input_userid" to "Portal User ID (12 Digits)",
            "input_password" to "Portal Password",
            "input_pratica" to "Questura File Number (Optional)",
            "button_save" to "Save & Enable Notifications",
            "profile_title" to "User Profile",
            "profile_subtitle" to "Manage your credentials and application settings.",
            "profile_edit_success" to "Profile updated!",
            "select_language" to "Select Language",
            "app_settings" to "Application Settings",
            "bg_sync" to "Background Synchronization",
            "notif_enabled" to "Notifications Enabled",
            "about_section" to "About",
            "about_text" to "Permesso Status Tracker helps you monitor the status of your immigration cases in Italy directly from official portals securely.",
            "onboarding_lang_title" to "Choose your Language",
            "onboarding_lang_subtitle" to "Select how to display the application",
            "button_skip" to "Skip",
            "button_continue" to "Continue",
            "button_start" to "Configure & Start",
            "welcome_title" to "Track your Permit",
            "welcome_body" to "Welcome! This application helps you monitor the status of your residency permit in real time, securely querying official Italian portals directly.",
            "privacy_title" to "Security & Privacy",
            "privacy_body" to "Your sensitive data, codes, and passwords are encrypted and saved exclusively on your phone in the local SQLite database (Room).\n\nNo third-party server stores or analyzes your credentials.",
            
            // NEW English strings for thorough translation
            "friend" to "Friend",
            "last_checked" to "Last checked: %s",
            "no_checks" to "No checks performed",
            "notif_active" to "Notifications Active 🔔",
            "notif_active_desc" to "The automatic monitoring system of PermessoSoggiorno is active!",
            "test_button" to "Test",
            "config_required" to "Configuration Required",
            "config_required_desc" to "Enter the registered receipt code to track the status of your application.",
            "config_receipt_btn" to "Configure Receipt Data",
            "no_history_recorded" to "No checks recorded",
            "history_help_desc" to "Automatic daily and manual checks will appear here.",
            "tracking_active" to "Active Tracking",
            "tracking_to_configure" to "To Configure",
            "document_status" to "Document Status",
            "awaiting_check" to "Awaiting status check",
            "not_active" to "Not Active",
            "configure_credentials_desc" to "Configure credentials",
            "not_verified" to "Not Verified",
            "manual_check_desc" to "Perform a manual check",
            "status" to "Status",
            "portal_status_header" to "PORTAL STATUS",
            "immigration_portal" to "Immigration Portal",
            "state_police_header" to "STATE POLICE",
            "ready" to "Ready",
            "in_processing" to "In Processing",
            "no_code" to "No code",
            "not_checked" to "Not checked",
            "ready_for_pickup" to "Ready for pickup",
            "secure_account" to "Secure Account",
            "credentials_summary" to "User: %1\$s • Pass: %2\$s",
            "edit_credentials_desc" to "Modify credentials",
            "not_entered" to "Not entered",
            "not_entered_f" to "Not entered",
            "italian_republic" to "ITALIAN REPUBLIC",
            "residency_permit" to "RESIDENCY PERMIT",
            "holder_not_configured" to "HOLDER TO CONFIGURE",
            "code_both" to "Receipt: %1\$s | File: %2\$s",
            "code_assicurata" to "Receipt: %s",
            "code_pratica" to "File Number: %s",
            "no_code_registered" to "No code registered",
            "last_checked_header" to "LAST CHECK",
            "never_checked" to "Never checked",
            "querying_portal" to "Querying portal...",
            "response_received" to "Response Received!",
            "check_error" to "Check Error",
            "close" to "Close",
            "querying_portal_desc" to "The system is connecting to the official portals to analyze the status of your case...",
            "portal_banner_immigrazione" to "Immigration Portal (Sportello Amico)",
            "portal_banner_polizia" to "State Police (Questura)",
            "portal_autofill_help_portale" to "Press Auto-fill to automatically input User ID and Password.",
            "portal_autofill_help_polizia" to "Press Auto-fill to automatically input the Registered Code.",
            "portal_data_title" to "Immigration Portal Data",
            "portal_data_desc" to "Configure your name and credentials to monitor the Restricted Area of the Immigration Portal.",
            "input_fullname_placeholder" to "Full Name (as on passport)",
            "input_userid_placeholder" to "E.g. 000000000000",
            "input_password_placeholder" to "Security code (e.g. 050000000000)",
            "show_password_desc" to "Show password",
            "polizia_tracking_title" to "State Police Tracking",
            "polizia_tracking_desc" to "Configure the registered receipt code or file number to query the official State Police website.",
            "input_assicurata_placeholder" to "E.g. 050000000000",
            "input_pratica_placeholder" to "E.g. 00XX00000000",
            "step1_title" to "Post Office",
            "step1_desc" to "Received",
            "step2_title" to "Questura",
            "step2_desc" to "Fingerprints",
            "step3_title" to "Processing",
            "step3_desc" to "Under Review",
            "step4_title" to "Printing",
            "step4_desc" to "In Production",
            "step5_title" to "Delivery",
            "step5_desc" to "Ready!",
            "timeline_exp_1" to "Awaiting postal kit submission or data entry into the system.",
            "timeline_exp_2" to "Kit submitted successfully. Next step: appointment at the Questura for fingerprinting.",
            "timeline_exp_3" to "Your application is under review/processing at the Questura. This process normally takes several months. No issues detected.",
            "timeline_exp_4" to "Review completed successfully! Your electronic residency permit is being printed by the State Printing Office.",
            "timeline_exp_5" to "Excellent! Your residency permit is ready for pickup. Check your local Questura's opening hours for collection.",
            "timeline_exp_none" to "No active application detected.",
            "timeline_advance_title" to "Progress Timeline",
            "timeline_current_exp_title" to "Current Status Explanation:",
            "community_wait_estimates_title" to "Indicative Community Estimates (Unofficial)",
            "live_estimates" to "Indicative 📊",
            "select_province_stats_desc" to "Official processing times depend exclusively on the competent Questura's workload:",
            "wait_fingerprints" to "Fingerprint Wait",
            "wait_post_to_fingerprints" to "Post → Fingerprints",
            "wait_printing" to "Printing Wait",
            "wait_fingerprints_to_ready" to "Fingerprints → Ready",
            "wait_total" to "Total Wait",
            "global_average" to "Estimated Range",
            "people_like_you" to "Aggregated Community Data (Questura of %s):",
            "people_like_you_desc" to "Purely indicative values displayed only when the minimum statistical sample threshold is reached. Actual processing times depend exclusively on the competent Immigration Office's workload.",
            "ai_advisor_title" to "Procedural Reference Desk",
            "ai_advisor_desc" to "Tap a common question to receive immediate guidance and practical tips:",
            "advisor_q1" to "What does the 'Under Review' status mean?",
            "advisor_a1" to "It indicates that the immigration office is examining the requirements (employment contract, income, accommodation). This is the longest phase and can take several months. Don't worry!",
            "advisor_q2" to "What should I do if I have been waiting for more than 12 months?",
            "advisor_a2" to "You can send a formal inquiry via PEC (Certified Electronic Mail) to your local Questura attaching a copy of the receipt. It is your legal right to receive a response.",
            "advisor_q3" to "What documents should I bring for pickup?",
            "advisor_a3" to "Bring with you: 1) Original passport, 2) The old expired permit (if applicable), 3) The postal receipt (cedolino), 4) Appointment letter or SMS (if received).",
            "advisor_q4" to "I lost my post office receipt! What should I do?",
            "advisor_a4" to "You must report the loss to the Carabinieri or Police. Bring the original report on the day of collection or fingerprinting to replace the receipt.",
            "doc_passport" to "Valid original passport 🛂",
            "doc_receipt" to "Post office receipt (Cedolino) 🧾",
            "doc_revenue_stamp" to "€16.00 Revenue Stamp 🎟️",
            "doc_photos" to "4 Recent identical passport photos 📸",
            "doc_accommodation" to "Rental contract / hospitality declaration 🏠",
            "doc_work_contract" to "Job contract / recent payslips 💼",
            "questura_appointments_title" to "Questura Appointments",
            "edit_appointment_desc" to "Edit appointment",
            "next_appointment" to "Next Appointment:",
            "appointment_formatted" to "%1\$s at %2\$s",
            "no_appointment_desc" to "No appointment added. Tap to insert date and time of convocation.",
            "doc_vault_title" to "Ready Documents Vault",
            "doc_vault_desc" to "Check the documents you have already prepared to make sure you don't forget anything:",
            "appointment_details_title" to "Convocation Details",
            "appointment_date_label" to "Appointment Date",
            "appointment_date_placeholder" to "E.g. 15/10/2026",
            "appointment_time_label" to "Appointment Time",
            "appointment_time_placeholder" to "E.g. 09:30",
            "appointment_location_label" to "Questura or Commissariato",
            "appointment_location_placeholder" to "E.g. Questura of Milan, via Fatebenefratelli",
            "save" to "Save",
            "cancel" to "Cancel",
            "portal_status_monitor_title" to "Institutional Portals Status 🌐",
            "online_status" to "Online 🟢",
            "anti_scam_warning_title" to "Anti-Scam Security Notice:",
            "anti_scam_warning_desc" to "Beware of unofficial websites or SMS messages requesting payments to track your permit. This application ONLY contacts official institutional servers and keeps your credentials 100% secure inside your phone.",
            "timeline_card_title" to "Application Progress Timeline",
            "step_1_poste" to "Post Office",
            "step_1_received" to "Received",
            "step_2_questura" to "Questura",
            "step_2_fingerprinting" to "Fingerprints",
            "step_3_processing" to "Processing",
            "step_3_under_investigation" to "Under Investigation",
            "step_4_printing" to "Printing",
            "step_4_in_production" to "In Production",
            "step_5_delivery" to "Delivery",
            "step_5_ready" to "Ready",
            "timeline_desc_1" to "The postal kit has been sent. Awaiting your appointment for fingerprinting.",
            "timeline_desc_2" to "Fingerprint registration and document verification at the Questura.",
            "timeline_desc_3" to "Your application is currently under investigation and review by the Questura offices.",
            "timeline_desc_4" to "Your residency permit is approved and currently being printed/produced.",
            "timeline_desc_5" to "The residency permit is ready for pickup at the competent office.",
            "timeline_desc_inactive" to "No progress data available. Please complete your profile configuration.",
            "current_status_explanation" to "Current Status Explanation:",
            "subtab_status" to "Status",
            "subtab_appointments" to "Appointments & AI",
            "subtab_stats" to "Stats & Portals",
            "guides_card_title" to "Immigration Guides",
            "guides_card_desc" to "Everything you need to know before and during your application.",
            "guides_screen_title" to "Immigration Guides",
            "guides_search_placeholder" to "Search permits, documents, offices...",
            "guides_bookmarks_title" to "My Bookmarks",
            "guides_all_title" to "All Guides",
            "guides_difficulty" to "Difficulty:",
            "guides_duration" to "Estimated Duration:",
            "guides_cost" to "Estimated Cost:",
            "guides_validity" to "Validity:",
            "guides_offices" to "Required Offices:",
            "guides_sec_overview" to "Overview",
            "guides_sec_timeline" to "Timeline",
            "guides_sec_documents" to "Required Documents",
            "guides_sec_deadlines" to "Important Deadlines",
            "guides_sec_where_start" to "Where to Start",
            "guides_sec_costs" to "Costs Breakdown",
            "guides_sec_mistakes" to "Common Mistakes",
            "guides_sec_faqs" to "FAQs",
            "guides_sec_contacts" to "Helpful Contacts",
            "guides_sec_resources" to "Official Resources",
            "guides_suggestions" to "Contextual Suggestions",
            "guides_no_bookmarks" to "No bookmarked guides yet. Click the bookmark icon on any guide to save it.",
            "guides_no_results" to "No guides match your search. Try another keyword.",
            "guides_disclaimer" to "DISCLAIMER: These guides are for informational purposes only. Soggiorno Track is not affiliated with the Italian government. Always cross-check with official sources.",
            "guides_last_reviewed" to "Last reviewed",
            "guides_version" to "Version",
            "guides_last_updated" to "Last updated",
            "guides_next_action" to "Next Action",
            "guides_what_to_do" to "What to do",
            "guides_local_notice" to "Local Notice",
            "guides_standard_procedure" to "Standard procedure applies in this province.",
            "guides_community_tips" to "Community Tips",
            "guides_tip_by_anonymous" to "Tip by Anonymous User",
            "guides_helpful_votes" to "Helpful (%d)",
            "guides_ask_ai" to "Procedural Regulatory Inquiry",
            "guides_ask_ai_desc" to "Get statutory guidance regarding the requirements and procedures in this guide.",
            "guides_ask_ai_placeholder" to "Enter a question about this procedure...",
            "guides_ask_ai_loading" to "Checking statutory references...",
            "guides_legal_references" to "Legislative References",
            "guides_related_guides" to "Related Guides",
            "guides_trust_verified" to "Verified Info",
            "guides_trust_official" to "Official Source",
            "guides_trust_updated" to "Updated",
            "guides_select_province_guide" to "Select your province for local information:",
            "guides_timeline_average" to "Average Processing Time",
            "guides_timeline_fast" to "Fast-Track Options",
            "guides_timeline_delays" to "Common Delay Reasons",
            "guides_timeline_relative" to "Relative Speed to Other Permits",
            "guides_success_completed" to "Congratulations! You completed all steps for this procedure.",
            "onboarding_personalize_title" to "Personalize Soggiorno Track",
            "onboarding_personalize_desc" to "Soggiorno Track can personalize information according to your Province and Questura. Select where your case is handled to receive tailored waiting times and local guides.",
            "onboarding_questura_title" to "Where is your immigration procedure handled?",
            "onboarding_search_placeholder" to "Search by Province, Questura, City, or Region...",
            "onboarding_choose_later" to "I'll choose later",
            "onboarding_permit_type_title" to "What type of permit are you applying for?",
            "onboarding_permit_type_desc" to "This allows us to personalize your dashboard, guides, and AI recommendations.",
            "your_questura_title" to "Your Questura",
            "change_questura" to "Change My Questura",
            "explore_questure" to "Explore Questure",
            "explore_questure_desc" to "Search and browse all Questure in Italy.",
            "office_status" to "Office Status",
            "community_estimate" to "Community waiting estimate",
            "quick_actions" to "Quick Actions",
            "call" to "Call",
            "open_maps" to "Maps",
            "visit_website" to "Website",
            "opening_hours" to "Opening Hours",
            "appointment_method" to "Appointment Method",
            "local_warnings" to "Local Warnings",
            "pec" to "PEC (Certified Email)",
            "address" to "Address",
            "phone" to "Phone",
            "email" to "Email",
            "website" to "Website",
            "official_notes" to "Official Notes",
            "community_notes" to "Community Notes",
            "guides_available" to "Guides Available",
            "average_waiting_time" to "Average Waiting Time",
            "related_permit_guides" to "Related Permit Guides",
            "permit_work" to "Work",
            "permit_study" to "Study",
            "permit_family_reunification" to "Family Reunification",
            "permit_family_cohesion" to "Family Cohesion",
            "permit_asylum" to "Asylum",
            "permit_eu_family" to "EU Family Member",
            "permit_long_term" to "Long-Term Residence",
            "permit_renewal" to "Renewal",
            "permit_not_sure" to "I'm not sure"
        ),
        "ar" to mapOf(
            "app_name" to "متابع حالة الإقامة",
            "tab_status" to "الحالة",
            "tab_portal" to "البوابة",
            "tab_receipt" to "الإيصال",
            "tab_sofia" to "صوفيا الذكية",
            "tab_profile" to "الحساب",
            "dashboard_title" to "حالة تصريح الإقامة",
            "button_check_now" to "تحقق الآن",
            "checked_on" to "تم التحقق في",
            "no_history" to "لم يتم إجراء أي تحقق بعد",
            "card_postal_title" to "البريد الإيطالي",
            "card_polizia_title" to "شرطة الدولة",
            "card_portale_title" to "بوابة الهجرة",
            "credentials_card_title" to "بيانات الإيصال",
            "credentials_missing" to "مفقودة",
            "credentials_configure" to "تكوين",
            "timeline_title" to "مخطط التقدم",
            "status_approved" to "مقبول / جاهز للاستلام",
            "status_pending" to "قيد المعالجة / تحت المراجعة",
            "status_missing_docs" to "وثائق ناقصة / هناك مشاكل",
            "status_unknown" to "لا توجد حالة (قم بضبط الأكواد)",
            "province_selector" to "اختر المقاطعة",
            "appointment_title" to "موعد الكويستورا",
            "appointment_not_set" to "لا يوجد موعد محفوظ",
            "appointment_set" to "تم حفظ الموعد",
            "appointment_date" to "التاريخ",
            "appointment_time" to "الوقت",
            "appointment_location" to "المكان",
            "documents_checklist" to "قائمة الوثائق المطلوبة",
            "history_title" to "سجل التحقق",
            "history_clear" to "مسح السجل",
            "reset_state" to "إعادة تعيين الحالة",
            "save_success" to "تم حفظ البيانات بنجاح!",
            "webview_loading" to "جاري تحميل البوابة...",
            "webview_offline" to "أنت غير متصل بالإنترنت. يرجى التحقق من اتصالك.",
            "guide_title" to "دليل رموز الإيصال",
            "guide_body" to "• رمز التأمين على وصل البريد الأصلي (12 رقم، مثل 0500...) مطبوع في أعلى الإيصال ويستخدم للبريد الإيطالي وشرطة الدولة.\n\n• المنطقة المحفوظة تستخدم معرف المستخدم (12 رقم من الهولوغرام) وكلمة المرور (رمز الأمان على وصل البريد الأصلي).",
            "input_fullname" to "الاسم الكامل لحامل الإيصال",
            "input_assicurata" to "رقم الإيصال (12 رقم)",
            "input_userid" to "معرف مستخدم البوابة (12 رقم)",
            "input_password" to "كلمة مرور البوابة",
            "input_pratica" to "رقم ملف الكويستورا (اختياري)",
            "button_save" to "حفظ وتفعيل التنبيهات",
            "profile_title" to "الملف الشخصي",
            "profile_subtitle" to "إدارة بياناتك وإعدادات التطبيق.",
            "profile_edit_success" to "تم تحديث الملف الشخصي!",
            "select_language" to "اختر اللغة",
            "app_settings" to "إعدادات التطبيق",
            "bg_sync" to "المزامنة في الخلفية",
            "notif_enabled" to "التنبيهات مفعلة",
            "about_section" to "حول التطبيق",
            "about_text" to "يساعدك متابع حالة الإقامة في مراقبة وضع ملفات الهجرة الخاصة بك في إيطاليا مباشرة من البوابات الرسمية وبأمان تام.",
            "onboarding_lang_title" to "اختر لغتك",
            "onboarding_lang_subtitle" to "اختر لغة عرض التطبيق",
            "button_skip" to "تخطي",
            "button_continue" to "متابعة",
            "button_start" to "تهيئة وبدء",
            "welcome_title" to "تتبع تصريح إقامتك",
            "welcome_body" to "مرحباً بك! يساعدك هذا التطبيق في مراقبة حالة تصريح إقامتك في الوقت الفعلي عن طريق الاستعلام الآمن والمباشر من البوابات الإيطالية الرسمية.",
            "privacy_title" to "الأمان والخصوصية",
            "privacy_body" to "بياناتك الحساسة وأكوادك وكلمات مرورك مشفرة ومحفوظة بالكامل على هاتفك فقط في قاعدة البيانات المحلية SQLite (Room).\n\nلا تقوم أي خوادم خارجية بحفظ أو تحليل بياناتك.",
            
            // NEW Arabic strings for thorough translation
            "friend" to "صديق",
            "last_checked" to "آخر تحقق: %s",
            "no_checks" to "لم يتم إجراء أي تحقق",
            "notif_active" to "التنبيهات نشطة 🔔",
            "notif_active_desc" to "نظام المراقبة التلقائية لـ PermessoSoggiorno نشط!",
            "test_button" to "تجربة",
            "config_required" to "مطلوب إعداد البيانات",
            "config_required_desc" to "أدخل رقم الإيصال البريدي لتتبع حالة طلبك.",
            "config_receipt_btn" to "ضبط بيانات الإيصال",
            "no_history_recorded" to "لا يوجد سجل للتحقق",
            "history_help_desc" to "عمليات التحقق اليومية التلقائية واليدوية ستظهر هنا.",
            "tracking_active" to "التتبع نشط",
            "tracking_to_configure" to "قيد الإعداد",
            "document_status" to "حالة المستند",
            "awaiting_check" to "في انتظار التحقق من الحالة",
            "not_active" to "غير نشط",
            "configure_credentials_desc" to "إعداد بيانات الدخول",
            "not_verified" to "لم يتم التحقق",
            "manual_check_desc" to "إجراء تحقق يدوي",
            "status" to "الحالة",
            "portal_status_header" to "حالة البوابة",
            "immigration_portal" to "بوابة الهجرة",
            "state_police_header" to "شرطة الدولة",
            "ready" to "جاهز",
            "in_processing" to "قيد المعالجة",
            "no_code" to "لا يوجد رمز",
            "not_checked" to "لم يتم التحقق",
            "ready_for_pickup" to "جاهز للاستلام",
            "secure_account" to "حساب آمن",
            "credentials_summary" to "المستخدم: %1\$s • الرمز: %2\$s",
            "edit_credentials_desc" to "تعديل بيانات الدخول",
            "not_entered" to "لم يتم الإدخال",
            "not_entered_f" to "لم يتم الإدخال",
            "italian_republic" to "الجمهورية الإيطالية",
            "residency_permit" to "تصريح الإقامة",
            "holder_not_configured" to "اسم صاحب الإقامة قيد الإعداد",
            "code_both" to "الإيصال: %1\$s | الملف: %2\$s",
            "code_assicurata" to "رقم الإيصال: %s",
            "code_pratica" to "رقم الملف: %s",
            "no_code_registered" to "لا توجد رموز مسجلة",
            "last_checked_header" to "آخر فحص",
            "never_checked" to "لم يفحص أبداً",
            "querying_portal" to "جاري الاستعلام من البوابة...",
            "response_received" to "تم استلام الرد!",
            "check_error" to "خطأ في التحقق",
            "close" to "إغلاق",
            "querying_portal_desc" to "يتصل النظام حاليًا بالبوابات الرسمية لتحليل حالة ملفك...",
            "portal_banner_immigrazione" to "بوابة الهجرة (Sportello Amico)",
            "portal_banner_polizia" to "شرطة الدولة (الكويستورا)",
            "portal_autofill_help_portale" to "اضغط على الملء التلقائي لإدخال اسم المستخدم وكلمة المرور تلقائيًا.",
            "portal_autofill_help_polizia" to "اضغط على الملء التلقائي لإدخال رقم الإيصال تلقائيًا.",
            "portal_data_title" to "بيانات بوابة الهجرة",
            "portal_data_desc" to "قم بضبط اسمك وبيانات الدخول لمراقبة المنطقة الخاصة بك في بوابة الهجرة الرسمية.",
            "input_fullname_placeholder" to "الاسم الكامل (كما في جواز السفر)",
            "input_userid_placeholder" to "مثال: 000000000000",
            "input_password_placeholder" to "رمز الأمان (مثال: 050000000000)",
            "show_password_desc" to "عرض كلمة المرور",
            "polizia_tracking_title" to "تتبع شرطة الدولة",
            "polizia_tracking_desc" to "اضبط رقم التأمين أو رقم الملف للاستعلام مباشرة من الموقع الرسمي لشرطة الدولة.",
            "input_assicurata_placeholder" to "مثال: 050000000000",
            "input_pratica_placeholder" to "مثال: 00XX00000000",
            "step1_title" to "البريد",
            "step1_desc" to "تم الاستلام",
            "step2_title" to "الشرطة",
            "step2_desc" to "البصمات",
            "step3_title" to "المعالجة",
            "step3_desc" to "قيد الدراسة",
            "step4_title" to "الطباعة",
            "step4_desc" to "قيد الإصدار",
            "step5_title" to "التسليم",
            "step5_desc" to "جاهز!",
            "timeline_exp_1" to "في انتظار إرسال المغلف البريدي أو إدخال البيانات في النظام الالكتروني للوزارة.",
            "timeline_exp_2" to "تم إرسال الملف بنجاح. الخطوة التالية: موعد في إدارة الشرطة لأخذ بصمات الأصابع (fotosegnalamento).",
            "timeline_exp_3" to "ملفك قيد المراجعة والمعالجة في إدارة الشرطة الكويستورا. تستغرق هذه المرحلة عادةً عدة أشهر ولا توجد مشاكل.",
            "timeline_exp_4" to "اكتملت المراجعة بنجاح! بطاقة إقامتك الإلكترونية قيد الطباعة الآن في مطبعة الدولة الرسمية.",
            "timeline_exp_5" to "رائع! تصريح إقامتك جاهز تمامًا للتسليم. يرجى مراجعة ساعات العمل الرسمية للكويستورا الخاصة بك لاستلامه.",
            "timeline_exp_none" to "لم يتم اكتشاف أي طلبات نشطة حالياً.",
            "timeline_advance_title" to "مخطط تقدم الطلب",
            "timeline_current_exp_title" to "تفسير الحالة الحالية لملفك:",
            "community_wait_estimates_title" to "تقديرات المجتمع الاسترشادية (غير رسمية)",
            "live_estimates" to "تقديرات استرشادية 📊",
            "select_province_stats_desc" to "تعتمد أوقات المعالجة الرسمية حصرياً على ضغط العمل في مكتب الهجرة المختص (الكويستورا):",
            "wait_fingerprints" to "انتظار البصمات",
            "wait_post_to_fingerprints" to "البريد ← البصمات",
            "wait_printing" to "انتظار الطباعة",
            "wait_fingerprints_to_ready" to "البصمات ← الاستلام",
            "wait_total" to "الانتظار الإجمالي",
            "global_average" to "النطاق التقديري",
            "people_like_you" to "بيانات المجتمع المجمعة (مكتب الهجرة %s):",
            "people_like_you_desc" to "هذه الأرقام استرشادية فقط وتُعرض عند بلوغ الحد الأدنى الإحصائي للبلاغات. تعتمد المواعيد الرسمية حصرياً على مكتب الهجرة (Questura) المختص.",
            "ai_advisor_title" to "مرجع الإجراءات الإدارية",
            "ai_advisor_desc" to "انقر على سؤال شائع للحصول على إرشادات فورية ونصائح عملية:",
            "advisor_q1" to "ماذا تعني حالة 'قيد الدراسة والمراجعة'؟",
            "advisor_a1" to "تشير إلى أن مكتب الهجرة يقوم بفحص الشروط القانونية (عقد العمل، الدخل، السكن). وهي أطول مرحلة وقد تستغرق عدة أشهر. لا تقلق!",
            "advisor_q2" to "ماذا أفعل إذا كنت أنتظر لأكثر من 12 شهرًا؟",
            "advisor_a2" to "يمكنك إرسال استفسار أو تذكير رسمي عبر البريد الإلكتروني المعتمد (PEC) إلى الكويستورا الخاصة بك مرفقًا بنسخة من الإيصال. هذا حقك القانوني.",
            "advisor_q3" to "ما هي الوثائق المطلوبة لإحضارها عند الاستلام؟",
            "advisor_a3" to "أحضر معك: 1) جواز السفر الأصلي، 2) بطاقة الإقامة القديمة المنتهية (إن وجدت)، 3) الإيصال البريدي (cedolino)، 4) رسالة الموعد أو SMS إن وجد.",
            "advisor_q4" to "لقد فقدت إيصال البريد! ماذا علي أن أفعل؟",
            "advisor_a4" to "يجب عليك الإبلاغ عن الفقدان لدى الدرك (Carabinieri) أو الشرطة، وإحضار التقرير الأصلي في يوم الاستلام أو أخذ البصمات كبديل للإيصال المفقود.",
            "doc_passport" to "جواز سفر أصلي ساري الصلاحية 🛂",
            "doc_receipt" to "إيصال مكتب البريد (Cedolino) 🧾",
            "doc_revenue_stamp" to "طابع إيرادات بقيمة 16 يورو 🎟️",
            "doc_photos" to "4 صور شخصية حديثة ومتطابقة 📸",
            "doc_accommodation" to "عقد إيجار أو إقرار استضافة سكنية 🏠",
            "doc_work_contract" to "عقد العمل أو كشوف المرتبات الأخيرة 💼",
            "questura_appointments_title" to "مواعيد الكويستورا",
            "edit_appointment_desc" to "تعديل الموعد",
            "next_appointment" to "الموعد القادم:",
            "appointment_formatted" to "%1\$s في تمام الساعة %2\$s",
            "no_appointment_desc" to "لم يتم إضافة موعد بعد. انقر لإدخال تاريخ ووقت الاستدعاء الرسمي.",
            "doc_vault_title" to "خزنة الوثائق الجاهزة",
            "doc_vault_desc" to "حدد المستندات التي قمت بإعدادها بالفعل للتأكد من عدم نسيان أي شيء:",
            "appointment_details_title" to "تفاصيل الاستدعاء",
            "appointment_date_label" to "تاريخ الموعد",
            "appointment_date_placeholder" to "مثال: 15/10/2026",
            "appointment_time_label" to "وقت الموعد",
            "appointment_time_placeholder" to "مثال: 09:30",
            "appointment_location_label" to "الكويستورا أو مركز الشرطة",
            "appointment_location_placeholder" to "مثال: كويستورا ميلانو، شارع فاتيبينفراتيلي",
            "save" to "حفظ",
            "cancel" to "إلغاء",
            "portal_status_monitor_title" to "حالة المواقع الحكومية الرسمية 🌐",
            "online_status" to "متصل الآن 🟢",
            "anti_scam_warning_title" to "تنبيه أمان ضد الاحتيال:",
            "anti_scam_warning_desc" to "احذر من المواقع غير الرسمية أو الرسائل القصيرة (SMS) التي تطلب مدفوعات لتتبع إقامتك. يتصل هذا التطبيق فقط بالخوادم الحكومية الرسمية ويحفظ بياناتك بأمان 100٪ داخل هاتفك فقط.",
            "timeline_card_title" to "جدول زمني لتقدم الطلب",
            "step_1_poste" to "البريد الإيطالي",
            "step_1_received" to "تم الاستلام",
            "step_2_questura" to "الكويستورا",
            "step_2_fingerprinting" to "تبصيم الأصابع",
            "step_3_processing" to "قيد المعالجة",
            "step_3_under_investigation" to "تحت التحقيق والتقييم",
            "step_4_printing" to "الطباعة",
            "step_4_in_production" to "قيد الإنتاج",
            "step_5_delivery" to "التسليم",
            "step_5_ready" to "جاهز للاستلام",
            "timeline_desc_1" to "تم إرسال الملف البريدي. في انتظار موعد أخذ البصمات.",
            "timeline_desc_2" to "تسجيل البصمات والتحقق من المستندات في مقر الكويستورا.",
            "timeline_desc_3" to "طلبك قيد الدراسة والتقييم حاليًا من قبل مكاتب الكويستورا.",
            "timeline_desc_4" to "تمت الموافقة على تصريح إقامتك وهو قيد الطباعة والإنتاج حاليًا.",
            "timeline_desc_5" to "تصريح الإقامة جاهز للتسليم في المكتب المختص.",
            "timeline_desc_inactive" to "لا توجد بيانات تقدم متاحة. يرجى إكمال إعدادات الملف الشخصي.",
            "current_status_explanation" to "تفسير الحالة الحالية:",
            "subtab_status" to "الحالة",
            "subtab_appointments" to "المواعيد والذكاء الاصطناعي",
            "subtab_stats" to "التحليلات والمواقع",
            "guides_card_title" to "أدلة الهجرة",
            "guides_card_desc" to "كل ما تحتاج إلى معرفته قبل وخلال تقديم طلب الإقامة الخاص بك.",
            "guides_screen_title" to "أدلة الهجرة والإقامة",
            "guides_search_placeholder" to "ابحث عن تصاريح، وثائق، مكاتب...",
            "guides_bookmarks_title" to "الإشارات المرجعية المحفوظة",
            "guides_all_title" to "جميع الأدلة المتاحة",
            "guides_difficulty" to "مستوى الصعوبة:",
            "guides_duration" to "المدة المقدرة:",
            "guides_cost" to "التكلفة المقدرة:",
            "guides_validity" to "فترة الصلاحية:",
            "guides_offices" to "المكاتب المطلوبة:",
            "guides_sec_overview" to "نظرة عامة",
            "guides_sec_timeline" to "الخطوات الزمنية",
            "guides_sec_documents" to "المستندات المطلوبة",
            "guides_sec_deadlines" to "مواعيد هامة",
            "guides_sec_where_start" to "من أين تبدأ",
            "guides_sec_costs" to "تفاصيل التكاليف",
            "guides_sec_mistakes" to "أخطاء شائعة",
            "guides_sec_faqs" to "الأسئلة الشائعة",
            "guides_sec_contacts" to "جهات اتصال مفيدة",
            "guides_sec_resources" to "المواقع الرسمية",
            "guides_suggestions" to "اقتراحات مخصصة لك",
            "guides_no_bookmarks" to "لم تقم بحفظ أي دليل بعد. انقر على أيقونة الحفظ أعلى الدليل لحفظه في مفضلاتك.",
            "guides_no_results" to "لا توجد نتائج مطابقة لبحثك. جرب كلمة مفتاحية أخرى.",
            "guides_disclaimer" to "إخلاء مسؤولية: هذه الأدلة استرشادية وإعلامية فقط ولا تمثل أي جهة حكومية رسمية. يرجى دائماً مراجعة المواقع والجهات الرسمية.",
            "guides_last_reviewed" to "آخر مراجعة",
            "guides_version" to "الإصدار",
            "guides_last_updated" to "آخر تحديث",
            "guides_next_action" to "الخطوة التالية",
            "guides_what_to_do" to "ماذا تفعل",
            "guides_local_notice" to "تنبيه محلي",
            "guides_standard_procedure" to "الإجراء القياسي ينطبق في هذه المقاطعة.",
            "guides_community_tips" to "نصائح المجتمع",
            "guides_tip_by_anonymous" to "نصيحة من مستخدم مجهول",
            "guides_helpful_votes" to "مفيد (%d)",
            "guides_ask_ai" to "استفسار قانوني حول الإجراء",
            "guides_ask_ai_desc" to "احصل على توضيحات قانونية حول المتطلبات والإجراءات في هذا الدليل.",
            "guides_ask_ai_placeholder" to "اكتب سؤالك حول هذا الدليل...",
            "guides_ask_ai_loading" to "جاري مراجعة المراجع القانونية...",
            "guides_legal_references" to "المراجع القانونية",
            "guides_related_guides" to "أدلة ذات صلة",
            "guides_trust_verified" to "معلومات موثقة",
            "guides_trust_official" to "مصدر رسمي",
            "guides_trust_updated" to "محدث باستمرار",
            "guides_select_province_guide" to "اختر مقاطعتك للحصول على معلومات محلية:",
            "guides_timeline_average" to "متوسط وقت المعالجة",
            "guides_timeline_fast" to "خيارات التسريع",
            "guides_timeline_delays" to "أسباب التأخير الشائعة",
            "guides_timeline_relative" to "السرعة النسبية مقارنة بالتصاريح الأخرى",
            "guides_success_completed" to "تهانينا! لقد أكملت جميع الخطوات لهذه العملية.",
            "onboarding_personalize_title" to "تخصيص متابع حالة الإقامة",
            "onboarding_personalize_desc" to "يمكن لمتابع حالة الإقامة تخصيص المعلومات وفقاً للمقاطعة والكويستورا الخاصة بك. اختر مكان معالجة ملفك للحصول على أوقات الانتظار والإرشادات المحلية المخصصة.",
            "onboarding_questura_title" to "أين تتم معالجة إجراءات الهجرة الخاصة بك؟",
            "onboarding_search_placeholder" to "ابحث حسب المقاطعة، الكويستورا، المدينة أو الإقليم...",
            "onboarding_choose_later" to "سأختار لاحقاً",
            "onboarding_permit_type_title" to "ما هو نوع التصريح الذي تتقدم بطلب للحصول عليه؟",
            "onboarding_permit_type_desc" to "يتيح لنا ذلك تخصيص لوحة التحكم والإرشادات وتوصيات الذكاء الاصطناعي الخاصة بك.",
            "your_questura_title" to "الكويستورا الخاصة بك",
            "change_questura" to "تغيير الكويستورا الخاصة بي",
            "explore_questure" to "استكشاف الكويستورات",
            "explore_questure_desc" to "ابحث وتصفح جميع الكويستورات في إيطاليا.",
            "office_status" to "حالة المكتب",
            "community_estimate" to "تقدير وقت الانتظار من المجتمع",
            "quick_actions" to "إجراءات سريعة",
            "call" to "اتصال",
            "open_maps" to "الخرائط",
            "visit_website" to "الموقع الإلكتروني",
            "opening_hours" to "أوقات العمل",
            "appointment_method" to "طريقة حجز الموعد",
            "local_warnings" to "تنبيهات محلية",
            "pec" to "البريد الإلكتروني المعتمد (PEC)",
            "address" to "العنوان",
            "phone" to "الهاتف",
            "email" to "البريد الإلكتروني",
            "website" to "الموقع الإلكتروني",
            "official_notes" to "ملاحظات رسمية",
            "community_notes" to "ملاحظات المجتمع",
            "guides_available" to "الإرشادات المتاحة",
            "average_waiting_time" to "متوسط وقت الانتظار",
            "related_permit_guides" to "إرشادات التصاريح ذات الصلة",
            "permit_work" to "عمل",
            "permit_study" to "دراسة",
            "permit_family_reunification" to "لم شمل الأسرة",
            "permit_family_cohesion" to "التماسك الأسري",
            "permit_asylum" to "لجوء",
            "permit_eu_family" to "أحد أفراد عائلة الاتحاد الأوروبي",
            "permit_long_term" to "إقامة طويلة الأجل",
            "permit_renewal" to "تجديد",
            "permit_not_sure" to "لست متأكداً"
        ),
        "es" to mapOf(
            "app_name" to "Rastreador de Permiso",
            "tab_status" to "Estado",
            "tab_portal" to "Portal",
            "tab_receipt" to "Recibo",
            "tab_sofia" to "Sofía AI",
            "tab_profile" to "Perfil",
            "dashboard_title" to "Estado del Permiso",
            "button_check_now" to "Consultar Ahora",
            "checked_on" to "Verificado el",
            "no_history" to "Ninguna consulta realizada",
            "card_postal_title" to "Correos Italianos",
            "card_polizia_title" to "Policía del Estado",
            "card_portale_title" to "Portal de Inmigración",
            "credentials_card_title" to "Credenciales",
            "credentials_missing" to "Faltan",
            "credentials_configure" to "Configurar",
            "timeline_title" to "Cronología de Progreso",
            "status_approved" to "Aprobado / Listo para Retirar",
            "status_pending" to "En Trámite / En Proceso",
            "status_missing_docs" to "Documentos Faltantes / Anomalías",
            "status_unknown" to "Sin estado (Configura tus códigos)",
            "province_selector" to "Seleccionar Provincia",
            "appointment_title" to "Cita Jefatura (Questura)",
            "appointment_not_set" to "Ninguna cita guardada",
            "appointment_set" to "Cita guardada",
            "appointment_date" to "Fecha",
            "appointment_time" to "Hora",
            "appointment_location" to "Lugar",
            "documents_checklist" to "Lista de Documentos Requeridos",
            "history_title" to "Historial de Consultas",
            "history_clear" to "Limpiar Historial",
            "reset_state" to "Restablecer Estado",
            "save_success" to "¡Credenciales guardadas con éxito!",
            "webview_loading" to "Cargando Portal...",
            "webview_offline" to "Estás desconectado. Verifica tu conexión.",
            "guide_title" to "Guía de Códigos de Recibo",
            "guide_body" to "• El Número de Asegurada (12 dígitos, ej: 0500...) está impreso en la parte superior del recibo y sirve para Correos Italianos y Policía de Estado.\n\n• El Área Reservada utiliza el ID de Usuario (12 dígitos del holograma) y la Contraseña (el código de seguridad del recibo de correos).",
            "input_fullname" to "Nombre Completo del Titular",
            "input_assicurata" to "Número de Asegurada (12 dígitos)",
            "input_userid" to "ID de Usuario Portal (12 Dígitos)",
            "input_password" to "Contraseña del Portal",
            "input_pratica" to "Número de Expediente Questura (Opcional)",
            "button_save" to "Guardar y Activar Notificaciones",
            "profile_title" to "Perfil de Usuario",
            "profile_subtitle" to "Gestiona tus credenciales y ajustes de la aplicación.",
            "profile_edit_success" to "¡Perfil actualizado!",
            "select_language" to "Seleccionar Idioma",
            "app_settings" to "Ajustes de la Aplicación",
            "bg_sync" to "Sincronización en Segundo Plano",
            "notif_enabled" to "Notificaciones Activadas",
            "about_section" to "Información",
            "about_text" to "Rastreador de Permiso te ayuda a monitorear el estado de tus trámites de inmigración en Italia directamente desde los portales oficiales con total seguridad.",
            "onboarding_lang_title" to "Elige tu Idioma",
            "onboarding_lang_subtitle" to "Selecciona cómo mostrar la aplicación",
            "button_skip" to "Saltar",
            "button_continue" to "Continuar",
            "button_start" to "Configurar e Iniciar",
            "welcome_title" to "Rastrea tu Permiso",
            "welcome_body" to "¡Bienvenido! Esta aplicación te ayuda a monitorear el estado de tu permiso de residencia en tiempo real, consultando de forma segura y directa los portales oficiales italianos.",
            "privacy_title" to "Seguridad y Privacidad",
            "privacy_body" to "Tus datos sensibles, códigos y contraseñas están encriptados y guardados exclusivamente en tu teléfono en la base de datos local SQLite (Room).\n\nNingún servidor de terceros almacena o analiza tus credenciales.",
            
            // NEW Spanish strings for thorough translation
            "friend" to "Amigo",
            "last_checked" to "Última consulta: %s",
            "no_checks" to "Ningún control realizado",
            "notif_active" to "Notificaciones Activas 🔔",
            "notif_active_desc" to "¡El sistema de monitoreo automático de PermessoSoggiorno está activo!",
            "test_button" to "Probar",
            "config_required" to "Configuración Requerida",
            "config_required_desc" to "Introduce el número de asegurada para rastrear el estado de tu expediente.",
            "config_receipt_btn" to "Configurar Datos de Recibo",
            "no_history_recorded" to "Ninguna consulta registrada",
            "history_help_desc" to "Las consultas automáticas diarias y manuales aparecerán aquí.",
            "tracking_active" to "Rastreo Activo",
            "tracking_to_configure" to "Por Configurar",
            "document_status" to "Estado del Documento",
            "awaiting_check" to "A la espera de verificación",
            "not_active" to "Inactivo",
            "configure_credentials_desc" to "Configurar credenciales",
            "not_verified" to "No Verificado",
            "manual_check_desc" to "Hacer una consulta manual",
            "status" to "Estado",
            "portal_status_header" to "ESTADO DEL PORTAL",
            "immigration_portal" to "Portal de Inmigración",
            "state_police_header" to "POLICÍA DEL ESTADO",
            "ready" to "Listo",
            "in_processing" to "En Trámite",
            "no_code" to "Sin código",
            "not_checked" to "No verificado",
            "ready_for_pickup" to "Listo para retirar",
            "secure_account" to "Cuenta Segura",
            "credentials_summary" to "User: %1\$s • Pass: %2\$s",
            "edit_credentials_desc" to "Modificar credenciales",
            "not_entered" to "No introducido",
            "not_entered_f" to "No introducida",
            "italian_republic" to "REPÚBLICA ITALIANA",
            "residency_permit" to "PERMISO DE RESIDENCIA",
            "holder_not_configured" to "TITULAR POR CONFIGURAR",
            "code_both" to "Recibo: %1\$s | Expediente: %2\$s",
            "code_assicurata" to "Asegurada: %s",
            "code_pratica" to "Expediente: %s",
            "no_code_registered" to "Ningún código registrado",
            "last_checked_header" to "ÚLTIMA CONSULTA",
            "never_checked" to "Nunca consultado",
            "querying_portal" to "Consultando portal...",
            "response_received" to "¡Respuesta Recibida!",
            "check_error" to "Error de Consulta",
            "close" to "Cerrar",
            "querying_portal_desc" to "El sistema se está conectando a los portales oficiales para analizar el estado de tu expediente...",
            "portal_banner_immigrazione" to "Portal de Inmigración (Sportello Amico)",
            "portal_banner_polizia" to "Policía del Estado (Jefatura)",
            "portal_autofill_help_portale" to "Presiona Auto-fill para rellenar automáticamente ID de Usuario y Contraseña.",
            "portal_autofill_help_polizia" to "Presiona Auto-fill para rellenar automáticamente el Número de Asegurada.",
            "portal_data_title" to "Datos del Portal de Inmigración",
            "portal_data_desc" to "Configura tu nombre y credenciales para monitorear el Área Reservada del Portal de Inmigración.",
            "input_fullname_placeholder" to "Nombre y Apellido (según pasaporte)",
            "input_userid_placeholder" to "Ej: 000000000000",
            "input_password_placeholder" to "Código de seguridad (ej: 050000000000)",
            "show_password_desc" to "Mostrar contraseña",
            "polizia_tracking_title" to "Rastreo Policía del Estado",
            "polizia_tracking_desc" to "Configura el número de asegurada o expediente para consultar el sitio web oficial de la Policía del Estado.",
            "input_assicurata_placeholder" to "Ej: 050000000000",
            "input_pratica_placeholder" to "Ej: 00XX00000000",
            "step1_title" to "Correos",
            "step1_desc" to "Recibido",
            "step2_title" to "Jefatura",
            "step2_desc" to "Huellas",
            "step3_title" to "Trámite",
            "step3_desc" to "En Proceso",
            "step4_title" to "Impresión",
            "step4_desc" to "En Producción",
            "step5_title" to "Entrega",
            "step5_desc" to "¡Listo!",
            "timeline_exp_1" to "A la espera del envío del kit postal o de la introducción de datos en el sistema.",
            "timeline_exp_2" to "Kit enviado con éxito. Siguiente paso: cita en la Jefatura para la toma de huellas dactilares (fotosegnalamento).",
            "timeline_exp_3" to "Tu expediente está en fase de trámite/evaluación en la Jefatura. Este proceso normalmente requiere varios meses. Sin anomalías detectadas.",
            "timeline_exp_4" to "¡Trámite completado con éxito! Tu tarjeta electrónica de residencia está en fase de impresión en la Casa de la Moneda del Estado.",
            "timeline_exp_5" to "¡Excelente! Tu permiso de residencia está listo para ser retirado. Comprueba los horarios de apertura de tu Jefatura para la recogida.",
            "timeline_exp_none" to "No se detectó ningún trámite activo.",
            "timeline_advance_title" to "Línea Temporal de Progreso",
            "timeline_current_exp_title" to "Explicación del Estado Actual:",
            "community_wait_estimates_title" to "Estimaciones Orientativas de la Comunidad (No Oficiales)",
            "live_estimates" to "Orientativo 📊",
            "select_province_stats_desc" to "Los tiempos oficiales de tramitación dependen exclusivamente de la carga de trabajo de la Questura competente:",
            "wait_fingerprints" to "Espera Huellas",
            "wait_post_to_fingerprints" to "Correos → Huellas",
            "wait_printing" to "Espera Impresión",
            "wait_fingerprints_to_ready" to "Huellas → Listo",
            "wait_total" to "Espera Total",
            "global_average" to "Rango Estimado",
            "people_like_you" to "Datos Agregados de la Comunidad (Questura de %s):",
            "people_like_you_desc" to "Valores puramente orientativos mostrados solo al alcanzar el umbral estadístico mínimo. Los tiempos reales dependen exclusivamente de la carga de trabajo de la Oficina de Inmigración competente.",
            "ai_advisor_title" to "Referencia de Trámites",
            "ai_advisor_desc" to "Toca una pregunta común para recibir respuestas inmediatas y consejos prácticos:",
            "advisor_q1" to "¿Qué significa el estado 'En Proceso/Trámite'?",
            "advisor_a1" to "Indica que la oficina de extranjería está examinando los requisitos (contrato de trabajo, ingresos, vivienda). Es la fase más larga y puede requerir varios meses. ¡No te alarmes!",
            "advisor_q2" to "¿Qué hacer si he estado esperando más de 12 meses?",
            "advisor_a2" to "Puedes enviar una solicitud formal por PEC (Correo Electrónico Certificado) a tu Jefatura adjuntando copia del recibo. Es tu derecho legal recibir respuesta.",
            "advisor_q3" to "¿Qué documentos llevar para el retiro?",
            "advisor_a3" to "Lleva contigo: 1) Pasaporte original, 2) Tu anterior permiso vencido (si aplica), 3) El recibo de correos (cedolino), 4) Carta o SMS de cita (si los recibiste).",
            "advisor_q4" to "¡Perdí el recibo de Correos! ¿Qué hago?",
            "advisor_a4" to "Debes presentar una denuncia por pérdida ante los Carabinieri o la Policía. Lleva la denuncia original el día de recogida o de huellas para sustituir el recibo.",
            "doc_passport" to "Pasaporte original vigente 🛂",
            "doc_receipt" to "Recibo de correos (Cedolino) 🧾",
            "doc_revenue_stamp" to "Marca da Bollo de €16.00 🎟️",
            "doc_photos" to "4 Fotos tamaño carnet idénticas y recientes 📸",
            "doc_accommodation" to "Contrato de alquiler / declaración de hospitalidad 🏠",
            "doc_work_contract" to "Contrato de trabajo / últimas nóminas 💼",
            "questura_appointments_title" to "Citas Jefatura",
            "edit_appointment_desc" to "Modificar cita",
            "next_appointment" to "Próxima Cita:",
            "appointment_formatted" to "%1\$s a las %2\$s horas",
            "no_appointment_desc" to "Ninguna cita guardada. Toca para introducir la fecha y hora de la convocatoria.",
            "doc_vault_title" to "Caja Fuerte de Documentos Listos",
            "doc_vault_desc" to "Marca los documentos que ya has preparado para asegurarte de no olvidar nada:",
            "appointment_details_title" to "Detalles de la Convocatoria",
            "appointment_date_label" to "Fecha de la Cita",
            "appointment_date_placeholder" to "Ej: 15/10/2026",
            "appointment_time_label" to "Hora de la Cita",
            "appointment_time_placeholder" to "Ej: 09:30",
            "appointment_location_label" to "Jefatura o Comisaría",
            "appointment_location_placeholder" to "Ej: Questura di Milano, via Fatebenefratelli",
            "save" to "Guardar",
            "cancel" to "Cancelar",
            "portal_status_monitor_title" to "Estado de Portales Oficiales 🌐",
            "online_status" to "Conectado 🟢",
            "anti_scam_warning_title" to "Aviso de Seguridad Anti-Estafa:",
            "anti_scam_warning_desc" to "Ten cuidado con los sitios web no oficiales o mensajes SMS que soliciten pagos para rastrear tu permiso. Esta aplicación SOLO contacta con los servidores institucionales oficiales y guarda tus credenciales de forma 100% segura en tu teléfono.",
            "timeline_card_title" to "Cronología de Progreso de la Solicitud",
            "step_1_poste" to "Correos",
            "step_1_received" to "Recibido",
            "step_2_questura" to "Comisaría",
            "step_2_fingerprinting" to "Huellas",
            "step_3_processing" to "Tratamiento",
            "step_3_under_investigation" to "En Trámite",
            "step_4_printing" to "Impresión",
            "step_4_in_production" to "En Producción",
            "step_5_delivery" to "Entrega",
            "step_5_ready" to "Listo",
            "timeline_desc_1" to "El kit postal ha sido enviado. Esperando cita para la toma de huellas dactilares.",
            "timeline_desc_2" to "Registro de huellas dactilares y verificación de documentos en la Comisaría.",
            "timeline_desc_3" to "Su solicitud está actualmente en trámite y evaluación por las oficinas de la Comisaría.",
            "timeline_desc_4" to "Su permiso de residencia ha sido aprobado y está en proceso de impresión/producción.",
            "timeline_desc_5" to "El permiso de residencia está listo para ser retirado en la oficina correspondiente.",
            "timeline_desc_inactive" to "No hay datos de progreso disponibles. Complete la configuración de su perfil.",
            "current_status_explanation" to "Explicación del Estado Actual:",
            "subtab_status" to "Estado",
            "subtab_appointments" to "Citas e IA",
            "subtab_stats" to "Análisis y Portales",
            "guides_card_title" to "Guías de Inmigración",
            "guides_card_desc" to "Todo lo que necesitas saber antes y durante tu solicitud de estancia.",
            "guides_screen_title" to "Guías de Inmigración",
            "guides_search_placeholder" to "Buscar permisos, documentos, oficinas...",
            "guides_bookmarks_title" to "Mis Favoritos",
            "guides_all_title" to "Todas las Guías",
            "guides_difficulty" to "Dificultad:",
            "guides_duration" to "Duración Estimada:",
            "guides_cost" to "Costo Estimado:",
            "guides_validity" to "Validez:",
            "guides_offices" to "Oficinas Requeridas:",
            "guides_sec_overview" to "Resumen",
            "guides_sec_timeline" to "Cronología",
            "guides_sec_documents" to "Documentos Requeridos",
            "guides_sec_deadlines" to "Plazos Importantes",
            "guides_sec_where_start" to "Dónde Iniciar",
            "guides_sec_costs" to "Desglose de Costos",
            "guides_sec_mistakes" to "Errores Comunes",
            "guides_sec_faqs" to "Preguntas Frecuentes",
            "guides_sec_contacts" to "Contactos Útiles",
            "guides_sec_resources" to "Recursos Oficiales",
            "guides_suggestions" to "Sugerencias Contextuales",
            "guides_no_bookmarks" to "No has guardado ninguna guía todavía. Presiona el ícono del marcador para guardarla.",
            "guides_no_results" to "No se encontraron guías. Intenta con otra palabra clave.",
            "guides_disclaimer" to "AVISO LEGAL: Estas guías son puramente informativas y no representan una fuente oficial del gobierno italiano. Comprueba siempre los requisitos oficiales.",
            "guides_last_reviewed" to "Última revisión",
            "guides_version" to "Versión",
            "guides_last_updated" to "Última actualización",
            "guides_next_action" to "Siguiente Acción",
            "guides_what_to_do" to "Qué hacer",
            "guides_local_notice" to "Aviso Local",
            "guides_standard_procedure" to "Se aplica el procedimiento estándar en esta provincia.",
            "guides_community_tips" to "Consejos de la Comunidad",
            "guides_tip_by_anonymous" to "Consejo de Usuario Anónimo",
            "guides_helpful_votes" to "Útil (%d)",
            "guides_ask_ai" to "Consulta Normativa del Trámite",
            "guides_ask_ai_desc" to "Obtenga respuestas normativas sobre los requisitos y procedimientos de esta guía.",
            "guides_ask_ai_placeholder" to "Consultar sobre este trámite...",
            "guides_ask_ai_loading" to "Verificando referencias normativas...",
            "guides_legal_references" to "Referencias Legislativas",
            "guides_related_guides" to "Guías Relacionadas",
            "guides_trust_verified" to "Info Verificada",
            "guides_trust_official" to "Fuente Oficial",
            "guides_trust_updated" to "Actualizado",
            "guides_select_province_guide" to "Selecciona tu provincia para información local:",
            "guides_timeline_average" to "Tiempo Promedio de Procesamiento",
            "guides_timeline_fast" to "Opciones de Vía Rápida",
            "guides_timeline_delays" to "Causas Comunes de Retraso",
            "guides_timeline_relative" to "Velocidad Relativa a Otros Permisos",
            "guides_success_completed" to "¡Felicitaciones! Has completado todos los pasos de este trámite.",
            "onboarding_personalize_title" to "Personaliza Soggiorno Track",
            "onboarding_personalize_desc" to "Soggiorno Track puede personalizar la información según tu Provincia y Questura. Selecciona dónde se gestiona tu trámite para recibir tiempos de espera y guías locales a medida.",
            "onboarding_questura_title" to "¿Dónde se gestiona tu trámite de extranjería?",
            "onboarding_search_placeholder" to "Buscar por Provincia, Questura, Ciudad o Región...",
            "onboarding_choose_later" to "Elegiré más tarde",
            "onboarding_permit_type_title" to "¿Qué tipo de permiso estás solicitando?",
            "onboarding_permit_type_desc" to "Esto nos permite personalizar tu panel, guías y recomendaciones de IA.",
            "your_questura_title" to "Tu Questura",
            "change_questura" to "Cambiar Mi Questura",
            "explore_questure" to "Explorar Questuras",
            "explore_questure_desc" to "Buscar y navegar por todas las Questuras de Italia.",
            "office_status" to "Estado de la Oficina",
            "community_estimate" to "Estimación de espera de la comunidad",
            "quick_actions" to "Acciones Rápidas",
            "call" to "Llamar",
            "open_maps" to "Mapas",
            "visit_website" to "Sitio Web",
            "opening_hours" to "Horarios de Apertura",
            "appointment_method" to "Método de Cita",
            "local_warnings" to "Avisos Locales",
            "pec" to "PEC (Correo Certificado)",
            "address" to "Dirección",
            "phone" to "Teléfono",
            "email" to "Correo Electrónico",
            "website" to "Sitio Web",
            "official_notes" to "Notas Oficiales",
            "community_notes" to "Notas de la Comunidad",
            "guides_available" to "Guías Disponibles",
            "average_waiting_time" to "Tiempo Medio de Espera",
            "related_permit_guides" to "Guías de Permisos Relacionados",
            "permit_work" to "Trabajo",
            "permit_study" to "Estudio",
            "permit_family_reunification" to "Reagrupación Familiar",
            "permit_family_cohesion" to "Cohesión Familiar",
            "permit_asylum" to "Asilo",
            "permit_eu_family" to "Familiar de la UE",
            "permit_long_term" to "Larga Duración",
            "permit_renewal" to "Renovación",
            "permit_not_sure" to "No estoy seguro"
        ),
        "fr" to mapOf(
            "app_name" to "Suivi de Permis",
            "tab_status" to "Statut",
            "tab_portal" to "Portail",
            "tab_receipt" to "Reçu",
            "tab_sofia" to "Sofia AI",
            "tab_profile" to "Profil",
            "dashboard_title" to "Statut du Permis",
            "button_check_now" to "Vérifier Maintenant",
            "checked_on" to "Vérifié le",
            "no_history" to "Aucune vérification effectuée",
            "card_postal_title" to "Poste Italienne",
            "card_polizia_title" to "Police d'État",
            "card_portale_title" to "Portail de l'Immigration",
            "credentials_card_title" to "Identifiants",
            "credentials_missing" to "Manquants",
            "credentials_configure" to "Configurer",
            "timeline_title" to "Chronologie du Progrès",
            "status_approved" to "Approuvé / Prêt pour Retrait",
            "status_pending" to "En Cours / En Instruction",
            "status_missing_docs" to "Documents Manquants / Anomalies",
            "status_unknown" to "Aucun statut (Configurez vos codes)",
            "province_selector" to "Sélectionner la Province",
            "appointment_title" to "Rendez-vous à la Préfecture",
            "appointment_not_set" to "Aucun rendez-vous enregistré",
            "appointment_set" to "Rendez-vous enregistré",
            "appointment_date" to "Date",
            "appointment_time" to "Heure",
            "appointment_location" to "Lieu",
            "documents_checklist" to "Liste des Documents Requis",
            "history_title" to "Historique des Vérifications",
            "history_clear" to "Effacer l'Historique",
            "reset_state" to "Réinitialiser le Statut",
            "save_success" to "Identifiants enregistrés avec succès !",
            "webview_loading" to "Chargement du Portail...",
            "webview_offline" to "Vous êtes hors ligne. Veuillez vérifier votre connexion.",
            "guide_title" to "Guide des Codes du Reçu",
            "guide_body" to "• Le Numéro d'Assurée (12 chiffres, ex : 0500...) est imprimé en haut du reçu et sert pour la Poste Italienne et la Police d'État.\n\n• L'Espace Réservé utilise l'Identifiant (12 chiffres de l'hologramme) et le Mot de Passe (le code de sécurité sur le reçu postal).",
            "input_fullname" to "Nom Complet du Titulaire",
            "input_assicurata" to "Numéro d'Assurée (12 chiffres)",
            "input_userid" to "Identifiant Portail (12 chiffres)",
            "input_password" to "Mot de Passe Portail",
            "input_pratica" to "Numéro de Dossier Préfecture (Optionnel)",
            "button_save" to "Enregistrer & Activer Notifs",
            "profile_title" to "Profil Utilisateur",
            "profile_subtitle" to "Gérez vos identifiants et réglages de l'application.",
            "profile_edit_success" to "Profil mis à jour !",
            "select_language" to "Sélectionner la Langue",
            "app_settings" to "Réglages de l'Application",
            "bg_sync" to "Synchronisation en Arrière-plan",
            "notif_enabled" to "Notifications Activées",
            "about_section" to "À Propos",
            "about_text" to "Suivi de Permis vous aide à surveiller l'état de vos dossiers d'immigration en Italie directement depuis les portails officiels en toute sécurité.",
            "onboarding_lang_title" to "Choisissez votre Langue",
            "onboarding_lang_subtitle" to "Sélectionnez la langue d'affichage",
            "button_skip" to "Passer",
            "button_continue" to "Continuer",
            "button_start" to "Configurer & Commencer",
            "welcome_title" to "Suivez votre Permis",
            "welcome_body" to "Bienvenue ! Cette application vous aide à suivre l'état de votre titre de séjour en temps réel, en interrogeant directement et en toute sécurité les portails officiels italiens.",
            "privacy_title" to "Sécurité & Confidentialité",
            "privacy_body" to "Vos données sensibles, codes et mots de passe sont cryptés et enregistrés exclusivement sur votre téléphone dans la base de données locale SQLite (Room).\n\nAucun serveur tiers ne stocke ni n'analyse vos identifiants.",
            
            // NEW French strings for thorough translation
            "friend" to "Ami",
            "last_checked" to "Dernière vérification: %s",
            "no_checks" to "Aucune vérification effectuée",
            "notif_active" to "Notifications Actives 🔔",
            "notif_active_desc" to "Le système de surveillance automatique de PermessoSoggiorno est actif !",
            "test_button" to "Tester",
            "config_required" to "Configuration Requise",
            "config_required_desc" to "Saisissez le numéro d'assurée pour suivre l'état de votre dossier.",
            "config_receipt_btn" to "Configurer les données du reçu",
            "no_history_recorded" to "Aucun contrôle enregistré",
            "history_help_desc" to "Les contrôles automatiques quotidiens et manuels apparaîtront ici.",
            "tracking_active" to "Suivi Actif",
            "tracking_to_configure" to "À Configurer",
            "document_status" to "Statut du Document",
            "awaiting_check" to "En attente de vérification",
            "not_active" to "Inactif",
            "configure_credentials_desc" to "Configurer les identifiants",
            "not_verified" to "Non Vérifié",
            "manual_check_desc" to "Effectuer un contrôle manuel",
            "status" to "Statut",
            "portal_status_header" to "STATUT DU PORTAIL",
            "immigration_portal" to "Portail de l'Immigration",
            "state_police_header" to "POLICE D'ÉTAT",
            "ready" to "Prêt",
            "in_processing" to "En Cours d'Instruction",
            "no_code" to "Aucun code",
            "not_checked" to "Non vérifié",
            "ready_for_pickup" to "Prêt pour le retrait",
            "secure_account" to "Compte Sécurisé",
            "credentials_summary" to "User: %1\$s • Pass: %2\$s",
            "edit_credentials_desc" to "Modifier les identifiants",
            "not_entered" to "Non saisi",
            "not_entered_f" to "Non saisie",
            "italian_republic" to "RÉPUBLIQUE ITALIENNE",
            "residency_permit" to "TITRE DE SÉJOUR",
            "holder_not_configured" to "TITULAIRE À CONFIGURER",
            "code_both" to "Assurée: %1\$s | Dossier: %2\$s",
            "code_assicurata" to "Assurée: %s",
            "code_pratica" to "Numéro Dossier: %s",
            "no_code_registered" to "Aucun code enregistré",
            "last_checked_header" to "DERNIER CONTRÔLE",
            "never_checked" to "Jamais vérifié",
            "querying_portal" to "Interrogation du portail...",
            "response_received" to "Réponse Reçue !",
            "check_error" to "Erreur de Contrôle",
            "close" to "Fermer",
            "querying_portal_desc" to "Le système se connecte actuellement aux portails officiels pour analyser le statut de votre demande...",
            "portal_banner_immigrazione" to "Portail de l'Immigration (Sportello Amico)",
            "portal_banner_polizia" to "Police d'État (Préfecture)",
            "portal_autofill_help_portale" to "Appuyez sur Auto-fill pour saisir automatiquement l'Identifiant et le Mot de passe.",
            "portal_autofill_help_polizia" to "Appuyez sur Auto-fill pour saisir automatiquement le Numéro d'Assurée.",
            "portal_data_title" to "Données du Portail de l'Immigration",
            "portal_data_desc" to "Configurez votre nom et vos identifiants pour surveiller l'Espace Réservé du Portail de l'Immigration.",
            "input_fullname_placeholder" to "Nom et Prénom (selon passeport)",
            "input_userid_placeholder" to "Ex: 000000000000",
            "input_password_placeholder" to "Code de sécurité (ex : 050000000000)",
            "show_password_desc" to "Afficher le mot de passe",
            "polizia_tracking_title" to "Suivi Police d'État",
            "polizia_tracking_desc" to "Configurez le numéro d'assurée ou de dossier pour interroger le site officiel de la Police d'État.",
            "input_assicurata_placeholder" to "Ex: 050000000000",
            "input_pratica_placeholder" to "Ex: 00XX00000000",
            "step1_title" to "Poste",
            "step1_desc" to "Reçu",
            "step2_title" to "Préfecture",
            "step2_desc" to "Empreintes",
            "step3_title" to "Instruction",
            "step3_desc" to "En Cours",
            "step4_title" to "Impression",
            "step4_desc" to "En Production",
            "step5_title" to "Livraison",
            "step5_desc" to "Prêt!",
            "timeline_exp_1" to "En attente de l'envoi du kit postal ou de la saisie des données dans le système.",
            "timeline_exp_2" to "Kit envoyé avec succès. Prochaine étape : rendez-vous à la Préfecture pour le relevé des empreintes digitales (fotosegnalamento).",
            "timeline_exp_3" to "Votre dossier est en cours d'instruction/traitement à la Préfecture. Ce processus prend généralement plusieurs mois. Aucune anomalie détectée.",
            "timeline_exp_4" to "L'instruction est terminée avec succès ! Votre titre de séjour électronique est en cours d'impression à l'Imprimerie Nationale.",
            "timeline_exp_5" to "Excellent ! Votre Titre de Séjour est prêt pour la remise. Vérifiez les horaires d'ouverture de votre Préfecture pour le retrait.",
            "timeline_exp_none" to "Aucune demande active détectée.",
            "timeline_advance_title" to "Chronologie du Progrès",
            "timeline_current_exp_title" to "Explication du Statut Actuel:",
            "community_wait_estimates_title" to "Estimations Indicatives de la Communauté (Non Officielles)",
            "live_estimates" to "Indicatif 📊",
            "select_province_stats_desc" to "Les délais officiels de traitement dépendent exclusivement de la charge de travail de la Questura compétente :",
            "wait_fingerprints" to "Attente Empreintes",
            "wait_post_to_fingerprints" to "Poste → Empreintes",
            "wait_printing" to "Attente Impression",
            "wait_fingerprints_to_ready" to "Empreintes → Prêt",
            "wait_total" to "Attente Totale",
            "global_average" to "Fourchette Estimée",
            "people_like_you" to "Données Agrégées de la Communauté (Questura de %s) :",
            "people_like_you_desc" to "Valeurs purement indicatives affichées uniquement lorsque le seuil statistique minimum est atteint. Les délais réels dépendent exclusivement de la charge de travail du Bureau de l'Immigration compétent.",
            "ai_advisor_title" to "Référentiel des Procédures",
            "ai_advisor_desc" to "Appuyez sur une question fréquente pour recevoir des conseils immédiats et pratiques :",
            "advisor_q1" to "Que signifie le statut 'En Cours d'Instruction' ?",
            "advisor_a1" to "Cela indique que le bureau de l'immigration examine les conditions (contrat de travail, revenus, logement). C'est la phase la plus longue et elle peut prendre plusieurs mois. Ne vous inquiétez pas !",
            "advisor_q2" to "Que faire si j'attends depuis plus de 12 mois ?",
            "advisor_a2" to "Vous pouvez envoyer un rappel formel par PEC (Courrier Électronique Certifié) à votre Préfecture en joignant une copie du reçu. C'est votre droit légal d'obtenir une réponse.",
            "advisor_q3" to "Quels documents apporter pour le retrait ?",
            "advisor_a3" to "Apportez avec vous : 1) Passeport original, 2) Votre ancien titre expiré (le cas échéant), 3) Le reçu de la poste (cedolino), 4) La lettre ou SMS de rendez-vous (si reçus).",
            "advisor_q4" to "J'ai perdu le reçu de la Poste ! Que faire ?",
            "advisor_a4" to "Vous devez déclarer la perte auprès des Carabinieri ou de la Police. Apportez la déclaration originale le jour du retrait ou du relevé d'empreintes pour remplacer le reçu.",
            "doc_passport" to "Passeport original en cours de validité 🛂",
            "doc_receipt" to "Reçu de la Poste (Cedolino) 🧾",
            "doc_revenue_stamp" to "Timbre Fiscal de 16,00 € 🎟️",
            "doc_photos" to "4 Photos d'identité identiques et récentes 📸",
            "doc_accommodation" to "Contrat de location / déclaration d'hébergement 🏠",
            "doc_work_contract" to "Contrat de travail / fiches de paie récentes 💼",
            "questura_appointments_title" to "Rendez-vous Préfecture",
            "edit_appointment_desc" to "Modifier le rendez-vous",
            "next_appointment" to "Prochain Rendez-vous:",
            "appointment_formatted" to "%1\$s à %2\$s heures",
            "no_appointment_desc" to "Aucun rendez-vous enregistré. Appuyez pour saisir la date et l'heure de la convocation.",
            "doc_vault_title" to "Coffre-Fort des Documents Prêts",
            "doc_vault_desc" to "Cochez les documents que vous avez déjà préparés pour ne rien oublier :",
            "appointment_details_title" to "Détails de la Convocation",
            "appointment_date_label" to "Date du Rendez-vous",
            "appointment_date_placeholder" to "Ex: 15/10/2026",
            "appointment_time_label" to "Heure du Rendez-vous",
            "appointment_time_placeholder" to "Ex: 09:30",
            "appointment_location_label" to "Préfecture ou Commissariat",
            "appointment_location_placeholder" to "Ex: Jefatura de Milan, via Fatebenefratelli",
            "save" to "Enregistrer",
            "cancel" to "Annuler",
            "portal_status_monitor_title" to "Statut des Portails Institutionnels 🌐",
            "online_status" to "En Ligne 🟢",
            "anti_scam_warning_title" to "Avis de Sécurité Anti-Arnaque:",
            "anti_scam_warning_desc" to "Faites attention aux sites web non officiels ou aux messages SMS demandant des paiements pour suivre votre permis. Cette application contacte UNIQUEMENT les serveurs institutionnels officiels et conserve vos identifiants en toute sécurité à 100% sur votre téléphone.",
            "timeline_card_title" to "Chronologie d'Avancement du Dossier",
            "step_1_poste" to "La Poste",
            "step_1_received" to "Reçu",
            "step_2_questura" to "Préfecture",
            "step_2_fingerprinting" to "Empreintes",
            "step_3_processing" to "Traitement",
            "step_3_under_investigation" to "En Instruction",
            "step_4_printing" to "Impression",
            "step_4_in_production" to "En Production",
            "step_5_delivery" to "Remise",
            "step_5_ready" to "Prêt",
            "timeline_desc_1" to "Le kit postal a été envoyé. En attente de votre convocation pour les empreintes.",
            "timeline_desc_2" to "Enregistrement des empreintes et vérification des documents à la Préfecture.",
            "timeline_desc_3" to "Votre dossier est en cours d'instruction et d'évaluation par les bureaux de la Préfecture.",
            "timeline_desc_4" to "Votre titre de séjour est approuvé et en cours d'impression/production.",
            "timeline_desc_5" to "Le titre de séjour est prêt à être retiré au bureau compétent.",
            "timeline_desc_inactive" to "Aucune donnée d'avancement disponible. Veuillez compléter la configuration du profil.",
            "current_status_explanation" to "Explication de l'État Actuel:",
            "subtab_status" to "Statut",
            "subtab_appointments" to "Rendez-vous & IA",
            "subtab_stats" to "Analyses & Portails",
            "guides_card_title" to "Guides d'Immigration",
            "guides_card_desc" to "Tout ce que vous devez savoir avant et pendant votre demande de séjour.",
            "guides_screen_title" to "Guides d'Immigration",
            "guides_search_placeholder" to "Rechercher des permis, documents, bureaux...",
            "guides_bookmarks_title" to "Mes Favoris",
            "guides_all_title" to "Tous les Guides",
            "guides_difficulty" to "Difficulté :",
            "guides_duration" to "Durée Estimée :",
            "guides_cost" to "Coût Estimé :",
            "guides_validity" to "Validité :",
            "guides_offices" to "Bureaux Requis :",
            "guides_sec_overview" to "Aperçu",
            "guides_sec_timeline" to "Chronologie",
            "guides_sec_documents" to "Documents Requis",
            "guides_sec_deadlines" to "Délais Importants",
            "guides_sec_where_start" to "Où Commencer",
            "guides_sec_costs" to "Détail des Coûts",
            "guides_sec_mistakes" to "Erreurs Courantes",
            "guides_sec_faqs" to "Questions Fréquentes",
            "guides_sec_contacts" to "Contacts Utiles",
            "guides_sec_resources" to "Sites Officiels",
            "guides_suggestions" to "Suggestions Contextuelles",
            "guides_no_bookmarks" to "Aucun guide enregistré. Cliquez sur l'icône de favori pour l'ajouter.",
            "guides_no_results" to "Aucun guide trouvé. Essayez un autre mot-clé.",
            "guides_disclaimer" to "DISCLAIMER : Ces guides sont fournis à titre informatif et ne sauraient remplacer les informations officielles du gouvernement italien. Veuillez toujours vérifier.",
            "guides_last_reviewed" to "Dernière révision",
            "guides_version" to "Version",
            "guides_last_updated" to "Dernière mise à jour",
            "guides_next_action" to "Prochaine Action",
            "guides_what_to_do" to "Que faire",
            "guides_local_notice" to "Avis Local",
            "guides_standard_procedure" to "La procédure standard s'applique dans cette province.",
            "guides_community_tips" to "Conseils de la Communauté",
            "guides_tip_by_anonymous" to "Conseil par un Utilisateur Anonyme",
            "guides_helpful_votes" to "Utile (%d)",
            "guides_ask_ai" to "Vérification Réglementaire",
            "guides_ask_ai_desc" to "Obtenez des précisions réglementaires sur les exigences et procédures de ce guide.",
            "guides_ask_ai_placeholder" to "Poser une question sur cette procédure...",
            "guides_ask_ai_loading" to "Vérification des références légales...",
            "guides_legal_references" to "Références Législatives",
            "guides_related_guides" to "Guides Associés",
            "guides_trust_verified" to "Info Vérifiée",
            "guides_trust_official" to "Source Officielle",
            "guides_trust_updated" to "Mis à jour",
            "guides_select_province_guide" to "Sélectionnez votre province pour des informations locales :",
            "guides_timeline_average" to "Temps de Traitement Moyen",
            "guides_timeline_fast" to "Options de Voie Rapide",
            "guides_timeline_delays" to "Motifs de Retard Courants",
            "guides_timeline_relative" to "Vitesse Relative par Rapport aux Autres Permis",
            "guides_success_completed" to "Félicitations ! Vous avez terminé toutes les étapes de cette procédure.",
            "onboarding_personalize_title" to "Personnaliser Soggiorno Track",
            "onboarding_personalize_desc" to "Soggiorno Track peut personnaliser les informations en fonction de votre province et de votre Questura. Sélectionnez l'endroit où votre dossier est traité pour obtenir des temps d'attente et des guides locaux personnalisés.",
            "onboarding_questura_title" to "Où votre procédure d'immigration est-elle traitée ?",
            "onboarding_search_placeholder" to "Rechercher par province, Questura, ville ou région...",
            "onboarding_choose_later" to "Je choisirai plus tard",
            "onboarding_permit_type_title" to "Quel type de titre de séjour demandez-vous ?",
            "onboarding_permit_type_desc" to "Cela nous permet de personnaliser votre tableau de bord, vos guides et les recommandations de l'IA.",
            "your_questura_title" to "Votre Questura",
            "change_questura" to "Modifier Ma Questura",
            "explore_questure" to "Explorer les Questuras",
            "explore_questure_desc" to "Rechercher et parcourir toutes les Questuras en Italie.",
            "office_status" to "Statut du Bureau",
            "community_estimate" to "Estimation d'attente de la communauté",
            "quick_actions" to "Actions Rapides",
            "call" to "Appeler",
            "open_maps" to "Cartes",
            "visit_website" to "Site Web",
            "opening_hours" to "Heures d'Ouverture",
            "appointment_method" to "Méthode de Rendez-vous",
            "local_warnings" to "Avertissements Locaux",
            "pec" to "PEC (Courrier Certifié)",
            "address" to "Adresse",
            "phone" to "Téléphone",
            "email" to "E-mail",
            "website" to "Site Web",
            "official_notes" to "Notes Officielles",
            "community_notes" to "Notes de la Communauté",
            "guides_available" to "Guides Disponibles",
            "average_waiting_time" to "Temps d'Attente Moyen",
            "related_permit_guides" to "Guides des Permis Associés",
            "permit_work" to "Travail",
            "permit_study" to "Études",
            "permit_family_reunification" to "Regroupement Familial",
            "permit_family_cohesion" to "Cohésion Familiale",
            "permit_asylum" to "Asile",
            "permit_eu_family" to "Membre de la Famille UE",
            "permit_long_term" to "Longue Durée",
            "permit_renewal" to "Renouvellement",
            "permit_not_sure" to "Je ne suis pas sûr"
        )
    )

    fun string(key: String, lang: String): String {
        val custom = when (key) {
            "confirm" -> when (lang) {
                "en" -> "Confirm"
                "ar" -> "تأكيد"
                "es" -> "Confirmar"
                "fr" -> "Confirmer"
                else -> "Conferma"
            }
            "delete_profile" -> when (lang) {
                "en" -> "Delete Profile & Data"
                "ar" -> "حذف الملف الشخصي والبيانات"
                "es" -> "Eliminar Perfil y Datos"
                "fr" -> "Supprimer le Profil et les Données"
                else -> "Elimina Profilo e Dati"
            }
            "delete_profile_confirm_title" -> when (lang) {
                "en" -> "Delete Profile"
                "ar" -> "حذف الملف الشخصي"
                "es" -> "Eliminar Perfil"
                "fr" -> "Supprimer le Profil"
                else -> "Elimina Profilo"
            }
            "delete_profile_confirm_msg" -> when (lang) {
                "en" -> "This action will permanently delete your profile, all credentials, checking history, and Sofia AI messages. This cannot be undone."
                "ar" -> "سيؤدي هذا الإجراء إلى حذف ملفك الشخصي وجميع بياناتك وسجل الفحوصات ومحادثات صوفيا نهائياً. لا يمكن التراجع عن هذا الإجراء."
                "es" -> "Esta acción eliminará permanentemente tu perfil, credenciales, historial de controles y mensajes de Sofia AI. No se puede deshacer."
                "fr" -> "Cette action supprimera définitivement votre profil, vos identifiants, l'historique des contrôles et les messages de Sofia AI. Cette action est irréversible."
                else -> "Questa azione eliminerà definitivamente il tuo profilo, tutte le credenziali, la cronologia dei controlli e i messaggi di Sofia AI dal tuo dispositivo. Questa operazione non può essere annullata."
            }
            "delete_profile_success" -> when (lang) {
                "en" -> "All local data has been successfully deleted."
                "ar" -> "تم حذف جميع البيانات المحلية بنجاح."
                "es" -> "Todos los datos locales han sido eliminados con éxito."
                "fr" -> "Toutes les données locales ont été supprimées avec succès."
                else -> "Tutti i dati locali sono stati eliminati con successo dal tuo dispositivo."
            }
            "barcode_optical_support_title", "optical_barcode_support_title" -> when (lang) {
                "en" -> "Optical Barcode Support (Mod. 22A)"
                "ar" -> "الدعم البصري للباركود (نموذج Mod. 22A - وصل البريد الأصلي)"
                "es" -> "Soporte Óptico de Código de Barras (Mod. 22A)"
                "fr" -> "Support Optique Code-Barres (Mod. 22A)"
                else -> "Supporto Ottico Codice a Barre (Mod. 22A)"
            }
            "paper_receipt_warning_title" -> when (lang) {
                "en" -> "⚠️ ORIGINAL PAPER RECEIPT MANDATORY"
                "ar" -> "⚠️ إشعار قانوني: إلزامية إحضار وصل البريد الأصلي الورقي"
                "es" -> "⚠️ OBLIGACIÓN DE RECIBO EN PAPEL ORIGINAL"
                "fr" -> "⚠️ OBLIGATION DU RÉCÉPISSÉ PAPIER ORIGINAL"
                else -> "⚠️ OBBLIGO DI RICEVUTA CARTACEA ORIGINALE"
            }
            "paper_receipt_warning_body" -> when (lang) {
                "en" -> "This digital barcode only assists optical scanning if the paper slip is worn. It DOES NOT replace the original Mod. 22A with hologram, which you must ALWAYS present physically at the counter."
                "ar" -> "هذا الباركود الرقمي مخصص فقط لتسهيل القراءة الضوئية في حال تلف القسيمة الورقية. ولا يغني أبداً عن وصل البريد الأصلي (Mod. 22A) المزود بالهولوغرام، والذي يجب عليك دائماً تقديمه ورقياً في مكتب الهجرة."
                "es" -> "Este código de barras digital solo sirve para facilitar la lectura óptica si el resguardo en papel está desgastado. NO sustituye al Mod. 22A original con holograma, que SIEMPRE debes presentar físicamente en ventanilla."
                "fr" -> "Ce code-barres numérique sert uniquement à faciliter la lecture optique si le récépissé papier est usé. Il NE remplace PAS le Mod. 22A original avec hologramme, que vous devez TOUJOURS présenter physiquement au guichet."
                else -> "Questo codice a barre digitale serve solo ad agevolare la lettura ottica se il cedolino cartaceo è usurato. NON sostituisce il Mod. 22A originale con ologramma, che devi SEMPRE presentare fisicamente allo sportello."
            }
            "pec_tool_title", "pec_generator_title" -> when (lang) {
                "en" -> "Official Communications & Terms Check (Law 241/1990)"
                "ar" -> "المراسلات الرسمية والتحقق من المواعيد القانونية (قانون 241/1990)"
                "es" -> "Comunicaciones Oficiales y Verificación de Plazos (L. 241/1990)"
                "fr" -> "Communications Officielles & Vérification des Délais (L. 241/1990)"
                else -> "Comunicazioni Ufficiali & Verifica Termini (L. 241/1990)"
            }
            "pec_generator_subtitle" -> when (lang) {
                "en" -> "Collaborative administrative inquiry models for the Questura Immigration Office"
                "ar" -> "نماذج مراسلات إدارية تعاونية موجهة إلى مكتب الهجرة (Questura)"
                "es" -> "Modelos colaborativos de comunicación administrativa para la Oficina de Inmigración"
                "fr" -> "Modèles collaboratifs de communication administrative pour le Bureau de l'Immigration"
                else -> "Modelli di comunicazione amministrativa collaborativa per l'Ufficio Immigrazione"
            }
            "pec_deadline_note" -> when (lang) {
                "en" -> "Note: While Art. 5 c. 9 D.Lgs. 286/98 indicates 60 days, real administrative timelines vary by province, Questura workload, and procedural complexity."
                "ar" -> "إشعار قانوني: رغم أن المادة 5 فقرة 9 من المرسوم التشريعي 286/98 تشير إلى 60 يوماً، إلا أن المواعيد الإدارية الفعلية تختلف حسب المقاطعة وضغط العمل في مكتب الهجرة."
                "es" -> "Nota: Aunque el Art. 5 c. 9 D.Lgs. 286/98 indica 60 días, los plazos administrativos reales varían según la provincia y la carga de trabajo de la Questura."
                "fr" -> "Note : Bien que l'Art. 5 c. 9 D.Lgs. 286/98 indique 60 jours, les délais administratifs réels varient selon la province et la charge de travail de la Questura."
                else -> "Nota: Sebbene l'Art. 5 c. 9 D.Lgs. 286/98 indichi 60 giorni, i tempi amministrativi reali variano in base alla provincia, al carico di lavoro della Questura e alla complessità dell'istruttoria."
            }
            "pec_template_1", "pec_tpl_1_title" -> when (lang) {
                "en" -> "Standard Application Status Inquiry (90+ days)"
                "ar" -> "طلب استفسار عادي عن حالة الملف لدى مكتب الهجرة (بعد +90 يوماً)"
                "es" -> "Solicitud Ordinaria de Información de Estado (90+ días)"
                "fr" -> "Demande Ordinaire d'Informations sur l'État du Dossier (90+ jours)"
                else -> "Richiesta Informazioni Stato Pratica (Ordinaria)"
            }
            "pec_tpl_1_sub" -> when (lang) {
                "en" -> "Polite administrative inquiry after 90+ days requesting an update on procedural status."
                "ar" -> "استفسار إداري مهذب بعد مرور أكثر من 90 يوماً لطلب تحديث حول حالة الإجراءات."
                "es" -> "Consulta administrativa cortés tras 90+ días solicitando actualización del trámite."
                "fr" -> "Demande administrative courtoise après 90+ jours pour connaître l'état d'avancement."
                else -> "Cortese richiesta amministrativa (dopo 90+ giorni) per conoscere lo stato di avanzamento della pratica."
            }
            "pec_template_2", "pec_tpl_2_title" -> when (lang) {
                "en" -> "Documented Urgency Notification (Work / Study / Health)"
                "ar" -> "إشعار قانوني بوجود حالة طارئة موثقة (عمل / دراسة / صحة)"
                "es" -> "Notificación de Urgencia Comprobada (Trabajo / Estudio / Salud)"
                "fr" -> "Signalement d'Urgence Justifiée (Travail / Études / Santé)"
                else -> "Segnalazione Comprovata Urgenza (Lavoro / Studio / Salute)"
            }
            "pec_tpl_2_sub" -> when (lang) {
                "en" -> "Formal notification of a documented urgent deadline (contract expiry, enrollment, health) — attach PDF proof."
                "ar" -> "إشعار رسمي لمكتب الهجرة بوجود موعد نهائي طارئ وموثق (انتهاء عقد عمل، تسجيل جامعي، ضرورة صحية) مع إرفاق إثبات PDF."
                "es" -> "Comunicación formal notificando un plazo urgente documentado (contrato, estudios, salud) adjuntando prueba PDF."
                "fr" -> "Communication formelle signalant une échéance urgente justifiée (contrat, études, santé) avec justificatif PDF."
                else -> "Comunicazione formale per segnalare una scadenza urgente documentata (contratto, università, salute) allegando prova in PDF."
            }
            "pec_template_3", "pec_tpl_3_title" -> when (lang) {
                "en" -> "Civic Access / Anomaly Verification Request (Art. 22 L. 241/1990)"
                "ar" -> "طلب اطلاع إداري والتحقق من النواقص (المادة 22 قانون 241/1990)"
                "es" -> "Instancia de Acceso Cívico / Verificación de Anomalías (Art. 22 L. 241/1990)"
                "fr" -> "Demande d'Accès Administratif / Vérification d'Anomalies (Art. 22 L. 241/1990)"
                else -> "Istanza di Accesso Civico / Verifica Anomalie (Art. 22 L. 241/1990)"
            }
            "pec_tpl_3_sub" -> when (lang) {
                "en" -> "Polite formal request to verify if any document integration (Art. 10-bis) is holding up the file."
                "ar" -> "طلب رسمي مهذب للتحقق مما إذا كان هناك أي نقص في المستندات (المادة 10-مكرر) يؤخر معالجة الملف."
                "es" -> "Solicitud formal cortés para verificar si alguna integración documental (Art. 10-bis) detiene el expediente."
                "fr" -> "Demande formelle courtoise pour vérifier si un complément de dossier (Art. 10-bis) bloque l'instruction."
                else -> "Cortese richiesta formale per verificare se eventuali integrazioni documentali (Art. 10-bis) bloccano la pratica."
            }
            "pec_antispam_checkbox" -> when (lang) {
                "en" -> "I confirm that I have a concrete reason (e.g. contract expiry, work/family urgency) or prolonged delay, and I understand that repeated PEC messages slow down the offices' work."
                "ar" -> "أؤكد وجود سبب جدي وملموس (مثل انتهاء عقد العمل أو ضرورة عائلية/صحية) أو تأخر طويل، وأدرك أن الإرسال المتكرر لرسائل البريد المعتمد (PEC) يبطئ عمل مكتب الهجرة."
                "es" -> "Confirmo que tengo un motivo concreto (ej. vencimiento de contrato, urgencia laboral/familiar) o un retraso prolongado, y comprendo que el envío repetido de PEC ralentiza el trabajo de las oficinas."
                "fr" -> "Je confirme avoir un motif concret (ex. expiration de contrat, urgence professionnelle/familiale) ou un retard prolongé, et je comprends que l'envoi répété de PEC ralentit le travail des bureaux."
                else -> "Confermo di avere un motivo concreto (es. scadenza contratto, urgenza lavorativa/familiare) o un ritardo prolungato, e comprendo che l'invio ripetuto di PEC rallenta il lavoro degli uffici."
            }
            "travel_verdict_allowed", "travel_verdict_compliant" -> when (lang) {
                "en" -> "COMPLIANT WITH INTERIOR MINISTRY CIRCULAR (Verify with Airline)"
                "ar" -> "متوافق مع تعميم وزارة الداخلية (يرجى التحقق من شركة الطيران)"
                "es" -> "CONFORME A LA CIRCULAR DEL MIN. DEL INTERIOR (Verificar con Aerolínea)"
                "fr" -> "CONFORME À LA CIRCULAIRE DU MIN. DE L'INTÉRIEUR (Vérifier Compagnie)"
                else -> "CONFORME ALLA CIRCOLARE MIN. INTERNO (Verifica Compagnia)"
            }
            "travel_verdict_schengen_denied" -> when (lang) {
                "en" -> "SCHENGEN TRANSIT NOT PERMITTED WITH RECEIPT ALONE"
                "ar" -> "العبور عبر دول شنغن غير مسموح به باستخدام وصل البريد الأصلي فقط"
                "es" -> "TRÁNSITO SCHENGEN NO PERMITIDO SOLO CON RECIBO"
                "fr" -> "TRANSIT SCHENGEN NON AUTORISÉ AVEC LE SEUL RÉCÉPISSÉ"
                else -> "TRANSITO SCHENGEN NON CONSENTITO CON SOLA RICEVUTA"
            }
            "travel_verdict_first_issue" -> when (lang) {
                "en" -> "FIRST ISSUANCE: EXIT/RE-ENTRY NOT PERMITTED WITHOUT VISA"
                "ar" -> "الإصدار الأول: الخروج والعودة غير مسموح بهما بدون تأشيرة سارية"
                "es" -> "PRIMER EXPEDIENTE: SALIDA/REINGRESO NO PERMITIDOS SIN VISADO"
                "fr" -> "PREMIÈRE DÉLIVRANCE : SORTIE/RETOUR NON AUTORISÉS SANS VISA"
                else -> "PRIMO RILASCIO: USCITA/RIENTRO NON CONSENTITI SENZA VISTO"
            }
            "travel_regulatory_disclaimer", "travel_regulations_disclaimer" -> when (lang) {
                "en" -> "Please Note: This tool provides guidance based on the Ministry of the Interior Circular of August 5, 2006 and D.P.R. 394/1999. Actual boarding always depends on Border Police checks and airline policies. Always contact your airline before purchasing a ticket."
                "ar" -> "إشعار قانوني هام: تقدم هذه الأداة توجيهاً استرشادياً استناداً إلى تعميم وزارة الداخلية بتاريخ 5 أغسطس 2006 والمرسوم الرئاسي D.P.R. 394/1999. يعتمد السماح الفعلي بصعود الطائرة دائماً على تدقيق شرطة الحدود وسياسات شركة الطيران. تواصل دائماً مع شركة الطيران قبل شراء التذكرة."
                "es" -> "Nota Bene: Esta herramienta proporciona orientación basada en la Circular del Ministerio del Interior del 5 de agosto de 2006 y el D.P.R. 394/1999. El embarque efectivo depende siempre de los controles de la Policía de Fronteras y de las políticas de la aerolínea. Contacte siempre a la aerolínea antes de comprar el billete."
                "fr" -> "Note Importante : Cet outil fournit une orientation basée sur la Circulaire du Ministère de l'Intérieur du 5 août 2006 et le D.P.R. 394/1999. L'embarquement effectif dépend toujours des contrôles de la Police aux Frontières et des politiques de la compagnie aérienne. Contactez toujours la compagnie aérienne avant d'acheter votre billet."
                else -> "Nota Bene: Questo strumento fornisce un orientamento basato sulla Circolare del Ministero dell'Interno del 5 agosto 2006 e D.P.R. 394/1999. L'imbarco effettivo dipende sempre dai controlli della Polizia di Frontiera e dalle policy della compagnia aerea. Contatta sempre la compagnia aerea prima di acquistare il biglietto."
            }
            "sofia_persistent_disclaimer" -> when (lang) {
                "en" -> "Sofia provides indicative information based on public sources and does not constitute professional legal counsel. For assistance with your application, please contact an accredited Patronato."
                "ar" -> "إشعار قانوني: تقدم صوفيا معلومات توجيهية بناءً على مصادر عامة ولا تُعد استشارة قانونية مهنية. للحصول على مساعدة رسمية بخصوص ملفك لدى مكتب الهجرة، يرجى مراجعة مكتب باتروناتو (Patronato) معتمد."
                "es" -> "Sofía proporciona información orientativa basada en fuentes públicas y no constituye asesoramiento legal profesional. Para asistencia con tu trámite, acude a un Patronato."
                "fr" -> "Sofia fournit des informations indicatives basées sur des sources publiques et ne constitue pas un conseil juridique professionnel. Pour toute assistance sur votre dossier, adressez-vous à un Patronato."
                else -> "Sofia fornisce informazioni orientative basate su fonti pubbliche e non costituisce consulenza legale professionale. Per assistenza sulla tua pratica, rivolgiti a un Patronato."
            }
            "portal_unreachable_title" -> when (lang) {
                "en" -> "Ministerial Portal Under Maintenance / Unreachable"
                "ar" -> "البوابة الوزارية قيد الصيانة أو غير متاحة حالياً"
                "es" -> "Portal Ministerial en Mantenimiento / No Disponible"
                "fr" -> "Portail Ministériel en Maintenance / Injoignable"
                else -> "Portale Ministeriale in Manutenzione"
            }
            "portal_unreachable_msg" -> when (lang) {
                "en" -> "The ministerial portal is temporarily under maintenance or unreachable. No changes detected on your application."
                "ar" -> "البوابة الوزارية لمكتب الهجرة قيد الصيانة مؤقتاً أو غير قابلة للوصول حالياً. لم يتم رصد أي تغيير على حالة ملفك."
                "es" -> "El portal ministerial está temporalmente en mantenimiento o no disponible. No se detectaron cambios en tu expediente."
                "fr" -> "Le portail ministériel est momentanément en maintenance ou injoignable. Aucune modification détectée sur votre dossier."
                else -> "Il portale ministeriale è momentaneamente in manutenzione o non raggiungibile. Nessuna modifica rilevata sulla tua pratica."
            }
            "empty_profile_prompt" -> when (lang) {
                "en" -> "Complete your profile or scan your receipt"
                "ar" -> "يرجى إكمال ملفك الشخصي أو مسح وصل البريد الأصلي"
                "es" -> "Completa tu perfil o escanea el recibo"
                "fr" -> "Complétez votre profil ou scannez le récépissé"
                else -> "Completa il tuo profilo o scansiona la ricevuta"
            }
            "greeting_general" -> when (lang) {
                "en" -> "Welcome!"
                "ar" -> "أهلاً بك!"
                "es" -> "¡Bienvenido!"
                "fr" -> "Bienvenue !"
                else -> "Ciao!"
            }
            "greeting_user" -> when (lang) {
                "en" -> "Welcome, %s!"
                "ar" -> "أهلاً بك، %s!"
                "es" -> "¡Bienvenido, %s!"
                "fr" -> "Bienvenue, %s !"
                else -> "Ciao, %s!"
            }
            "subtab_practice" -> when (lang) {
                "en" -> "1. Your Application"
                "ar" -> "\u200E1. معاملتك"
                "es" -> "1. Tu trámite"
                "fr" -> "1. Votre dossier"
                else -> "1. La Tua Pratica"
            }
            "subtab_tools" -> when (lang) {
                "en" -> "2. Tool Hub"
                "ar" -> "\u200E2. حقيبة الأدوات"
                "es" -> "2. Kit de herramientas"
                "fr" -> "2. Boîte à outils"
                else -> "2. Kit Strumenti"
            }
            "subtab_stats_tab" -> when (lang) {
                "en" -> "3. Statistics"
                "ar" -> "\u200E3. الإحصائيات"
                "es" -> "3. Estadísticas"
                "fr" -> "3. Statistiques"
                else -> "3. Statistiche"
            }
            "section_main_status_title" -> when (lang) {
                "en" -> "Main Status & Live Check"
                "ar" -> "الحالة الرئيسية والتحقق الفوري"
                "es" -> "Estado principal y verificación en vivo"
                "fr" -> "Statut principal et vérification en direct"
                else -> "Stato Principale & Verifica Live"
            }
            "section_main_status_sub" -> when (lang) {
                "en" -> "Current status of your application and immediate check button"
                "ar" -> "الوضع الحالي لملفك مع إمكانية التحقق الفوري"
                "es" -> "Estado actual de tu trámite y botón de verificación inmediata"
                "fr" -> "Statut actuel de votre dossier et bouton de vérification immédiate"
                else -> "Stato attuale della tua pratica e pulsante di verifica immediata"
            }
            "badge_4_tools" -> when (lang) {
                "en" -> "4 TOOLS"
                "ar" -> "4 أدوات"
                "es" -> "4 HERRAMIENTAS"
                "fr" -> "4 OUTILS"
                else -> "4 STRUMENTI"
            }
            "section_quick_tools_title" -> when (lang) {
                "en" -> "Quick Tool Access (1-Tap)"
                "ar" -> "الوصول السريع للأدوات"
                "es" -> "Acceso rápido a herramientas"
                "fr" -> "Accès rapide aux outils"
                else -> "Accesso Rapido Strumenti (1-Tap)"
            }
            "section_quick_tools_sub" -> when (lang) {
                "en" -> "Open Barcode Pass, Travel Rules, PEC Inquiries, or Legal Rights"
                "ar" -> "الوصول إلى باركود الشباك، شروط السفر، طلبات الاستعجال، والحقوق القانونية"
                "es" -> "Abre el pase de código de barras, reglas de viaje, solicitud PEC o derechos"
                "fr" -> "Ouvrez le pass code-barres, règles de voyage, relance PEC ou droits"
                else -> "Apri subito Supporto Ottico, Regole Viaggio, Sollecito PEC o Diritti"
            }
            "tool_tile_1_title" -> when (lang) {
                "en" -> "#1 Mod. 22A Barcode"
                "ar" -> "#1 باركود Mod. 22A"
                "es" -> "#1 Código Mod. 22A"
                "fr" -> "#1 Code-barres Mod. 22A"
                else -> "#1 Supporto Mod. 22A"
            }
            "tool_tile_1_sub" -> when (lang) {
                "en" -> "Optical reader and receipt scanner"
                "ar" -> "قارئ الرمز الشريطي والمسح الضوئي للإيصال"
                "es" -> "Lector óptico y escáner de recibo"
                "fr" -> "Lecteur optique et scanner de reçu"
                else -> "Lettura ottica e scansione cedolino"
            }
            "tool_tile_2_title" -> when (lang) {
                "en" -> "#2 Travel Rules with Receipt"
                "ar" -> "#2 قواعد السفر بالوصل"
                "es" -> "#2 Reglas de Viaje con Recibo"
                "fr" -> "#2 Règles de Voyage avec Récépissé"
                else -> "#2 Regole Viaggio con Ricevuta"
            }
            "tool_tile_2_sub" -> when (lang) {
                "en" -> "Transit rules and direct flights"
                "ar" -> "شروط العبور والرحلات المباشرة"
                "es" -> "Condiciones de tránsito y vuelos directos"
                "fr" -> "Conditions de transit et vols directs"
                else -> "Verifica scali e voli diretti"
            }
            "tool_tile_3_title" -> when (lang) {
                "en" -> "#3 Official PEC Inquiries"
                "ar" -> "#3 مراسلات PEC الرسمية"
                "es" -> "#3 Comunicaciones PEC Oficiales"
                "fr" -> "#3 Communications PEC Officielles"
                else -> "#3 Comunicazioni PEC Ufficiali"
            }
            "tool_tile_3_sub" -> when (lang) {
                "en" -> "Urgent inquiries & Law 241/90"
                "ar" -> "نماذج الاستعجال وقانون 241/90"
                "es" -> "Modelos de urgencia y Ley 241/90"
                "fr" -> "Modèles urgents et Loi 241/90"
                else -> "Modelli formali e verifica L. 241/90"
            }
            "tool_tile_4_title" -> when (lang) {
                "en" -> "#4 Minimum Income Calculator"
                "ar" -> "#4 حاسبة الحد الأدنى للدخل"
                "es" -> "#4 Calculadora de Ingresos Mínimos"
                "fr" -> "#4 Calculateur de Revenu Minimum"
                else -> "#4 Calcolatore Reddito Minimo"
            }
            "tool_tile_4_sub" -> when (lang) {
                "en" -> "Social allowance thresholds for renewal and family"
                "ar" -> "حساب متطلبات الدخل لتجديد الإقامة ولم الشمل"
                "es" -> "Límites de subsidio para renovación y familia"
                "fr" -> "Seuils d'allocation pour renouvellement et famille"
                else -> "Soglie Assegno Sociale per Rinnovo e Famiglia"
            }
            "tool_tile_5_title" -> when (lang) {
                "en" -> "#5 Document Vault & Deadlines"
                "ar" -> "#5 جدول مواعيد الوثائق"
                "es" -> "#5 Bóveda de Documentos y Plazos"
                "fr" -> "#5 Coffre-fort Documents & Dates"
                else -> "#5 Scadenziario & Vault Documenti"
            }
            "tool_tile_5_sub" -> when (lang) {
                "en" -> "Passport tracking and civic protections"
                "ar" -> "متابعة صلاحية الجواز والحقوق"
                "es" -> "Seguimiento de pasaporte y derechos"
                "fr" -> "Suivi du passeport et droits civiques"
                else -> "Monitoraggio passaporto e tutele"
            }
            "tool_pill_1" -> when (lang) {
                "ar" -> "Mod. 22A والمسح الضوئي"
                "es" -> "MOD. 22A Y OCR"
                else -> "MOD. 22A & OCR"
            }
            "tool_pill_2" -> when (lang) {
                "en" -> "TRAVEL RULES"
                "ar" -> "قواعد السفر"
                "es" -> "REGLAS DE VIAJE"
                "fr" -> "RÈGLES DE VOYAGE"
                else -> "NORMATIVA VIAGGI"
            }
            "tool_pill_3" -> when (lang) {
                "en" -> "LAW 241/1990"
                "ar" -> "قانون 241/90"
                "es" -> "LEY 241/1990"
                "fr" -> "LOI 241/1990"
                else -> "L. 241/1990"
            }
            "tool_pill_4" -> when (lang) {
                "en" -> "INPS THRESHOLDS 2026"
                "ar" -> "دخل INPS 2026"
                "es" -> "LÍMITES INPS 2026"
                "fr" -> "SEUILS INPS 2026"
                else -> "SOGLIE INPS 2026"
            }
            "tool_pill_5" -> when (lang) {
                "en" -> "QUESTURA CHECKLIST"
                "ar" -> "قائمة الكويستورا"
                "es" -> "CHECKLIST QUESTURA"
                "fr" -> "CHECKLIST QUESTURA"
                else -> "CHECKLIST QUESTURA"
            }
            "badge_2_sources" -> when (lang) {
                "en" -> "2 SOURCES"
                "ar" -> "مصدران"
                "es" -> "2 FUENTES"
                "fr" -> "2 SOURCES"
                else -> "2 FONTI"
            }
            "section_portals_detail_title" -> when (lang) {
                "en" -> "Official Portals & Convocation Details"
                "ar" -> "تفاصيل البوابات الرسمية والمواعيد"
                "es" -> "Detalle de portales oficiales y citación"
                "fr" -> "Détails des portails officiels et convocation"
                else -> "Dettaglio Portali Ufficiali & Convocazione"
            }
            "section_portals_detail_sub" -> when (lang) {
                "en" -> "Comparison between Immigration Portal (Poste) and State Police (Questura)"
                "ar" -> "مقارنة بين بوابة الهجرة وشرطة الدولة (الكويستورا)"
                "es" -> "Comparación entre Portal de Inmigración y Policía del Estado"
                "fr" -> "Comparaison entre Portail Immigration et Police d'État"
                else -> "Confronto tra Portale Immigrazione (Poste) e Questura (Polizia di Stato)"
            }
            "section_questura_guides_title" -> when (lang) {
                "en" -> "Your Questura & Official Guides"
                "ar" -> "الكويستورا الخاصة بك والأدلة الرسمية"
                "es" -> "Tu Questura y guías oficiales"
                "fr" -> "Votre Questura et guides officiels"
                else -> "La Tua Questura & Guide Ufficiali"
            }
            "section_questura_guides_sub" -> when (lang) {
                "en" -> "Competent Immigration Office contacts and step-by-step guides"
                "ar" -> "بيانات مكتب الهجرة المختص وأدلة إرشادية خطوة بخطوة"
                "es" -> "Contactos de la Oficina de Inmigración competente y guías paso a paso"
                "fr" -> "Coordonnées du bureau d'immigration compétent et guides pratiques"
                else -> "Riferimenti dell'Ufficio Immigrazione competente e guide pratiche"
            }
            "section_history_title" -> when (lang) {
                "en" -> "Recent Check History"
                "ar" -> "سجل عمليات التحقق الأخيرة"
                "es" -> "Historial de verificaciones recientes"
                "fr" -> "Historique des vérifications récentes"
                else -> "Cronologia Controlli Recenti"
            }
            "section_history_sub" -> when (lang) {
                "en" -> "Chronological log of checks performed on official portals"
                "ar" -> "سجل زمني لعمليات التحقق التي تمت على البوابات الرسمية"
                "es" -> "Registro cronológico de las comprobaciones realizadas en los portales"
                "fr" -> "Journal chronologique des vérifications effectuées sur les portails"
                else -> "Registro cronologico delle verifiche effettuate sui portali"
            }
            "section_appointment_checklist_title" -> when (lang) {
                "en" -> "Appointment & Document Checklist"
                "ar" -> "الموعد وقائمة الوثائق المطلوبة"
                "es" -> "Cita y lista de documentos"
                "fr" -> "Rendez-vous et liste des documents"
                else -> "Appuntamento & Checklist Documenti"
            }
            "section_appointment_checklist_sub" -> when (lang) {
                "en" -> "Prepare everything required for your fingerprint appointment at Questura"
                "ar" -> "جهّز جميع المستندات المطلوبة لموعد أخذ البصمات في الكويستورا"
                "es" -> "Prepara todos los documentos requeridos para las huellas en la Questura"
                "fr" -> "Préparez tous les documents requis pour les empreintes à la Questura"
                else -> "Prepara tutti i documenti richiesti per il fotosegnalamento in Questura"
            }
            "badge_checklist" -> when (lang) {
                "ar" -> "قائمة"
                "es" -> "LISTA"
                "fr" -> "LISTE"
                else -> "CHECKLIST"
            }
            "section_faq_title" -> when (lang) {
                "en" -> "Status Meaning & Official FAQ"
                "ar" -> "معاني الحالات والأسئلة الشائعة"
                "es" -> "Significado de estados y preguntas frecuentes"
                "fr" -> "Signification des statuts et FAQ officielle"
                else -> "Significato Stati & Domande Frequenti"
            }
            "section_faq_sub" -> when (lang) {
                "en" -> "Understand each phase of your permit application on official portals"
                "ar" -> "تعرف على معنى كل مرحلة من مراحل ملف إقامتك على البوابات الرسمية"
                "es" -> "Comprende qué significa cada estado en los portales oficiales"
                "fr" -> "Comprenez chaque étape de votre dossier sur les portails officiels"
                else -> "Comprendi cosa significa ogni stato sui portali ufficiali"
            }
            "badge_guide" -> when (lang) {
                "ar" -> "دليل"
                "es" -> "GUÍA"
                else -> "GUIDA"
            }
            "section_wait_times_title" -> when (lang) {
                "en" -> "Local Processing Wait Times"
                "ar" -> "متوسط أوقات الانتظار حسب المقاطعة"
                "es" -> "Tiempos medios de espera por provincia"
                "fr" -> "Délais moyens de traitement par province"
                else -> "Tempi Medi di Attesa per Provincia"
            }
            "section_wait_times_sub" -> when (lang) {
                "en" -> "Estimated fingerprint and card printing timelines"
                "ar" -> "تقديرات لأخذ البصمات وطباعة البطاقة الإلكترونية"
                "es" -> "Estimaciones para huellas e impresión de la tarjeta"
                "fr" -> "Estimations pour les empreintes et l'impression de la carte"
                else -> "Stime per fotosegnalamento e stampa card elettronica"
            }
            "section_uptime_title" -> when (lang) {
                "en" -> "Official Portals Uptime Monitor"
                "ar" -> "مراقبة خوادم البوابات الرسمية"
                "es" -> "Monitoreo de servidores de portales oficiales"
                "fr" -> "Surveillance des serveurs des portails officiels"
                else -> "Monitoraggio Server Portali Ufficiali"
            }
            "section_uptime_sub" -> when (lang) {
                "en" -> "Live status of State Police and Immigration Portal"
                "ar" -> "حالة اتصال شرطة الدولة وبوابة الهجرة"
                "es" -> "Disponibilidad de la Policía del Estado y Portal de Inmigración"
                "fr" -> "Disponibilité de la Police d'État et du Portail Immigration"
                else -> "Stato di raggiungibilità di Polizia di Stato e Portale Immigrazione"
            }
            "section_credentials_summary_title" -> when (lang) {
                "en" -> "Saved Receipt Credentials"
                "ar" -> "ملخص بيانات الإيصال المحفوظة"
                "es" -> "Resumen de credenciales guardadas"
                "fr" -> "Résumé des identifiants enregistrés"
                else -> "Riepilogo Credenziali Ricevuta"
            }
            "section_credentials_summary_sub" -> when (lang) {
                "en" -> "Your official receipt tracking codes saved on device"
                "ar" -> "الرموز الرسمية لإيصالك المحفوظة حالياً على جهازك"
                "es" -> "Tus códigos oficiales configurados actualmente en el dispositivo"
                "fr" -> "Vos codes officiels actuellement configurés sur l'appareil"
                else -> "I codici ufficiali della tua ricevuta attualmente configurati"
            }
            "badge_active" -> when (lang) {
                "en" -> "ACTIVE"
                "ar" -> "نشط"
                "es" -> "ACTIVOS"
                "fr" -> "ACTIFS"
                else -> "ATTIVI"
            }
            "check_cooldown_button" -> when (lang) {
                "en" -> "Wait before checking again (%ds)"
                "ar" -> "انتظر قبل إعادة الفحص (%d ث)"
                "es" -> "Espera antes de verificar de nuevo (%ds)"
                "fr" -> "Attendez avant de revérifier (%ds)"
                else -> "Attendi prima di riverificare (%ds)"
            }
            "check_cooldown_subtitle" -> when (lang) {
                "en" -> "Safety interval to avoid overloading ministerial servers."
                "ar" -> "فترة أمان لتجنب التحميل الزائد على الخوادم الوزارية."
                "es" -> "Intervalo de seguridad para no sobrecargar los servidores ministeriales."
                "fr" -> "Intervalle de sécurité pour ne pas surcharger les serveurs ministériels."
                else -> "Intervallo di sicurezza per non sovraccaricare i server ministeriali."
            }
            "demo_mode_label" -> when (lang) {
                "en" -> "Institutional Demo Mode"
                "ar" -> "الوضع التجريبي المؤسسي (بيانات نموذجية)"
                "es" -> "Modo Demo Institucional"
                "fr" -> "Mode Démo Institutionnel"
                else -> "Modalità Demo Istituzionale"
            }
            "demo_mode_subtitle" -> when (lang) {
                "en" -> "Using neutral sample placeholders (MARIO ROSSI • 050000000000)"
                "ar" -> "استخدام بيانات نموذجية محايدة للعرض المؤسسي (MARIO ROSSI • 050000000000)"
                "es" -> "Usando datos ficticios institucionales (MARIO ROSSI • 050000000000)"
                "fr" -> "Utilisation de données fictives institutionnelles (MARIO ROSSI • 050000000000)"
                else -> "Dati fittizi di esempio attivi (MARIO ROSSI • Assicurata 050000000000)"
            }
            else -> null
        }
        if (custom != null) return custom

        return translations[lang]?.get(key)
            ?: translations["it"]?.get(key)
            ?: key
    }

    fun get(key: String, lang: String): String = string(key, lang)

    fun string(key: String, lang: String, vararg args: Any): String {
        val base = string(key, lang)
        return try {
            if (args.isNotEmpty()) {
                String.format(base, *args)
            } else {
                base
            }
        } catch (e: Exception) {
            base
        }
    }
}

typealias Strings = Localization

