package it.giovanni.arkivio.fragments.detail.favorites

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.fragments.detail.favorites.FavoritesAdapter.Companion.EDIT_IDENTIFIER
import it.giovanni.arkivio.utils.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Favorite
import java.util.Collections

class FavoritesViewModel : ViewModel() {

    private val _personals = MutableLiveData<List<Favorite?>>()
    val personals: LiveData<List<Favorite?>> get() = _personals

    private val _availables = MutableLiveData<List<Favorite?>>()
    val availables: LiveData<List<Favorite?>> get() = _availables

    private var responseAvailables: MutableList<Favorite?>

    init {
        val responsePersonals: MutableList<Favorite?> = FavoriteUtils.getPersonals()
        // val editablePersonals: MutableList<Favorite?> = if (responsePersonals.size > 7) responsePersonals.take(7).toMutableList() else responsePersonals
        val editablePersonals: MutableList<Favorite?> = responsePersonals.filterNotNull().take(7).toMutableList()

        val editItem = Favorite()
        editItem.title = "Edit"
        editItem.identifier = EDIT_IDENTIFIER

        editablePersonals.add(editItem)

        responseAvailables = FavoriteUtils.convertAvailableToFavorite(FavoriteUtils.getAvailables())

        val filteredAvailables: MutableList<Favorite?> = responseAvailables.filter { available ->
            editablePersonals.none { personal -> personal?.identifier == available?.identifier }
        }.toMutableList()

        _personals.value = editablePersonals

        val headerAvailables: MutableList<Favorite?> = FavoriteUtils.addAvailableHeaders(filteredAvailables)

        _availables.value = headerAvailables
    }

    fun onSet(isPersonal: Boolean, targetIndex: Int, sourceIndex: Int, targetFavorite: Favorite) {
        if (isPersonal) {
            Log.i("[FAVORITES]", "onSet personal to available")

            _personals.value?.let { personals ->
                val updatedPersonals: MutableList<Favorite?> = personals.toMutableList()
                updatedPersonals.removeAt(sourceIndex)
                _personals.value = updatedPersonals

                val filteredAvailables: MutableList<Favorite?> = responseAvailables.filter { available ->
                    updatedPersonals.none { personal -> personal?.identifier == available?.identifier }
                }.toMutableList()

                val headerAvailables: MutableList<Favorite?> = FavoriteUtils.addAvailableHeaders(filteredAvailables)

                _availables.value = headerAvailables
            }
        } else {
            Log.i("[FAVORITES]", "onSet available to personal")

            _personals.value?.let { personals ->
                // Check if personals has space (i.e., less than 7 items)
                if (personals.size < 7) {
                    val updatedPersonals: MutableList<Favorite?> = personals.toMutableList()

                    // Insert targetFavorite at the specified targetIndex
                    updatedPersonals.add(targetIndex, targetFavorite)

                    // Remove the item from availables
                    responseAvailables.remove(targetFavorite)

                    // Update _personals to reflect the changes
                    _personals.value = updatedPersonals.take(7)  // Ensure limit of 7 items

                    // Update _availables with headers after removal
                    val filteredAvailables: MutableList<Favorite?> = responseAvailables.filter { available ->
                        updatedPersonals.none { personal -> personal?.identifier == available?.identifier }
                    }.toMutableList()

                    val headerAvailables: MutableList<Favorite?> = FavoriteUtils.addAvailableHeaders(filteredAvailables)
                    _availables.value = headerAvailables
                }
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

    fun removeEditItem(position: Int) {
        _personals.value?.let { personals ->
            val updatedPersonals: MutableList<Favorite?> = personals.toMutableList().apply {
                removeAt(position)
            }
            _personals.value = updatedPersonals
        }
    }
}