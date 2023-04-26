package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.userinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * UserInputViewModel is the ViewModel class that will handle the business logic of the app.
 * This class will have a MutableLiveData object that will hold (or store) the number entered by the
 * user, and a method that will perform the calculation. When the user clicks the "Calculate" button,
 * the calculate() method will be called, which will perform the calculation and update the result
 * LiveData object.
 */
class UserInputViewModel : ViewModel() {

    val number = MutableLiveData<Int>()
    val result = MutableLiveData<String>()

    fun calculate() {
        val res = number.value?.let { it * 2 }
        result.value = res?.toString() ?: "Invalid input"
    }
}