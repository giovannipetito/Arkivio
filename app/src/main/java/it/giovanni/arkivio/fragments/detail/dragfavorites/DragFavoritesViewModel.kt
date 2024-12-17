package it.giovanni.arkivio.fragments.detail.dragfavorites

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.fragments.detail.dragfavorites.DragFavoritesAdapter.Companion.EDIT_IDENTIFIER
import it.giovanni.arkivio.fragments.detail.dragfavorites.DragFavoritesAdapter.Companion.MAX_FAVORITES_SIZE
import it.giovanni.arkivio.utils.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Favorite
import java.util.Collections

class DragFavoritesViewModel : ViewModel() {

    private val _personals = MutableLiveData<List<Favorite?>>()
    val personals: LiveData<List<Favorite?>> get() = _personals

    private val _availables = MutableLiveData<List<Favorite?>>()
    val availables: LiveData<List<Favorite?>> get() = _availables

    private lateinit var responseAvailables: MutableList<Favorite?>
    lateinit var responsePersonals: MutableList<Favorite?>

    private val _isPersonalsChanged = MutableLiveData<Boolean>()
    val isPersonalsChanged: LiveData<Boolean> get() = _isPersonalsChanged

    private val _isMaxReached = MutableLiveData<Boolean>()
    val isMaxReached: LiveData<Boolean> get() = _isMaxReached

    private val _editState = MutableLiveData<EditState>()
    val editState: LiveData<EditState> get() = _editState

    private var pendingAvailable: Favorite? = null

    init {
        _editState.value = EditState.INIT
        _isPersonalsChanged.value = false
        _isMaxReached.value = false
        loadPersonalFavorites()
    }

    private fun loadPersonalFavorites() {
        val personals: MutableList<Favorite?> = FavoriteUtils.getPersonals()
        responsePersonals = personals.filterNotNull().take(MAX_FAVORITES_SIZE).toMutableList()

        val editablePersonals: MutableList<Favorite?> = responsePersonals.toMutableList()

        val editItem = Favorite()
        editItem.identifier = EDIT_IDENTIFIER

        editablePersonals.add(editItem)

        _personals.value = editablePersonals

        loadAvailableFavorites(editablePersonals)
    }

    private fun loadAvailableFavorites(editablePersonals: MutableList<Favorite?>) {
        val availables: MutableList<Favorite?> = FavoriteUtils.convertAvailableToFavorite(FavoriteUtils.getAvailables())
        responseAvailables = availables

        val filteredAvailables: MutableList<Favorite?> = availables.filter { available ->
            editablePersonals.none { personal -> personal?.identifier == available?.identifier }
        }.toMutableList()

        val headerAvailables: MutableList<Favorite?> = FavoriteUtils.addAvailableHeaders(filteredAvailables)
        _availables.value = headerAvailables
    }

    fun saveFavorites(newFavorites: MutableList<Favorite?>) {

        _editState.value = EditState.SUCCESS
        _isPersonalsChanged.value = false

        responsePersonals = newFavorites.filterNotNull().take(MAX_FAVORITES_SIZE).toMutableList()

        val editablePersonals: MutableList<Favorite?> = responsePersonals.toMutableList()

        val editItem = Favorite()
        editItem.identifier = EDIT_IDENTIFIER

        editablePersonals.add(editItem)

        _personals.value = editablePersonals

        loadAvailableFavorites(editablePersonals)
    }

    fun onSwap(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int) {
        if (isPersonal) {
            // Swap personal items.
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()
                Collections.swap(tempPersonals, sourcePosition, targetPosition)
                _personals.value = tempPersonals.toList()
            }
            setIsPersonalsChanged()
        }
        /*
        else {
            // Swap available items. Available items must not be swappable.
            _availables.value?.let { availables ->
                val tempAvailables: MutableList<Favorite?> = availables.toMutableList()
                Collections.swap(tempAvailables, sourcePosition, targetPosition)
                _availables.value = tempAvailables.toList()
            }
        }
        */
    }

    fun onSwapManually(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int) {
        if (isPersonal) {
            // Swap personal items manually.
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()

                // Perform the swap
                val temp = tempPersonals[sourcePosition]
                tempPersonals[sourcePosition] = tempPersonals[targetPosition]
                tempPersonals[targetPosition] = temp

                _personals.value = tempPersonals.toList()
            }
            setIsPersonalsChanged()
        }
    }

    fun onDrop(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int) {
        if (isPersonal) {
            // Drag personal to availables
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()

                if (tempPersonals.size > 1) {
                    tempPersonals.removeAt(sourcePosition)

                    if (_isMaxReached.value == true) {
                        tempPersonals.add(sourcePosition, pendingAvailable)
                    }

                    _personals.value = tempPersonals

                    updateDraggableAvailables(tempPersonals)
                    setIsPersonalsChanged()
                } else {
                    Toast.makeText(context, "You must have at least one personal favorite.", Toast.LENGTH_SHORT).show()
                }
            }
            _isMaxReached.value = false
        } else {
            // Drag available to personals
            _personals.value?.let { personals ->
                val tempPersonals: MutableList<Favorite?> = personals.toMutableList()
                val sourceFavorite: Favorite? = _availables.value?.get(sourcePosition)
                if (personals.size < MAX_FAVORITES_SIZE) {
                    tempPersonals.add(targetPosition, sourceFavorite)
                    _personals.value = tempPersonals.take(MAX_FAVORITES_SIZE) // Ensure limit of 7 items.

                    updateDraggableAvailables(tempPersonals)
                    setIsPersonalsChanged()
                    _isMaxReached.value = false
                } else {
                    pendingAvailable = sourceFavorite
                    _isMaxReached.value = true
                }
            }
        }
    }

    fun removeEditItem(position: Int) {
        _personals.value?.let { personals ->
            val updatedPersonals: MutableList<Favorite?> = personals.toMutableList().apply {
                removeAt(position)
            }
            _personals.value = updatedPersonals
        }
    }

    fun initEditState(state: EditState) {
        _editState.value = state
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

    enum class EditState {
        INIT,
        SUCCESS,
        ERROR
    }
}