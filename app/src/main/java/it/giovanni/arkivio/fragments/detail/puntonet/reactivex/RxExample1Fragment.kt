package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
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

    private lateinit var viewModel: RxViewModel

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

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RxViewModel::class.java]

        loadData()

        viewModel.message1.observe(viewLifecycleOwner) {
            binding?.labelRx?.text = it
        }
    }

    /*
    - subscribeOn(Schedulers.io()) indica all'observable di eseguire il task su un thread in background.
    - observeOn(AndroidSchedulers.mainThread()) indica all'observer di ricevere i dati sul thread della
      UI in modo da poter eseguire qualsiasi azione relativa alla UI.
    */
    private fun loadData() {
        viewModel.getAnimalsObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(viewModel.getAnimalsObserver(1))
    }

    // This method is called when the fragment's view is destroyed. It clears the layoutBinding
    // reference and disposes of the subscription by calling dispose() on the disposable.
    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        viewModel.disposable?.dispose()
    }
}