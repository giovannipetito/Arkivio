package it.giovanni.arkivio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.HomeLayoutBinding
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.viewinterfaces.IDarkMode
import kotlinx.android.synthetic.main.home_layout.*

abstract class HomeFragment : BaseFragment(SectionType.HOME), IDarkMode.View {

    abstract fun getLayout(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // ----- DATA BINDING ----- //
        val binding: HomeLayoutBinding? = DataBindingUtil.inflate(inflater, R.layout.home_layout, container, false)
        val view = binding?.root

        val darkModePresenter = DarkModePresenter(this, context!!)
        val model = DarkModeModel(context!!)
        binding?.temp = model
        binding?.presenter = darkModePresenter

        if (getLayout() == NO_LAYOUT)
            binding?.frameLayout?.addView(onCreateBindingView(inflater, binding.frameLayout, savedInstanceState))
        // ------------------------ //

        return view
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
}