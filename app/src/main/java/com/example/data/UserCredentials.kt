package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_credentials")
data class UserCredentials(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val userId: String = "", // 12-digit User ID (Codice Ologramma) for Portale Immigrazione login
    val password: String = "", // 12-digit Password (Codice Assicurata without hyphen) for Portale Immigrazione login
    val numeroAssicurata: String = "", // 12-digit Post office receipt number for Polizia di Stato
    val numeroPratica: String = "", // 10/12-character Pratica / Istanza number
    val lastCheckedTime: Long = 0L,
    val lastPoliziaStatus: String = "No checks yet",
    val lastPortaleStatus: String = "No checks yet",
    val codiceIstanza: String = "",
    val codiceOlogramma: String = "",
    val dettaglioAnomalie: String = "",
    val convocazioneDate: String = "",
    val convocazioneLocation: String = "",
    val convocazioneMotivo: String = "",
    val selectedQuesturaId: String? = null,
    val selectedProvinceCode: String? = null,
    val selectedPermitType: String? = null
)
