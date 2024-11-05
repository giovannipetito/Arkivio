package it.giovanni.arkivio.fragments.detail.link

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.LinkDynamicItemBinding
import it.giovanni.arkivio.databinding.LinkLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.webview.WebViewActivity
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.model.Link
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.UserFactory
import it.giovanni.arkivio.utils.Utils
import it.giovanni.arkivio.utils.Utils.setBitmapFromUrl

class LinkFragment : DetailFragment() {

    private var layoutBinding: LinkLayoutBinding? = null
    private val binding get() = layoutBinding

    private var bundleW3B: Bundle = Bundle()
    private var bundleWAW3: Bundle = Bundle()
    private var bundleGitHub: Bundle = Bundle()
    private var bundleVideo: Bundle = Bundle()
    private var bundleHtml: Bundle = Bundle()

    private var listLink: ArrayList<Link>? = null

    private val webviewType = "webview"
    private val appLinkType = "inAppLink"
    private val appType = "app"
    private val extType = "ext"
    private var bundleDeepLink: Bundle = Bundle()

    override fun getTitle(): Int {
        return R.string.link_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(searchString: String) {
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = LinkLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        listLink =
            if (UserFactory.getInstance().listLink != null)
                UserFactory.getInstance().listLink
            else init()

        if (listLink != null) {
            addLinkViews(listLink)
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelWebviewDriveW3b?.setOnClickListener {
            currentActivity.openDetail(Globals.WEB_VIEW, bundleW3B)
        }

        binding?.labelLinkDriveWaw3?.setOnClickListener {
            Utils.openBrowser(requireContext(), requireContext().getString(R.string.url_drive_waw3))
        }

        binding?.labelBrowserGithub?.setOnClickListener {
            Utils.openBrowser(requireContext(), requireContext().getString(R.string.url_github))
        }

        binding?.labelWebviewGithub?.setOnClickListener {
            currentActivity.openDetail(Globals.WEB_VIEW, bundleGitHub)
        }

        binding?.labelLinkApp?.setOnClickListener {
            Utils.openApp(requireContext(), requireContext().resources.getString(R.string.url_gympass))
        }

        binding?.labelLinkTest?.setOnClickListener {
            Utils.openBrowser(requireContext(), requireContext().getString(R.string.test_url))
        }

        binding?.labelWebviewVideoPlayer?.setOnClickListener {

            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("bundle_video", bundleVideo)

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.left_to_right_2, R.anim.right_to_left_2)
        }

        binding?.labelWebviewText?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("TEXT_KEY", message())
            currentActivity.openDetail(Globals.WEB_VIEW, bundle)
        }

        binding?.labelWebviewHtml?.setOnClickListener {
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

    private fun addLinkViews(listLink: ArrayList<Link>?) {

        binding?.linkUtiliContainer?.removeAllViews()

        if (listLink.isNullOrEmpty())
            return

        for (item in listLink) {

            val itemBinding = LinkDynamicItemBinding.inflate(LayoutInflater.from(context), binding?.linkUtiliContainer, false)
            val rowView: View = itemBinding.root

            val labelName: TextView = itemBinding.linkName
            val labelIcon: ImageView = itemBinding.linkIcon

            setBitmapFromUrl(item.image, labelIcon, requireActivity())

            val linkItem: RelativeLayout = itemBinding.linkItem

            labelName.text = item.name

            binding?.linkUtiliContainer?.addView(rowView)

            linkItem.setOnClickListener {
                when (item.type) {
                    webviewType -> {
                        bundleDeepLink.putString("link_deeplink", item.name)
                        bundleDeepLink.putString("url_deeplink", item.link)
                        currentActivity.openDetail(Globals.WEB_VIEW, bundleDeepLink)
                    }
                    appLinkType -> {
                        if (item.link == "waw3://contacts") {
                            currentActivity.openDetail(Globals.RUBRICA_REALTIME, null)
                        }
                    }
                    appType -> {
                        currentActivity.openApp(item.appLinkAndroid)
                    }
                    extType -> {
                        currentActivity.openBrowser(item.link)
                    }
                }
            }
        }
        binding?.linkUtiliContainer?.visibility = View.VISIBLE
    }

    private fun init(): ArrayList<Link> {

        val list = ArrayList<Link>()
        list.add(
            Link(
                "",
                "Grande Cinema 3",
                "waw3://cinema",
                "1",
                "inAppLink",
                "",
                "https://static.windtrebusiness.it/mosaicow3b/static/configuration/ico_gctre.png"
            )
        )
        list.add(
            Link(
                "Daytronic",
                "DayTronic",
                "",
                "2",
                "app",
                "it.day.daytronicFLAT",
                "https://static.windtrebusiness.it/mosaicow3b/static/configuration/ico_daytronic.png"
            )
        )
        list.add(Link("", "Gympass", "", "3", "app", "com.gympass", ""))
        list.add(
            Link(
                "Bacheca",
                "Rubrica",
                "waw3://contacts",
                "4",
                "inAppLink",
                "",
                "https://static.windtrebusiness.it/mosaicow3b/static/configuration/ico_rubrica.png"
            )
        )
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}