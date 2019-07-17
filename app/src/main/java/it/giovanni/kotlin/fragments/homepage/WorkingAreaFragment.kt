package it.giovanni.kotlin.fragments.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.kotlin.fragments.HomeFragment
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.R
import it.giovanni.kotlin.utils.Globals
import kotlinx.android.synthetic.main.working_area_layout.*

class WorkingAreaFragment : HomeFragment() {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.working_area_layout
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    companion object {
        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): WorkingAreaFragment {
            caller = c
            return WorkingAreaFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        label_logcat.setOnClickListener {
            currentActivity.openDetail(Globals.LOGCAT_PROJECTS, null)
        }
        label_date_manager.setOnClickListener {
            currentActivity.openDetail(Globals.DATE_MANAGER, null)
        }
        label_rubrica.setOnClickListener {
            currentActivity.openDetail(Globals.RUBRICA_HOME, null)
        }
        label_pdf.setOnClickListener {
            currentActivity.openDetail(Globals.PDF, null)
        }
        label_oauth_2.setOnClickListener {
            currentActivity.openDetail(Globals.OAUTH_2, null)
        }
        label_layout_manage.setOnClickListener {
            currentActivity.openDetail(Globals.LAYOUT_MANAGE, null)
        }
        label_user_preference.setOnClickListener {
            currentActivity.openDetail(Globals.USER_PREFERENCE, null)
        }
    }
}