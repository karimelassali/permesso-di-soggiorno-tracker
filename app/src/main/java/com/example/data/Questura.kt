package com.example.data

data class Questura(
    val id: String,
    val name: String,
    val province: String,
    val provinceCode: String,
    val region: String,
    val address: String,
    val phone: String,
    val email: String,
    val pec: String,
    val website: String,
    val googleMapsUrl: String,
    val officeNotes: LocalizedText,
    val openingHours: LocalizedText,
    val appointmentMethod: LocalizedText,
    val portalAvailability: String, // "Both", "Polizia", "Poste"
    val averageWaitingTime: LocalizedText,
    val localWarnings: LocalizedText,
    val communityNotes: LocalizedText
)
