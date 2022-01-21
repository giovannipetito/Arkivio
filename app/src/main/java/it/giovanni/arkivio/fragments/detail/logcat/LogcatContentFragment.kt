package it.giovanni.arkivio.fragments.detail.logcat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.BaseFragment
import it.giovanni.arkivio.customview.MultiSwipeRefreshLayout
import kotlinx.android.synthetic.main.message_error_layout.*
import kotlinx.android.synthetic.main.logcat_tab_layout.*

class LogcatContentFragment : BaseFragment(SectionType.TAB_DETAIL) {

    private var viewFragment: View? = null

    private var tabDateFrom: String = ""
    private var tabDateTo: String = ""
    var position: Int = -1
    private lateinit var swipeRefreshLayout: MultiSwipeRefreshLayout

    override fun getLayout(): Int {
        return NO_LAYOUT
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    companion object {
        var DATE_FROM: String = "DATE_FROM"
        var DATE_TO: String = "DATE_TO"
        var POSITION: String = "POSITION"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)

        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            position = requireArguments().getInt(POSITION)
            tabDateFrom = requireArguments().getString(DATE_FROM)!!
            tabDateTo = requireArguments().getString(DATE_TO)!!
            tabDateTo = "$tabDateTo 23:59:59"
        }

        error_smile.setOnClickListener {
            loadData()
        }

        swipeRefreshLayout = viewFragment?.findViewById(R.id.logcat_swipe_refresh_layout) as MultiSwipeRefreshLayout
        swipeRefreshLayout.setSwipeableChildren(R.id.logcat_tab_container)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        if (position == 0) {
            loadData()
        }
    }

    private fun loadData() {
        hideErrorSmile()
    }

    private fun loadErrorSmileData() {
        showErrorSmile("Tap on me!")
    }

    private fun hideErrorSmile() {
        error_smile.visibility = View.GONE
    }

    private fun showErrorSmile(message: String) {
        error_message_smile.text = message
        error_smile.visibility = View.VISIBLE
        stopSwipeRefresh()
    }

    private fun refresh() {
        loadErrorSmileData()
    }

    private fun stopSwipeRefresh() {
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.destroyDrawingCache()
        swipeRefreshLayout.clearAnimation()
    }
}