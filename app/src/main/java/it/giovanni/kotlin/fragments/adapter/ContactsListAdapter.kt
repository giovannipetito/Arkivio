package it.giovanni.kotlin.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.kotlin.R
import it.giovanni.kotlin.bean.Persona
import it.giovanni.kotlin.fragments.viewholder.ContactsListViewHolder

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
        holder.nome.text = contact.nome
        holder.email.text = contact.email
        holder.itemView.setOnClickListener {
            onItemViewClicked?.onItemClicked(contact, contacts)
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
        fun onItemClicked(persona: Persona, list: ArrayList<Persona>?)
    }
}