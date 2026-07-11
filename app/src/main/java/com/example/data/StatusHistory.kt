package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status_history")
data class StatusHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val portalType: String, // "POLIZIA" or "PORTALE"
    val statusText: String, // e.g., "Pronto per la consegna" or "In trattazione"
    val isSuccess: Boolean, // Whether the check succeeded or had a network/web error
    val details: String = "" // Optional notes like error message, or questura location
)
