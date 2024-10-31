package it.giovanni.arkivio.fragments.detail.favorites

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

    fun onSet(isPersonal: Boolean, targetIndex: Int, sourceIndex: Int) {
        if (isPersonal) {
            // Drag personal to availables
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()

                if (tempPersonals.size > 1) {
                    tempPersonals.removeAt(sourceIndex)
                    _personals.value = tempPersonals

                    updateDraggableAvailables(tempPersonals)
                } else {
                    Toast.makeText(context, "You must have at least one personal favorite.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Drag available to personals
            _personals.value?.let { personals ->
                if (personals.size < 7) {
                    val tempPersonals: MutableList<Favorite?> = personals.toMutableList()

                    val sourceFavorite: Favorite? = _availables.value?.get(sourceIndex)
                    tempPersonals.add(targetIndex, sourceFavorite)
                    _personals.value = tempPersonals.take(7) // Ensure limit of 7 items.

                    updateDraggableAvailables(tempPersonals)
                } else {
                    Toast.makeText(context, "You can only have 7 personal favorites.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onSwap(isPersonal: Boolean, from: Int, to: Int) {
        if (isPersonal) {
            // Swap personal items.
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()
                Collections.swap(tempPersonals, from, to)
                _personals.value = tempPersonals.toList()
            }
        }
        /*
        else {
            // Swap available items. Available items must not be swappable.
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

    fun updateDraggableAvailables(tempPersonals: MutableList<Favorite?>) {
        val filteredAvailables: MutableList<Favorite?> = responseAvailables.filter { available ->
            tempPersonals.none { personal -> personal?.identifier == available?.identifier }
        }.toMutableList()

        val headerAvailables: MutableList<Favorite?> = FavoriteUtils.addAvailableHeaders(filteredAvailables)
        _availables.value = headerAvailables
    }
}