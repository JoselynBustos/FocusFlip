package mx.edu.utez.focusflip.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mx.edu.utez.focusflip.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE correo = :identifier OR matricula = :identifier LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): UserEntity?

    @Query("UPDATE users SET daily_goal_minutes = :goalMinutes WHERE matricula = :matricula")
    suspend fun updateDailyGoal(matricula: String, goalMinutes: Int)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int
}
