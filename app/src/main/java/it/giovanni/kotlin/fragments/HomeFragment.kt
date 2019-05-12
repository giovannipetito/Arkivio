package it.giovanni.kotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.detail_layout.*

abstract class HomeFragment : BaseFragment(SectionType.HOME) {

    abstract fun getLayout(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // manage arguments
        if (getLayout() != -1) {
            val customLayout = LayoutInflater.from(context).inflate(getLayout(), null, false)
            frame_layout.addView(customLayout)
        }
    }
}