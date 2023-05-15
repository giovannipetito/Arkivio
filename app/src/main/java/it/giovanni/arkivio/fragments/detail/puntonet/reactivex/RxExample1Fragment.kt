package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RxExampleLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

/**
 *  This class demonstrates how to create an observable that emits a sequence of animal names,
 *  subscribe to the observable, and handle the emitted items using an observer. The emitted
 *  items are logged and displayed in a TextView using data binding.
 */
class RxExample1Fragment : DetailFragment() {

    private var layoutBinding: RxExampleLayoutBinding? = null
    private val binding get() = layoutBinding

    /*
    disposable viene utilizzato per terminare (dispose) la subscription quando un observer non
    ascolta più un observable (evitando memory leak). Supponiamo di eseguire una chiamata network
    per aggiornare la UI, ma di distruggere il fragment prima che la chiamata termini, poiché la
    subscription all’observer è ancora attiva, tenta di aggiornare il fragment già distrutto
    generando memory leak. Utilizzando i disposable, la unsubscription avviene non appena il
    fragment viene distrutto.
    */
    private var disposable: Disposable? = null

    private var message: String? = ""

    override fun getTitle(): Int {
        return R.string.rx_example1_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = RxExampleLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observable is created using the getAnimalsObservable() function, which emits a list of animal names.
        val observable: Observable<String> = getAnimalsObservable()

        val observer: Observer<String> = getAnimalsObserver()

        observable
            .subscribeOn(Schedulers.io()) // indica all'observable di eseguire il task su un thread in background.
            .observeOn(AndroidSchedulers.mainThread()) // indica all'observer di ricevere i dati sul thread della UI in modo da poter eseguire qualsiasi azione relativa alla UI.
            .subscribe(observer)
    }

    // This function returns an Observable that emits a sequence of animal names using Observable.just().
    private fun getAnimalsObservable(): Observable<String> {
        return Observable.just("Ant", "Bee", "Cat", "Dog", "Fox")
    }

    // This function returns an Observer implementation that defines how to handle the emitted items
    // from the observable. In this case, it logs each animal name using Log.d().
    private fun getAnimalsObserver(): Observer<String> {

        return object : Observer<String> {

            // This method is called when the observer subscribes to the observable.
            // It saves the disposable object and logs a message using Log.d().
            override fun onSubscribe(d: Disposable) {
                disposable = d
                val onSubscribeMessage = "onSubscribe"
                message = onSubscribeMessage + "\n"
                Log.d("[RX]", onSubscribeMessage)
            }

            // This method is called when a new item is emitted from the observable.
            override fun onNext(name: String) {
                message = message + name + "\n"
                Log.d("[RX]", "Name: $name")
            }

            // This method is called when an error occurs during the observable emission.
            override fun onError(error: Throwable) {
                Log.e("[RX]", "onError: " + error.message)
            }

            // This method is called when the observable completes emitting all items.
            override fun onComplete() {
                val onCompleteMessage = "All items are emitted!"
                message += onCompleteMessage
                binding?.labelRx?.text = message
                Log.d("[RX]", "onCompleteMessage: $onCompleteMessage")
            }
        }
    }

    // This method is called when the fragment's view is destroyed. It clears the layoutBinding
    // reference and disposes of the subscription by calling dispose() on the disposable.
    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        disposable?.dispose()
    }
}