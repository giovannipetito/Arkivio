package it.giovanni.kotlin.fragments.detail.addcontacts

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import it.giovanni.kotlin.R
import it.giovanni.kotlin.bean.Persona
import it.giovanni.kotlin.customview.Brick
import it.giovanni.kotlin.customview.popup.CustomDialogPopup
import it.giovanni.kotlin.fragments.DetailFragment
import it.giovanni.kotlin.fragments.adapter.ContactsListAdapter
import it.giovanni.kotlin.interfaces.IFlexBoxCallback
import it.giovanni.kotlin.utils.Globals
import it.giovanni.kotlin.utils.Utils
import kotlinx.android.synthetic.main.contacts_list_layout.*
import kotlinx.android.synthetic.main.detail_layout.*

class ContactsListFragment: DetailFragment(), ContactsListAdapter.OnItemViewClicked, IFlexBoxCallback {

    var TRANSITION_TIME: Long = 275
    private var FLEXBOX_CONTAINER_HEIGHT: Int? = 80

    private var viewFragment: View? = null
    lateinit var adapter: ContactsListAdapter
    private var list: ArrayList<Persona>? = null
    private var filtered: ArrayList<Persona>? = null
    private var updatedList: ArrayList<Persona>? = null
    private var customPopupGoOut: CustomDialogPopup? = null
    private var reallyGoOut: Boolean = false
    private var labelIsClicked: Boolean = false

    private var sentence: String? = null
    private var isOpen = false

    companion object {
        var KEY_EMPLOYEES: String = "KEY_EMPLOYEES"
    }

    override fun getLayout(): Int {
        return R.layout.contacts_list_layout
    }

    override fun getTitle(): Int {
        return R.string.add_contacts_title
    }

    override fun getActionTitle(): Int {
        return R.string.contacts_list_action_label
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

    override fun beforeClosing(): Boolean {
        if (!labelIsClicked) {
            if (!reallyGoOut) {
                customPopupGoOut = CustomDialogPopup(currentActivity, R.style.PopupTheme)
                customPopupGoOut!!.setCancelable(false)
                customPopupGoOut!!.setTitle("")
                customPopupGoOut!!.setMessage(R.string.popup_really_close_message)

                customPopupGoOut!!.setButtons(resources.getString(R.string.popup_button_confirm), View.OnClickListener {
                    reallyGoOut = true
                    customPopupGoOut!!.dismiss()
                    currentActivity.onBackPressed()
                }, resources.getString(R.string.popup_button_cancel), View.OnClickListener {
                    customPopupGoOut!!.dismiss()
                })
                customPopupGoOut!!.show()
                return false
            } else {
                return true
            }
        }
        return true
    }

    override fun getResultBack(): Intent {
        val backIntent = Intent()
        if (labelIsClicked) {
            if (partecipanti_flex_search_box.childCount <= 0) {
                return backIntent
            } else {
                hideSoftKeyboard()
                list = ArrayList()
                val count = partecipanti_flex_search_box.childCount
                if (count > 0) {
                    for (index in 1..count) {
                        val eb = (partecipanti_flex_search_box.getChildAt(index - 1) as Brick)
                        if (eb.isVisible()) {
                            val contact = Persona(eb.getName(), eb.getEmail(), true)
                            list!!.add(contact)
                        }
                    }
                }
                backIntent.putExtra(Globals.BACK_PARAM_KEY_EMPLOYEE_SEARCH, list)
                return backIntent
            }
        } else {
            return backIntent
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)

        list = init()
        adapter = ContactsListAdapter(this)

        return viewFragment
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_contacts.setHasFixedSize(true)
        recyclerview_contacts.layoutManager = LinearLayoutManager(activity)
        recyclerview_contacts.adapter = adapter
        adapter.setList(list)
        adapter.notifyDataSetChanged()

        arrow_go_back.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_back_rvd))

