package it.giovanni.arkivio.fragments.viewholder

import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.UserItemBinding

class UserPreferenceViewHolder(userItemBinding: UserItemBinding) : RecyclerView.ViewHolder(userItemBinding.root) {

    internal var checkBox: CheckBox = userItemBinding.checkboxItem
    internal var nome: TextView = userItemBinding.textNomeCheckbox
    internal var cognome: TextView = userItemBinding.textCognomeCheckbox
    internal var msisdn: TextView = userItemBinding.textMsisdnCheckbox
}