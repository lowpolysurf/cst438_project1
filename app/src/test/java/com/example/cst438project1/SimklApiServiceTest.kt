package com.example.cst438project1

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SimklApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var api: SimklApiService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SimklApiService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun searchMedia_parsesResponseCorrectly() = runBlocking {
        val fakeJson = """
            [
              {
                "title": "Interstellar",
                "year": 2014,
                "ids": { "simkl": 12345, "slug": "interstellar", "imdb": "tt0816692", "tmdb": "157336" },
                "poster": "abc123"
              }
            ]
        """.trimIndent()

        server.enqueue(MockResponse().setBody(fakeJson).setResponseCode(200))

        val results = api.searchMedia(type = "movie", query = "Interstellar", clientId = "fake_id")

        Assert.assertEquals(1, results.size)
        Assert.assertEquals("Interstellar", results.first().title)
        Assert.assertEquals(2014, results.first().year)
        Assert.assertEquals(12345, results.first().ids?.simkl)
    }

    @Test
    fun searchMedia_emptyResponse_returnsEmptyList() = runBlocking {
        server.enqueue(MockResponse().setBody("[]").setResponseCode(200))

        val results =
            api.searchMedia(type = "movie", query = "NonexistentMovie12345", clientId = "fake_id")

        Assert.assertTrue(results.isEmpty())
    }
}