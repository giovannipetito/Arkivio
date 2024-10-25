package it.giovanni.arkivio.puntonet.reactivex

import android.os.Bundle
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
 * This class demonstrates how to transform emitted items using the map operator. It creates an
 * Observable from a list of items and emits them one by one. Each emitted item is then transformed
 * by converting the text to uppercase using map.
 */
class RxExample4Fragment : DetailFragment() {

    private var layoutBinding: RxExampleLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: RxViewModel

    private val compositeDisposable = CompositeDisposable()

    override fun getTitle(): Int {
        return R.string.rx_example4_title
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

        getNotes()

        viewModel.message4.observe(viewLifecycleOwner) {
            binding?.labelRx?.text = it
        }
    }

    private fun getNotes() {
        compositeDisposable.add(viewModel.getNotesObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { note: RxViewModel.Note ->
                note.note = note.note.uppercase(Locale.getDefault())
                // return
                note
            }
            .subscribeWith(viewModel.getNotesDisposableObserver())
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        compositeDisposable.dispose()
    }
}