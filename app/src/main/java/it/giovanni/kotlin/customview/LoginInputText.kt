package it.giovanni.kotlin.customview

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import it.giovanni.kotlin.R
import kotlinx.android.synthetic.main.input_text.view.*

class LoginInputText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    init {
        var passwordHidden = true
        LayoutInflater.from(context).inflate(R.layout.input_text, this, true)
        orientation = VERTICAL

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.LoginInputText, 0, 0)

            val title = resources.getText(
                typedArray.getResourceId(R.styleable.LoginInputText_login_textview_title, R.string.login_your_mail))

            val hint = resources.getText(
                typedArray.getResourceId(R.styleable.LoginInputText_login_textview_hint, R.string.login_hint_username))

            val icon = typedArray.getDrawable(R.styleable.LoginInputText_login_textview_icon)

            val texttype = resources.getText(
                typedArray.getResourceId(R.styleable.LoginInputText_login_textview_input_type, R.string.input_type_text))

            label.text = title
            image.setImageDrawable(icon)
            inputtext.hint = hint
            when (texttype) {
                "text" -> {
                    inputtext.inputType = InputType.TYPE_CLASS_TEXT
                    dominio_windtre.visibility = View.VISIBLE
                    val params = RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    params.addRule(RelativeLayout.LEFT_OF, dominio_windtre.id)
                    params.addRule(RelativeLayout.RIGHT_OF, image.id)
                    inputtext.layoutParams = params
                }
                "password" -> {
                    inputtext.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    inputtext.typeface = Typeface.DEFAULT
                    show_hide_password.visibility = View.VISIBLE
                    show_hide_password.setOnClickListener {
                        passwordHidden = !passwordHidden
                        if (passwordHidden) {
                            show_hide_password.setImageResource(R.drawable.ico_show_password)
                            inputtext.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                            inputtext.transformationMethod = PasswordTransformationMethod()
                        }else {
                            show_hide_password.setImageResource(R.drawable.ico_hide_password)
                            inputtext.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                            inputtext.transformationMethod = SingleLineTransformationMethod()
                        }
                        // inputtext.typeface = Typeface.DEFAULT
                        inputtext.setSelection(inputtext.text.length)
                    }
                    val params = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    params.addRule(RelativeLayout.LEFT_OF, show_hide_password.id)
                    params.addRule(RelativeLayout.RIGHT_OF, image.id)
                    inputtext.layoutParams = params
                }
            }

            typedArray.recycle()
        }
    }

    fun getText(): String {
        return inputtext.text.toString()
    }

    fun setInputText(value: String) {
        inputtext.setText(value)
    }

    fun getInputText(): EditText {
        return inputtext
    }
}