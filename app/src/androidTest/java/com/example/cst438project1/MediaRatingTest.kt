package com.example.cst438project1.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cst438project1.database.entities.MediaRating
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Runs this test on an Android device or emulator
@RunWith(AndroidJUnit4::class)
class MediaRatingTest {

    // Allows LiveData to work during the test
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: MediaDatabase
    private lateinit var mediaDAO: MediaDAO

    // Creates a temporary database before the test
    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MediaDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        mediaDAO = database.mediaDAO()
    }

    // Closes the database after the test
    @After
    fun closeDatabase() {
        database.close()
    }

    // Tests that ratings are averaged correctly
    @Test
    fun calculateAverageRating() {
        // Adds the first user's rating
        mediaDAO.saveRating(
            MediaRating(
                mediaTitle = "Inception",
                username = "user1",
                rating = 5
            )
        )

        // Adds the second user's rating
        mediaDAO.saveRating(
            MediaRating(
                mediaTitle = "Inception",
                username = "user2",
                rating = 3
            )
        )

        // Gets the average rating
        val averageRating = mediaDAO.getAverageRating("Inception")
        var result: Double? = null

        val observer = Observer<Double?> {
            result = it
        }

        averageRating.observeForever(observer)

        // The average of 5 and 3 should be 4
        assertEquals(4.0, result!!, 0.0)

        // Stops observing the rating
        averageRating.removeObserver(observer)
    }
}