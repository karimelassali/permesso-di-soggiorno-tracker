# 🇮🇹 Permesso Soggiorno Tracker — Soggiorno Track

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat-for-the-badge&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg?style=flat-for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg?style=flat-for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Database-Room%20SQLite-orange.svg?style=flat-for-the-badge&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![WorkManager](https://img.shields.io/badge/Scheduler-WorkManager-teal.svg?style=flat-for-the-badge)](https://developer.android.com/topic/libraries/architecture/workmanager)

A highly polished, modern, and offline-first Android application designed to help foreign residents in Italy track the status of their **Permesso di Soggiorno** (Residence Permit) across both official Italian portals effortlessly.

---

## 📖 The Problem & Solution

Navigating the Italian immigration system can be challenging. To check the status of a residence permit, applicants typically have to check two separate websites:
1. **Portale Immigrazione (Poste Italiane)** — To see receipt steps (Step 1 - Ricevuto, Step 2 - In istruttoria, Step 3 - Anomalie non riscontrate).
2. **Polizia di Stato (Questura)** — To check if the physical smartcard is ready for collection.

These portals often feature intrusive cookie consent banners, lack responsive mobile styling, and do not save user credentials.

**Permesso Soggiorno Tracker** solves these pain points by uniting both tracking mechanisms into a single, cohesive **Material 3 Bento-Grid Dashboard**, providing background automated checks, local offline caching, and smart browser enhancements.

---

## 🎨 Visual Identity & UI Polish

The application implements high-end design principles to offer an inviting, professional, and trustworthy user experience:
* **The "Tricolore" Stripe**: A premium, thin, and elegant Italian flag accent bar is pinned underneath the main action header, instantly providing national context.
* **Bento Grid Architecture**: Clean, high-contrast, rounded cards segment information with a clear visual hierarchy.
* **Tab-Based De-bloating Layout**: A beautifully styled sub-tab selection bar sits at the top of the main screen to divide rich features into logical workspaces and avoid scroll fatigue.
* **Dynamic Status Dots**: 
  * 🟢 **Green (Emerald)**: Ready for pickup / Step 3 complete.
  * 🟡 **Orange (Amber)**: Application currently in processing (In trattazione / Step 1-2).
  * 🔘 **Grey**: Unverified or not yet configured.
* **Responsive Scrolling**: Generous bottom padding in web containers ensures bottom action sheets do not block viewports or interactive inputs.

---

## ✨ Key Features & Micro-Details

### 1. Multi-Tabbed Bento Dashboard (Visual De-bloating)
To prevent scroll fatigue and visual overload, the dashboard is divided into three intuitive sub-tabs:
* **Status (`Stato`)**: Displays primary status cards, side-by-side portal status summaries, the progressive linear step timeline, and historical status audit logs.
* **Appointments & AI (`Appuntamenti & AI`)**: Manages the dynamic appointment countdown tracker, physical document checkable vault, and the Gemini-powered interactive AI Advisor.
* **Stats & Portals (`Analisi & Portali`)**: Houses community-sourced waiting estimations across all Italian provinces, official institutional server monitors, and secure on-device credential cards.

### 2. Multi-Language Internationalization (Intl)
The application is fully localized and supports seamless dynamic switching between five major languages:
* 🇮🇹 **Italian** (`it`)
* 🇬🇧 **English** (`en`)
* 🇦🇪 **Arabic** (`ar`)
* 🇪🇸 **Spanish** (`es`)
* 🇫🇷 **French** (`fr`)
All labels, headings, system placeholders, and contextual descriptions automatically re-render upon changing the language in the profile section.

### 3. Native Right-to-Left (RTL) Layout Engine
For Arabic speaking users, selecting the Arabic language dynamically triggers a full Right-to-Left layout adaptation:
* Leverages Jetpack Compose `CompositionLocalProvider` overriding `LocalLayoutDirection`.
* Margins, alignment patterns, input fields, and icon directions automatically mirror to offer a fully customized native experience.

### 4. Progressive Visual Timeline Stepper
A gorgeous linear step progress tracker (`BentoPermessoTimelineCard`) that details the five milestones of the immigration journey:
1. **Poste Italiane / Post Office**: Receipt of kit.
2. **Questura / Fingerprints**: Fingerprinting and document presentation.
3. **Processing / Investigation**: Dossier verification by police officials.
4. **Printing / Production**: Smartcard physical production.
5. **Delivery / Ready**: Collection stage.
* **Smart Visibility logic**: The timeline card dynamically hides if the application has never been checked (`lastCheckedTime == 0L`), ensuring unconfigured users aren't misled by random dummy values.

### 5. Interactive Status Interpreter & AI Advisor Chat
An on-device virtual helper designed to help users understand Italian bureaucratic jargon, status messages, and required documents. Powered by safe local prompt structures.

### 6. Dynamic Appointment Countdown & Document Vault
* **Appointment Tracker**: Set dates, times, and Questura locations with live, real-time countdown widgets showing the exact days remaining.
* **Stateful Document Vault**: Interactive checklist to help users organize essential folders (Passport, 4 photos, tax receipts, housing declarations) before entering their appointment.

### 7. Dual-Portal Tracking & Scraping Engine
* **Questura Portal (Polizia di Stato)**: Tracks and interprets status messages like *"Pronto per la consegna"* (Ready for delivery), *"In trattazione"* (Under processing), or *"Non presente in archivio"*.
* **Immigration Portal (Portale Immigrazione)**: Keeps track of application steps 1, 2, and 3, handling complex login strings and credentials securely.

### 8. Native Embedded Browser with "Smart Overlays"
The integrated web viewer loads the official websites directly but enhances them on-the-fly:
* **GDPR Auto-Consent Engine**: A silent Javascript loop automatically clicks cookie approval prompts and hides known consent overlays to save screen real estate.
* **"Nascondi Cookie 🍪" Floating Action Button**: A manual override FAB that dynamically purges and destroys stubborn sticky banners, unlocking full-screen web browsing instantly.
* **Background Cookies Preservation**: Configured with persistent HTML5 local storage and custom WebView cookie flushing to preserve your logged-in portal sessions.

### 9. Background Automated WorkManager Tasks
* Powered by Android's **WorkManager API**, the app schedules background checks (or periodic simulated validations) to check for updates when you are away.
* **Smart History Logging**: Automatically detects changes in background outputs and logs updates only if a status changes, preventing notification fatigue.

### 10. Local Offline Room SQLite Database
* All user credentials (User ID, password, assicurata receipt code, and application codes) are stored strictly on-device in a encrypted SQLite database via Jetpack Room.
* No third-party analytics, intermediate servers, or remote cloud database sync. Your sensitive legal data remains 100% private.
* **Historical Audit Logs**: Maintains a clean historical database of status checks, recording the exact time, source (Questura vs Portale), and response message.

### 11. Immigration Guides & Interactive Knowledge Platform (Guida ai Permessi) 📚
A comprehensive, production-grade immigration assistant that transforms Soggiorno Track from a simple tracking application into a complete, trust-centric immigration knowledge platform:
* **Rich Segmented Catalog**: Detailed step-by-step guides covering major Italian permit procedures: Work Permit (Lavoro), Student Permit (Studio), Family Reunion (Famiglia), Long-Term EU stays (Lungo Periodo), and Permit Renewals (Rinnovo).
* **Multi-Lingual Localization**: Every guide, checklist, FAQ, step, and warning is fully translated across Italian, English, Arabic (with native RTL rendering), Spanish, and French.
* **Trust Badges & Dynamic Versioning**: Displays "Last Reviewed" date, "Guide Version", and "Last Updated" metrics to assure applicants they are reading verified, up-to-date guidance.
* **Official Source Anchors**: Clickable professional badges (e.g., *Ministero dell'Interno*, *Questura*, *Poste Italiane*, *UNHCR*) with direct hyperlinks to open official institutional portals safely.
* **Province-Specific Insights**: Seamless integration with the user's active province (e.g., Milano, Roma, Torino, Bologna, Napoli) showing customized local warnings, wait-times, and office-specific procedural differences.
* **Interactive Next Action Card**: A highly-visible smart assistant card at the top of the guide that calculates the next incomplete document in your checklist, offering a direct "Complete Task" button to tick it off immediately.
* **Warning & Critical Alert Cards**: Styled Material 3 alert bars that adapt their colors to severity (Danger red vs Warning amber) to highlight pitfalls like expired visas or translation requirements.
* **Contextual "Ask AI" Gemini Client**: Live chat assistant integrated directly into each guide. It sends context-aware guide JSON alongside user queries to Google's **gemini-3.5-flash** model to answer custom questions, complete with clickable quick-query prompt chips and text copy capabilities.
* **Interactive Community Tips**: Contributed insights section featuring live thumbs-up feedback voting counters where users can upvote helpful tips in real-time.
* **Estimated Timeline Metrics Gauge**: Visual processing-speed rating gauge displaying delay reasons, average processing timelines, and fast-track options.
* **Expandable Legislative Citations**: Accordion-styled legal references mapping specific laws, articles, and circulars directly to the permit guide.
* **On-Device Bookmark Manager**: Tapping the bookmark ribbon saves the guide locally for instant offline consultation, complete with quick-action bookmarks lists on the guides dashboard.
* **Expandable Accordion FAQs**: Polished Material 3 collapsible accordion lists addressing common applicant questions with spring-based animated expands.
* **Direct Action Integrations**: Embedded hotkeys to directly phone help numbers (dialer intent integration) or open official application portals (browser intent integration).
* **Mandatory Legal Disclaimer**: Every page features a prominent regulatory warning advising users that instructions are informational and do not replace professional immigration advisors.

### 12. Personalized Province & Questura Setup Onboarding 🗺️
* **Step-by-Step Onboarding**: A complete, native 6-page onboarding flow welcomes new users and assists them in selecting their preferred language, learning about privacy standards, personalizing their application location, selecting their permit type, and entering their optional receipt credentials safely.
* **Offline Questura Catalog & Search**: Users can search and filter the fast, pre-bundled catalog of over 100+ Questure and provinces in Italy. No network API calls are made, keeping user location data completely confidential.
* **Permit Type Personalization**: Captures user permit categories (Work, Study, Family, Asylum, EU Family, etc.) with beautiful iconography to personalize guides and status workflows.
* **Personalized Questura Card**: Displays details of the selected Questura directly on the main status dashboard tab, featuring telephone links (one-click dialer integration), website links, PEC email copiers, custom local warnings (e.g. queue alerts, timing alerts), and an instant Google Maps locator button.
* **Active Questura Explorer**: A dedicated search and exploration screen lets the user browse and change their selected Questura anytime directly from the dashboard card.

---

## 🛠️ Architecture & Tech Stack

This app utilizes standard industry practices for modern Android development:

```
[UI Layer (Jetpack Compose)] 
      │ 
      ▼
[State Management (ViewModel & StateFlow)]
      │
      ▼
[Business Logic (AppRepository)]
      │
      ├──► [Local Persistence (Room DB)] ──► SQLite (Credentials & History)
      └──► [Background Tasks (WorkManager)] ──► StatusCheckWorker
```

* **UI Framework**: Jetpack Compose using Material Design 3.
* **State Engine**: Android Architecture Components `ViewModel` with reactive Kotlin `StateFlow`.
* **Background Worker**: `WorkManager` API executing periodic automated syncs.
* **Database Layer**: `Room DB` utilizing custom DAOs with safe background Coroutines dispatching.
* **Notification System**: Built with strict Android 13+ permission standards and native `NotificationCompat`.

---

## 🚀 How to Build and Run

To compile and inspect the app locally, ensure you have the Android SDK installed:

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd soggiorno-track
   ```

2. **Verify Dependencies**:
   Open the root `build.gradle.kts` and `/app/build.gradle.kts` to inspect Gradle tasks.

3. **Build the Debug APK**:
   ```bash
   gradle assembleDebug
   ```

4. **Run Local Unit & Robolectric Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## 🇮🇹 Crediti

Sviluppato con passione per aiutare la comunità internazionale in Italia. 
*Disclaimer: Questa applicazione non è affiliata, approvata o associata ufficialmente alla Polizia di Stato o a Poste Italiane.*
