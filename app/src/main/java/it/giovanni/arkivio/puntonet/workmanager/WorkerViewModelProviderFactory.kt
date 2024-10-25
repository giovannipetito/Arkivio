package it.giovanni.arkivio.puntonet.workmanager

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.puntonet.cleanarchitecture.presentation.viewmodel.UsersWorkerViewModel

class WorkerViewModelProviderFactory constructor(private val application: Application) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BlurWorkerViewModel::class.java) -> {
                BlurWorkerViewModel(application) as T
            }
            modelClass.isAssignableFrom(SimpleWorkerViewModel::class.java) -> {
                SimpleWorkerViewModel(application) as T
            }
            modelClass.isAssignableFrom(UsersWorkerViewModel::class.java) -> {
                UsersWorkerViewModel(application) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: "  + modelClass.name)
            }
        }
    }
}