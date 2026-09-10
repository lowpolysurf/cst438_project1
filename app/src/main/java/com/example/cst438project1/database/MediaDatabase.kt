package com.example.cst438project1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cst438project1.database.entities.MediaItem
import com.example.cst438project1.database.Converters
import com.example.cst438project1.database.entities.MediaRating
import java.util.concurrent.Executors

@Database(entities = [MediaItem::class, MediaRating::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase(){

    // Gives the app access to the MediaDAO
    abstract fun mediaDAO(): MediaDAO

    companion object {
        // Stores the single database instance
        @Volatile
        private var INSTANCE: MediaDatabase? = null

        // Background threads for db write operations
        val databaseWriteExecutor = Executors.newFixedThreadPool(4)

        // Returns database instance
        fun getDatabase(context: Context): MediaDatabase{
            // If db already exists, reuse it
            // If db does not exist, create it
            return INSTANCE ?: synchronized(this){

                // Creates the Room database using app's context
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaDatabase::class.java,
                    "media_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()

                // Saves the created database
                INSTANCE = instance

                // Returns the database
                instance
            }
        }
    }
}