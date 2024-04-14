package edu.singaporetech.inf2007.team48.project_orion.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.singaporetech.inf2007.team48.project_orion.models.OrionUser
import kotlinx.coroutines.flow.Flow


// Section 5: users
@Dao
interface OrionUserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(orionUser: OrionUser)

    @Query("SELECT * FROM users ORDER BY user_id ASC")
    fun getAllUsers(): Flow<List<OrionUser>>

    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getUserById(userId: Int): Flow<OrionUser>

    @Query("UPDATE users SET user_name = :userName WHERE user_id = :userId")
    suspend fun updateUser(userId: Int, userName: String)

    @Query("DELETE FROM users WHERE user_id = :userId")
    suspend fun deleteUser(userId: Int)
}