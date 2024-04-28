package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.userinput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.UserInputLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

/**
 * Example of MVVM pattern without fetching data from an API, but from EditText user input.
 *
 * UserInputFragment class will bind the view to the ViewModel and handle user input. We're using
 * the ViewModelProvider to get an instance of the UserInputViewModel class. We're also binding the
 * EditText, Button, and TextView views to the corresponding variables, and setting a click listener
 * on the buttonCalculate that will update the LiveData object with the user input and call the
 * calculate() method on the ViewModel.
 *
 * Finally, we're observing the result LiveData object and updating the labelResult with the result.
 */
class UserInputFragment : DetailFragment() {

    private var layoutBinding: UserInputLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: UserInputViewModel

    override fun getTitle(): Int {
        return R.string.user_input_title
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
        layoutBinding = UserInputLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[UserInputViewModel::class.java]

        binding?.buttonCalculate?.setOnClickListener {
            viewModel._number.value = binding?.editInsert?.text.toString().toIntOrNull()
            viewModel.calculate()
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            binding?.labelResult?.text = result
        }

        if (isDarkMode)
            binding?.buttonCalculate?.style(R.style.ButtonNormalDarkMode)
        else
            binding?.buttonCalculate?.style(R.style.ButtonNormalLightMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}