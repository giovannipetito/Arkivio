package it.giovanni.arkivio.fragments.viewholder

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R

class ContactsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal var contactInfoContainer: RelativeLayout = itemView.findViewById(R.id.contact_info_container)
    internal var addContactContainer: RelativeLayout = itemView.findViewById(R.id.add_contact_container)
    internal var nome: TextView = itemView.findViewById(R.id.employee_name)
    internal var email: TextView = itemView.findViewById(R.id.employee_email)
}