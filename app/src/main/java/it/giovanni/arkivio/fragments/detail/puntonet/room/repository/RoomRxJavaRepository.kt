package it.giovanni.arkivio.fragments.detail.puntonet.room.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import it.giovanni.arkivio.fragments.detail.puntonet.room.dao.UserRxJavaDao
import it.giovanni.arkivio.fragments.detail.puntonet.room.entity.User

/**
 * La classe RoomRxJavaRepository funge da repository che fornisce un livello di astrazione tra
 * ViewModel e il data source (database Room). Incapsula le operazioni del database relative alla
 * entity User ed espone le funzioni che possono essere utilizzate per eseguire queste operazioni
 * utilizzando il paradigma di programmazione reattiva di RxJava. L'uso di Flowable consente di
 * osservare i cambiamenti nel database e reagire di conseguenza.
 */
class RoomRxJavaRepository(private val userDao: UserRxJavaDao) {

    fun getUsers(): Flowable<List<User>> {
        return userDao.getUsers()
    }

    fun insertUser(user: User): Completable {
        return userDao.insertUser(user)
    }

    fun updateUser(user: User): Completable {
        return userDao.updateUser(user)
    }

    fun deleteUser(user: User): Completable {
        return userDao.deleteUser(user)
    }
    fun deleteUsers(): Completable {
        return userDao.deleteUsers()
    }
}