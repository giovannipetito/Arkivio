package it.giovanni.arkivio.fragments.detail.puntonet.coroutines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Let's see how to properly return a value from a coroutine scope, and specifically from inside the
 * coroutine scope and from the outside of the coroutine scope.
 *
 * Inside the ViewModel we have access to viewModelScope.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineValuesViewModel : ViewModel() {

    private val _result1: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val result1: LiveData<Boolean> = _result1

    private val _result2: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val result2: LiveData<Boolean> = _result2

    init {

        // With this code we are able to retrieve the value from the inside of the coroutine scope.
        viewModelScope.launch {
            val res1: Boolean = withContext(Dispatchers.IO) {
                // Let's return a Boolean value after three seconds.
                delay(3000L)
                true
            }
            _result1.value = res1
            Log.i("[Coroutine]", "result1: $result1")
        }

        // With this code we are able to retrieve the value from the outside of the coroutine scope
        // and wait until the result is actually completed.
        val res2: Deferred<Boolean> = viewModelScope.async {
            delay(3000L)
            false
        }
        res2.invokeOnCompletion {
            if (it == null) { // If there is no any Throwable exception.
                _result2.value = res2.getCompleted()
                Log.i("[Coroutine]", "result2: $result2")
            }
        }
    }
}