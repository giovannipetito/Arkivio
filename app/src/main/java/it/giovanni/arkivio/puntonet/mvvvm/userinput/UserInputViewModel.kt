package it.giovanni.arkivio.puntonet.mvvvm.userinput

import androidx.lifecycle.LiveData
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

    val _number: MutableLiveData<Int> = MutableLiveData<Int>()
    private val _result: MutableLiveData<String> = MutableLiveData<String>()

    val number: LiveData<Int> = _number
    val result: LiveData<String> = _result

    fun calculate() {
        val res = _number.value?.let { it * 2 }
        _result.value = res?.toString() ?: "Invalid input"
    }
}