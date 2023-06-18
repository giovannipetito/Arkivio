package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.datasource.remote.RickMortyDataSource
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.presentation.viewmodel.RickMortyViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelProviderFactory @Inject constructor(private val rickMortyDataSource: RickMortyDataSource) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RickMortyViewModel::class.java) -> {
                RickMortyViewModel(rickMortyDataSource) as T
            }
            /*
            modelClass.isAssignableFrom(RickMorty2ViewModel::class.java) -> {
                RickMorty2ViewModel() as T
            }
            */
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}