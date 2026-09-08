package com.example.cst438project1.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.cst438project1.SimklClient
import com.example.cst438project1.database.entities.MediaItem
import com.example.cst438project1.database.entities.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository private constructor(application: Application) {

    // Gets DAO from Room db
    private val mediaDAO: MediaDAO = MediaDatabase.getDatabase(application).mediaDAO()

    // Gets all saved media from db
    fun getAllMedia(): LiveData<List<MediaItem>> =
        mediaDAO.getAllMedia()

    // Gets media from saved type: "MOVIE" "ANIME" "TV"
    fun getMediaByType(type: MediaType): LiveData<List<MediaItem>> =
        mediaDAO.getMediaByType(type.name)

    // Inserts media item
    fun insertMedia(item: MediaItem) {
        MediaDatabase.databaseWriteExecutor.execute {
            mediaDAO.insert(item)
        }
    }

    // Searches Simkl and saves the results
    // type param must be "movie", "tv", or "anime"
    suspend fun fetchAndSaveFromSimkl(query: String, type: String) {
        // Dispatchers.IO is used for network and database work (Thanks, Claude)
        withContext(Dispatchers.IO) {

            // Calls the search API
            val results = SimklClient.api.searchMedia(
                type = type,
                query = query,
                clientId = SimklClient.CLIENT_ID
            )

            // Converts the types into enum values
            val mappedType = when (type) {
                "movie" -> MediaType.MOVIE
                "tv" -> MediaType.TV
                "anime" -> MediaType.ANIME

                // Just a default if type not provided
                else -> MediaType.MOVIE
            }

            // Converts SimklMedia object into a MediaItem
            // so it can be stored in the database
            val items = results.map { result ->
                MediaItem(
                    type = mappedType,
                    title = result.title,
                    year = result.year,
                    imageUrl = result.poster?.let { "https://simkl.in/posters/${it}_m.jpg" },
                    // Simkl ID is stored as a string
                    externalId = result.ids?.simkl?.toString()
                )
            }
            // Inserts all items into the database
            mediaDAO.insert(*items.toTypedArray())
        }
    }

    companion object {
        @Volatile
        private var repository: MediaRepository? = null

        // Returns existing repo or creates it
        fun getRepository(application: Application): MediaRepository {
            return repository ?: synchronized(this) {
                // Creates the repo using application object
                val instance = MediaRepository(application)

                // Saves the repo
                repository = instance

                // Returns the repo
                instance
            }
        }
    }
}