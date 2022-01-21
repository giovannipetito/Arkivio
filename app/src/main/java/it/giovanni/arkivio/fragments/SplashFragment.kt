package it.giovanni.arkivio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.databinding.SplashLayoutBinding

class SplashFragment : BaseFragment(SectionType.SPLASH) {

    private var layoutBinding: SplashLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getLayout(): Int {
        return NO_LAYOUT
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = SplashLayoutBinding.inflate(inflater, container, false)

        currentActivity.setStatusBarTransparent()

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}