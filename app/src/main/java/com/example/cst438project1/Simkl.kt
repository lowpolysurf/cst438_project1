package com.example.cst438project1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class SimklIds(
    val simkl: Int?,
    val slug: String?,
    val imdb: String?,
    val tmdb: String?
)

data class SimklMedia(
    val title: String,
    val year: Int?,
    val ids: SimklIds?,
    val poster: String?
)

interface SimklApiService{
    @GET("search/{type}")
    suspend fun searchMedia(
        @Path("type") type: String,
        @Query("q") query: String,
        @Query("client_id") clientId: String
    ): List<SimklMedia>
}

object SimklClient{
    private const val BASE_URL = "https://api.simkl.com/"
    const val CLIENT_ID = "f0ed39a21b7a9c850615b0b9180fb4ae8fdd92c48a931c66e859a55ec16e25a2"

    val api: SimklApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SimklApiService::class.java)
    }
}
// after some research the api doesn't have a  random built in.
// i need to code pulling from api and just picking a random one from the results of the api

private val LUCKY_SEARCH_TERMS = listOf(
    "batman", "friends", "naruto", "inception", "the office",
    "attack on titan", "star wars", "breaking bad", "spirited away", "the matrix"
)
