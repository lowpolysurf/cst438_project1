package com.example.cst438project1.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cst438project1.database.entities.MediaItem

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
}