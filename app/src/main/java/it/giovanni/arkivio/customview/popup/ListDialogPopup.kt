package it.giovanni.arkivio.customview.popup

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R
import kotlinx.android.synthetic.main.popup_labels_element.view.*
import kotlin.collections.ArrayList

class ListDialogPopup(activity: Activity, @StyleRes themeResId: Int) : CustomDialogPopup(activity, themeResId) {

    private var labels: ArrayList<String> = ArrayList()
    private val labelDelete = "annulla"

    init {
        prepare()
    }

    fun setLabels(mList: ArrayList<String>, callback: View.OnClickListener) {
        labels = mList
        labelList.visibility = View.VISIBLE
        titleDialog.visibility = View.GONE
        subtitleDialog.visibility = View.GONE
        messageDialog.visibility = View.VISIBLE
        buttonsContainer.visibility = View.GONE
        // Inflate element
        if (labels.size > 0) {
            val i = labels.iterator()
            while (i.hasNext()) {
                val element: String = i.next()
                val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.popup_labels_element, null)
                view.label.text = element.uppercase()
                if (element == labelDelete) {
                    view.label.setTextColor(ContextCompat.getColor(context, R.color.grey_2))
                }
                view.tag = element
                view.setOnClickListener(callback)
                labelList.addView(view)
            }
        }
    }
}