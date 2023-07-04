package it.giovanni.arkivio.fragments.detail.puntonet.room.repository

import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.User
import it.giovanni.arkivio.fragments.detail.puntonet.room.dao.UsersWorkerDao

class UsersWorkerRepository(private val usersDao: UsersWorkerDao) {

    suspend fun getUsers(): List<User> {
        return usersDao.getUsers()
    }

    suspend fun insertUsers(users: List<User>) {
        usersDao.insertUsers(users)
    }
}