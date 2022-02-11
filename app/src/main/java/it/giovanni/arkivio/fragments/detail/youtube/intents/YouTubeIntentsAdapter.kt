package it.giovanni.arkivio.fragments.detail.youtube.intents

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import it.giovanni.arkivio.databinding.IntentsListItemBinding

class YouTubeIntentsAdapter internal constructor(
    context: Context,
    textViewResourceId: Int,
    objects: List<ListViewItem?>?
) :
    ArrayAdapter<YouTubeIntentsAdapter.ListViewItem?>(context, textViewResourceId, objects!!) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {

        val itemBinding = IntentsListItemBinding.inflate(LayoutInflater.from(context), null, false)
        val mView = itemBinding.root

        val intentName = itemBinding.listItemText
        intentName.text = getItem(position)?.title

        val disabledText = itemBinding.listItemDisabledText
        disabledText.text = getItem(position)?.disabledText

        if (isEnabled(position)) {
            disabledText.visibility = View.GONE
        } else {
            disabledText.visibility = View.VISIBLE
            intentName.setTextColor(Color.GRAY)
        }

        return mView
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun isEnabled(position: Int): Boolean {
        return getItem(position)?.isEnabled!!
    }

    interface ListViewItem {
        val title: String?
        val isEnabled: Boolean?
        val disabledText: String?
    }
}