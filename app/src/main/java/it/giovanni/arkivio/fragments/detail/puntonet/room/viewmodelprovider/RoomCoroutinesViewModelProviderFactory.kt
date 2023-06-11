package it.giovanni.arkivio.fragments.detail.puntonet.room.viewmodelprovider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.fragments.detail.puntonet.room.dao.UserCoroutinesDao
import it.giovanni.arkivio.fragments.detail.puntonet.room.viewmodel.RoomCoroutinesViewModel

class RoomCoroutinesViewModelProviderFactory {}

/*
class RoomCoroutinesViewModelProviderFactory constructor(private val userDao: UserCoroutinesDao) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RoomCoroutinesViewModel::class.java) -> {
                RoomCoroutinesViewModel(userDao) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}
*/