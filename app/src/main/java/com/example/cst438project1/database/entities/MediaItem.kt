package com.example.cst438project1.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_table")
data class MediaItem(
    // Unique ID for each record in database
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Type of media from enum
    val type: MediaType,
    val title: String,
    val year: Int?,
    val imageUrl: String?,
    val externalId: String?
)