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
        val mBinding get() = layoutBinding
    }

    abstract fun getLayout(): Int
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

        // ----- DATA BINDING ----- //
        layoutBinding = DetailLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        mBinding?.temp = model
        mBinding?.presenter = darkModePresenter

        if (getLayout() == NO_LAYOUT)
            mBinding?.frameLayout?.addView(onCreateBindingView(inflater, mBinding?.frameLayout, savedInstanceState))
        // ------------------------ //

        return mBinding?.root
    }

    abstract fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentActivity.setStatusBarTransparent()

        // Manage arguments

        if (backAction())
            mBinding?.arrowGoBack?.visibility = View.VISIBLE
        else
            mBinding?.arrowGoBack?.visibility = View.GONE

        if (searchAction())
            mBinding?.detailSearch?.visibility = View.VISIBLE
        else
            mBinding?.detailSearch?.visibility = View.GONE
        mBinding?.detailSearch?.setOnClickListener(searchClickListener)

        if (deleteAction())
            mBinding?.detailTrash?.visibility = View.VISIBLE
        else
            mBinding?.detailTrash?.visibility = View.GONE

        if (editAction())
            mBinding?.editIcon?.visibility = View.VISIBLE
        else
            mBinding?.editIcon?.visibility = View.GONE

        mBinding?.editIcon?.setOnClickListener(editClickListener)

        if (closeAction()) {
            mBinding?.arrowGoBack?.visibility = View.VISIBLE
            mBinding?.arrowGoBack?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ico_close_rvd))
        }

        mBinding?.arrowGoBack?.setOnClickListener(arrowGoBackClickListener)

        if (arguments != null && requireArguments().containsKey("link_deeplink")) {
            mBinding?.detailTitle?.text = requireArguments().getString("link_deeplink")
        } else if (getTitle() != NO_TITLE) {
            mBinding?.detailTitle?.text = getString(getTitle())
        }

        if (getLayout() != NO_LAYOUT) {
            val customLayout = LayoutInflater.from(context).inflate(getLayout(), null, false)
            mBinding?.frameLayout?.addView(customLayout)
        }

        if (isRefreshEnabled()) {
            mBinding?.swipeRefreshLayout?.setSwipeableChildren(R.id.frame_layout)
            mBinding?.swipeRefreshLayout?.setOnRefreshListener {
                refresh()
            }
        }

        if (mBinding?.swipeRefreshLayout != null) {
            mBinding?.swipeRefreshLayout?.setColorSchemeResources(android.R.color.black)
        }

        if (getActionTitle() != NO_TITLE) {
            mBinding?.actionLabel?.visibility = View.VISIBLE
            mBinding?.actionLabel?.text = getString(getActionTitle())
            mBinding?.actionLabel?.setOnClickListener {
                onActionClickListener()
            }
        } else {
            mBinding?.actionLabel?.visibility = View.GONE
        }
    }

    open fun onActionClickListener() {}

    fun actionLabelState(state: Boolean) {
        when (state) {
            true -> {mBinding?.actionLabel?.isEnabled = true}
            false -> {mBinding?.actionLabel?.isEnabled = false}
        }
    }

    fun actionLabelText(label: String) {
        mBinding?.actionLabel?.text = label
    }

    fun actionLabelVisibility(visibility: Int) {
        mBinding?.actionLabel?.visibility = visibility
    }

    fun actionLabelClickListener(listener:View.OnClickListener) {
        mBinding?.actionLabel?.setOnClickListener(listener)
    }

    private var arrowGoBackClickListener = View.OnClickListener {
        currentActivity.onBackPressed()
        hideSoftKeyboard()
    }

    fun isUserSearch(): Boolean {
        return (mBinding?.searchInputText?.visibility == View.VISIBLE)
    }

    fun closeSearch() {
        mBinding?.searchInputText?.visibility = View.GONE
        hideSoftKeyboard()
        mBinding?.detailTitle?.visibility = View.VISIBLE
    }

    abstract fun editIconClick()

    private var editClickListener = View.OnClickListener {
        editIconClick()
    }

    private var searchClickListener = View.OnClickListener {

        if (mBinding?.searchInputText?.visibility == View.GONE) {
            openSearch()
        } else {
            // Search!
            onActionSearch(mBinding?.searchInputText?.text.toString())
        }
    }

    private fun openSearch() {
        mBinding?.searchInputText?.visibility = View.VISIBLE
        mBinding?.searchInputText?.requestFocus()
        showDetailSoftKeyboard()
        mBinding?.detailTitle?.visibility = View.GONE
    }

    abstract fun onActionSearch(search_string: String)

    private fun showDetailSoftKeyboard() {
        val imm = currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mBinding?.searchInputText, InputMethodManager.SHOW_IMPLICIT)
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
        if (mBinding?.swipeRefreshLayout != null) {
            mBinding?.swipeRefreshLayout?.isRefreshing = false
            mBinding?.swipeRefreshLayout?.destroyDrawingCache()
            mBinding?.swipeRefreshLayout?.clearAnimation()
        }
    }

    protected fun startSwipeRefresh() {
        if (mBinding?.swipeRefreshLayout != null && !mBinding?.swipeRefreshLayout?.isRefreshing!!)
            mBinding?.swipeRefreshLayout?.isRefreshing = true
    }

    override fun hidePullToRefresh() {
        stopSwipeRefresh()
    }

    override fun isPullToRefreshEnabled(isEnabled: Boolean) {
        if (mBinding?.swipeRefreshLayout != null) {
            mBinding?.swipeRefreshLayout?.isEnabled = isEnabled
        }
    }

    override fun onShowDataModel(model: DarkModeModel?) {}

    override fun onSetLayout(model: DarkModeModel?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}