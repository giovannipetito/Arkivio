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

        label_logcat_projects.setOnClickListener {
            currentActivity.openDetail(Globals.LOGCAT_PROJECTS, null)
        }
        label_date_manager.setOnClickListener {
            currentActivity.openDetail(Globals.DATE_MANAGER, null)
        }
        label_rubrica.setOnClickListener {
            currentActivity.openDetail(Globals.RUBRICA_HOME, null)
        }
        label_permissions.setOnClickListener {
            currentActivity.openDetail(Globals.PERMISSIONS, null)
        }
        label_oauth_2.setOnClickListener {
            currentActivity.openDetail(Globals.OAUTH_2, null)
        }
        label_layout_manager.setOnClickListener {
            currentActivity.openDetail(Globals.LAYOUT_MANAGER, null)
        }
        label_preference.setOnClickListener {
            currentActivity.openDetail(Globals.PREFERENCE, null)
        }
        label_sticky_header.setOnClickListener {
            currentActivity.openDetail(Globals.STICKY_HEADER, null)
        }
        label_card_io.setOnClickListener {
            currentActivity.openDetail(Globals.CARD_IO, null)
        }
        label_youtube.setOnClickListener {
            currentActivity.openDetail(Globals.YOUTUBE_MANAGER, null)
        }
        label_fonts.setOnClickListener {
            currentActivity.openDetail(Globals.FONTS, null)
        }
        label_notification.setOnClickListener {
            currentActivity.openDetail(Globals.NOTIFICATION, null)
        }
        label_nearby.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY, null)
        }
    }
}