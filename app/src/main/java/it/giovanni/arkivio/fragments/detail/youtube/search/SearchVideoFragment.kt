package it.giovanni.arkivio.fragments.detail.youtube.search

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.SearchVideoLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.youtube.YoutubeConnector
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class SearchVideoFragment: DetailFragment(), SearchVideoAdapter.OnItemViewClicked {

    private var layoutBinding: SearchVideoLayoutBinding? = null
    private val binding get() = layoutBinding

    private var recyclerView: RecyclerView? = null
    private var progressDialog: ProgressDialog? = null
    private var handler: Handler? = null
    private var searchResults: List<Video>? = null

    override fun getLayout(): Int {
        return NO_LAYOUT
    }

    override fun getTitle(): Int {
        return R.string.youtube_title
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

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = SearchVideoLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(context)
        val searchInput: EditText? = binding?.editSearch
        recyclerView = binding?.recyclerViewVideo

        progressDialog?.setTitle("Searching...")
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        handler = Handler(Looper.getMainLooper())

        searchInput?.setOnEditorActionListener { v: TextView, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                progressDialog?.setMessage("Finding videos for " + v.text.toString())
                progressDialog?.show()
                searchOnYoutube(v.text.toString())

                hideSoftKeyboard2()

                return@setOnEditorActionListener false
            }
            true
        }

        binding?.arrowSearch?.setOnClickListener {
            progressDialog?.setMessage("Finding videos for " + searchInput?.text.toString())
            progressDialog?.show()
            searchOnYoutube(searchInput?.text.toString())

            hideSoftKeyboard2()
        }
    }

    private fun searchOnYoutube(keywords: String) {
        object : Thread() {
            override fun run() {
                val yc = YoutubeConnector()
                searchResults = yc.search(keywords)
                handler?.post {
                    fillYoutubeVideos()
                    progressDialog?.dismiss()
                }
            }
        }.start()
    }

    private fun fillYoutubeVideos() {
        val youtubeAdapter = SearchVideoAdapter(context, searchResults, this)
        recyclerView?.adapter = youtubeAdapter
        youtubeAdapter.notifyDataSetChanged()
    }

    override fun onItemToPlayerViewClicked(video: Video?) {
        val intent = Intent(context, YouTubePlayerViewActivity::class.java)
        intent.putExtra("VIDEO_ID", video?.id)
        intent.putExtra("VIDEO_TITLE", video?.title)
        intent.putExtra("VIDEO_DESCRIPTION", video?.description)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onItemToPlayerFragmentClicked(video: Video?) {
        val intent = Intent(context, YouTubePlayerFragmentActivity::class.java)
        intent.putExtra("VIDEO_ID", video?.id)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}