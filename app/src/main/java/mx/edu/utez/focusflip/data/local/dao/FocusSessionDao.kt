package mx.edu.utez.focusflip.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mx.edu.utez.focusflip.data.local.entity.FocusSessionEntity

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY id DESC")
    fun observeAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Int): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE remote_id = :remoteId LIMIT 1")
    suspend fun getSessionByRemoteId(remoteId: Int): FocusSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Query("DELETE FROM focus_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)

    @Query("SELECT COUNT(*) FROM focus_sessions")
    suspend fun countSessions(): Int
}
