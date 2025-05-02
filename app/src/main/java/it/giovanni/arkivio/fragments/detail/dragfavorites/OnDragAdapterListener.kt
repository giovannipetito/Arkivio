package it.giovanni.arkivio.fragments.detail.dragfavorites

interface OnDragAdapterListener {
    fun onDrag(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
    fun onEditModeChanged(isEditMode: Boolean)
    fun onEditModeRemoved(position: Int)
}