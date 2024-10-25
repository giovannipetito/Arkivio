package it.giovanni.arkivio.fragments.detail.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.fragments.detail.favorites.FavoritesAdapter.Companion.EDIT_IDENTIFIER
import it.giovanni.arkivio.utils.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Favorite
import java.util.Collections

class FavoritesViewModel : ViewModel() {

    private val _favorites = MutableLiveData<List<Favorite?>>()
    val favorites: LiveData<List<Favorite?>> get() = _favorites

    private val _availables = MutableLiveData<List<Favorite?>>()
    val availables: LiveData<List<Favorite?>> get() = _availables

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

        val responseAvailables: MutableList<Favorite> = FavoriteUtils.convertAvailableToFavorite(
            FavoriteUtils.getAvailables())

        val filteredAvailables: MutableList<Favorite> = responseAvailables.filter { available ->
            editableFavorites.none { favorite -> favorite.identifier == available.identifier }
        }.toMutableList()

        _favorites.value = editableFavorites

        _availables.value = filteredAvailables
    }

    fun onSet(isFavorite: Boolean, targetIndex: Int, sourceIndex: Int, targetFavorite: Favorite) {

        val tempFavorites: MutableList<Favorite?> = _favorites.value?.toMutableList()!!
        val tempAvailables: MutableList<Favorite?> = _availables.value?.toMutableList()!!

        if (isFavorite) {
            val favorite = tempAvailables[sourceIndex]
            tempFavorites.let {
                it[targetIndex] = favorite
            }

            tempAvailables.let {
                it[sourceIndex] = targetFavorite
            }
        } else {
            val favorite = tempFavorites[targetIndex]
            tempFavorites.let {
                it[targetIndex] = targetFavorite
            }

            tempAvailables.let {
                it[sourceIndex] = favorite
            }
        }

        _availables.value = tempAvailables.toList()
        _favorites.value = tempFavorites.toList()
    }

    fun onAdd(isFavorite: Boolean, favorite: Favorite) {
        if (isFavorite) {
            _availables.value?.let { availables ->
                val tempFavorites: MutableList<Favorite?> = _favorites.value?.toMutableList()!!
                val tempAvailables: MutableList<Favorite?> = availables.toMutableList()

                tempAvailables.add(favorite)
                _availables.value = tempAvailables.toList()
                tempFavorites.remove(favorite)
                tempFavorites.add(null)
                tempFavorites.sortWith(Comparator.nullsLast(null))
                _favorites.value = tempFavorites.toList()
            }
        } else {
            _favorites.value?.let { favorites ->
                val tempFavorites: MutableList<Favorite?> = favorites.toMutableList()
                val tempAvailables: MutableList<Favorite?> = _availables.value?.toMutableList()!!

                // if (tempFavorites.filterNotNull().size < TOP_DATA_MAX_SIZE) {
                for (i in tempFavorites.indices) {
                    if (tempFavorites[i] == null) {
                        tempFavorites[i] = favorite
                        break
                    }
                }
                _favorites.value = tempFavorites.toList()
                tempAvailables.remove(favorite)
                _availables.value = tempAvailables.toList()
                // }
            }
        }
    }

    fun onRemove(isFavorite: Boolean, favorite: Favorite) {
        if (isFavorite) {
            _favorites.value?.let { favorites ->
                val tempFavorites: MutableList<Favorite?> = favorites.toMutableList()
                val tempAvailables: MutableList<Favorite?> = _availables.value?.toMutableList()!!
                for (i in tempFavorites.indices) {
                    if (tempFavorites[i] == favorite) {
                        tempFavorites[i] = null
                        break
                    }
                }
                _favorites.value = tempFavorites.toList()
                tempAvailables.add(favorite)
                _availables.value = tempAvailables.toList()
            }
        } else {
            _availables.value?.let { availables ->
                val tempFavorites: MutableList<Favorite?> = _favorites.value?.toMutableList()!!
                val tempAvailables: MutableList<Favorite?> = availables.toMutableList()
                tempAvailables.remove(favorite)
                _availables.value = tempAvailables.toList()
                for (i in tempFavorites.indices) {
                    if (tempFavorites[i] == null) {
                        tempFavorites[i] = favorite
                        break
                    }
                }
                tempFavorites.sortWith(Comparator.nullsLast(null))
                _favorites.value = tempFavorites.toList()
            }
        }
    }

    fun onSwap(isFavorite: Boolean, from: Int, to: Int) {
        if (isFavorite) {
            _favorites.value?.let { favorites ->
                val tempFavorites: MutableList<Favorite?> = favorites.toMutableList()
                Collections.swap(tempFavorites, from, to)
                _favorites.value = tempFavorites.toList()
            }
        }
        /*
        Available items must not be swappable.
        else {
            _availables.value?.let { availables ->
                val tempAvailables: MutableList<Favorite?> = availables.toMutableList()
                Collections.swap(tempAvailables, from, to)
                _availables.value = tempAvailables.toList()
            }
        }
        */
    }
}