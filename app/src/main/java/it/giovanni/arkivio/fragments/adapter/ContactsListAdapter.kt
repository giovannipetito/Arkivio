package it.giovanni.arkivio.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.Persona
import it.giovanni.arkivio.fragments.viewholder.ContactsListViewHolder

class ContactsListAdapter(onItemClicked: OnItemViewClicked) : RecyclerView.Adapter<ContactsListViewHolder>() {

    private var onItemViewClicked : OnItemViewClicked? = onItemClicked
    private var contacts : ArrayList<Persona>? = null

    fun setList(list: ArrayList<Persona>?) {
        contacts = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ContactsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsListViewHolder, position: Int) {

        val contact : Persona = contacts!![position]
        val name = contact.nome + " " + contact.cognome
        holder.nome.text = name
        holder.email.text = contact.email

        holder.contactInfoContainer.setOnClickListener {
            onItemViewClicked?.onItemInfoClicked(contact, contacts)
        }
        holder.addContactContainer.setOnClickListener {
            onItemViewClicked?.onItemIconClicked(contact)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.contact_row
    }

    override fun getItemCount(): Int {
        return if (contacts == null) 0
        else contacts?.size!!
    }

    interface OnItemViewClicked {
        fun onItemInfoClicked(persona: Persona, list: ArrayList<Persona>?)
        fun onItemIconClicked(persona: Persona)
    }
}