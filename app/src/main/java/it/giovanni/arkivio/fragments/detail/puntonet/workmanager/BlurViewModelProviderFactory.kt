package it.giovanni.arkivio.fragments.detail.puntonet.workmanager

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BlurViewModelProviderFactory constructor(private val application: Application) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // ViewModel 1
            modelClass.isAssignableFrom(BlurViewModel::class.java) -> {
                BlurViewModel(application) as T
            }
            /*
            Eventual ViewModel 2
            modelClass.isAssignableFrom(Blur2ViewModel::class.java) -> {
                Blur2ViewModel() as T
            }
            */
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: "  + modelClass.name)
            }
        }
    }
}