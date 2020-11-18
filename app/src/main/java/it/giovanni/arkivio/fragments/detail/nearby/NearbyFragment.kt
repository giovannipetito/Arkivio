package it.giovanni.arkivio.fragments.detail.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Globals
import kotlinx.android.synthetic.main.nearby_layout.*

class NearbyFragment: DetailFragment() {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.nearby_layout
    }

    override fun getTitle(): Int {
        return R.string.nearby_title
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        label_nearby_search.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY_SEARCH, null)
        }
        label_nearby_chat.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY_CHAT, null)
        }
        label_nearby_game.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY_GAME, null)
        }
        label_nearby_beacons.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY_BEACONS, null)
        }
    }
}