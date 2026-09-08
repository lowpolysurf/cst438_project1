package com.example.cst438project1

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cst438project1.database.UserDAO
import com.example.cst438project1.database.UserDatabase
import com.example.cst438project1.database.entities.User
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class UserDAOTest {

    private lateinit var db: UserDatabase
    private lateinit var userDAO: UserDAO

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDAO = db.userDAO()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertUser_andRetrieveByUsername_returnsCorrectUser() = runTest {
        val user = User(username = "testUser", password = "testPass")
        userDAO.insert(user)

        val result = getLiveDataValue(userDAO.getUserByUsername("testUser"))

        assertNotNull(result)
        assertEquals("testUser", result?.username)
        assertEquals("testPass", result?.password)
    }

    @Test
    fun getUserByUsername_nonexistentUser_returnsNull() = runTest {
        val result = getLiveDataValue(userDAO.getUserByUsername("doesNotExist"))
        assertNull(result)
    }

    @Test
    fun insertUser_withDuplicateUsername_replacesOldEntry() = runTest {
        userDAO.insert(User(username = "dupUser", password = "firstPass"))
        userDAO.insert(User(username = "dupUser", password = "secondPass"))

        val result = getLiveDataValue(userDAO.getUserByUsername("dupUser"))
        assertEquals("secondPass", result?.password)
    }

    @Test
    fun deleteAll_removesEveryUser() = runTest {
        userDAO.insert(User(username = "userA", password = "pass"))
        userDAO.insert(User(username = "userB", password = "pass"))

        userDAO.deleteAll()

        val result = getLiveDataValue(userDAO.getUserByUsername("userA"))
        assertNull(result)
    }
}