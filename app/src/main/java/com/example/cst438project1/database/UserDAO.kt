package com.example.cst438project1.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cst438project1.database.entities.User

@Dao
interface UserDAO {
    // Replaces an existing row if there is a conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    // Adds one or more users to database
    fun insert(vararg user: User)

    // Delete user from database
    @Delete
    fun delete(user: User)

    // Retrieves every user
    @Query("SELECT * FROM user_table ORDER BY username")
    fun getAllUsers(): LiveData<List<User>> // LiveData updates observers when data is changed

    // Delete EVERY row from "user_table"
    @Query("DELETE FROM user_table")
    fun deleteAll()

    // Finds username matches
    @Query("SELECT * FROM user_table WHERE username == :username")
    fun getUserByUsername(username: String): LiveData<User>
}