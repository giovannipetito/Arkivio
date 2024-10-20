package it.giovanni.arkivio.fragments.detail.dragdrop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Collections

class FavoritesViewModel : ViewModel() {

    private val _personalFavorites: MutableLiveData<List<Favorite?>> = MutableLiveData()
    val personalFavorites: LiveData<List<Favorite?>> = _personalFavorites

    private val _availableFavorites: MutableLiveData<List<Favorite?>> = MutableLiveData()
    val availableFavorites: LiveData<List<Favorite?>> = _availableFavorites

    private var remotePersonalFavorites: MutableList<Favorite?> = arrayListOf(
        Favorite("1", true, false),
        Favorite("2", true, false),
        Favorite("3", true, false),
        Favorite("4", true, false),
        Favorite("5", true, false),
        null,
        null
    )

    private val remoteAvailableFavorites: MutableList<Favorite> = arrayListOf(
        Favorite("A", false, true),
        Favorite("B", false, true),
        Favorite("C", false, true),
        Favorite("D", false, true),
        Favorite("E", false, true),
    )

    init {
        _personalFavorites.value = remotePersonalFavorites
        _availableFavorites.value = remoteAvailableFavorites
    }

    fun onSet(targetIndex: Int, sourceIndex: Int, targetItem: Favorite) {

        val tempPersonalFavorites: MutableList<Favorite?>? = _personalFavorites.value?.toMutableList()
        val tempAvailableFavorites: MutableList<Favorite?>? = _availableFavorites.value?.toMutableList()

        if (targetItem.isPersonal) {
            val favorite = tempAvailableFavorites?.get(sourceIndex)?.copy(isPersonal = true)
            tempPersonalFavorites?.let {
                it[targetIndex] = favorite
            }

            tempAvailableFavorites?.let {
                it[sourceIndex] = targetItem.copy(isPersonal = false)
            }
        } else {
            val favorite = tempPersonalFavorites?.get(targetIndex)?.copy(isPersonal = false)
            tempPersonalFavorites?.let {
                it[targetIndex] = targetItem.copy(isPersonal = true)
            }

            tempAvailableFavorites?.let {
                it[sourceIndex] = favorite
            }
        }

        _availableFavorites.value = tempAvailableFavorites?.toList()
        _personalFavorites.value = tempPersonalFavorites?.toList()
    }

    fun onRemove(item: Favorite) {
        if (item.isPersonal) {
            _personalFavorites.value?.let { personalFavorites ->
                val tempPersonalFavorites: MutableList<Favorite?> = personalFavorites.toMutableList()
                val tempAvailableFavorites: MutableList<Favorite?>? = _availableFavorites.value?.toMutableList()
                for (i in tempPersonalFavorites.indices) {
                    if (tempPersonalFavorites[i] == item) {
                        tempPersonalFavorites[i] = null
                        break
                    }
                }
                _personalFavorites.value = tempPersonalFavorites.toList()
                tempAvailableFavorites?.add(item.copy(isPersonal = false))
                _availableFavorites.value = tempAvailableFavorites?.toList()
            }
        } else {
            _availableFavorites.value?.let { availableFavorites ->
                val tempPersonalFavorites: MutableList<Favorite?>? = _personalFavorites.value?.toMutableList()
                val tempAvailableFavorites: MutableList<Favorite?> = availableFavorites.toMutableList()
                tempAvailableFavorites.remove(item)
                _availableFavorites.value = tempAvailableFavorites.toList()
                for (i in tempPersonalFavorites?.indices!!) {
                    if (tempPersonalFavorites[i] == null) {
                        tempPersonalFavorites[i] = item.copy(isPersonal = true)
                        break
                    }
                }
                tempPersonalFavorites.sortWith(Comparator.nullsLast(null))
                _personalFavorites.value = tempPersonalFavorites.toList()
            }
        }
    }

    fun onAdd(favorite: Favorite) {
        if (!favorite.isPersonal) {
            _personalFavorites.value?.let { personalFavorites ->
                val tempPersonalFavorites: MutableList<Favorite?> = personalFavorites.toMutableList()
                val tempAvailableFavorites: MutableList<Favorite?>? = _availableFavorites.value?.toMutableList()

                // if (tempPersonalFavorites.filterNotNull().size < TOP_DATA_MAX_SIZE) {
                    for (i in tempPersonalFavorites.indices) {
                        if (tempPersonalFavorites[i] == null) {
                            tempPersonalFavorites[i] = favorite.copy(isPersonal = true)
                            break
                        }
                    }
                    _personalFavorites.value = tempPersonalFavorites.toList()
                    tempAvailableFavorites?.remove(favorite)
                    _availableFavorites.value = tempAvailableFavorites?.toList()
                // }
            }
        } else {
            _availableFavorites.value?.let { availableFavorites ->
                val tempPersonalFavorites: MutableList<Favorite?>? = _personalFavorites.value?.toMutableList()
                val tempAvailableFavorites: MutableList<Favorite?> = availableFavorites.toMutableList()

                tempAvailableFavorites.add(favorite.copy(isPersonal = false))
                _availableFavorites.value = tempAvailableFavorites.toList()
                tempPersonalFavorites?.remove(favorite)
                tempPersonalFavorites?.add(null)
                tempPersonalFavorites?.sortWith(Comparator.nullsLast(null))
                _personalFavorites.value = tempPersonalFavorites?.toList()
            }
        }
    }

    fun onSwap(isPersonal: Boolean, from: Int, to: Int) {
        if (isPersonal) {
            _personalFavorites.value?.let { personalFavorites ->
                val tempPersonalFavorites: MutableList<Favorite?> = personalFavorites.toMutableList()
                Collections.swap(tempPersonalFavorites, from, to)
                _personalFavorites.value = tempPersonalFavorites.toList()
            }
        } else {
            _availableFavorites.value?.let { availableFavorites ->
                val tempAvailableFavorites: MutableList<Favorite?> = availableFavorites.toMutableList()
                Collections.swap(tempAvailableFavorites, from, to)
                _availableFavorites.value = tempAvailableFavorites.toList()
            }
        }
    }

    companion object {
        const val TOP_DATA_MAX_SIZE = 3
    }
}