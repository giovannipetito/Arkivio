package it.giovanni.kotlin.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import it.giovanni.kotlin.interfaces.IDetailFragment
import it.giovanni.kotlin.R
import kotlinx.android.synthetic.main.detail_layout.*

abstract class DetailFragment : BaseFragment(SectionType.DETAIL),
    IDetailFragment {

    abstract fun getLayout(): Int
    abstract fun searchAction(): Boolean
    abstract fun backAction(): Boolean
    abstract fun closeAction(): Boolean
    abstract fun deleteAction(): Boolean
    abstract fun editAction():Boolean

    open fun beforeClosing(): Boolean {
        // Default DO NOTHING, return true if action is just elaborated!
        return true
    }

    // return data to caller fragment
    open fun getResultBack(): Intent {
        return Intent()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        view!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // manage arguments

                if (backAction())
                    arrow_go_back.visibility = View.VISIBLE
                else
                    arrow_go_back.visibility = View.GONE

                if (searchAction())
                    detail_search.visibility = View.VISIBLE
                else
                    detail_search.visibility = View.GONE
                detail_search.setOnClickListener(searchClickListener)

                if (deleteAction())
                    detail_trash.visibility = View.VISIBLE
                else
                    detail_trash.visibility = View.GONE

                if(editAction())
                    edit_icon.visibility = View.VISIBLE
                else
                    edit_icon.visibility = View.GONE

                edit_icon.setOnClickListener(editClickListener)

                if (closeAction()) {
                    arrow_go_back.visibility = View.VISIBLE
                    arrow_go_back.setImageDrawable(ContextCompat.getDrawable(context!!,
                        R.drawable.ico_close_rvd
                    ))
                }

                arrow_go_back.setOnClickListener(arrowGoBackClickListener)

                if (getTitle() != -1) {
                    detail_title.text = getString(getTitle())
                }

                if (getLayout() != -1) {
                    val customLayout = LayoutInflater.from(context).inflate(getLayout(), null, false)
                    frame_layout.addView(customLayout)
                }

                onFragmentReady()

                if (isRefreshEnabled()) {
                    swipeRefreshLayout.setSwipeableChildren(R.id.frame_layout)
                    swipeRefreshLayout!!.setOnRefreshListener {
                        refresh()
                    }
                }
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout!!.setColorSchemeResources(android.R.color.black)
        }
    }

    override fun onFragmentReady() {
    }

    protected var arrowGoBackClickListener = View.OnClickListener {
        currentActivity.onBackPressed()
        hideSoftKeyboard()
    }

    fun isUserSearch(): Boolean {
        return (search_input_text.visibility == View.VISIBLE)
    }

    fun closeSearch() {
        search_input_text.visibility = View.GONE
        hideSoftKeyboard()
        detail_title.visibility = View.VISIBLE
    }

    abstract fun editIconClick()

    private var editClickListener = View.OnClickListener {
        editIconClick()
    }

    private var searchClickListener = View.OnClickListener {

        if (search_input_text.visibility == View.GONE) {
            openSearch()
        } else {
            //search!
            onActionSearch(search_input_text.text.toString())
        }
    }

    private fun openSearch() {
        search_input_text.visibility = View.VISIBLE
        search_input_text.requestFocus()
        showSoftKeyboard()
        detail_title.visibility = View.GONE
    }

    abstract fun onActionSearch(search_string: String)

    fun hideSoftKeyboard() {
        val imm = currentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentActivity.currentFocus?.windowToken, 0)
    }

    fun showSoftKeyboard() {
        val imm = currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(search_input_text, InputMethodManager.SHOW_IMPLICIT)
    }

    open fun isRefreshEnabled(): Boolean {
        return false
    }

    open fun refresh() {
    }

    fun stopSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout!!.isRefreshing = false
            swipeRefreshLayout!!.destroyDrawingCache()
            swipeRefreshLayout!!.clearAnimation()
        }
    }

    protected fun startSwipeRefresh() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout!!.isRefreshing())
            swipeRefreshLayout!!.isRefreshing = true
    }

    override fun hidePullToRefresh() {
        stopSwipeRefresh()
    }

    override fun isPullToRefreshEnabled(isEnabled: Boolean) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.isEnabled = isEnabled
        }
    }
}