package it.giovanni.kotlin.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import it.giovanni.kotlin.interfaces.IProgressLoader
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.*
import it.giovanni.kotlin.fragments.detail.DateManagerFragment
import it.giovanni.kotlin.fragments.detail.LogcatFragment
import it.giovanni.kotlin.fragments.detail.rubrica.RubricaDetailFragment
import it.giovanni.kotlin.fragments.detail.rubrica.RubricaHomeFragment
import it.giovanni.kotlin.fragments.detail.rubrica.RubricaListFragment
import it.giovanni.kotlin.fragments.detail.webview.WebViewFragment
import it.giovanni.kotlin.utils.Globals
import it.giovanni.kotlin.utils.UserFactory
import it.giovanni.kotlin.utils.Utils

class MainActivity : BaseActivity(), IProgressLoader {

    private val SPLASH_DISPLAY_TIME: Long = 3000
    private val DELAY_TIME: Long = 3000
    private var progressDialog: Dialog? = null
    private var spinnerLogo: ImageView? = null
    private var spinnerAnimation: AnimationDrawable? = null

    private var HOME_FRAGMENT: String = "HOME_FRAGMENT"
    private var LOGIN_FRAGMENT: String = "LOGIN_FRAGMENT"
    private var SPLASH_FRAGMENT: String = "SPLASH_FRAGMENT"

    companion object {
        var running = false
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = Dialog(this, R.style.DialogTheme)
        progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val view = LayoutInflater.from(applicationContext).inflate(R.layout.progress_dialog_custom, null)

        spinnerLogo = view.findViewById(R.id.spinnerImageView) as ImageView
        progressDialog?.setContentView(view)
        progressDialog?.setCancelable(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_container, SplashFragment(), SPLASH_FRAGMENT)
                .commit()

            Handler().postDelayed({
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(
                        R.id.frame_container,
                        LoginFragment(), LOGIN_FRAGMENT)
                    .commit()

            }, SPLASH_DISPLAY_TIME)
        }
    }

    private fun removeAllFragmentsToMainFragment() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null && fragment !is MainFragment) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        running = true
    }

    override fun onStop() {
        super.onStop()
        running = false
    }

    override fun showProgressDialog() {
        if (!progressDialog!!.isShowing) {
            progressDialog?.show()
            spinnerAnimation = (spinnerLogo?.background as AnimationDrawable)
            spinnerLogo?.post {
                try {
                    spinnerAnimation!!.start()
                } catch (e: Exception) {
                }
            }
        }
    }

    // Questo è il metodo hideProgressDialog originale, non quello col metodo postDelayed.
//    override fun hideProgressDialog() {
//        try {
//            progressDialog?.dismiss()
//            if (spinnerAnimation != null)
//                spinnerAnimation?.stop()
//        } catch (e: Exception) {
//        }
//    }

    override fun hideProgressDialog() {
        Handler().postDelayed({
            try {
                progressDialog?.dismiss()
                if (spinnerAnimation != null)
                    spinnerAnimation?.stop()
            } catch (e: Exception) {
            }
        }, DELAY_TIME)
    }

    fun openHomeFragment() {
        removeAllFragments()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.right_to_left, R.anim.left_to_right)
            .add(R.id.frame_container, MainFragment(), HOME_FRAGMENT)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragment = supportFragmentManager.findFragmentById(R.id.frame_container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    private fun removeAllFragments() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
    }

    fun logout() {
        removeAllFragments()
        val loginFragment = LoginFragment()
        loginFragment.arguments = Bundle()
        UserFactory.getInstance().clear()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.frame_container, loginFragment, LOGIN_FRAGMENT)
            .commit()
    }

    override fun openDetail(detailType: String, extraParams: Bundle?) {
        openDetail(detailType, extraParams, null, null)
    }

    override fun openDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {

        var baseFragment: BaseFragment? = null

        when (detailType) {

            Globals.LOGCAT_PROJECTS -> {
                baseFragment = LogcatFragment()
            }
            Globals.DATE_MANAGER -> {
                baseFragment = DateManagerFragment()
            }
            Globals.RUBRICA_HOME -> {
                baseFragment = RubricaHomeFragment()
            }
            Globals.RUBRICA_LIST -> {
                baseFragment = RubricaListFragment()
            }
            Globals.RUBRICA_DETAIL -> {
                baseFragment = RubricaDetailFragment()
            }
            Globals.WEB_VIEW -> {
                baseFragment = WebViewFragment()
            }
        }

        if (baseFragment != null) {
            if (supportFragmentManager.findFragmentByTag(baseFragment.javaClass.canonicalName) != null) {
                return
            }
            if (caller != null && requestCode != null) {
                baseFragment.setTargetFragment(caller, requestCode)
            }

            if (extraParams != null) {
                baseFragment.arguments = extraParams
            }
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.right_to_left, R.anim.left_to_right)
                .add(R.id.frame_container, baseFragment, baseFragment.javaClass.canonicalName)
                .commit()
        }
    }

    override fun onBackPressed() {
        val frameLayout: FrameLayout = findViewById(R.id.frame_container)
        val baseFragment: BaseFragment = (supportFragmentManager.findFragmentById(frameLayout.id) as BaseFragment)
        if (supportFragmentManager.findFragmentById(frameLayout.id) is DetailFragment) {
            if (!(baseFragment as DetailFragment).isUserSearch() && !(baseFragment).closeAction()) {

                fragmentsTransition(baseFragment)

            } else if ((baseFragment).closeAction()) {
                if ((baseFragment).beforeClosing()) {
                    fragmentsTransition(baseFragment)
                }
            } else {
                (baseFragment).closeSearch()
            }
        } else
            super.onBackPressed()
    }

    private fun fragmentsTransition(baseFragmentView: DetailFragment) {
        if (baseFragmentView.targetFragment != null) {
            baseFragmentView.targetFragment!!.onActivityResult(
                baseFragmentView.targetRequestCode,
                Activity.RESULT_OK,
                baseFragmentView.getResultBack()
            )
        }
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.popBackStackImmediate(baseFragmentView.javaClass.name, 1)
        transaction.setCustomAnimations(
            R.anim.out_left_to_right,
            R.anim.out_right_to_left
        )
        transaction.remove(baseFragmentView).commit()
    }

    fun openApp(packageName: String) {
        Utils.openApp(this, packageName)
    }

    fun openBrowser(url: String) {
        Utils.openBrowser(this, url)
    }
}