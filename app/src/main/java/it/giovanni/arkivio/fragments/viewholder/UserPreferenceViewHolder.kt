package it.giovanni.arkivio.fragments.viewholder

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R

class UserPreferenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal var checkBox: CheckBox = itemView.findViewById(R.id.checkbox_item)
    internal var nome: TextView = itemView.findViewById(R.id.text_nome_checkbox)
    internal var cognome: TextView = itemView.findViewById(R.id.text_cognome_checkbox)
    internal var msisdn: TextView = itemView.findViewById(R.id.text_msisdn_checkbox)
}