package it.giovanni.arkivio.fragments.detail.drag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.fragments.detail.drag.DragAdapter.Companion.EDIT_IDENTIFIER
import it.giovanni.arkivio.model.favorite.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Favorite

class DragViewModel : ViewModel() {

    private val _favorites = MutableLiveData<List<Favorite>>()
    val favorites: LiveData<List<Favorite>> get() = _favorites

    private val _availables = MutableLiveData<List<Favorite>>()
    val availables: LiveData<List<Favorite>> get() = _availables

    init {
        val responseFavorites: MutableList<Favorite> = FavoriteUtils.getFavorites()
        val editableFavorites: MutableList<Favorite> =
            if (responseFavorites.size > 7)
                responseFavorites.take(7).toMutableList()
            else
                responseFavorites

        val editItem = Favorite()
        editItem.title = "Edit"
        editItem.identifier = EDIT_IDENTIFIER

        editableFavorites.add(editItem)

        val responseAvailables: MutableList<Favorite> = FavoriteUtils.convertAvailableToFavorite(FavoriteUtils.getAvailables())

        val filteredAvailables: MutableList<Favorite> = responseAvailables.filter { available ->
            editableFavorites.none { favorite -> favorite.identifier == available.identifier }
        }.toMutableList()

        _favorites.value = editableFavorites

        _availables.value = filteredAvailables
    }
}