package it.giovanni.kotlin.fragments.homepage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.HomeFragment
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.fragments.detail.webview.WebViewActivity
import it.giovanni.kotlin.utils.Globals
import it.giovanni.kotlin.utils.Utils
import kotlinx.android.synthetic.main.link_area_layout.*

class LinkAreaFragment : HomeFragment() {

    private var viewFragment: View? = null
    private var bundleW3B: Bundle = Bundle()
    private var bundleWAW3: Bundle = Bundle()
    private var bundleGitHub: Bundle = Bundle()
    private var bundleVideoTest: Bundle = Bundle()

    override fun getLayout(): Int {
        return R.layout.link_area_layout
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    companion object {
        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): LinkAreaFragment {
            caller = c
            return LinkAreaFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundleW3B.putInt("link_drive_w3b", R.string.link_drive_w3b)
        bundleW3B.putString("url_drive_w3b", resources.getString(R.string.url_drive_w3b))

        bundleWAW3.putInt("link_drive_waw3", R.string.link_drive_waw3)
        bundleWAW3.putString("url_drive_waw3", resources.getString(R.string.url_drive_waw3))

        bundleGitHub.putInt("link_github", R.string.link_github)
        bundleGitHub.putString("url_github", resources.getString(R.string.url_github))

        bundleVideoTest.putString("link_video_test", resources.getString(R.string.link_video_test))
        bundleVideoTest.putString("url_video_test", resources.getString(R.string.url_video_test))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webview_drive_w3b.setOnClickListener {
            currentActivity.openDetail(Globals.WEB_VIEW, bundleW3B)
        }

        link_drive_waw3.setOnClickListener {
            Utils.openBrowser(context!!, context!!.getString(R.string.url_drive_waw3))
            // currentActivity.openDetail(Globals.WEB_VIEW, bundleWAW3)
        }

        link_github.setOnClickListener {
            Utils.openBrowser(context!!, context!!.getString(R.string.url_github))
        }

        webview_github.setOnClickListener {
            currentActivity.openDetail(Globals.WEB_VIEW, bundleGitHub)
        }

        link_app.setOnClickListener {
            Utils.openApp(context!!, context!!.resources.getString(R.string.url_gympass))
        }

        link_test.setOnClickListener {
            Utils.openBrowser(context!!, context!!.getString(R.string.test_url))
        }

        webview_video_test.setOnClickListener {

            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("bundle_video_test", bundleVideoTest)

            // 1)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.left_to_right_2, R.anim.right_to_left_2)

            // 2)
            // val options = ActivityOptions.makeCustomAnimation(context, R.anim.left_to_right_2, R.anim.right_to_left_2)
            // startActivity(intent, options.toBundle())

            // Nota: La 1) e la 2) sono equivalenti e permettono l'animazione da destra a sinistra delle activities.

            // startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
            // Nota: ActivityOptions.makeSceneTransitionAnimation(activity).toBundle() crea un effetto dissolvenza.
        }
    }
}