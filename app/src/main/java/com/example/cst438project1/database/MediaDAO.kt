package com.example.cst438project1.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cst438project1.database.entities.MediaItem
import com.example.cst438project1.database.entities.MediaRating
@Dao
interface MediaDAO{
    // Inserts 1+ medias into the database
    // If already exists, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: MediaItem)

    // Delete the MediaItem from db
    @Delete
    fun delete(item: MediaItem)

    // Returns every MediaItem sorted
    // LiveData updates when table changes
    @Query("SELECT * FROM media_table ORDER BY title")
    fun getAllMedia(): LiveData<List<MediaItem>>

    // Returns items with requested media type
    @Query("SELECT * FROM media_table WHERE type == :type ORDER BY title")
    fun getMediaByType(type: String): LiveData<List<MediaItem>>

    @Query("DELETE FROM media_table")
    fun deleteAll()

    // Saves a user's rating
    // Replaces the old rating if the user already rated this media
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRating(rating: MediaRating)

    // Calculates the average rating for a media item
    // Returns null when there are no ratings
    @Query("SELECT AVG(rating) FROM rating_table WHERE mediaTitle = :mediaTitle")
    fun getAverageRating(mediaTitle: String): LiveData<Double?>

    // Gets one user's rating for a media item
    @Query("""SELECT rating FROM rating_table WHERE mediaTitle = :mediaTitle AND username = :username
""")
    fun getUserRating(
        mediaTitle: String,
        username: String
    ): LiveData<Int?>
}