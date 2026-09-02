package com.example.cst438project1.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cst438project1.database.entities.User

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM user_table ORDER BY username")
    fun getAllUsers(): LiveData<List<User>>

    @Query("DELETE FROM user_table")
    fun deleteAll()

    @Query("SELECT * FROM user_table WHERE username == :username")
    fun getUserByUsername(username: String): LiveData<User>
}