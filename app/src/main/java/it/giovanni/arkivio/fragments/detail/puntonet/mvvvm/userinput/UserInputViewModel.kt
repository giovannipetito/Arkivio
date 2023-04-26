package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.userinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserInputViewModel : ViewModel() {

    val number = MutableLiveData<Int>()
    val result = MutableLiveData<String>()

    fun calculate() {
        val res = number.value?.let { it * 2 }
        result.value = res?.toString() ?: "Invalid input"
    }
}