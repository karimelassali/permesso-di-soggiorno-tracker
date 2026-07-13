package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_credentials")
data class UserCredentials(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Karim El Assali",
    val userId: String = "550000133317", // 12-digit User ID for Portale Immigrazione login
    val password: String = "055987476941", // Password for Portale Immigrazione login
    val numeroAssicurata: String = "055987476941", // 12-digit Post office receipt number for Polizia di Stato
    val numeroPratica: String = "", // 10-character Pratica number for Polizia di Stato (e.g. MI12345678)
    val lastCheckedTime: Long = 0L,
    val lastPoliziaStatus: String = "No checks yet",
    val lastPortaleStatus: String = "No checks yet",
    val selectedQuesturaId: String? = null,
    val selectedProvinceCode: String? = null,
    val selectedPermitType: String? = null
)
