package it.giovanni.arkivio.fragments.detail.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.UserCardBinding
import it.giovanni.arkivio.databinding.SimpleRetrofitLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.User
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.UsersResponse
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.restclient.retrofit.IRetrofit
import it.giovanni.arkivio.restclient.retrofit.SimpleRetrofitClient
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences

class SimpleRetrofitFragment: DetailFragment(), IRetrofit {

    private var layoutBinding: SimpleRetrofitLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.simple_retrofit_title
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
        layoutBinding = SimpleRetrofitLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = loadDarkModeStateFromPreferences()

        SimpleRetrofitClient.getUsers(this)
        showProgressDialog()
    }

    override fun onRetrofitSuccess(usersResponse: UsersResponse?, message: String) {
        hideProgressDialog()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        showUsers(usersResponse?.users)
    }

    override fun onRetrofitFailure(message: String) {
        hideProgressDialog()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUsers(list: List<User?>?) {

        binding?.retrofitUsersContainer?.removeAllViews()

        if (list.isNullOrEmpty())
            return

        for (user in list) {

            val itemBinding: UserCardBinding = UserCardBinding.inflate(layoutInflater, binding?.retrofitUsersContainer, false)
            val itemView: View = itemBinding.root

            val imageUrl: String? = user?.avatar

            Glide.with(requireActivity())
                .load(imageUrl)
                .placeholder(R.mipmap.logo_audioslave_blue)
                .error(R.mipmap.logo_audioslave_blue)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(54)))
                .into(itemBinding.userAvatar)

            val labelFirstName: TextView = itemBinding.userFirstName
            val labelLastName: TextView = itemBinding.userLastName

            labelFirstName.text = user?.firstName
            labelLastName.text = user?.lastName

            if (isDarkMode) {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            } else {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }

            binding?.retrofitUsersContainer?.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}