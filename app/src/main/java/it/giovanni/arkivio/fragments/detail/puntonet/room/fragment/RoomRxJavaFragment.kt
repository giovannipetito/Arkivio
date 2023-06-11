package it.giovanni.arkivio.fragments.detail.puntonet.room.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RoomRxjavaLayoutBinding
import it.giovanni.arkivio.databinding.UserCardBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.puntonet.room.entity.User
import it.giovanni.arkivio.fragments.detail.puntonet.room.viewmodel.RoomRxJavaViewModel
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

/**
 * Utilizzando RxJava, puoi chiamare le operazioni del database Room e gestire l'esecuzione
 * asincrona utilizzando i vari operatori RxJava. Puoi iscriverti a un thread in background
 * usando subscribeOn(Schedulers.io()) e osservare i risultati sul thread principale usando
 * osservOn(AndroidSchedulers.mainThread()). Gli aggiornamenti possono essere osservati con
 * LiveData o gestiti in altri modi a seconda dell'architettura dell'applicazione.
 */
class RoomRxJavaFragment : DetailFragment() {

    private var layoutBinding: RoomRxjavaLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: RoomRxJavaViewModel

    override fun getTitle(): Int {
        return R.string.room_rxjava_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = RoomRxjavaLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RoomRxJavaViewModel::class.java]

        setViewStyle()

        handleInsertButtonEnabling()

        binding?.buttonInsertUser?.setOnClickListener {
            val firstName: String = binding?.editFirstName?.text.toString()
            val lastName: String = binding?.editLastName?.text.toString()
            val age: String = binding?.editAge?.text.toString()

            val newUser = User(1, firstName, lastName, age.toInt())
            viewModel.addUser(newUser)
        }

        binding?.buttonShowUsers?.setOnClickListener {
            showProgressDialog()
            viewModel.getUsers()
        }

        viewModel.users.observe(viewLifecycleOwner) { users ->
            hideProgressDialog()
            showUsers(users)
        }
    }

    private fun showUsers(list: List<User>) {
        if (list.isEmpty())
            return
        for (user in list) {

            val itemBinding: UserCardBinding = UserCardBinding.inflate(layoutInflater, binding?.usersRxjavaContainer, false)
            val itemView: View = itemBinding.root

            val labelFirstName: TextView = itemBinding.userFirstName
            val labelLastName: TextView = itemBinding.userLastName
            val labelAge: TextView = itemBinding.userAge

            labelFirstName.text = user.firstName
            labelLastName.text = user.lastName
            labelAge.text = user.age.toString()

            if (isDarkMode) {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelAge.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            } else {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelAge.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }

            binding?.usersRxjavaContainer?.addView(itemView)
        }
    }

    private fun handleInsertButtonEnabling() {

        binding?.editFirstName?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding?.buttonInsertUser?.isEnabled = binding?.editFirstName?.text?.trim()?.isNotEmpty()!! && binding?.editFirstName?.text?.trim()?.isNotEmpty()!! && binding?.editAge?.text?.trim()?.isNotEmpty()!!
            }
        })

        binding?.editLastName?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding?.buttonInsertUser?.isEnabled = binding?.editFirstName?.text?.trim()?.isNotEmpty()!! && binding?.editFirstName?.text?.trim()?.isNotEmpty()!! && binding?.editAge?.text?.trim()?.isNotEmpty()!!
            }
        })

        binding?.editAge?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding?.buttonInsertUser?.isEnabled = binding?.editFirstName?.text?.trim()?.isNotEmpty()!! && binding?.editFirstName?.text?.trim()?.isNotEmpty()!! && binding?.editAge?.text?.trim()?.isNotEmpty()!!
            }
        })
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
        if (isDarkMode) {
            binding?.buttonInsertUser?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonShowUsers?.style(R.style.ButtonNormalDarkMode)
        }
        else {
            binding?.buttonInsertUser?.style(R.style.ButtonNormalLightMode)
            binding?.buttonShowUsers?.style(R.style.ButtonNormalLightMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        viewModel.disposable?.dispose()
    }
}