package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver

class RxViewModel : ViewModel() {

    /*
    disposable viene utilizzato per terminare (dispose) la subscription quando un observer non
    ascolta più un observable (evitando così memory leak). Supponiamo di eseguire una chiamata
    network per aggiornare la UI, ma di distruggere il fragment prima che la chiamata termini,
    poiché la subscription all’observer è ancora attiva, tenta di aggiornare il fragment già
    distrutto generando memory leak. Utilizzando i disposable, la unsubscription avviene non
    appena il fragment viene distrutto.
    */
    var disposable: Disposable? = null

    // observable is created using the getAnimalsObservable() function, which emits a list of animal names.
    val observable: Observable<String> = getAnimalsObservable()

    val observer: Observer<String> = getAnimalsObserver()

    private val _message: MutableLiveData<String> = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    // This function returns an Observable that emits a sequence of animal names using Observable.just().
    private fun getAnimalsObservableJust(): Observable<String> {
        return Observable.just("Ape", "Balena", "Cane", "Delfino", "Elefante")
    }

    // This function returns an Observable that emits a sequence of animal names using Observable.fromArray().
    private fun getAnimalsObservable(): Observable<String> {
        return Observable.fromArray(
            "Ape", "Anatra",
            "Balena", "Bradipo", "Bisonte", "Boa",
            "Cane", "Coccodrillo", "Cervo", "Cinghiale",
            "Delfino", "Daino", "Donnola",
            "Elefante", "Ermellino",
            "Farfalla", "Falco",
            "Gatto", "Gallo", "Giraffa"
        )
    }

    // This function returns an Observer implementation that defines how to handle the emitted
    // items from the observable. In this case, it logs each animal name using Log.d().
    private fun getAnimalsObserver(): Observer<String> {

        return object : Observer<String> {

            // This method is called when the observer subscribes to the observable.
            // It saves the disposable object and logs a message using Log.d().
            override fun onSubscribe(d: Disposable) {
                disposable = d
                val onSubscribeMessage = "onSubscribe"
                _message.value = onSubscribeMessage + "\n"
                Log.d("[RX]", onSubscribeMessage)
            }

            // This method is called when a new item is emitted from the observable.
            override fun onNext(name: String) {
                _message.value = _message.value + name + "\n"
                Log.d("[RX]", "Name: $name")
            }

            // This method is called when an error occurs during the observable emission.
            override fun onError(error: Throwable) {
                Log.e("[RX]", "onError: " + error.message)
            }

            // This method is called when the observable completes emitting all items.
            override fun onComplete() {
                val onCompleteMessage = "All items are emitted!"
                _message.value = _message.value + onCompleteMessage
                Log.d("[RX]", "onCompleteMessage: $onCompleteMessage")
            }
        }
    }

    private fun getAnimalsDisposableObserver(): DisposableObserver<String> {

        return object : DisposableObserver<String>() {

            override fun onNext(name: String) {
                _message.value = _message.value + name + "\n"
                Log.d("[RX]", "Name: $name")
            }

            override fun onError(error: Throwable) {
                Log.e("[RX]", "onError: " + error.message)
            }

            override fun onComplete() {
                val onCompleteMessage = "All items are emitted!"
                _message.value = _message.value + onCompleteMessage
                Log.d("[RX]", "onCompleteMessage: $onCompleteMessage")
            }
        }
    }
}