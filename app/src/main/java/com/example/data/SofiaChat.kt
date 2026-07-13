package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sofia_conversations")
data class SofiaConversation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val lastActive: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val category: String? = null
)

@Entity(
    tableName = "sofia_messages",
    foreignKeys = [
        ForeignKey(
            entity = SofiaConversation::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["conversationId"])]
)
data class SofiaMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val conversationId: Long,
    val sender: String, // "user" or "sofia"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceType: String = "ai", // "ai", "official", "community" (distinguishes source of info)
    val liked: Boolean? = null // null, true (like), false (dislike)
)
