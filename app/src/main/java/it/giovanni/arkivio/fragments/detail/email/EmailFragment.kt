package it.giovanni.arkivio.fragments.detail.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Utils.Companion.sendFilledOutMail
import it.giovanni.arkivio.utils.Utils.Companion.sendGmailMail
import it.giovanni.arkivio.utils.Utils.Companion.sendOutlookMail
import it.giovanni.arkivio.utils.Utils.Companion.sendSimpleMail
import kotlinx.android.synthetic.main.email_layout.*

class EmailFragment: DetailFragment() {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.email_layout
    }

    override fun getTitle(): Int {
        return R.string.email_title
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

        val to = arrayOf("raf@gmail.com", "gio@gmail.com")
        val cc = arrayOf("raf@outlook.it", "gio@outlook.it")

        label_simple_mail.setOnClickListener {
            sendSimpleMail(context!!, "raf@gmail.com")
        }

        label_filled_out_mail.setOnClickListener {
            sendFilledOutMail(context!!, to, cc, "Subject", "Text")
        }

        label_gmail_mail.setOnClickListener {
            sendGmailMail(currentActivity, to, cc, "Subject", "Text")
        }

        label_outlook_mail.setOnClickListener {
            sendOutlookMail(currentActivity, to, cc, "Subject", "Text")
        }
    }
}