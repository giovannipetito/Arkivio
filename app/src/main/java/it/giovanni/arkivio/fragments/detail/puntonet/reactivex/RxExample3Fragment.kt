package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
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

    private lateinit var viewModel: RxViewModel

    // compositeDisposable is an instance of CompositeDisposable, which is used to hold multiple
    // disposables and dispose of them together.
    private val compositeDisposable = CompositeDisposable()

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

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = RxExampleLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RxViewModel::class.java]

        loadFilteredData()

        loadFilteredMappedData()

        viewModel.message3.observe(viewLifecycleOwner) {
            binding?.labelRx?.text = it
        }
    }

    private fun loadFilteredData() {
        // The compositeDisposable.add(...) method is used to add disposables to the
        // CompositeDisposable for proper management of subscriptions.

        // This block subscribes to getAnimalsObservable(), filters the items starting with the letter
        // "b", and subscribes with getAnimalsDisposableObserver().
        compositeDisposable.add(viewModel.getAnimalsObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { string: String ->
                string.lowercase(
                    Locale.getDefault()
                ).startsWith("b")
            }
            .subscribeWith(viewModel.getAnimalsDisposableObserver()) // Operator used to subscribe to an observable and attach a specific observer to it.
        )
    }

    private fun loadFilteredMappedData() {
        // This block subscribes to getAnimalsObservable(), filters the items starting with the letter
        // "c", maps them to uppercase, and subscribes with getAnimalsDisposableObserver().
        compositeDisposable.add(viewModel.getAnimalsObservable()
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
            .subscribeWith(viewModel.getAnimalsDisposableObserver())
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        compositeDisposable.dispose()
    }
}