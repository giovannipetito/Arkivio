package it.giovanni.arkivio.fragments.detail.drag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.fragments.detail.drag.DragAdapter.Companion.EDIT_IDENTIFIER
import it.giovanni.arkivio.model.favorite.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Personal

class DragViewModel : ViewModel() {

    private val _personalFavorites = MutableLiveData<List<Personal>>()
    val personalFavorites: LiveData<List<Personal>> get() = _personalFavorites

    private val _availableFavorites = MutableLiveData<List<Personal>>()
    val availableFavorites: LiveData<List<Personal>> get() = _availableFavorites

    init {
        val responsePersonalFavorites: MutableList<Personal> = FavoriteUtils.getPersonalFavorites()
        val uiPersonalFavorites: MutableList<Personal> =
            if (responsePersonalFavorites.size > 7)
                responsePersonalFavorites.take(7).toMutableList()
            else
                responsePersonalFavorites

        val editItem = Personal()
        editItem.title = "Edit"
        editItem.identifier = EDIT_IDENTIFIER

        uiPersonalFavorites.add(editItem)

        val responseAvailableFavorites: MutableList<Personal> = FavoriteUtils.convertAvailableToPersonal(FavoriteUtils.getAvailableFavorites())

        val uiAvailableFavorites: MutableList<Personal> = responseAvailableFavorites.filter { availableItem ->
            uiPersonalFavorites.none { personalItem -> personalItem.identifier == availableItem.identifier }
        }.toMutableList()

        _personalFavorites.value = uiPersonalFavorites

        _availableFavorites.value = uiAvailableFavorites
    }
}