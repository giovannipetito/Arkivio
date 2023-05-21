package it.giovanni.arkivio.fragments.detail.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.EmailLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Utils.Companion.sendFilledOutMail
import it.giovanni.arkivio.utils.Utils.Companion.sendGmailMail
import it.giovanni.arkivio.utils.Utils.Companion.sendOutlookMail
import it.giovanni.arkivio.utils.Utils.Companion.sendSimpleMail

class EmailFragment: DetailFragment() {

    private var layoutBinding: EmailLayoutBinding? = null
    private val binding get() = layoutBinding

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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = EmailLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val to = arrayOf("raf@gmail.com", "gio@gmail.com")
        val cc = arrayOf("raf@outlook.it", "gio@outlook.it")

        binding?.labelSimpleMail?.setOnClickListener {
            sendSimpleMail(requireContext(), "raf@gmail.com")
        }

        binding?.labelFilledOutMail?.setOnClickListener {
            sendFilledOutMail(requireContext(), to, cc, "Subject", "Text")
        }

        binding?.labelGmailMail?.setOnClickListener {
            showProgressDialog()
            sendGmailMail(requireContext(), to, cc, "Subject", "Text")
        }

        binding?.labelOutlookMail?.setOnClickListener {
            showProgressDialog()
            sendOutlookMail(requireContext(), to, cc, "Subject", "Text")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}