package it.giovanni.arkivio.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.viewinterfaces.IDetailFragment
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DetailLayoutBinding
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.viewinterfaces.IDarkMode

abstract class DetailFragment : BaseFragment(SectionType.DETAIL), IDetailFragment, IDarkMode.View {

    companion object {
        private var layoutBinding: DetailLayoutBinding? = null
        val detailLayoutBinding get() = layoutBinding
    }

    abstract fun searchAction(): Boolean
    abstract fun backAction(): Boolean
    abstract fun closeAction(): Boolean
    abstract fun deleteAction(): Boolean
    abstract fun editAction():Boolean
    abstract fun getActionTitle(): Int

    open fun beforeClosing(): Boolean {
        // Default DO NOTHING, return true if action is just elaborated!
        return true
    }

    // return data to caller fragment
    open fun getResultBack(): Intent {
        return Intent()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        layoutBinding = DetailLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        detailLayoutBinding?.temp = model
        detailLayoutBinding?.presenter = darkModePresenter

        detailLayoutBinding?.frameLayout?.addView(onCreateBindingView(inflater, detailLayoutBinding?.frameLayout, savedInstanceState))

        return detailLayoutBinding?.root
    }

    abstract fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentActivity.setStatusBarTransparent()

        // Manage arguments

        if (backAction())
            detailLayoutBinding?.arrowGoBack?.visibility = View.VISIBLE
        else
            detailLayoutBinding?.arrowGoBack?.visibility = View.GONE

        if (searchAction())
            detailLayoutBinding?.detailSearch?.visibility = View.VISIBLE
        else
            detailLayoutBinding?.detailSearch?.visibility = View.GONE
        detailLayoutBinding?.detailSearch?.setOnClickListener(searchClickListener)

        if (deleteAction())
            detailLayoutBinding?.detailTrash?.visibility = View.VISIBLE
        else
            detailLayoutBinding?.detailTrash?.visibility = View.GONE

        if (editAction())
            detailLayoutBinding?.editIcon?.visibility = View.VISIBLE
        else
            detailLayoutBinding?.editIcon?.visibility = View.GONE

        detailLayoutBinding?.editIcon?.setOnClickListener(editClickListener)

        if (closeAction()) {
            detailLayoutBinding?.arrowGoBack?.visibility = View.VISIBLE
            detailLayoutBinding?.arrowGoBack?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ico_close_rvd))
        }

        detailLayoutBinding?.arrowGoBack?.setOnClickListener(arrowGoBackClickListener)

        if (arguments != null && requireArguments().containsKey("link_deeplink")) {
            detailLayoutBinding?.detailTitle?.text = requireArguments().getString("link_deeplink")
        } else if (getTitle() != NO_TITLE) {
            detailLayoutBinding?.detailTitle?.text = getString(getTitle())
        }

        if (isRefreshEnabled()) {
            detailLayoutBinding?.swipeRefreshLayout?.setSwipeableChildren(R.id.frame_layout)
            detailLayoutBinding?.swipeRefreshLayout?.setOnRefreshListener {
                refresh()
            }
        }

        if (detailLayoutBinding?.swipeRefreshLayout != null) {
            detailLayoutBinding?.swipeRefreshLayout?.setColorSchemeResources(android.R.color.black)
        }

        if (getActionTitle() != NO_TITLE) {
            detailLayoutBinding?.actionLabel?.visibility = View.VISIBLE
            detailLayoutBinding?.actionLabel?.text = getString(getActionTitle())
            detailLayoutBinding?.actionLabel?.setOnClickListener {
                onActionClickListener()
            }
        } else {
            detailLayoutBinding?.actionLabel?.visibility = View.GONE
        }
    }

    open fun onActionClickListener() {}

    fun actionLabelState(state: Boolean) {
        when (state) {
            true -> {detailLayoutBinding?.actionLabel?.isEnabled = true}
            false -> {detailLayoutBinding?.actionLabel?.isEnabled = false}
        }
    }

    fun actionLabelText(label: String) {
        detailLayoutBinding?.actionLabel?.text = label
    }

    fun actionLabelVisibility(visibility: Int) {
        detailLayoutBinding?.actionLabel?.visibility = visibility
    }

    fun actionLabelClickListener(listener:View.OnClickListener) {
        detailLayoutBinding?.actionLabel?.setOnClickListener(listener)
    }

    private var arrowGoBackClickListener = View.OnClickListener {
        currentActivity.onBackPressed()
        hideSoftKeyboard()
    }

    fun isUserSearch(): Boolean {
        return (detailLayoutBinding?.searchInputText?.visibility == View.VISIBLE)
    }

    fun closeSearch() {
        detailLayoutBinding?.searchInputText?.visibility = View.GONE
        hideSoftKeyboard()
        detailLayoutBinding?.detailTitle?.visibility = View.VISIBLE
    }

    abstract fun editIconClick()

    private var editClickListener = View.OnClickListener {
        editIconClick()
    }

    private var searchClickListener = View.OnClickListener {

        if (detailLayoutBinding?.searchInputText?.visibility == View.GONE) {
            openSearch()
        } else {
            onActionSearch(detailLayoutBinding?.searchInputText?.text.toString())
        }
    }

    private fun openSearch() {
        detailLayoutBinding?.searchInputText?.visibility = View.VISIBLE
        detailLayoutBinding?.searchInputText?.requestFocus()
        showDetailSoftKeyboard()
        detailLayoutBinding?.detailTitle?.visibility = View.GONE
    }

    abstract fun onActionSearch(search_string: String)

    private fun showDetailSoftKeyboard() {
        val imm = currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(detailLayoutBinding?.searchInputText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun showSoftKeyboard(editText: EditText) {
        val imm = currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSoftKeyboard() {
        val imm = currentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentActivity.currentFocus?.windowToken, 0)
    }

    fun hideSoftKeyboard2() {
        val imm = currentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentActivity.currentFocus?.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    open fun isRefreshEnabled(): Boolean {
        return false
    }

    open fun refresh() {
    }

    fun stopSwipeRefresh() {
        if (detailLayoutBinding?.swipeRefreshLayout != null) {
            detailLayoutBinding?.swipeRefreshLayout?.isRefreshing = false
            detailLayoutBinding?.swipeRefreshLayout?.destroyDrawingCache()
            detailLayoutBinding?.swipeRefreshLayout?.clearAnimation()
        }
    }

    protected fun startSwipeRefresh() {
        if (detailLayoutBinding?.swipeRefreshLayout != null && !detailLayoutBinding?.swipeRefreshLayout?.isRefreshing!!)
            detailLayoutBinding?.swipeRefreshLayout?.isRefreshing = true
    }

    override fun hidePullToRefresh() {
        stopSwipeRefresh()
    }

    override fun isPullToRefreshEnabled(isEnabled: Boolean) {
        if (detailLayoutBinding?.swipeRefreshLayout != null) {
            detailLayoutBinding?.swipeRefreshLayout?.isEnabled = isEnabled
        }
    }

    override fun onShowDataModel(model: DarkModeModel?) {}

    override fun onSetLayout(model: DarkModeModel?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}