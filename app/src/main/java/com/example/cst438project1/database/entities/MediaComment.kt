package com.example.cst438project1.database.entities
// imports will be used soon

import androidx.room.Entity
import androidx.room.PrimaryKey
// the table will store one comment users leave on a movie/tvshow
@Entity(tableName = "comment_table")
data class MediaComment
    (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val mediaTitle: String,
    val username: String,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)