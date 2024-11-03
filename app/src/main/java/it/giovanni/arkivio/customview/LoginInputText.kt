package it.giovanni.arkivio.customview

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.InputTextBinding

class LoginInputText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    var layoutBinding: InputTextBinding? = null
    val binding get() = layoutBinding

    init {
        // LayoutInflater.from(context).inflate(R.layout.input_text, this, true)
        layoutBinding = InputTextBinding.inflate(LayoutInflater.from(context), this, true)

        orientation = VERTICAL

        var passwordHidden = true

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.LoginInputText, 0, 0)

            val title = resources.getText(
                typedArray.getResourceId(R.styleable.LoginInputText_login_textview_title, R.string.login_your_mail))

            val hint = resources.getText(
                typedArray.getResourceId(R.styleable.LoginInputText_login_textview_hint, R.string.login_hint_username))

            val icon = typedArray.getDrawable(R.styleable.LoginInputText_login_textview_icon)

            val textType = resources.getText(
                typedArray.getResourceId(R.styleable.LoginInputText_login_textview_input_type, R.string.input_type_text))

            binding?.label?.text = title
            binding?.image?.setImageDrawable(icon)
            binding?.inputText?.hint = hint
            when (textType) {
                "text" -> {
                    binding?.inputText?.inputType = InputType.TYPE_CLASS_TEXT
                    binding?.mailDomain?.visibility = VISIBLE
                    val params = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    params.addRule(RelativeLayout.LEFT_OF, binding?.mailDomain?.id!!)
                    params.addRule(RelativeLayout.RIGHT_OF, binding?.image?.id!!)
                    binding?.inputText?.layoutParams = params
                }
                "password" -> {
                    binding?.inputText?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    val firaRegular = Typeface.createFromAsset(context.assets, "fonts/fira_regular.ttf")
                    // binding?.inputText?.typeface = Typeface.DEFAULT
                    binding?.inputText?.typeface = firaRegular
                    binding?.showHidePassword?.visibility = VISIBLE
                    binding?.showHidePassword?.setOnClickListener {
                        passwordHidden = !passwordHidden
                        if (passwordHidden) {
                            binding?.showHidePassword?.setImageResource(R.drawable.ico_show_password)
                            binding?.inputText?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding?.inputText?.transformationMethod = PasswordTransformationMethod()
                        } else {
                            binding?.showHidePassword?.setImageResource(R.drawable.ico_hide_password)
                            binding?.inputText?.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                            binding?.inputText?.transformationMethod = SingleLineTransformationMethod()
                        }
                        binding?.inputText?.setSelection(binding?.inputText?.text?.length!!)
                    }
                    val params = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    params.addRule(RelativeLayout.LEFT_OF, binding?.showHidePassword?.id!!)
                    params.addRule(RelativeLayout.RIGHT_OF, binding?.image?.id!!)
                    binding?.inputText?.layoutParams = params
                }
            }

            typedArray.recycle()
        }
    }

    fun getText(): String {
        return binding?.inputText?.text.toString()
    }

    fun getInputText(): EditText {
        return binding?.inputText!!
    }
}