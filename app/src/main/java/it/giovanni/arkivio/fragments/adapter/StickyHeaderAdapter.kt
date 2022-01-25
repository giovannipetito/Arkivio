package it.giovanni.arkivio.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.bean.Persona
import it.giovanni.arkivio.bean.Persona.Companion.HEADER_TYPE
import it.giovanni.arkivio.bean.Persona.Companion.ITEM_TYPE
import it.giovanni.arkivio.databinding.RowHeaderBinding
import it.giovanni.arkivio.databinding.RowItemBinding

class StickyHeaderAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: ArrayList<Persona>? = null

    fun setList(list: ArrayList<Persona>) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            val rowHeaderBinding: RowHeaderBinding = RowHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(rowHeaderBinding)
        } else {
            val rowItemBinding: RowItemBinding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(rowItemBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val persona = list?.get(position)
        if (persona != null) {
            when (persona.tipo) {
                HEADER_TYPE -> {
                    if (holder is HeaderViewHolder) {
                        holder.header.text = persona.nome
                    }
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
        else list?.size!!
    }

    inner class HeaderViewHolder(rowHeaderBinding: RowHeaderBinding) : RecyclerView.ViewHolder(rowHeaderBinding.root) {
        val header: TextView = rowHeaderBinding.textHeader
    }

    inner class ItemViewHolder(rowHeaderBinding: RowItemBinding) : RecyclerView.ViewHolder(rowHeaderBinding.root) {
        val item1: TextView = rowHeaderBinding.textItem1
        val item2: TextView = rowHeaderBinding.textItem2
    }
}