package it.giovanni.arkivio.fragments.detail.dragdrop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Collections

class FavoritesViewModel : ViewModel() {

    private val _topData: MutableLiveData<List<Favorite?>> = MutableLiveData()
    val topData: LiveData<List<Favorite?>> = _topData

    private val _bottomData: MutableLiveData<List<Favorite?>> = MutableLiveData()
    val bottomData: LiveData<List<Favorite?>> = _bottomData

    private var remoteTopData: MutableList<Favorite?> = arrayListOf(
        Favorite("1", true, false),
        Favorite("2", true, false),
        Favorite("3", true, false),
        Favorite("4", true, false),
        Favorite("5", true, false),
        null,
        null
    )

    private val remoteBottomData: MutableList<Favorite> = arrayListOf(
        Favorite("A", false, true),
        Favorite("B", false, true),
        Favorite("C", false, true),
        Favorite("D", false, true),
        Favorite("E", false, true),
    )

    init {
        _topData.value = remoteTopData
        _bottomData.value = remoteBottomData
    }

    fun setData(items: List<Favorite>) {
        _bottomData.value = items
    }

    /*
    fun setTopData(list: MutableList<Favorite?>) {
        _topData.value = list
    }

    fun setBottomData(list: MutableList<Favorite?>) {
        _bottomData.value = list
    }
    */

    fun onSet(targetIndex: Int, sourceIndex: Int, targetItem: Favorite) {
        val topTempData = _topData.value?.toMutableList()
        val bottomTempData = _bottomData.value?.toMutableList()

        if (targetItem.isPersonal) {
            val item = bottomTempData?.get(sourceIndex)?.copy(isPersonal = true)
            topTempData?.let {
                it[targetIndex] = item
            }

            bottomTempData?.let {
                it[sourceIndex] = targetItem.copy(isPersonal = false)
            }
        } else {
            val item = topTempData?.get(targetIndex)?.copy(isPersonal = false)
            topTempData?.let {
                it[targetIndex] = targetItem.copy(isPersonal = true)
            }

            bottomTempData?.let {
                it[sourceIndex] = item
            }
        }
        _bottomData.value = bottomTempData?.toList()
        _topData.value = topTempData?.toList()
    }

    fun onRemove(item: Favorite) {
        if (item.isPersonal) {
            _topData.value?.let { topData ->
                val topTempData = topData.toMutableList()
                val bottomTempData = _bottomData.value?.toMutableList()
                for (i in topTempData.indices) {
                    if (topTempData[i] == item) {
                        topTempData[i] = null
                        break
                    }
                }
                _topData.value = topTempData.toList()
                bottomTempData?.add(item.copy(isPersonal = false))
                _bottomData.value = bottomTempData?.toList()
            }
        } else {
            _bottomData.value?.let { bottomData ->
                val topTempData = _topData.value?.toMutableList()
                val bottomTempData = bottomData.toMutableList()
                bottomTempData.remove(item)
                _bottomData.value = bottomTempData.toList()
                for (i in topTempData?.indices!!) {
                    if (topTempData[i] == null) {
                        topTempData[i] = item.copy(isPersonal = true)
                        break
                    }
                }
                topTempData.sortWith(Comparator.nullsLast(null))
                _topData.value = topTempData.toList()
            }
        }
    }

    fun onAdd(item: Favorite) {
        if (!item.isPersonal) {
            _topData.value?.let { topData ->
                val topTempData = topData.toMutableList()
                val bottomTempData = _bottomData.value?.toMutableList()

                // if (topTempData.filterNotNull().size < TOP_DATA_MAX_SIZE) {
                    for (i in topTempData.indices) {
                        if (topTempData[i] == null) {
                            topTempData[i] = item.copy(isPersonal = true)
                            break
                        }
                    }
                    _topData.value = topTempData.toList()
                    bottomTempData?.remove(item)
                    _bottomData.value = bottomTempData?.toList()
                // }
            }
        } else {
            _bottomData.value?.let { bottomData ->
                val topTempData = _topData.value?.toMutableList()
                val bottomTempData = bottomData.toMutableList()

                bottomTempData.add(item.copy(isPersonal = false))
                _bottomData.value = bottomTempData.toList()
                topTempData?.remove(item)
                topTempData?.add(null)
                topTempData?.sortWith(Comparator.nullsLast(null))
                _topData.value = topTempData?.toList()
            }
        }
    }

    fun onSwap(isPersonal: Boolean, from: Int, to: Int) {
        if (isPersonal) {
            _topData.value?.let {
                val data = it.toMutableList()
                Collections.swap(data, from, to)
                _topData.value = data.toList()
            }
        } else {
            _bottomData.value?.let {
                val data = it.toMutableList()
                Collections.swap(data, from, to)
                _bottomData.value = data.toList()
            }
        }
    }

    companion object {
        const val TOP_DATA_MAX_SIZE = 3
    }
}