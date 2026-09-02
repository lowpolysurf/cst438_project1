package com.example.cst438project1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cst438project1.database.entities.User
import java.util.concurrent.Executors

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    // Generates implementation that returns UserDAO
    abstract fun userDAO(): UserDAO

    companion object {
        // Stores the one shared database instance
        @Volatile
        private var INSTANCE: UserDatabase? = null

        val databaseWriteExecutor = Executors.newFixedThreadPool(4)

        // Returns database instance used by the app
        fun getDatabase(context: Context): UserDatabase {
            // If database exists, returns it. Otherwise, creates one
            return INSTANCE ?: synchronized(this) {
                // Builds database only the first time this function runs
                val instance = Room.databaseBuilder(
                    // Application context denies database to retain an Activity
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()

                // Save created database
                INSTANCE = instance
                instance
            }
        }
    }
}