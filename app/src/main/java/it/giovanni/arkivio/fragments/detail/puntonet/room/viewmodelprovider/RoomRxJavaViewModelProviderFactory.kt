package it.giovanni.arkivio.fragments.detail.puntonet.room.viewmodelprovider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.fragments.detail.puntonet.room.dao.UserRxJavaDao
import it.giovanni.arkivio.fragments.detail.puntonet.room.viewmodel.RoomRxJavaViewModel
import javax.inject.Inject
import javax.inject.Singleton

class RoomRxJavaViewModelProviderFactory {}

// @Singleton
/*
class RoomRxJavaViewModelProviderFactory /* @Inject */ constructor(private val userDao: UserRxJavaDao) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RoomRxJavaViewModel::class.java) -> {
                RoomRxJavaViewModel(userDao) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}
*/