package it.giovanni.arkivio.fragments.detail.favorites

import it.giovanni.arkivio.model.favorite.Favorite

interface OnAdapterListener {
    fun onSet(targetIndex: Int, sourceIndex: Int, targetFavorite: Favorite)
    fun onAdd(favorite: Favorite)
    fun onRemove(favorite: Favorite)
    fun onSwap(isFavorite: Boolean, from: Int, to: Int)
}