package it.giovanni.arkivio.customview.popup

import android.app.Activity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.PopupEditElementBinding
import it.giovanni.arkivio.fragments.detail.puntonet.room.EditUserListener
import it.giovanni.arkivio.fragments.detail.puntonet.room.KEY_AGE
import it.giovanni.arkivio.fragments.detail.puntonet.room.KEY_FIRST_NAME
import it.giovanni.arkivio.fragments.detail.puntonet.room.KEY_LAST_NAME

class EditDialogPopup(activity: Activity, @StyleRes themeResId: Int) : CustomDialogPopup(activity, themeResId) {

    var layoutBinding: PopupEditElementBinding? = null
    val binding get() = layoutBinding

    init {
        prepare()
    }

    fun setEditLabels(map: HashMap<String, Any>, callback: EditUserListener) {
        labelList.visibility = View.VISIBLE
        titleDialog.visibility = View.GONE
        subtitleDialog.visibility = View.GONE
        messageDialog.visibility = View.VISIBLE
        buttonsContainer.visibility = View.VISIBLE
        // Inflate element
        if (map.size > 0) {

            for ((key, value) in map) {
                layoutBinding = PopupEditElementBinding.inflate(LayoutInflater.from(context))
                val view: View? = binding?.root

                val element: String = value.toString()
                binding?.labelEdit?.setText(element)

                if (key == KEY_FIRST_NAME) {
                    binding?.labelEdit?.setHint(R.string.edit_first_name)
                    binding?.labelEdit?.inputType = InputType.TYPE_CLASS_TEXT
                }

                if (key == KEY_LAST_NAME) {
                    binding?.labelEdit?.setHint(R.string.edit_last_name)
                    binding?.labelEdit?.inputType = InputType.TYPE_CLASS_TEXT
                }

                if (key == KEY_AGE) {
                    binding?.labelEdit?.setHint(R.string.edit_age)
                    binding?.labelEdit?.inputType = InputType.TYPE_CLASS_NUMBER
                }

                binding?.labelEdit?.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (key == KEY_FIRST_NAME)
                            callback.onAddFirstNameChangedListener(s.toString())
                        if (key == KEY_LAST_NAME)
                            callback.onAddLastNameChangedListener(s.toString())
                        if (key == KEY_AGE)
                            callback.onAddAgeChangedListener(s.toString())
                    }
                })
                view?.tag = element
                labelList.addView(view)
            }
        }
    }
}