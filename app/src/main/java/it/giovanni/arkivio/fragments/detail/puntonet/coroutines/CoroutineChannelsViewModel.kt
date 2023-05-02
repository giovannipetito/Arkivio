package it.giovanni.arkivio.fragments.detail.puntonet.coroutines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoroutineChannelsViewModel : ViewModel() {

    private val _result1: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val result1: LiveData<Boolean> = _result1

    init {

    }
}