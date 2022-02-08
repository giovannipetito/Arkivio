package it.giovanni.arkivio.fragments.detail.logcat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.BaseFragment
import it.giovanni.arkivio.customview.MultiSwipeRefreshLayout
import it.giovanni.arkivio.databinding.LogcatTabLayoutBinding

class LogcatContentFragment : BaseFragment(SectionType.TAB_DETAIL) {

    private var layoutBinding: LogcatTabLayoutBinding? = null
    private val binding get() = layoutBinding

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
        layoutBinding = LogcatTabLayoutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            position = requireArguments().getInt(POSITION)
            tabDateFrom = requireArguments().getString(DATE_FROM)!!
            tabDateTo = requireArguments().getString(DATE_TO)!!
            tabDateTo = "$tabDateTo 23:59:59"
        }

        binding?.includeErrorSmile?.errorSmile?.setOnClickListener {
            loadData()
        }

        swipeRefreshLayout = binding?.logcatSwipeRefreshLayout!!
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
        binding?.includeErrorSmile?.errorSmile?.visibility = View.GONE
    }

    private fun showErrorSmile(message: String) {
        binding?.includeErrorSmile?.errorMessageSmile?.text = message
        binding?.includeErrorSmile?.errorSmile?.visibility = View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}