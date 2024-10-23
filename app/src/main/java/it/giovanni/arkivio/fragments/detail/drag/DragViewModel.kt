package it.giovanni.arkivio.fragments.detail.drag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.model.favorite.Available
import it.giovanni.arkivio.model.favorite.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Personal

class DragViewModel : ViewModel() {

    private val _personalFavorites = MutableLiveData<List<Personal>>()
    val personalFavorites: LiveData<List<Personal>> get() = _personalFavorites

    private val _availableFavorites = MutableLiveData<List<Available>>()
    val availableFavorites: LiveData<List<Available>> get() = _availableFavorites

    init {
        _personalFavorites.value = FavoriteUtils.getPersonalFavorites()

        _availableFavorites.value = FavoriteUtils.getAvailableFavorites()
    }
}