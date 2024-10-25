package it.giovanni.arkivio.puntonet.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.giovanni.arkivio.puntonet.retrofitgetpost.User

@Dao
interface UsersWorkerDao {

    @Query("SELECT * FROM users_worker_table ORDER BY id ASC")
    suspend fun getUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(users: List<User>)
}