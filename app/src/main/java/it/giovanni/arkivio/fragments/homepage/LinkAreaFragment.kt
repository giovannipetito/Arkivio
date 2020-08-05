package it.giovanni.arkivio.fragments.homepage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.HomeFragment
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.fragments.detail.webview.WebViewActivity
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.Utils
import kotlinx.android.synthetic.main.link_area_layout.*

class LinkAreaFragment : HomeFragment() {

    private var viewFragment: View? = null
    private var bundleW3B: Bundle = Bundle()
    private var bundleWAW3: Bundle = Bundle()
    private var bundleGitHub: Bundle = Bundle()
    private var bundleVideo: Bundle = Bundle()
    private var bundleHtml: Bundle = Bundle()

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

        bundleVideo.putString("link_video", resources.getString(R.string.link_video))
        bundleVideo.putString("url_video", resources.getString(R.string.url_video))

        bundleHtml.putInt("link_html", R.string.link_html)
        bundleHtml.putString("url_html", resources.getString(R.string.url_html))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        label_webview_drive_w3b.setOnClickListener {
            currentActivity.openDetail(Globals.WEB_VIEW, bundleW3B)
        }

        label_link_drive_waw3.setOnClickListener {
            Utils.openBrowser(context!!, context!!.getString(R.string.url_drive_waw3))
            // currentActivity.openDetail(Globals.WEB_VIEW, bundleWAW3)
        }

        label_browser_github.setOnClickListener {
            Utils.openBrowser(context!!, context!!.getString(R.string.url_github))
        }

        label_webview_github.setOnClickListener {
            currentActivity.openDetail(Globals.WEB_VIEW, bundleGitHub)
        }

        label_link_app.setOnClickListener {
            Utils.openApp(context!!, context!!.resources.getString(R.string.url_gympass))
        }

        label_link_test.setOnClickListener {
            Utils.openBrowser(context!!, context!!.getString(R.string.test_url))
        }

        label_webview_video_player.setOnClickListener {

            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("bundle_video", bundleVideo)

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

        label_webview_text.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("TEXT_KEY", message())
            currentActivity.openDetail(Globals.WEB_VIEW, bundle)
        }

        label_webview_html.setOnClickListener {
            currentActivity.openDetail(Globals.WEB_VIEW, bundleHtml)
        }
    }

    private fun message(): String {
        return "Steve Jobs" +
                "<br>" +
                "<br>" +
                "Ma era comunque una cosa straordinaria, soprattutto per un ragazzino di dieci anni, " +
                "poter scrivere un programma diciamo in BASIC o Fortran, e questa macchina prendeva " +
                "la tua idea, in qualche modo la realizzava e ti restituiva un risultato. E se il " +
                "risultato era quello che avevi previsto allora il tuo programma funzionava davvero. " +
                "Ma l’aspetto più importante non aveva niente a che fare con la pratica. Aveva a che " +
                "fare con la possibilità di veder rispecchiati i processi del pensiero. Imparare a " +
                "pensare. Credo sia la più alta forma di apprendimento. La programmazione informatica " +
                "insegna a pensare in modo leggermente differente. Ecco perché considero l’informatica " +
                "come una libera arte. Tutti dovrebbero impararla." +
                "</p><p>&nbsp;" +
                "<center>" +
                "<a href=\"https://www.w3schools.com\">" +
                "<img src=\"https://static.windtrebusiness.it/mosaicow3b/static/configuration/ico_rubrica.png\" alt=\"W3Schools.com\" width=\"48\" height=\"48\">" +
                "</a>" +
                "</center>" +
                "</p>"
    }
}