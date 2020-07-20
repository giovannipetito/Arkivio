package it.giovanni.kotlin.fragments.detail.logcat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.BaseFragment
import it.giovanni.kotlin.customview.MultiSwipeRefreshLayout
import kotlinx.android.synthetic.main.message_error_detail_layout.*
import kotlinx.android.synthetic.main.working_area_tab_detail.*

class ContentFragment : BaseFragment(SectionType.TAB_DETAIL) {

    private var viewFragment: View? = null
    private var tabDateFrom: String = ""
    private var tabDateTo: String = ""
    var position: Int = -1
    private lateinit var swipeRefreshLayout: MultiSwipeRefreshLayout

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
            position = arguments!!.getInt(POSITION)
            tabDateFrom = arguments?.getString(DATE_FROM)!!
            tabDateTo = arguments?.getString(DATE_TO)!!
            tabDateTo = "$tabDateTo 23:59:59"
        }

        smile_cries_detail_layout.setOnClickListener {
            loadData()
        }

        swipeRefreshLayout = viewFragment?.findViewById(R.id.swipe_refresh_layout) as MultiSwipeRefreshLayout
        swipeRefreshLayout.setSwipeableChildren(R.id.working_area_tab_detail_container)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        if (position == 0) {
            loadData()
        }
    }

    private fun loadData() {
        hideSmileCry()
    }

    private fun loadSmileCryData() {
        showSmileCry("Tap on me!")
    }

    private fun hideSmileCry() {
        smile_cries_detail_layout.visibility = View.GONE
    }

    private fun showSmileCry(message: String) {
        smile_message_detail.text = message
        smile_cries_detail_layout.visibility = View.VISIBLE
        stopSwipeRefresh()
    }

    private fun refresh() {
        loadSmileCryData()
    }

    @Suppress("DEPRECATION")
    fun stopSwipeRefresh() {
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.destroyDrawingCache()
        swipeRefreshLayout.clearAnimation()
    }
}