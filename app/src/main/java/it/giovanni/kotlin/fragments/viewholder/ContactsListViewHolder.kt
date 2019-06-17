package it.giovanni.kotlin.fragments.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.kotlin.R

class ContactsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal var nome: TextView = itemView.findViewById(R.id.employee_name)
    internal var email: TextView = itemView.findViewById(R.id.employee_email)
}