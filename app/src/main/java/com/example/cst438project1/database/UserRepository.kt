package com.example.cst438project1.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.cst438project1.database.entities.User

class UserRepository private constructor(application: Application) {

    // Gets the DAO used to perform database operations
    private val userDAO: UserDAO = UserDatabase.getDatabase(application).userDAO()

    // Passes a username lookup request to DAO
    fun getUserByUsername(username: String): LiveData<User> = userDAO.getUserByUsername(username)

    // Adds user to the database
    fun insertUser(user: User) {
        UserDatabase.databaseWriteExecutor.execute {
            userDAO.insert(user)
        }
    }

    companion object {
        // Stores shared repository instance
        @Volatile
        private var repository: UserRepository? = null

        // Returns existing repository or creates one if needed
        fun getRepository(application: Application): UserRepository {
            return repository ?: synchronized(this) {
                // Only creates if repo doesn't exist
                val instance = UserRepository(application)
                repository = instance
                instance
            }
        }
    }
}