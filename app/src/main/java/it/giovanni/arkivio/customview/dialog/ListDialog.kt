package it.giovanni.arkivio.customview.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DialogLabelElementBinding
import kotlin.collections.ArrayList

class ListDialog(activity: Activity, @StyleRes themeResId: Int) : CoreDialog(activity, themeResId) {

    var layoutBinding: DialogLabelElementBinding? = null
    val binding get() = layoutBinding

    init {
        prepare()
    }

    fun setLabels(list: ArrayList<String>, callback: View.OnClickListener) {
        labelList.visibility = View.VISIBLE
        titleDialog.visibility = View.GONE
        subtitleDialog.visibility = View.GONE
        messageDialog.visibility = View.VISIBLE
        buttonsContainer.visibility = View.GONE
        // Inflate element
        if (list.size > 0) {
            val i = list.iterator()
            while (i.hasNext()) {

                layoutBinding = DialogLabelElementBinding.inflate(LayoutInflater.from(context))
                val view: View? = binding?.root

                val element: String = i.next()
                binding?.label?.text = element.uppercase()
                if (element == "Annulla") {
                    binding?.label?.setTextColor(ContextCompat.getColor(context, R.color.red_700))
                }
                view?.tag = element
                view?.setOnClickListener(callback)
                labelList.addView(view)
            }
        }
    }
}