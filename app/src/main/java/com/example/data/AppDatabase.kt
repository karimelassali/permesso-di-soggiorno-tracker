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

@Database(entities = [UserCredentials::class, StatusHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userCredentialsDao(): UserCredentialsDao
    abstract fun statusHistoryDao(): StatusHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "permesso_status_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
