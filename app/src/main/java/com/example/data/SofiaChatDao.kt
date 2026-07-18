package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SofiaChatDao {
    @Query("SELECT * FROM sofia_conversations ORDER BY isPinned DESC, lastActive DESC")
    fun getAllConversationsFlow(): Flow<List<SofiaConversation>>

    @Query("SELECT * FROM sofia_conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: Long): SofiaConversation?

    @Query("SELECT * FROM sofia_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversationFlow(conversationId: Long): Flow<List<SofiaMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: SofiaConversation): Long

    @Update
    suspend fun updateConversation(conversation: SofiaConversation)

    @Query("DELETE FROM sofia_conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SofiaMessage): Long

    @Update
    suspend fun updateMessage(message: SofiaMessage)

    @Query("DELETE FROM sofia_messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: Long)
    
    @Query("DELETE FROM sofia_conversations")
    suspend fun deleteAllConversations()

    @Query("DELETE FROM sofia_messages")
    suspend fun deleteAllMessages()
    
    @Query("SELECT * FROM sofia_conversations WHERE title LIKE :query ORDER BY lastActive DESC")
    suspend fun searchConversations(query: String): List<SofiaConversation>
}
