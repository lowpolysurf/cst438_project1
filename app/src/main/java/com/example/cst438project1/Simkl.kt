package com.example.cst438project1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// '?' means the variable can be NULL
// These are the IDs from the API
data class SimklIds(
    val simkl: Int?,
    val slug: String?,
    val imdb: String?,
    val tmdb: String?
)

// This is the media type (Movie or TV show) from API
data class SimklMedia(
    val title: String,
    val year: Int?,
    val ids: SimklIds?,
    val poster: String?
)

// Defines the API requests that the application can make
interface SimklApiService{
    // Sends request to BASE_URL/search/{type}
    @GET("search/{type}")
    suspend fun searchMedia(
        // Path replaces the {type} with "movie", "tv" or "anime"
        @Path("type") type: String,

        // Adds the search text as the "q"
        @Query("q") query: String,

        // Adds the API client ID as the "client_id"
        @Query("client_id") clientId: String
    ): List<SimklMedia>
}

// Creates and stores the API client
object SimklClient{
    private const val BASE_URL = "https://api.simkl.com/"
    const val CLIENT_ID = "f0ed39a21b7a9c850615b0b9180fb4ae8fdd92c48a931c66e859a55ec16e25a2"

    // Creates the API service when it is used for the first time
    // "by lazy" prevents Retrofit from being initialized unnecessarily
    // (thanks for the fix Claude!)
    val api: SimklApiService by lazy{
        Retrofit.Builder()
            // Tells Retrofit where the API is
            .baseUrl(BASE_URL)
            // JSON responses are turned into data classes (SimklMedia/Ids)
            .addConverterFactory(GsonConverterFactory.create())
            // Creates the object
            .build()
            // Generates the implementation of the API
            .create(SimklApiService::class.java)
    }
}