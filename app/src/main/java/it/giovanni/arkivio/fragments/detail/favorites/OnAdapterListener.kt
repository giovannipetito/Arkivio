package it.giovanni.arkivio.fragments.detail.favorites

import it.giovanni.arkivio.model.favorite.Favorite

interface OnAdapterListener {
    fun onSet(isPersonal: Boolean, targetIndex: Int, sourceIndex: Int, targetFavorite: Favorite)
    fun onAdd(isPersonal: Boolean, favorite: Favorite)
    fun onRemove(isPersonal: Boolean, favorite: Favorite)
    fun onSwap(isPersonal: Boolean, from: Int, to: Int)
    fun onEditModeChanged(isEditMode: Boolean)
}