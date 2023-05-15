package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RxExampleLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import java.util.Locale

/**
 * This class demonstrates how to create multiple observables, apply operators such as filter and map
 * to transform and filter the emitted items, and manage subscriptions using a CompositeDisposable.
 */
class RxExample3Fragment : DetailFragment() {

    private var layoutBinding: RxExampleLayoutBinding? = null
    private val binding get() = layoutBinding

    // disposable is an instance of CompositeDisposable, which is used to hold multiple disposables
    // and dispose of them together.
    private val disposable = CompositeDisposable()

    private var message: String? = ""

    override fun getTitle(): Int {
        return R.string.rx_example3_title
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

        val animalsObservable: Observable<String> = getAnimalsObservable()

        val animalsObserver: DisposableObserver<String> = getAnimalsObserver()

        val capitalAnimalsObserver: DisposableObserver<String> = getAnimalsObserver()

        // The disposable.add(...) method is used to add disposables to the CompositeDisposable for
        // proper management of subscriptions.

        // This block subscribes to animalsObservable, filters the items starting with the letter
        // "b", and subscribes with animalsObserver.
        disposable.add(animalsObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { string: String ->
                string.lowercase(
                    Locale.getDefault()
                ).startsWith("b")
            }
            .subscribeWith(animalsObserver) // Operator used to subscribe to an observable and attach a specific observer to it.
        )

        // This block subscribes to animalsObservable, filters the items starting with the letter
        // "c", maps them to uppercase, and subscribes with capitalAnimalsObserver.
        disposable.add(animalsObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { string: String ->
                string.lowercase(
                    Locale.getDefault()
                ).startsWith("c")
            }
            .map { string: String ->
                Log.i("[RX]", "string: $string")
                string.uppercase(Locale.getDefault())
            }
            .subscribeWith(capitalAnimalsObserver)
        )
    }

    private fun getAnimalsObservable(): Observable<String> {
        return Observable.fromArray(
            "Ant", "Ape",
            "Bat", "Bee", "Bear", "Butterfly",
            "Cat", "Crab", "Cod",
            "Dog", "Dove",
            "Fox", "Frog"
        )
    }

    private fun getAnimalsObserver(): DisposableObserver<String> {

        return object : DisposableObserver<String>() {

            override fun onNext(name: String) {
                message = message + name + "\n"
                Log.d("[RX]", "Name: $name")
            }

            override fun onError(error: Throwable) {
                Log.e("[RX]", "onError: " + error.message)
            }

            override fun onComplete() {
                val onCompleteMessage = "All items are emitted!"
                message = message + onCompleteMessage + "\n"
                binding?.labelRx?.text = message
                Log.d("[RX]", "onCompleteMessage: $onCompleteMessage")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        disposable.dispose()
    }
}