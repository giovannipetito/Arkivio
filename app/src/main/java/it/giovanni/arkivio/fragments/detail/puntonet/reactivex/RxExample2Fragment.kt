package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RxExampleLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

/**
 * This class demonstrates how to create an observable that emits a sequence of animal names
 * and apply the filter operator to filter out items that meet a specific condition.
 */
class RxExample2Fragment : DetailFragment() {

    private var layoutBinding: RxExampleLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: RxViewModel

    override fun getTitle(): Int {
        return R.string.rx_example2_title
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

        viewModel = ViewModelProvider(requireActivity())[RxViewModel::class.java]

        loadFilteredData()

        viewModel.message.observe(viewLifecycleOwner) {
            binding?.labelRx?.text = it
        }
    }

    /*
    - The filter operator is applied to the observable and filters out only the items that satisfy
      the given condition. In this case, it checks if the lowercase version of the string starts
      with the letter "b". The filtered items are passed down the observable chain.
    */
    private fun loadFilteredData() {
        viewModel.observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { string: String ->
                Log.i("RX", "string: $string")
                string.lowercase().startsWith("b")
            }
            .subscribe(viewModel.observer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        viewModel.disposable?.dispose()
    }
}