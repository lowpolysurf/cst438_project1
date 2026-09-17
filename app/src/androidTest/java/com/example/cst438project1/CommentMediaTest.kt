package com.example.cst438project1

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.app.Application
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cst438project1.database.MediaRepository
import org.junit.Assert.assertTrue


@RunWith(AndroidJUnit4::class)
class CommentMediaTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun postCommentTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
        val mediaRepository = MediaRepository.getRepository(context)
        val testMedia = SimklMedia(

            title = "Test Movie ${System.currentTimeMillis()}",
            year = 2024,
            ids = null,
            poster = null,
            overview = "A test movie"
        )
        composeTestRule.setContent {
            MediaInfoDialog(
                media = testMedia,
                username = "testUser",
                mediaRepository = mediaRepository,
                onDismiss = { }
            )
        }
        composeTestRule.onNodeWithText("Add a comment").performTextInput("Great movie!")
        composeTestRule.onNodeWithText("Post Comment").performClick()


        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Great movie!").fetchSemanticsNodes().isNotEmpty()
        }

        assertTrue(composeTestRule.onAllNodesWithText("Great movie!").fetchSemanticsNodes().isNotEmpty())
        assertTrue(composeTestRule.onAllNodesWithText("testUser").fetchSemanticsNodes().isNotEmpty())

    }
}