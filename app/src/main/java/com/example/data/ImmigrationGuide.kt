package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guide_bookmarks")
data class GuideBookmark(
    @PrimaryKey val guideId: String
)

@Entity(tableName = "document_checklist_states", primaryKeys = ["guideId", "documentId"])
data class DocumentChecklistState(
    val guideId: String,
    val documentId: String,
    val isChecked: Boolean
)

data class LocalizedText(
    val it: String,
    val en: String,
    val ar: String,
    val es: String,
    val fr: String
) {
    fun get(lang: String): String = when (lang) {
        "it" -> it
        "ar" -> ar
        "es" -> es
        "fr" -> fr
        else -> en
    }
}

data class GuideOverview(
    val whoIsItFor: LocalizedText,
    val whoCanApply: LocalizedText,
    val mainRequirements: List<LocalizedText>
)

data class GuideTimelineStep(
    val stepNumber: Int,
    val title: LocalizedText,
    val description: LocalizedText
)

data class GuideDocument(
    val id: String,
    val name: LocalizedText,
    val description: LocalizedText
)

data class GuideDeadline(
    val period: LocalizedText,
    val action: LocalizedText,
    val severity: String // "warning", "info", "danger"
)

data class GuideWhereToStart(
    val mainOffice: LocalizedText,
    val explanation: LocalizedText
)

data class GuideCostItem(
    val name: LocalizedText,
    val amount: String
)

data class GuideCommonMistake(
    val title: LocalizedText,
    val explanation: LocalizedText
)

data class GuideFaq(
    val question: LocalizedText,
    val answer: LocalizedText
)

data class GuideContact(
    val name: LocalizedText,
    val type: String, // "phone", "email", "map", "web"
    val value: String
)

data class GuideOfficialResource(
    val name: LocalizedText,
    val url: String,
    val agency: String? = null // e.g. "Ministero dell'Interno", "Questura", etc.
)

data class ProvinceNote(
    val provinceCode: String, // e.g. "Milano", "Roma", etc.
    val note: LocalizedText
)

data class GuideWarning(
    val severity: String, // "warning", "danger"
    val title: LocalizedText,
    val description: LocalizedText
)

data class CommunityTip(
    val id: String,
    val author: LocalizedText,
    val content: LocalizedText,
    val initialHelpfulCount: Int
)

data class GuideEstimatedTimeline(
    val averageTime: LocalizedText,
    val fastTrackOption: LocalizedText,
    val delayReasons: List<LocalizedText>,
    val relativeSpeedPercent: Float // 0f (fastest) to 1f (slowest)
)

data class LegalReference(
    val law: LocalizedText,
    val article: LocalizedText,
    val circular: LocalizedText,
    val explanation: LocalizedText
)

data class ImmigrationGuide(
    val id: String,
    val category: String, // "work", "study", "family", "long_term", "citizenship", "renewal"
    val iconName: String, // "work", "school", "people", "star", "refresh", "home"
    val title: LocalizedText,
    val description: LocalizedText,
    val difficulty: LocalizedText,
    val estimatedDuration: LocalizedText,
    val estimatedCosts: LocalizedText,
    val validityPeriod: LocalizedText,
    val requiredOffices: List<String>,
    val overview: GuideOverview,
    val timeline: List<GuideTimelineStep>,
    val documents: List<GuideDocument>,
    val deadlines: List<GuideDeadline>,
    val whereToStart: GuideWhereToStart,
    val costBreakdown: List<GuideCostItem>,
    val commonMistakes: List<GuideCommonMistake>,
    val faqs: List<GuideFaq>,
    val helpfulContacts: List<GuideContact>,
    val officialResources: List<GuideOfficialResource>,
    val lastReviewed: LocalizedText,
    val guideVersion: String,
    val lastUpdated: LocalizedText,
    val provinceNotes: List<ProvinceNote>,
    val warnings: List<GuideWarning>,
    val communityTips: List<CommunityTip>,
    val aiPrompts: List<LocalizedText>,
    val estimatedTimelineDetails: GuideEstimatedTimeline,
    val legalReferences: List<LegalReference>,
    val recommendations: List<String>
)
