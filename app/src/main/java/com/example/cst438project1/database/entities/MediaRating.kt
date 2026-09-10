package com.example.cst438project1.database.entities

import androidx.room.Entity

// Stores one user's rating for one media item
@Entity(
    tableName = "rating_table",

    // Prevents the same user from rating the same media more than once
    primaryKeys = ["mediaTitle", "username"]
)
data class MediaRating(

    // Title of the media being rated
    val mediaTitle: String,

    // Username of the person giving the rating
    val username: String,

    // Rating must be between 1 and 5
    val rating: Int
)