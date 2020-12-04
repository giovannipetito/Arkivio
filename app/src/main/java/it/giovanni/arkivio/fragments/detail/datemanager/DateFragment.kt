package it.giovanni.arkivio.fragments.detail.datemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.resetSelectedDays
import kotlinx.android.synthetic.main.date_layout.*

class DateFragment : DetailFragment() {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.date_layout
    }

    override fun getTitle(): Int {
        return R.string.date_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        label_date_format.setOnClickListener {
            currentActivity.openDetail(Globals.DATE_FORMAT, null)
        }
        label_date_picker.setOnClickListener {
            currentActivity.openDetail(Globals.DATE_PICKER, null)
        }
        label_calendarview_horizontal.setOnClickListener {
            currentActivity.openDetail(Globals.CALENDARVIEW_HORIZONTAL, null)
        }
        label_calendarview_vertical.setOnClickListener {
            currentActivity.openDetail(Globals.CALENDARVIEW_VERTICAL, null)
        }
        label_smartworking.setOnClickListener {
            currentActivity.openDetail(Globals.SMARTWORKING, null)
        }
        reset_selected_days.setOnClickListener {
            resetSelectedDays()
        }
    }
}