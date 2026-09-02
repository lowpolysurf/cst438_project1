package com.example.cst438project1.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.cst438project1.database.entities.User

class UserRepository private constructor(application: Application) {

    private val userDAO: UserDAO = UserDatabase.getDatabase(application).userDAO()

    fun getUserByUsername(username: String): LiveData<User> = userDAO.getUserByUsername(username)

    fun insertUser(user: User) {
        UserDatabase.databaseWriteExecutor.execute {
            userDAO.insert(user)
        }
    }

    companion object {
        @Volatile
        private var repository: UserRepository? = null

        fun getRepository(application: Application): UserRepository {
            return repository ?: synchronized(this) {
                val instance = UserRepository(application)
                repository = instance
                instance
            }
        }
    }
}