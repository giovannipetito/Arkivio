package it.giovanni.kotlin.fragments.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.kotlin.R
import it.giovanni.kotlin.bean.Persona
import it.giovanni.kotlin.bean.Persona.Companion.HEADER_TYPE
import it.giovanni.kotlin.bean.Persona.Companion.ITEM_TYPE

class StickyHeaderAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: ArrayList<Persona>? = null

    fun setList(list: ArrayList<Persona>) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == HEADER_TYPE)
            HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_header, parent, false))
        else
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val persona = list?.get(position)
        if (persona != null) {
            when (persona.tipo) {
                HEADER_TYPE -> {
                    if (holder is HeaderViewHolder)
                        holder.header.text = persona.nome
                }
                ITEM_TYPE -> {
                    if (holder is ItemViewHolder) {
                        holder.item1.text = persona.nome
                        holder.item2.text = persona.cognome
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list != null) {
            val persona = list?.get(position)
            if (persona != null) {
                return persona.tipo!!
            }
        }
        return 0
    }

    override fun getItemCount(): Int {
        return if (list == null) 0
        else list!!.size
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val header: TextView = itemView.findViewById(R.id.text_header)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val item1: TextView = itemView.findViewById(R.id.text_item1)
        val item2: TextView = itemView.findViewById(R.id.text_item2)
    }
}