package com.example.cst438project1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cst438project1.database.entities.User
import java.util.concurrent.Executors

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null
        val databaseWriteExecutor = Executors.newFixedThreadPool(4)

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}