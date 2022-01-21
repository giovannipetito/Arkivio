package it.giovanni.arkivio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.databinding.HomeLayoutBinding
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.viewinterfaces.IDarkMode
import kotlinx.android.synthetic.main.home_layout.*

abstract class HomeFragment : BaseFragment(SectionType.HOME), IDarkMode.View {

    private var layoutBinding: HomeLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getLayout(): Int {
        return NO_LAYOUT
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // ----- DATA BINDING ----- //
        layoutBinding = HomeLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.temp = model
        binding?.presenter = darkModePresenter

        if (getLayout() == NO_LAYOUT)
            binding?.frameLayout?.addView(onCreateBindingView(inflater, binding?.frameLayout, savedInstanceState))
        // ------------------------ //

        return binding?.root
    }

    abstract fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // manage arguments
        if (getLayout() != NO_LAYOUT) {
            val customLayout = LayoutInflater.from(context).inflate(getLayout(), null, false)
            frame_layout.addView(customLayout)
        }
    }

    override fun onShowDataModel(model: DarkModeModel?) {}

    override fun onSetLayout(model: DarkModeModel?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}