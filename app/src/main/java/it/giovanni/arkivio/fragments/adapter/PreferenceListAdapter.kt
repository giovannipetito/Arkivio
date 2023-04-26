package it.giovanni.arkivio.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.model.Persona
import it.giovanni.arkivio.databinding.UserItemBinding
import it.giovanni.arkivio.fragments.viewholder.UserPreferenceViewHolder

class PreferenceListAdapter(private val onItemViewClicked: OnItemViewClicked) : RecyclerView.Adapter<UserPreferenceViewHolder>() {

    private var list: List<Persona>? = null

    fun setList(list: List<Persona>) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPreferenceViewHolder {
        val userItemBinding: UserItemBinding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserPreferenceViewHolder(userItemBinding)
    }

    override fun onBindViewHolder(holder: UserPreferenceViewHolder, position: Int) {
        val persona = list!![position]
        holder.nome.text = persona.nome
        holder.cognome.text = persona.cognome
        holder.msisdn.text = persona.cellulare

        holder.checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (!onItemViewClicked.onItemClicked(persona, isChecked)) {
                    buttonView.setOnCheckedChangeListener(null)
                    buttonView.isChecked = false
                    buttonView.setOnCheckedChangeListener(this)
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.user_item
    }

    override fun getItemCount(): Int {
        return if (list == null) 0
        else list?.size!!
    }

    interface OnItemViewClicked {
        fun onItemClicked(persona: Persona, isChecked: Boolean): Boolean
    }
}