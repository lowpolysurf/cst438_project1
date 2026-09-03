package com.example.cst438project1.model

enum class MediaType  {BOOK, MOVIE, TV}

data class MediaObj
    (
            val id: Int,
            val title: String,
            val type: MediaType,
            var completed : Boolean = false


)