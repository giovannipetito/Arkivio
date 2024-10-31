package it.giovanni.arkivio.fragments.detail.favorites

import it.giovanni.arkivio.model.favorite.Favorite

interface OnAdapterListener {
    fun onSet(isPersonal: Boolean, targetIndex: Int, sourceIndex: Int, targetFavorite: Favorite)
    fun onSwap(isPersonal: Boolean, from: Int, to: Int)
    fun onEditModeChanged(isEditMode: Boolean)
    fun onEditModeRemoved(position: Int)
}