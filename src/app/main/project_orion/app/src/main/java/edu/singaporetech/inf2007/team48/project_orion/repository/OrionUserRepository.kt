package edu.singaporetech.inf2007.team48.project_orion.repository

import androidx.annotation.WorkerThread
import edu.singaporetech.inf2007.team48.project_orion.dao.OrionUserDao
import edu.singaporetech.inf2007.team48.project_orion.models.OrionUser
import kotlinx.coroutines.flow.Flow

class OrionUserRepository(
    private val orionUserDao: OrionUserDao
) {
    // Section 5: OrionUserDao
    @WorkerThread
    suspend fun insertUser(orionUser: OrionUser) {
        orionUserDao.insertUser(orionUser)
    }

    @WorkerThread
    fun getAllUsers(): Flow<List<OrionUser>> {
        return orionUserDao.getAllUsers()
    }

    @WorkerThread
    fun getUserById(userId: Int): Flow<OrionUser> {
        return orionUserDao.getUserById(userId)
    }

    @WorkerThread
    suspend fun updateUser(newUser: OrionUser) {
        orionUserDao.updateUser(newUser.userId, newUser.userName)
    }

    @WorkerThread
    suspend fun deleteUser(userId: Int) {
        orionUserDao.deleteUser(userId)
    }
}