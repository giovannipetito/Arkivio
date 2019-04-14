package it.giovanni.kotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.detail_layout.*

abstract class HomeFragment : BaseFragment(SectionType.HOME) {

    abstract fun getLayout(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        view!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // manage arguments
                if (getLayout() != -1) {
                    val customLayout = LayoutInflater.from(context).inflate(getLayout(), null, false)
                    frame_layout.addView(customLayout)
                }

                onFragmentReady()
            }
        })
        return view
    }
}