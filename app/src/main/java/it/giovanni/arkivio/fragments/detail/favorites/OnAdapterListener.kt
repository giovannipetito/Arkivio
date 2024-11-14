package it.giovanni.arkivio.fragments.detail.favorites

interface OnAdapterListener {
    fun onSet(isPersonal: Boolean, targetPosition: Int, sourcePosition: Int)
    fun onSwap(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onEditModeChanged(isEditMode: Boolean)
    fun onEditModeRemoved(position: Int)
}