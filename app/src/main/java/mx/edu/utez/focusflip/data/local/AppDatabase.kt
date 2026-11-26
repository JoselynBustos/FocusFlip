package mx.edu.utez.focusflip.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.edu.utez.focusflip.data.local.dao.FocusSessionDao
import mx.edu.utez.focusflip.data.local.dao.UserDao
import mx.edu.utez.focusflip.data.local.entity.FocusSessionEntity
import mx.edu.utez.focusflip.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        FocusSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focusflip_local_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
