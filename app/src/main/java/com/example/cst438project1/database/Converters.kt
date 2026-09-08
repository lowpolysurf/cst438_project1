package com.example.cst438project1.database

import androidx.room.TypeConverter
import com.example.cst438project1.database.entities.MediaType

class Converters {


    // Converts enum into its name: MediaType.Movie = "MOVIE"
    @TypeConverter
    fun fromMediaType(type: MediaType): String = type.name

    // Converts the string into enum: "MOVIE" = MediaType.Movie
    @TypeConverter
    fun toMediaType(value: String): MediaType = MediaType.valueOf(value)
}