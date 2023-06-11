package it.giovanni.arkivio.fragments.detail.puntonet.room.repository

import it.giovanni.arkivio.fragments.detail.puntonet.room.dao.UserCoroutinesDao
import it.giovanni.arkivio.fragments.detail.puntonet.room.entity.User

/**
 * La classe RoomCoroutinesRepository funge da repository che fornisce un livello di astrazione tra
 * ViewModel e il data source (database Room). Incapsula le operazioni del database relative alla
 * entity User ed espone le funzioni di sospensione che possono essere chiamate dalle coroutine.
 * Utilizzando le coroutine, le operazioni del database possono essere eseguite in modo asincrono
 * senza bloccare il thread principale.
 */
class RoomCoroutinesRepository(private val userDao: UserCoroutinesDao) {

    suspend fun getUsers(): List<User> {
        return userDao.getUsers()
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
}