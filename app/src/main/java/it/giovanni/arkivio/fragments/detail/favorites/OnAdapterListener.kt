package it.giovanni.arkivio.fragments.detail.favorites

interface OnAdapterListener {
    fun onSet(isPersonal: Boolean, targetIndex: Int, sourceIndex: Int)
    fun onSwap(isPersonal: Boolean, from: Int, to: Int)
    fun onEditModeChanged(isEditMode: Boolean)
    fun onEditModeRemoved(position: Int)
}