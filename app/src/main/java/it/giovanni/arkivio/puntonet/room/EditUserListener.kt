package it.giovanni.arkivio.puntonet.room

interface EditUserListener {

    fun onAddFirstNameChangedListener(input: String)

    fun onAddLastNameChangedListener(input: String)

    fun onAddAgeChangedListener(input: String)
}