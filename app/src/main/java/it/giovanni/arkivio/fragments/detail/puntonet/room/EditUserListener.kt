package it.giovanni.arkivio.fragments.detail.puntonet.room

interface EditUserListener {

    fun onAddFirstNameChangedListener(input: String)

    fun onAddLastNameChangedListener(input: String)

    fun onAddAgeChangedListener(input: String)
}