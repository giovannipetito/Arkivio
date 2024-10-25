package it.giovanni.arkivio.fragments.detail.dragdrop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Collections

class FavoritesViewModel : ViewModel() {

    private val _favorites: MutableLiveData<List<OldFavorite?>> = MutableLiveData()
    val favorites: LiveData<List<OldFavorite?>> = _favorites

    private val _availables: MutableLiveData<List<OldFavorite?>> = MutableLiveData()
    val availables: LiveData<List<OldFavorite?>> = _availables

    private var responseFavorites: MutableList<OldFavorite?> = arrayListOf(
        OldFavorite("1", true),
        OldFavorite("2", true),
        OldFavorite("3", true),
        OldFavorite("4", true),
        OldFavorite("5", true),
        OldFavorite("6", true),
        OldFavorite("7", true)
    )

    private val responseAvailables: MutableList<OldFavorite> = arrayListOf(
        OldFavorite("A", false),
        OldFavorite("B", false),
        OldFavorite("C", false),
        OldFavorite("D", false),
        OldFavorite("E", false),
        OldFavorite("F", false),
        OldFavorite("G", false),
        OldFavorite("H", false),
        OldFavorite("I", false),
        OldFavorite("J", false),
        OldFavorite("K", false),
        OldFavorite("L", false)
    )

    init {
        _favorites.value = responseFavorites
        _availables.value = responseAvailables
    }

    fun onSet(targetIndex: Int, sourceIndex: Int, targetItem: OldFavorite) {

        val tempFavorites: MutableList<OldFavorite?>? = _favorites.value?.toMutableList()
        val tempAvailables: MutableList<OldFavorite?>? = _availables.value?.toMutableList()

        if (targetItem.isFavorite) {
            val favorite = tempAvailables?.get(sourceIndex)?.copy(isFavorite = true)
            tempFavorites?.let {
                it[targetIndex] = favorite
            }

            tempAvailables?.let {
                it[sourceIndex] = targetItem.copy(isFavorite = false)
            }
        } else {
            val favorite = tempFavorites?.get(targetIndex)?.copy(isFavorite = false)
            tempFavorites?.let {
                it[targetIndex] = targetItem.copy(isFavorite = true)
            }

            tempAvailables?.let {
                it[sourceIndex] = favorite
            }
        }

        _availables.value = tempAvailables?.toList()
        _favorites.value = tempFavorites?.toList()
    }

    fun onRemove(item: OldFavorite) {
        if (item.isFavorite) {
            _favorites.value?.let { favorites ->
                val tempFavorites: MutableList<OldFavorite?> = favorites.toMutableList()
                val tempAvailables: MutableList<OldFavorite?>? = _availables.value?.toMutableList()
                for (i in tempFavorites.indices) {
                    if (tempFavorites[i] == item) {
                        tempFavorites[i] = null
                        break
                    }
                }
                _favorites.value = tempFavorites.toList()
                tempAvailables?.add(item.copy(isFavorite = false))
                _availables.value = tempAvailables?.toList()
            }
        } else {
            _availables.value?.let { availables ->
                val tempFavorites: MutableList<OldFavorite?>? = _favorites.value?.toMutableList()
                val tempAvailables: MutableList<OldFavorite?> = availables.toMutableList()
                tempAvailables.remove(item)
                _availables.value = tempAvailables.toList()
                for (i in tempFavorites?.indices!!) {
                    if (tempFavorites[i] == null) {
                        tempFavorites[i] = item.copy(isFavorite = true)
                        break
                    }
                }
                tempFavorites.sortWith(Comparator.nullsLast(null))
                _favorites.value = tempFavorites.toList()
            }
        }
    }

    fun onAdd(favorite: OldFavorite) {
        if (!favorite.isFavorite) {
            _favorites.value?.let { favorites ->
                val tempFavorites: MutableList<OldFavorite?> = favorites.toMutableList()
                val tempAvailables: MutableList<OldFavorite?>? = _availables.value?.toMutableList()

                // if (tempFavorites.filterNotNull().size < TOP_DATA_MAX_SIZE) {
                    for (i in tempFavorites.indices) {
                        if (tempFavorites[i] == null) {
                            tempFavorites[i] = favorite.copy(isFavorite = true)
                            break
                        }
                    }
                    _favorites.value = tempFavorites.toList()
                    tempAvailables?.remove(favorite)
                    _availables.value = tempAvailables?.toList()
                // }
            }
        } else {
            _availables.value?.let { availables ->
                val tempFavorites: MutableList<OldFavorite?>? = _favorites.value?.toMutableList()
                val tempAvailables: MutableList<OldFavorite?> = availables.toMutableList()

                tempAvailables.add(favorite.copy(isFavorite = false))
                _availables.value = tempAvailables.toList()
                tempFavorites?.remove(favorite)
                tempFavorites?.add(null)
                tempFavorites?.sortWith(Comparator.nullsLast(null))
                _favorites.value = tempFavorites?.toList()
            }
        }
    }

    fun onSwap(isFavorite: Boolean, from: Int, to: Int) {
        if (isFavorite) {
            _favorites.value?.let { favorites ->
                val tempFavorites: MutableList<OldFavorite?> = favorites.toMutableList()
                Collections.swap(tempFavorites, from, to)
                _favorites.value = tempFavorites.toList()
            }
        } else {
            _availables.value?.let { availables ->
                val tempAvailables: MutableList<OldFavorite?> = availables.toMutableList()
                Collections.swap(tempAvailables, from, to)
                _availables.value = tempAvailables.toList()
            }
        }
    }

    companion object {
        const val TOP_DATA_MAX_SIZE = 3
    }
}