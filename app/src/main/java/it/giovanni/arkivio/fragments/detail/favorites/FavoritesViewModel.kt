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
    private var responsePersonals: MutableList<Favorite?>

    private val _isPersonalsChanged = MutableLiveData<Boolean>()
    val isPersonalsChanged: LiveData<Boolean> get() = _isPersonalsChanged

    init {
        _isPersonalsChanged.value = false

        val initResponsePersonals: MutableList<Favorite?> = FavoriteUtils.getPersonals()
        responsePersonals = initResponsePersonals.filterNotNull().take(7).toMutableList()
        val editablePersonals: MutableList<Favorite?> = responsePersonals.toMutableList()

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
                    setIsPersonalsChanged()
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
                    setIsPersonalsChanged()
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
            setIsPersonalsChanged()
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

    private fun setIsPersonalsChanged() {
        _personals.value?.let { currentPersonals ->

            if (currentPersonals.size != responsePersonals.size) {
                _isPersonalsChanged.value = true
                return
            }

            val currentIdentifiers: Set<String?> = currentPersonals.filterNotNull().map { it.identifier }.toSet()
            val initialIdentifiers: Set<String?> = responsePersonals.filterNotNull().map { it.identifier }.toSet()

            if (currentIdentifiers != initialIdentifiers) {
                _isPersonalsChanged.value = true
                return
            }

            for (i in currentPersonals.indices) {
                if (currentPersonals[i]?.identifier != responsePersonals[i]?.identifier) {
                    _isPersonalsChanged.value = true
                    return
                }
            }

            _isPersonalsChanged.value = false
        }
    }
}