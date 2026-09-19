package com.example.cst438project1.database.entities

import androidx.room.Entity

// Stores one user's saved watchlist
// Follows the same pattern as MediaRating
@Entity(
    tableName = "watchlist_table",
    primaryKeys = ["mediaTitle", "username"]
)
data class WatchlistItem(
    val mediaTitle: String,
    val username: String,
    val year: Int?,
    val imageUrl: String?,
    val externalId: String?
)