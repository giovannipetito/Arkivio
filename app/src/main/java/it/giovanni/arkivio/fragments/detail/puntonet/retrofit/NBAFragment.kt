package it.giovanni.arkivio.fragments.detail.puntonet.retrofit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.NbaLayoutBinding
import it.giovanni.arkivio.databinding.NbaTeamItemBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

class NBAFragment : DetailFragment() {

    private var layoutBinding: NbaLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: NBAViewModel

    override fun getTitle(): Int {
        return R.string.nba_retrofit_title
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
        layoutBinding = NbaLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[NBAViewModel::class.java]

        showProgressDialog()
        viewModel.fetchTeams(0)

        viewModel.list.observe(viewLifecycleOwner) { list ->
            hideProgressDialog()
            showUsers(list)
        }
    }

    private fun showUsers(list: List<AllTeamsDataItem>) {
        if (list.isEmpty())
            return
        for (team in list) {

            val itemBinding: NbaTeamItemBinding = NbaTeamItemBinding.inflate(layoutInflater, binding?.nbaContainer, false)
            val itemView: View = itemBinding.root

            val labelNBATeamName: TextView = itemBinding.nbaTeamName
            labelNBATeamName.text = team.name

            if (isDarkMode) {
                labelNBATeamName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }
            else {
                labelNBATeamName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }

            binding?.nbaContainer?.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}