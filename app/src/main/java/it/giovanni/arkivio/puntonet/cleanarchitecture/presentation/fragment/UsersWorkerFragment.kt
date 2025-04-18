package it.giovanni.arkivio.puntonet.cleanarchitecture.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.UserCardBinding
import it.giovanni.arkivio.databinding.UsersWorkerLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.puntonet.cleanarchitecture.presentation.viewmodel.UsersWorkerViewModel
import it.giovanni.arkivio.puntonet.retrofitgetpost.UsersResponse
import it.giovanni.arkivio.puntonet.workmanager.WorkerViewModelProviderFactory
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class UsersWorkerFragment : DetailFragment() {

    private var layoutBinding: UsersWorkerLayoutBinding? = null
    private val binding get() = layoutBinding

    private val viewModel: UsersWorkerViewModel by viewModels {
        WorkerViewModelProviderFactory(currentActivity.application)
    }

    override fun getTitle(): Int {
        return R.string.clean_architecture_worker_title
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

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = UsersWorkerLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProgressDialog()

        viewModel.getUsersFromDatabase()
        viewModel.users.observe(viewLifecycleOwner) { users ->
            if (users.isNullOrEmpty()) {
                Log.i("[WORKER]", "1) ENTRO QUI")
                viewModel.getUsersFromWorkManager(currentActivity.application, 1)
                viewModel.workInfos.observe(viewLifecycleOwner, workInfosObserver())
            }
            else {
                Log.i("[WORKER]", "2) ENTRO QUI")
                hideProgressDialog()
                showUsers(users)
            }
        }
    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            if (listOfWorkInfo.isEmpty()) {
                Log.i("[WORKER]", "3) ENTRO QUI")
                return@Observer
            }

            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                Log.i("[WORKER]", "4) ENTRO QUI")
                viewModel.getUsersFromDatabase()
                viewModel.users.observe(viewLifecycleOwner) { users ->
                    Log.i("[WORKER]", "5) ENTRO QUI")
                    if (!users.isNullOrEmpty()) {
                        Log.i("[WORKER]", "6) ENTRO QUI")
                        hideProgressDialog()
                        showUsers(users)
                    }
                }
            }
        }
    }

    private fun Data.getUsers(key: String): UsersResponse? {
        var usersResponse: UsersResponse? = null
        val jsonString = getString(key)
        if (jsonString != null) {
            val gson: Gson? = GsonBuilder().serializeNulls().create()
            usersResponse = gson?.fromJson(jsonString, UsersResponse::class.java)
        }
        return usersResponse
    }

    private fun showUsers(list: List<it.giovanni.arkivio.puntonet.retrofitgetpost.User?>?) {

        binding?.usersContainer?.removeAllViews()

        if (list.isNullOrEmpty())
            return

        for (character in list) {

            val itemBinding: UserCardBinding = UserCardBinding.inflate(layoutInflater, binding?.usersContainer, false)
            val itemView: View = itemBinding.root

            val characterName: TextView = itemBinding.userFirstName
            val characterImage: ImageView = itemBinding.userAvatar

            val fullName = character?.firstName + character?.lastName
            characterName.text = fullName

            val imageUrl: String? = character?.avatar
            Glide.with(requireContext())
                .load(imageUrl)
                .into(characterImage)

            binding?.usersContainer?.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}