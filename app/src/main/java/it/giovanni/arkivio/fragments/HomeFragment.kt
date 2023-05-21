package it.giovanni.arkivio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.databinding.HomeLayoutBinding
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.viewinterfaces.IDarkMode

abstract class HomeFragment : BaseFragment(SectionType.HOME), IDarkMode.View {

    private var layoutBinding: HomeLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        layoutBinding = HomeLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.temp = model
        binding?.presenter = darkModePresenter

        binding?.frameLayout?.addView(onCreateBindingView(inflater, binding?.frameLayout, savedInstanceState))

        return binding?.root
    }

    abstract fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    override fun onShowDataModel(model: DarkModeModel?) {}

    override fun onSetLayout(model: DarkModeModel?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}