        partecipanti_flex_search_box?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            scrollview_flexbox?.scrollTo(0, partecipanti_flex_search_box.height)
        }

        if (arguments != null) {
            val contacts = arguments!!.getSerializable(KEY_EMPLOYEES) as ArrayList<Persona>
            if (contacts.size > 0) {
                partecipanti_flex_search_box.removeAllViews()
                for ((i, contact) in contacts.withIndex()) {
                    val employeeBrick = Brick(context!!)
                    employeeBrick.mode(Brick.ModeType.EDIT)
                    employeeBrick.setName(contact.nome)
                    employeeBrick.setEmail(contact.email)
                    employeeBrick.callback(this)
                    employeeBrick.position(i)
                    partecipanti_flex_search_box.addView(employeeBrick)
                }
                actionLabelState(true)
            }
        }

        switch_compat.setOnClickListener {

            close_action.visibility = View.GONE
            edit_search.setText("")
            startAnimation()

            if (switch_compat.isChecked) {
                edit_search.requestFocus()
                val imm = currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edit_search, InputMethodManager.SHOW_IMPLICIT)
                recyclerview_contacts.visibility = View.VISIBLE
            } else {
                edit_search.clearFocus()
                recyclerview_contacts.visibility = View.GONE
            }
        }

        edit_search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (edit_search.text?.isNotEmpty()!!) {
                    close_action.visibility = View.VISIBLE
                    filter()

                    if (edit_search.text.endsWith(",") || edit_search.text.endsWith(" ")) {
                        var email: String = edit_search.text.toString().toLowerCase()
                        email = email.substring(0, email.length - 1)
                        if (Utils.checkEmail(email)) {
                            if (list?.size != 0) {
                                if (list != null && list?.isNotEmpty()!!) {

                                    if (email == list!![0].email) {
                                        addBrick(list!![0].nome, email)
                                        edit_search.setText("")
                                    }
                                }
                            } else {
                                addBrick(email, email)
                                edit_search.setText("")
                            }
                        }
                    }
                } else if (edit_search.text?.isEmpty()!!) {
                    close_action.visibility = View.GONE
                    resetList()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        edit_search.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var email: String = edit_search.text.toString().toLowerCase()
                email = email.substring(0, email.length)
                if (Utils.checkEmail(email)) {
                    if (list?.size != 0) {
                        if (email == list!![0].email) {
                            addBrick(list!![0].nome, email)
                            edit_search.setText("")
                        }
                    } else {
                        addBrick(email, email)
                        edit_search.setText("")
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        close_action.setOnClickListener {
            edit_search.setText("")
            resetList()
        }
    }

    private fun addBrick(name: String, email: String) {
        if (!isAdded(email)) {
            val employeeBrick = Brick(context!!)
            employeeBrick.mode(Brick.ModeType.EDIT)
            employeeBrick.setName(name)
            employeeBrick.setEmail(email)
            employeeBrick.callback(this)
            employeeBrick.position(partecipanti_flex_search_box.childCount)
            partecipanti_flex_search_box.addView(employeeBrick)
        }
    }

    private fun isAdded(email: String): Boolean {
        val count = partecipanti_flex_search_box.childCount
        if (count > 0) {
            for (index in 1..count) {
                val eb = (partecipanti_flex_search_box.getChildAt(index - 1) as Brick)
                if (eb.isVisible() && eb.getEmail() == email)
                    return true
            }
        }
        return false
    }

    override fun onItemClicked(persona: Persona, list: ArrayList<Persona>?) {

        updatedList = ArrayList()
        persona.isVisible = false
        addBrick(persona.nome, persona.email)
        val density = resources.displayMetrics.density
        if (flexbox_container.measuredHeight >= Math.round(FLEXBOX_CONTAINER_HEIGHT?.toFloat()!! * density)) {
            flexbox_container.layoutParams.height = Math.round(FLEXBOX_CONTAINER_HEIGHT?.toFloat()!! * density)
        }

        if (list != null) {
            for (item in list) {
                if (persona.email != item.email)
                    updatedList!!.add(item)
            }
        }

        adapter.setList(updatedList)
        adapter.notifyDataSetChanged()

        actionLabelState(true)
    }

    override fun onActionClickListener() {
        labelIsClicked = true
        currentActivity.onBackPressed()
    }

    override fun flexBoxRemoved(position: Int) {
        partecipanti_flex_search_box.getChildAt(position).visibility = View.GONE
    }

    private fun filter() {

        if (sentence != null && sentence.equals(edit_search.text.toString(), ignoreCase = true))
            return
        sentence = edit_search.text.toString()
        if (sentence?.isEmpty()!!) {
            sentence = null
            clearFilteredList()
            no_result.visibility = View.GONE
            recyclerview_contacts.visibility = View.VISIBLE
            adapter.setList(list)
            adapter.notifyDataSetChanged()
            return
        }
        clearFilteredList()
        if (list == null || list?.size == 0) {
            recyclerview_contacts.visibility = View.GONE
            no_result.visibility = View.VISIBLE
            no_result.setText(R.string.no_list)
            return
        }
        for (persona in list!!) {
            if (persona.nome.toLowerCase().contains(sentence?.toLowerCase()!!))
                filtered?.add(persona)
        }
        if (filtered == null || filtered?.size == 0) {
            recyclerview_contacts.visibility = View.GONE
            no_result.visibility = View.VISIBLE
        } else {
            no_result.visibility = View.GONE
            recyclerview_contacts.visibility = View.VISIBLE
            adapter.setList(filtered)
            adapter.notifyDataSetChanged()
        }
    }

    private fun clearFilteredList() {
        if (filtered == null)
            filtered = ArrayList()
        else
            filtered?.clear()
    }

    private fun resetList() {
        list = init()
        adapter.setList(list)
        adapter.notifyDataSetChanged()
        no_result.visibility = View.GONE
        recyclerview_contacts.visibility = View.VISIBLE
    }

    private fun startAnimation() {

        if (!isOpen) {
            val centerX = switch_compat.x.toInt() + switch_compat.width/2
            val centerY = switch_compat.y.toInt() + switch_compat.height/2
            val startRadius = 0
            val endRadius = Math.hypot(container.width.toDouble(), container.height.toDouble()).toInt()

            val anim = ViewAnimationUtils.createCircularReveal(content, centerX, centerY, startRadius.toFloat(), endRadius.toFloat())
            anim.duration = TRANSITION_TIME
            anim.start()

            content.visibility = View.VISIBLE
            isOpen = true
        } else {
            val centerX = switch_compat.x.toInt() + switch_compat.width/2
            val centerY = switch_compat.y.toInt() + switch_compat.height/2
            val startRadius = Math.max(container.width, container.height)
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(content, centerX, centerY, startRadius.toFloat(), endRadius.toFloat())
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    content.visibility = View.GONE
                }

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })
            anim.duration = TRANSITION_TIME
            anim.start()

            isOpen = false
        }
    }

    private fun init(): ArrayList<Persona> {

        val list = ArrayList<Persona>()
        list.add(Persona("Giovanni", "Petito", false))
        list.add(Persona("Raffaele", "Petito", false))
        list.add(Persona("Teresa", "Petito", false))
        list.add(Persona("Salvatore", "Pragliola", false))
        list.add(Persona("Angelina", "Basile", false))
        list.add(Persona("Vincenzo", "Petito", false))
        list.add(Persona("Giovanni", "Basile", false))
        list.add(Persona("Marco", "Basile", false))
        list.add(Persona("Antonio", "D'Ascia", false))
        list.add(Persona("Giovanni", "D'Ascia", false))
        list.add(Persona("Mariano", "Pinto", false))
        list.add(Persona("Pasquale", "Amato", false))
        list.add(Persona("Francesco", "Mongiello", false))
        list.add(Persona("Gianluigi", "Santillo", false))
        list.add(Persona("Daniele", "Musacchia", false))

        return list
    }
}