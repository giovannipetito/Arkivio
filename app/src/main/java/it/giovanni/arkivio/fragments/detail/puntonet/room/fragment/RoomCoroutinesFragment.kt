package it.giovanni.arkivio.fragments.detail.puntonet.room.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.customview.popup.EditDialogPopup
import it.giovanni.arkivio.databinding.RoomCoroutinesLayoutBinding
import it.giovanni.arkivio.databinding.UserCardBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.puntonet.room.EditUserListener
import it.giovanni.arkivio.fragments.detail.puntonet.room.KEY_AGE
import it.giovanni.arkivio.fragments.detail.puntonet.room.KEY_FIRST_NAME
import it.giovanni.arkivio.fragments.detail.puntonet.room.KEY_LAST_NAME
import it.giovanni.arkivio.fragments.detail.puntonet.room.entity.User
import it.giovanni.arkivio.fragments.detail.puntonet.room.viewmodel.RoomCoroutinesViewModel
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

/**
 * Utilizzando le coroutine, puoi chiamare le operazioni del database Room tramite funzioni di
 * sospensione e quindi avviare una coroutine all'interno del componente dell'applicazione per
 * eseguire queste operazioni in modo asincrono. Il risultato può essere osservato tramite
 * LiveData o gestito in altri modi a seconda dell'architettura dell'applicazione.
 */
class RoomCoroutinesFragment : DetailFragment(), EditUserListener {

    private var layoutBinding: RoomCoroutinesLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: RoomCoroutinesViewModel

    private lateinit var editDialogPopup: EditDialogPopup

    private lateinit var customDialogPopup: CustomDialogPopup

    private lateinit var selectedUser: User

    override fun getTitle(): Int {
        return R.string.room_coroutines_title
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
        layoutBinding = RoomCoroutinesLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RoomCoroutinesViewModel::class.java]

        setViewStyle()

        binding?.fabInsertUser?.setOnClickListener {
            showInsertUserDialog()
            // Device File Explorer/data/data/it.giovanni.arkivio/databases/arkivio_database
        }

        binding?.buttonGetCoroutinesUsers?.setOnClickListener {
            updateView()
        }

