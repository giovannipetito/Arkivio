package it.giovanni.kotlin.youtube.intents

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import it.giovanni.kotlin.R

class YouTubeIntentsAdapter internal constructor(
    context: Context,
    textViewResourceId: Int,
    objects: List<ListViewItem?>?
) :
    ArrayAdapter<YouTubeIntentsAdapter.ListViewItem?>(context, textViewResourceId, objects!!) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {

        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView = inflater.inflate(R.layout.intents_list_item, null)

        val intentName = mView?.findViewById<TextView>(R.id.list_item_text)
        intentName?.text = getItem(position)?.title

        val disabledText = mView?.findViewById<TextView>(R.id.list_item_disabled_text)
        disabledText?.text = getItem(position)?.disabledText

        if (isEnabled(position)) {
            disabledText?.visibility = View.GONE
        } else {
            disabledText?.visibility = View.VISIBLE
            intentName?.setTextColor(Color.GRAY)
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