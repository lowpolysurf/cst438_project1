package com.example.cst438project1

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cst438project1.database.MediaDAO
import com.example.cst438project1.database.MediaDatabase
import com.example.cst438project1.database.entities.MediaItem
import com.example.cst438project1.database.entities.MediaType
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDAOTest {

    private lateinit var db: MediaDatabase
    private lateinit var mediaDAO: MediaDAO

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, MediaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        mediaDAO = db.mediaDAO()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertMedia_andGetAllMedia_returnsInsertedItem() = runTest {
        val item = MediaItem(
            type = MediaType.MOVIE,
            title = "Interstellar",
            year = 2014,
            imageUrl = "https://example.com/poster.jpg",
            externalId = "12345"
        )
        mediaDAO.insert(item)

        val results = getLiveDataValue(mediaDAO.getAllMedia())

        assertEquals(1, results?.size)
        assertEquals("Interstellar", results?.first()?.title)
    }

    @Test
    fun getMediaByType_returnsOnlyMatchingType() = runTest {
        mediaDAO.insert(MediaItem(type = MediaType.MOVIE, title = "A Movie", year = 2020, imageUrl = null, externalId = "1"))
        mediaDAO.insert(MediaItem(type = MediaType.TV, title = "A Show", year = 2021, imageUrl = null, externalId = "2"))

        val movies = getLiveDataValue(mediaDAO.getMediaByType(MediaType.MOVIE.name))

        assertEquals(1, movies?.size)
        assertEquals("A Movie", movies?.first()?.title)
    }

    @Test
    fun deleteAll_clearsTable() = runTest {
        mediaDAO.insert(MediaItem(type = MediaType.ANIME, title = "Some Anime", year = 2019, imageUrl = null, externalId = "3"))
        mediaDAO.deleteAll()

        val results = getLiveDataValue(mediaDAO.getAllMedia())
        assertTrue(results.isNullOrEmpty())
    }
}