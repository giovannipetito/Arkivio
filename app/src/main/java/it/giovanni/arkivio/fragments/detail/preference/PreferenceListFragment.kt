package it.giovanni.arkivio.fragments.detail.preference

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.Persona
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.adapter.PreferenceListAdapter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.Utils.Companion.turnArrayListToString
import kotlinx.android.synthetic.main.preference_list_layout.*
import kotlin.collections.ArrayList

class PreferenceListFragment: DetailFragment(), PreferenceListAdapter.OnItemViewClicked {

    var THRESHOLD = 3
    private var viewFragment: View? = null
    private var button: Button? = null
    private var checkedList: ArrayList<Persona>? = null
    private var clonedList: ArrayList<Persona>? = null
    private var checkedContacts: ArrayList<String>? = null
    private var contacts: String? = null
    private var isButtonClicked: Boolean? = false

    override fun getLayout(): Int {
        return R.layout.preference_list_layout
    }

    override fun getTitle(): Int {
        return R.string.preference_title
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isButtonClicked = false
        val recyclerView = viewFragment?.findViewById(R.id.search_checkbox_recyclerview) as RecyclerView
        button = viewFragment?.findViewById(R.id.button_user_preference)

        val list = init()
        checkedList = ArrayList()
        checkedContacts = ArrayList()
        val adapter = PreferenceListAdapter(this)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        adapter.setList(list)
        adapter.notifyDataSetChanged()

        button_user_preference.setOnClickListener {
            isButtonClicked = true
            currentActivity.onBackPressed()
        }

        // Attraverso il metodo cloneListaPersone() e la logica presente nella classe Persona, gli elementi della lista
        // clonedList clonata da list non punteranno alla stessa area di memoria degli elementi della lista list.
        clonedList = cloneListaPersone(list)
    }

    override fun onItemClicked(persona: Persona, isChecked: Boolean): Boolean {

        if (isChecked && checkedList?.size!! < THRESHOLD) {
            checkedList?.add(persona)
            checkedContacts?.add(persona.nome!!)
            button?.isEnabled = checkedList?.size!! in 1..THRESHOLD
            return true
        } else if (!isChecked) {
            checkedList?.remove(persona)
            checkedContacts?.remove(persona.nome!!)
            button?.isEnabled = checkedList?.size!! in 1..THRESHOLD
            return true
        } else if (checkedList?.size!! == THRESHOLD) {
            Toast.makeText(context, "Seleziona al massimo 3 elementi.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun getResultBack(): Intent {

        val backIntent = Intent()
        if (isButtonClicked!!) {
            contacts = turnArrayListToString(checkedContacts!!)
            backIntent.putExtra(Globals.BACK_PARAM_KEY_USER_PREFERENCE, contacts)
        }
        return backIntent
    }

    private fun cloneListaPersone(list: ArrayList<Persona>?): ArrayList<Persona>? {
        return if (list != null) {
            val clonedList = ArrayList<Persona>()
            for (i in list.indices) {
                clonedList.add(list[i].cloneList())
            }
            clonedList
        } else null
    }

    private fun init(): ArrayList<Persona> {

        val list = ArrayList<Persona>()
        list.add(Persona("Giovanni", "Petito", "3331582355", false))
        list.add(Persona("Raffaele", "Petito", "3802689011", false))
        list.add(Persona("Teresa", "Petito", "3343540536", false))
        list.add(Persona("Salvatore", "Pragliola", "3384672609", false))
        list.add(Persona("Angelina", "Basile", "3334392578", false))
        list.add(Persona("Vincenzo", "Petito", "3666872262", false))
        list.add(Persona("Giovanni", "Basile", "3884723340", false))
        list.add(Persona("Marco", "Basile", "3892148853", false))
        list.add(Persona("Antonio", "D'Ascia", "3315605694", false))
        list.add(Persona("Giovanni", "D'Ascia", "3331711437", false))
        list.add(Persona("Mariano", "Pinto", "3397016728", false))
        list.add(Persona("Pasquale", "Amato", "3665917760", false))
        list.add(Persona("Francesco", "Mongiello", "3299376402", false))
        list.add(Persona("Gianluigi", "Santillo", "3386124867", false))
        list.add(Persona("Daniele", "Musacchia", "3494977374", false))

        return list
    }
}