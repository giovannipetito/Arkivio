package it.giovanni.arkivio.fragments.detail.rubrica

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.Persona
import it.giovanni.arkivio.customview.Brick
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.viewinterfaces.IFlexBoxCallback
import it.giovanni.arkivio.utils.Globals
import kotlinx.android.synthetic.main.rubrica_home_layout.*
import kotlin.math.roundToInt

class RubricaHomeFragment: DetailFragment(), IFlexBoxCallback {

    private var viewFragment: View? = null
    private var contacts: ArrayList<Persona>? = null

    override fun getLayout(): Int {
        return R.layout.rubrica_home_layout
    }

    override fun getTitle(): Int {
        return R.string.rubrica_home_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return false
    }

    override fun closeAction(): Boolean {
        return true
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

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contacts = ArrayList()

        final_step_users.setOnClickListener {

            val contactsBundle = Bundle()
            val contacts = ArrayList<Persona>()
            val count = partecipanti_flex_box.childCount
            if (count > 0) {
                for (index in 1..count) {
                    val eb = (partecipanti_flex_box.getChildAt(index - 1) as Brick)
                    val contact = Persona(eb.getName(), eb.getName(), "", "", "", "", "", true)
                    contacts.add(contact)
                }
            }
            contactsBundle.putSerializable(RubricaListFragment.KEY_EMPLOYEES, contacts)
            currentActivity.openDetail(Globals.RUBRICA_LIST, contactsBundle, this@RubricaHomeFragment, Globals.REQUEST_CODE_EVENT_EMPLOYEE_SEARCH)
        }

        partecipanti_flex_box.setOnClickListener {

            val contactsBundle = Bundle()
            val contacts = ArrayList<Persona>()
            val count = partecipanti_flex_box.childCount
            if (count > 0) {
                for (index in 1..count) {
                    val eb = (partecipanti_flex_box.getChildAt(index - 1) as Brick)
                    val contact = Persona(eb.getName(), eb.getName(), "", "", "", "", "", true)
                    contacts.add(contact)
                }
            }
            contactsBundle.putSerializable(RubricaListFragment.KEY_EMPLOYEES, contacts)
            currentActivity.openDetail(Globals.RUBRICA_LIST, contactsBundle, this@RubricaHomeFragment, Globals.REQUEST_CODE_EVENT_EMPLOYEE_SEARCH)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Globals.REQUEST_CODE_EVENT_EMPLOYEE_SEARCH && data != null && data.hasExtra(Globals.BACK_PARAM_KEY_EMPLOYEE_SEARCH)) {

            contacts = data.getSerializableExtra(Globals.BACK_PARAM_KEY_EMPLOYEE_SEARCH) as ArrayList<Persona>

            if (contacts != null) {
                partecipanti_flex_box.removeAllViews()
                for (contact in contacts!!) {
                    val employeeBrick = Brick(context!!)
                    employeeBrick.mode(Brick.ModeType.VIEW)
                    employeeBrick.setName(contact.nome!!)
                    employeeBrick.callback(this)
                    partecipanti_flex_box.addView(employeeBrick)
                }
                setFlexBoxHeight(partecipanti_flex_box.childCount)
            }
        }
    }

    private fun setFlexBoxHeight(brickCount: Int) {
        val density = resources.displayMetrics.density
        if (brickCount == 0) {
            partecipanti_flex_box.visibility = View.GONE
            users_text.visibility = View.VISIBLE
            flexboxlayout_container.layoutParams.height = (50.toFloat() * density).roundToInt()
        } else {
            partecipanti_flex_box.visibility = View.VISIBLE
            users_text.visibility = View.GONE
            if (brickCount == 1)
                flexboxlayout_container.layoutParams.height = (50.toFloat() * density).roundToInt()
            if (brickCount == 2)
                flexboxlayout_container.layoutParams.height = (90.toFloat() * density).roundToInt()
            if (brickCount == 3 || brickCount > 3)
                flexboxlayout_container.layoutParams.height = (130.toFloat() * density).roundToInt()
        }
    }

    override fun flexBoxRemoved(position: Int) {}
}