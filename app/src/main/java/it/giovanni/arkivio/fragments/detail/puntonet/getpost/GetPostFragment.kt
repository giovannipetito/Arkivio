package it.giovanni.arkivio.fragments.detail.puntonet.getpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.paris.extensions.style
import com.bumptech.glide.Glide
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.GetPostLayoutBinding
import it.giovanni.arkivio.databinding.GetPostUserItemBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager

class GetPostFragment : DetailFragment() {

    private var layoutBinding: GetPostLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: GetPostViewModel

    override fun getTitle(): Int {
        return R.string.get_post_title
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
        layoutBinding = GetPostLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[GetPostViewModel::class.java]

        showProgressDialog()
        viewModel.fetchUsers(0)

        viewModel.list.observe(viewLifecycleOwner) { list ->
            hideProgressDialog()
            showUsers(list)
        }

        binding?.buttonAddUser?.setOnClickListener {
            currentActivity.openDetail(Globals.POST, null)
        }
    }

    private fun showUsers(list: List<ListUsersDataItem>) {
        if (list.isEmpty())
            return
        for (user in list) {

            val itemBinding: GetPostUserItemBinding = GetPostUserItemBinding.inflate(layoutInflater, binding?.getPostContainer, false)
            val itemView: View = itemBinding.root

            val imageUrl: String? = user.avatar

            Glide.with(requireActivity())
                .load(imageUrl)
                .into(itemBinding.userAvatar)

            val labelFirstName: TextView = itemBinding.getPostUserFirstName
            val labelLastName: TextView = itemBinding.getPostUserLastName

            labelFirstName.text = user.firstName
            labelLastName.text = user.lastName

            if (isDarkMode) {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding?.buttonAddUser?.style(R.style.ButtonNormalDarkMode)
            }
            else {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                binding?.buttonAddUser?.style(R.style.ButtonNormalLightMode)
            }

            binding?.getPostContainer?.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}