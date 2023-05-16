package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
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

    var message1: MutableLiveData<String> = MutableLiveData<String>()
    var message2: MutableLiveData<String> = MutableLiveData<String>()
    var message3: MutableLiveData<String> = MutableLiveData<String>()
    var message4: MutableLiveData<String> = MutableLiveData<String>()

    // This function returns an Observable that emits a sequence of animal names using Observable.just().
    private fun getAnimalsObservableJust(): Observable<String> {
        return Observable.just("Ape", "Balena", "Cane", "Delfino", "Elefante")
    }

    // This function returns an Observable that emits a list of animal names using Observable.fromArray().
    fun getAnimalsObservable(): Observable<String> {
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
    fun getAnimalsObserver(id: Int): Observer<String> {

        return object : Observer<String> {

            // This method is called when the observer subscribes to the observable.
            // It saves the disposable object and logs a message using Log.d().
            override fun onSubscribe(d: Disposable) {
                disposable = d
                val onSubscribeMessage = "onSubscribe"

                if (id == 1) message1.value = onSubscribeMessage + "\n"
                if (id == 2) message2.value = onSubscribeMessage + "\n"

                Log.d("[RX]", onSubscribeMessage)
            }

            // This method is called when a new item is emitted from the observable.
            override fun onNext(name: String) {

                if (id == 1) message1.value = message1.value + name + "\n"
                if (id == 2) message2.value = message2.value + name + "\n"

                Log.d("[RX]", "Name: $name")
            }

            // This method is called when an error occurs during the observable emission.
            override fun onError(error: Throwable) {
                Log.e("[RX]", "onError: " + error.message)
            }

            // This method is called when the observable completes emitting all items.
            override fun onComplete() {
                val onCompleteMessage = "All items are emitted!" + "\n"

                if (id == 1) message1.value = message1.value + onCompleteMessage
                if (id == 2) message2.value = message2.value + onCompleteMessage

                Log.d("[RX]", "onCompleteMessage: $onCompleteMessage")
            }
        }
    }

    fun getAnimalsDisposableObserver(): DisposableObserver<String> {

        return object : DisposableObserver<String>() {

            override fun onNext(name: String) {

                message3.value =
                    if (message3.value == null) name + "\n"
                    else message3.value + name + "\n"

                Log.d("[RX]", "Name: $name")
            }

            override fun onError(error: Throwable) {
                Log.e("[RX]", "onError: " + error.message)
            }

            override fun onComplete() {
                val onCompleteMessage = "All items are emitted!" + "\n"
                message3.value = message3.value + onCompleteMessage
                Log.d("[RX]", "onCompleteMessage: $onCompleteMessage")
            }
        }
    }

    // This function creates an Observable using Observable.create that emits individual Note objects
    // from a list of Note items. It iterates through the list and calls onNext for each note. Finally,
    // it calls onComplete to indicate the completion of emissions.
    fun getNotesObservable(): Observable<Note> {
        val notes: List<Note> = getNotes()
        return Observable.create { emitter: ObservableEmitter<Note> ->
            for (note in notes) {
                if (!emitter.isDisposed) {
                    emitter.onNext(note)
                }
            }
            if (!emitter.isDisposed) {
                emitter.onComplete()
            }
        }
    }

    // This function returns a DisposableObserver implementation that handles the emitted Note objects.
    fun getNotesDisposableObserver(): DisposableObserver<Note> {
        return object :
            DisposableObserver<Note>() {
            override fun onNext(note: Note) {

                message4.value =
                    if (message4.value == null) note.note + "\n"
                    else message4.value + note.note + "\n"

                Log.d("[RX]", "Note: " + note.note)
            }

            override fun onError(error: Throwable) {
                Log.e("[RX]", "onError: " + error.message)
            }

            override fun onComplete() {
                val onCompleteMessage = "All notes are emitted!" + "\n"
                message4.value = message4.value + onCompleteMessage
                Log.d("[RX]", "onCompleteMessage: $onCompleteMessage")
            }
        }
    }

    private fun getNotes(): List<Note> {
        val notes: MutableList<Note> = ArrayList()
        notes.add(Note(1, "Wash the floor"))
        notes.add(Note(2, "Call mom"))
        notes.add(Note(3, "Prepare dinner"))
        notes.add(Note(4, "Watch TV"))
        return notes
    }

    class Note(var id: Int, var note: String)
}