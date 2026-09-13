package com.example.cst438project1

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

data class SimklIds(
    @SerializedName(value = "simkl", alternate = ["simkl_id"])
    val simkl: Int?,
    val slug: String?,
    val imdb: String?,
    val tmdb: String?
)

data class SimklMedia(
    val title: String,
    val year: Int?,
    val ids: SimklIds?,
    val poster: String?,
    val overview: String?
)

interface SimklApiService {
    @Headers("User-Agent: cst438project1/1.0")
    @GET("search/{type}")
    suspend fun searchMedia(
        @Path("type") type: String,
        @Query("q") query: String,
        @Query("client_id") clientId: String,
        @Query("app-name") appName: String = SimklClient.APP_NAME,
        @Query("app-version") appVersion: String = SimklClient.APP_VERSION,
        // Requests overview text for the media-information popup.
        @Query("extended") extended: String = "full"
    ): List<SimklMedia>
}

object SimklClient {
    private const val BASE_URL = "https://api.simkl.com/"
    const val CLIENT_ID = "f0ed39a21b7a9c850615b0b9180fb4ae8fdd92c48a931c66e859a55ec16e25a2"
    const val APP_NAME = "cst438project1"
    const val APP_VERSION = "1.0"

    val api: SimklApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SimklApiService::class.java)
    }
}

// Lucky Search: the API has no built-in "random" endpoint,
// so this picks a random term/type and queries the real API, then picks a random result.

private val LUCKY_SEARCH_TERMS = listOf(
    "batman", "friends", "naruto", "inception", "the office",
    "attack on titan", "star wars", "breaking bad", "spirited away", "the matrix"
)

private val LUCKY_MEDIA_TYPES = listOf("movie", "tv", "anime")

val PLACEHOLDER_SUGGESTION = SimklMedia(
    title = "Mystery Movie Night (placeholder for now)",
    year = null,
    ids = null,
    poster = null,
    overview = null
)

object LuckySearch {
    suspend fun getRandomSuggestion(): SimklMedia {
        return try {
            val randomType = LUCKY_MEDIA_TYPES.random()
            val randomTerm = LUCKY_SEARCH_TERMS.random()

            val results = SimklClient.api.searchMedia(
                type = randomType,
                query = randomTerm,
                clientId = SimklClient.CLIENT_ID
            )

            results.randomOrNull() ?: PLACEHOLDER_SUGGESTION
        } catch (e: Exception) {
            PLACEHOLDER_SUGGESTION
        }
    }
}
