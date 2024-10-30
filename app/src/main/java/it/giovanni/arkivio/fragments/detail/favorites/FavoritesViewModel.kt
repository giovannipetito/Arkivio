package it.giovanni.arkivio.fragments.detail.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.fragments.detail.favorites.FavoritesAdapter.Companion.EDIT_IDENTIFIER
import it.giovanni.arkivio.utils.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Favorite
import java.util.Collections

class FavoritesViewModel : ViewModel() {

    private val _personals = MutableLiveData<List<Favorite?>>()
    val personals: LiveData<List<Favorite?>> get() = _personals

    private val _availables = MutableLiveData<List<Favorite?>>()
    val availables: LiveData<List<Favorite?>> get() = _availables

    init {
        val responsePersonals: MutableList<Favorite> = FavoriteUtils.getPersonals()
        val editablePersonals: MutableList<Favorite> =
            if (responsePersonals.size > 7)
                responsePersonals.take(7).toMutableList()
            else
                responsePersonals

        val editItem = Favorite()
        editItem.title = "Edit"
        editItem.identifier = EDIT_IDENTIFIER

        editablePersonals.add(editItem)

        val responseAvailables: MutableList<Favorite> = FavoriteUtils.convertAvailableToFavorite(
            FavoriteUtils.getAvailables())

        val filteredAvailables: MutableList<Favorite?> = responseAvailables.filter { available ->
            editablePersonals.none { personal -> personal.identifier == available.identifier }
        }.toMutableList()

        _personals.value = editablePersonals

        val headerAvailables: MutableList<Favorite?> = FavoriteUtils.addAvailableHeaders(filteredAvailables)

        _availables.value = headerAvailables
    }

    fun onSet(isPersonal: Boolean, targetIndex: Int, sourceIndex: Int, targetFavorite: Favorite) {

        val tempPersonals: MutableList<Favorite?> = _personals.value?.toMutableList()!!
        val tempAvailables: MutableList<Favorite?> = _availables.value?.toMutableList()!!

        if (isPersonal) {
            val tempAvailable = tempAvailables[sourceIndex]
            tempPersonals.let {
                it[targetIndex] = tempAvailable
            }

            tempAvailables.let {
                it[sourceIndex] = targetFavorite
            }
        } else {
            val tempPersonal = tempPersonals[targetIndex]
            tempPersonals.let {
                it[targetIndex] = targetFavorite
            }

            tempAvailables.let {
                it[sourceIndex] = tempPersonal
            }
        }

        _availables.value = tempAvailables.toList()
        _personals.value = tempPersonals.toList()
    }

    fun onAdd(isPersonal: Boolean, favorite: Favorite) {
        if (isPersonal) {
            _availables.value?.let { availables ->
                val tempPersonals: MutableList<Favorite?> = _personals.value?.toMutableList()!!
                val tempAvailables: MutableList<Favorite?> = availables.toMutableList()

                tempAvailables.add(favorite)
                _availables.value = tempAvailables.toList()
                tempPersonals.remove(favorite)
                tempPersonals.add(null)
                tempPersonals.sortWith(Comparator.nullsLast(null))
                _personals.value = tempPersonals.toList()
            }
        } else {
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()
                val tempAvailables: MutableList<Favorite?> = _availables.value?.toMutableList()!!

                // if (tempPersonals.filterNotNull().size < PERSONALS_MAX_SIZE) {
                for (i in tempPersonals.indices) {
                    if (tempPersonals[i] == null) {
                        tempPersonals[i] = favorite
                        break
                    }
                }
                _personals.value = tempPersonals.toList()
                tempAvailables.remove(favorite)
                _availables.value = tempAvailables.toList()
                // }
            }
        }
    }

    fun onRemove(isPersonal: Boolean, favorite: Favorite) {
        if (isPersonal) {
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()
                val tempAvailables: MutableList<Favorite?> = _availables.value?.toMutableList()!!
                for (i in tempPersonals.indices) {
                    if (tempPersonals[i] == favorite) {
                        tempPersonals[i] = null
                        break
                    }
                }
                _personals.value = tempPersonals.toList()
                tempAvailables.add(favorite)
                _availables.value = tempAvailables.toList()
            }
        } else {
            _availables.value?.let { availables ->
                val tempPersonals: MutableList<Favorite?> = _personals.value?.toMutableList()!!
                val tempAvailables: MutableList<Favorite?> = availables.toMutableList()
                tempAvailables.remove(favorite)
                _availables.value = tempAvailables.toList()
                for (i in tempPersonals.indices) {
                    if (tempPersonals[i] == null) {
                        tempPersonals[i] = favorite
                        break
                    }
                }
                tempPersonals.sortWith(Comparator.nullsLast(null))
                _personals.value = tempPersonals.toList()
            }
        }
    }

    fun onSwap(isPersonal: Boolean, from: Int, to: Int) {
        if (isPersonal) {
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()
                Collections.swap(tempPersonals, from, to)
                _personals.value = tempPersonals.toList()
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