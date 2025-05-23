package it.giovanni.arkivio.customview.dialog

import android.app.Activity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DialogEditElementBinding
import it.giovanni.arkivio.puntonet.room.EditUserListener
import it.giovanni.arkivio.puntonet.room.KEY_AGE
import it.giovanni.arkivio.puntonet.room.KEY_FIRST_NAME
import it.giovanni.arkivio.puntonet.room.KEY_LAST_NAME

class EditDialog(activity: Activity, @StyleRes themeResId: Int) : CoreDialog(activity, themeResId) {

    var layoutBinding: DialogEditElementBinding? = null
    val binding get() = layoutBinding

    init {
        prepare()
    }

    fun setEditLabels(map: HashMap<String, Any>, callback: EditUserListener) {
        labelList.visibility = View.VISIBLE
        titleDialog.visibility = View.VISIBLE
        subtitleDialog.visibility = View.GONE
        messageDialog.visibility = View.VISIBLE
        buttonsContainer.visibility = View.VISIBLE
        // Inflate element
        if (map.isNotEmpty()) {

            for ((key, value) in map) {
                layoutBinding = DialogEditElementBinding.inflate(LayoutInflater.from(context))
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