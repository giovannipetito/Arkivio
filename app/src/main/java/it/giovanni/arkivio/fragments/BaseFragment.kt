package it.giovanni.arkivio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.giovanni.arkivio.R
import it.giovanni.arkivio.activities.MainActivity
import it.giovanni.arkivio.customview.dialog.CoreDialog
import it.giovanni.arkivio.utils.SharedPreferencesManager.loadDarkModeStateFromPreferences
import it.giovanni.arkivio.viewinterfaces.IProgressLoader

abstract class BaseFragment(private var sectionType: SectionType) : Fragment() {

    lateinit var currentActivity: MainActivity

    protected var errorDialog: CoreDialog? = null

    var isDarkMode = false

    companion object {
        var NO_TITLE: Int = -1
    }

    abstract fun getTitle(): Int

    // Define layout
    enum class SectionType {
        SPLASH,
        LOGIN,
        MAIN,
        HOME,
        DETAIL,
        TAB_DETAIL,
        DIALOG_FLOW
    }

    fun showErrorDialog(msg: String) {
        errorDialog = CoreDialog(currentActivity, R.style.CoreDialogTheme)
        errorDialog?.setCancelable(false)
        errorDialog?.setTitle("")
        errorDialog?.setMessage(msg)
        errorDialog?.setButtons(resources.getString(R.string.button_ok)
        ) {
            errorDialog?.dismiss()
        }
        errorDialog?.show()
    }

    fun showErrorDialog(msg: String, clickListener: View.OnClickListener) {
        errorDialog = CoreDialog(currentActivity, R.style.CoreDialogTheme)
        errorDialog?.setCancelable(false)
        errorDialog?.setTitle("")
        errorDialog?.setMessage(msg)
        errorDialog?.setButtons(resources.getString(R.string.button_ok), clickListener)
        errorDialog?.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        isDarkMode = loadDarkModeStateFromPreferences()

        if (isDarkMode)
            currentActivity.setTheme(R.style.AppTheme)
        else
            currentActivity.setTheme(R.style.AppThemeLight)

        val view: View?
        when (sectionType) {
            SectionType.MAIN -> {
                view = inflater.inflate(R.layout.main_layout, container, false)
            }
            SectionType.TAB_DETAIL -> {
                view = inflater.inflate(R.layout.logcat_tab_layout, container, false)
            }
            SectionType.HOME -> {
                view = inflater.inflate(R.layout.home_layout, container, false)
            }
            SectionType.DETAIL -> {
                view = inflater.inflate(R.layout.detail_layout, container, false)
            }
            SectionType.SPLASH -> {
                view = inflater.inflate(R.layout.splash_layout, container, false)
            }
            SectionType.LOGIN -> {
                view = inflater.inflate(R.layout.login_layout, container, false)
            }
            SectionType.DIALOG_FLOW -> {
                view = inflater.inflate(R.layout.dialog_flow_layout, container, false)
            }
        }
        return view
    }

    fun getSectionType(): SectionType {
        return this.sectionType
    }

    fun showProgressDialog() {
        (currentActivity as IProgressLoader).showProgressDialog()
    }

    fun hideProgressDialog() {
        (currentActivity as IProgressLoader).hideProgressDialog()
    }

    fun isSafe(): Boolean {
        return !(isRemoving || activity == null || isDetached || !isAdded || view == null)
    }
}