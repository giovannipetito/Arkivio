package it.giovanni.arkivio.fragments.detail.favorites

interface OnAdapterListener {
    fun onSwap(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onDrop(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onEditModeChanged(isEditMode: Boolean)
    fun onEditModeRemoved(position: Int)

    fun onSwapEntered(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onSwapEnded(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onDragEntered(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onDragEnded(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
}