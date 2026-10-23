package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCredentialsDao {
    @Query("SELECT * FROM user_credentials WHERE id = 1 LIMIT 1")
    fun getCredentialsFlow(): Flow<UserCredentials?>

    @Query("SELECT * FROM user_credentials WHERE id = 1 LIMIT 1")
    suspend fun getCredentialsDirect(): UserCredentials?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(credentials: UserCredentials)

    @Query("UPDATE user_credentials SET biometricProtectionEnabled = :enabled WHERE id = 1")
    suspend fun updateBiometricProtection(enabled: Boolean)

    @Query("UPDATE user_credentials SET biometricAutoFillEnabled = :enabled WHERE id = 1")
    suspend fun updateBiometricAutoFill(enabled: Boolean)

    @Query("UPDATE user_credentials SET lastBiometricAuthTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastBiometricAuthTimestamp(timestamp: Long)
}

@Dao
interface CredentialVaultDao {
    @Query("SELECT * FROM saved_credential_vault ORDER BY updatedAt DESC")
    fun getAllVaultEntriesFlow(): Flow<List<SavedCredentialVaultEntry>>

    @Query("SELECT * FROM saved_credential_vault ORDER BY updatedAt DESC")
    suspend fun getAllVaultEntriesDirect(): List<SavedCredentialVaultEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateVaultEntry(entry: SavedCredentialVaultEntry): Long

    @Query("DELETE FROM saved_credential_vault WHERE entryId = :entryId")
    suspend fun deleteVaultEntryById(entryId: Long)

    @Query("DELETE FROM saved_credential_vault")
    suspend fun clearAllVaultEntries()
}

@Dao
interface StatusHistoryDao {
    @Query("SELECT * FROM status_history ORDER BY timestamp DESC")
    fun getAllHistoryFlow(): Flow<List<StatusHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: StatusHistory)

    @Query("DELETE FROM status_history")
    suspend fun clearAll()
}

@Dao
interface GuideBookmarkDao {
    @Query("SELECT * FROM guide_bookmarks")
    fun getAllBookmarksFlow(): Flow<List<GuideBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: GuideBookmark)

    @Query("DELETE FROM guide_bookmarks WHERE guideId = :guideId")
    suspend fun delete(guideId: String)
}

@Dao
interface DocumentChecklistDao {
    @Query("SELECT * FROM document_checklist_states WHERE guideId = :guideId")
    fun getChecklistStatesFlow(guideId: String): Flow<List<DocumentChecklistState>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(state: DocumentChecklistState)
}

@Database(
    entities = [
        UserCredentials::class,
        SavedCredentialVaultEntry::class,
        StatusHistory::class,
        GuideBookmark::class,
        DocumentChecklistState::class,
        SofiaConversation::class,
        SofiaMessage::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userCredentialsDao(): UserCredentialsDao
    abstract fun credentialVaultDao(): CredentialVaultDao
    abstract fun statusHistoryDao(): StatusHistoryDao
    abstract fun guideBookmarkDao(): GuideBookmarkDao
    abstract fun documentChecklistDao(): DocumentChecklistDao
    abstract fun sofiaChatDao(): SofiaChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "permesso_status_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
