package com.example.cst438project1.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Create a database table named "user_table"
@Entity(tableName = "user_table")
data class User(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Each user has unique ID

    // Chosen username upon registering
    val username: String,
    // User's password
    val password: String
)