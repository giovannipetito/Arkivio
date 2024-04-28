package it.giovanni.arkivio.fragments

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.LoginLayoutBinding
import it.giovanni.arkivio.utils.SharedPreferencesManager.loadRememberMeFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.saveRememberMeToPreferences
import it.giovanni.arkivio.utils.Utils.clearCache
import it.giovanni.arkivio.utils.Utils.isOnline

class LoginFragment : BaseFragment(SectionType.LOGIN) {

    private var layoutBinding: LoginLayoutBinding? = null
    private val binding get() = layoutBinding

    private var rememberMe: Boolean = false
    private var action: Action? = null

    internal enum class Action {
        NONE
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = LoginLayoutBinding.inflate(inflater, container, false)

        currentActivity.setStatusBarTransparent()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action = Action.NONE

        binding?.checkboxRememberMe?.isChecked = loadRememberMeFromPreferences()

        binding?.username?.getInputText()?.setOnKeyListener { _, _, _ ->
            binding?.loginButton?.isEnabled = binding?.username?.getText()?.trim()?.isNotEmpty()!! && binding?.password?.getText()?.trim()?.isNotEmpty()!!
            false
        }

        binding?.username?.getInputText()?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding?.loginButton?.isEnabled = binding?.username?.getText()?.trim()?.isNotEmpty()!! && binding?.password?.getText()?.trim()?.isNotEmpty()!!
            }
        })

        binding?.password?.getInputText()?.setOnKeyListener { _, _, _ ->
            binding?.loginButton?.isEnabled = binding?.username?.getText()?.trim()?.isNotEmpty()!! && binding?.password?.getText()?.trim()?.isNotEmpty()!!
            false
        }

        binding?.password?.getInputText()?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding?.loginButton?.isEnabled = binding?.username?.getText()?.trim()?.isNotEmpty()!! && binding?.password?.getText()?.trim()?.isNotEmpty()!!
            }
        })

        binding?.loginButton?.setOnClickListener {
            if (isOnline()) {
                showProgressDialog()
                currentActivity.openMainFragment()
            } else
                Toast.makeText(context,"Errore di connessione", Toast.LENGTH_SHORT).show()
        }

        binding?.checkboxRememberMe?.setOnCheckedChangeListener { _, isChecked ->
            rememberMe = isChecked
            saveRememberMeToPreferences(rememberMe)
        }

        binding?.clearCache?.setOnClickListener {
            clearCache(context)
        }

        val apiVersion = Build.VERSION.SDK_INT
        binding?.apiVersionText?.text = getString(R.string.api_version, "" + apiVersion)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}