        binding?.buttonDeleteAllUsers?.setOnClickListener {
            showDeleteAllUsersDialog()
        }
    }

    private fun updateView() {
        showProgressDialog()
        viewModel.getUsers()
        viewModel.users.observe(viewLifecycleOwner) { users ->
            hideProgressDialog()
            showUsers(users)
        }
    }

    private fun showUsers(list: List<User>) {

        binding?.usersCoroutinesContainer?.removeAllViews()

        if (list.isEmpty())
            return

        for (user in list) {

            val itemBinding: UserCardBinding = UserCardBinding.inflate(layoutInflater, binding?.usersCoroutinesContainer, false)
            val itemView: View = itemBinding.root

            val labelId: TextView = itemBinding.userId
            val labelFirstName: TextView = itemBinding.userFirstName
            val labelLastName: TextView = itemBinding.userLastName
            val labelAge: TextView = itemBinding.userAge

            labelId.text = user.id.toString()
            labelFirstName.text = user.firstName
            labelLastName.text = user.lastName
            val age = user.age + " anni"
            labelAge.text = age

            itemBinding.icoUpdateUser.setOnClickListener {
                showUpdateUserDialog(user)
            }

            itemBinding.icoDeleteUser.setOnClickListener {
                showDeleteUserDialog(user)
            }

            if (isDarkMode) {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelAge.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            } else {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelAge.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }

            binding?.usersCoroutinesContainer?.addView(itemView)
        }
    }

    private fun showInsertUserDialog() {

        selectedUser = User(0, "", "", "")

        val map: HashMap<String, Any> = HashMap()
        map[KEY_FIRST_NAME] = selectedUser.firstName
        map[KEY_LAST_NAME] = selectedUser.lastName
        map[KEY_AGE] = selectedUser.age

        editDialogPopup = EditDialogPopup(currentActivity, R.style.PopupTheme)
        editDialogPopup.setCancelable(false)
        editDialogPopup.setTitle(resources.getString(R.string.insert_user_title_dialog))
        editDialogPopup.setMessage(resources.getString(R.string.insert_user_message_dialog))
        editDialogPopup.setEditLabels(map, this)

        editDialogPopup.setButtons(resources.getString(R.string.button_confirm), {

            if (selectedUser.firstName == "" || selectedUser.lastName == "" || selectedUser.age == "") {
                Toast.makeText(requireContext(), "Riempi tutti i campi!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addUser(selectedUser)
                editDialogPopup.dismiss()
                updateView()
            }
        },
            resources.getString(R.string.button_cancel), {
                editDialogPopup.dismiss()
            }
        )
        editDialogPopup.show()
    }

    private fun showUpdateUserDialog(user: User) {

        selectedUser = User(user.id, user.firstName, user.lastName, user.age)

        val map: HashMap<String, Any> = HashMap()
        map[KEY_FIRST_NAME] = selectedUser.firstName
        map[KEY_LAST_NAME] = selectedUser.lastName
        map[KEY_AGE] = selectedUser.age

        editDialogPopup = EditDialogPopup(currentActivity, R.style.PopupTheme)
        editDialogPopup.setCancelable(false)
        editDialogPopup.setTitle(resources.getString(R.string.update_user_title_dialog))
        editDialogPopup.setMessage(resources.getString(R.string.update_user_message_dialog))
        editDialogPopup.setEditLabels(map, this)

        editDialogPopup.setButtons(resources.getString(R.string.button_confirm), {

            if (selectedUser.firstName == user.firstName && selectedUser.lastName == user.lastName && selectedUser.age == user.age) {
                Toast.makeText(requireContext(), "L'utente " + selectedUser.firstName + " non ha subito alcuna modifica!", Toast.LENGTH_SHORT).show()
            } else if (selectedUser.firstName == "" || selectedUser.lastName == "" || selectedUser.age == "") {
                Toast.makeText(requireContext(), "Riempi tutti i campi!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateUser(selectedUser)
                editDialogPopup.dismiss()
                updateView()
            }
        },
            resources.getString(R.string.button_cancel), {
                editDialogPopup.dismiss()
            }
        )
        editDialogPopup.show()
    }

    private fun showDeleteUserDialog(user: User) {

        selectedUser = User(user.id, user.firstName, user.lastName, user.age)

        customDialogPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
        customDialogPopup.setCancelable(false)
        customDialogPopup.setTitle(resources.getString(R.string.delete_user_title_dialog))
        customDialogPopup.setMessage(resources.getString(R.string.delete_user_message_dialog))

        customDialogPopup.setButtons(resources.getString(R.string.button_confirm), {
            viewModel.deleteUser(selectedUser)
            customDialogPopup.dismiss()
            updateView()
        },
            resources.getString(R.string.button_cancel), {
                customDialogPopup.dismiss()
            }
        )
        customDialogPopup.show()
    }

    private fun showDeleteAllUsersDialog() {

        customDialogPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
        customDialogPopup.setCancelable(false)
        customDialogPopup.setTitle(resources.getString(R.string.delete_all_users_title_dialog))
        customDialogPopup.setMessage(resources.getString(R.string.delete_all_users_message_dialog))

        customDialogPopup.setButtons(resources.getString(R.string.button_confirm), {
            viewModel.deleteUsers()
            customDialogPopup.dismiss()
            updateView()
        },
            resources.getString(R.string.button_cancel), {
                customDialogPopup.dismiss()
            }
        )
        customDialogPopup.show()
    }

    override fun onAddFirstNameChangedListener(input: String) {
        Log.i("[ROOM]", "$KEY_FIRST_NAME: $input")
        selectedUser.firstName = input
    }

    override fun onAddLastNameChangedListener(input: String) {
        Log.i("[ROOM]", "$KEY_LAST_NAME: $input")
        selectedUser.lastName = input
    }

    override fun onAddAgeChangedListener(input: String) {
        Log.i("[ROOM]", "$KEY_AGE: $input")
        selectedUser.age = input
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
        if (isDarkMode) {
            binding?.buttonGetCoroutinesUsers?.style(R.style.ButtonEmptyDarkMode)
            binding?.buttonDeleteAllUsers?.style(R.style.ButtonEmptyDarkMode)
        }
        else {
            binding?.buttonGetCoroutinesUsers?.style(R.style.ButtonEmptyLightMode)
            binding?.buttonDeleteAllUsers?.style(R.style.ButtonEmptyLightMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}