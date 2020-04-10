package it.giovanni.kotlin.customview.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import it.giovanni.kotlin.R
import kotlinx.android.synthetic.main.popup_labels_element.view.*
import java.util.*
import kotlin.collections.ArrayList

class ListDialogPopup : CustomDialogPopup {

    private var labels: ArrayList<String> = ArrayList()
    val LABEL_DELETE = "annulla"

    constructor(activity: Activity, @StyleRes themeResId: Int) : super(activity, themeResId) {
        this.activity = activity
        prepare()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
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
                view.label.text = element.toUpperCase(Locale.ITALIAN)
                if (element == LABEL_DELETE) {
                    view.label.setTextColor(context.resources.getColor(R.color.grey_2))
                }
                view.tag = element
                view.setOnClickListener(callback)
                labelList.addView(view)
            }
        }
    }
}