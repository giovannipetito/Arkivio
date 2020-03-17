package it.giovanni.kotlin.fragments.detail

import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment

class FontsFragment: DetailFragment() {

    override fun getLayout(): Int {
        return R.layout.fonts_layout
    }

    override fun getTitle(): Int {
        return R.string.fonts_title
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
}