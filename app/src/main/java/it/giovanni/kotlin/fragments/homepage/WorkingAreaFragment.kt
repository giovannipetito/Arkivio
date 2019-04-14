package it.giovanni.kotlin.fragments.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.kotlin.fragments.HomeFragment
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.R
import kotlinx.android.synthetic.main.working_area_layout.*

class WorkingAreaFragment : HomeFragment() {

    var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.working_area_layout
    }

    override fun getTitle(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        var caller: MainFragment? = null
        fun newInstance(c: MainFragment): WorkingAreaFragment {
            caller = c
            return WorkingAreaFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onFragmentReady() {
        super.onFragmentReady()
        example_title_1.setOnClickListener {
            showPopupError(context!!.getString(R.string.app_name))
        }
    }
}