package it.giovanni.arkivio.fragments.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.HomeFragment
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.utils.Globals
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

        var oldPostion = 0

        if (scroll_view != null) {
            scroll_view.viewTreeObserver.addOnScrollChangedListener {
                if (scroll_view != null) {
                    if (scroll_view.scrollY > oldPostion) {
                        fab.hide()
                    } else if (scroll_view.scrollY < oldPostion || scroll_view.scrollY <= 0) {
                        fab.show()
                    }
                    oldPostion = scroll_view.scrollY
                }
            }
        }

        label_logcat_projects.setOnClickListener {
            currentActivity.openDetail(Globals.LOGCAT_PROJECTS, null)
        }
        label_date.setOnClickListener {
            currentActivity.openDetail(Globals.DATE, null)
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
        label_machine_learning.setOnClickListener {
            currentActivity.openDetail(Globals.MACHINE_LEARNING, null)
        }
    }
}