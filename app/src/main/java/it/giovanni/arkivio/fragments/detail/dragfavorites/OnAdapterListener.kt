package it.giovanni.arkivio.fragments.detail.dragfavorites

interface OnAdapterListener {
    fun onSwap(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onDrop(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onEditModeChanged(isEditMode: Boolean)
    fun onEditModeRemoved(position: Int)
}