package it.giovanni.kotlin.customview.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import it.giovanni.kotlin.R
import kotlinx.android.synthetic.main.popup_labels_element.view.*

class DialogListPopup : CustomDialogPopup {

    private var labels: ArrayList<String> = ArrayList()
    val LABEL_DELETE = "annulla"

    constructor(activity: Activity, @StyleRes themeResId: Int) : super(activity, themeResId) {
        this.activity = activity
        prepare()
    }

    @SuppressLint("InflateParams")
    fun setLabels(_list: ArrayList<String>, callback: View.OnClickListener) {
        labels = _list
        labelList!!.visibility = View.VISIBLE
        bodyContent!!.visibility = View.VISIBLE
        buttonsContainer!!.visibility = View.GONE
        // inflate element
        if (labels.size > 0) {
            val i = labels.iterator()
            while (i.hasNext()) {
                val element: String = i.next()
                val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.popup_labels_element, null)
                view.label.text = element.toUpperCase()
                if (element == LABEL_DELETE) {
                    view.label.setTextColor(context.resources.getColor(R.color.grey_2))
                }
                view.tag = element
                view.setOnClickListener(callback)
                labelList!!.addView(view)
            }
        }
    }
}