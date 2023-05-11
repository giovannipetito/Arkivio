package it.giovanni.arkivio.fragments.detail.puntonet.paging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.PagingLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import kotlinx.coroutines.launch

class PagingFragment : DetailFragment() {

    private var layoutBinding: PagingLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var viewModel: PagingViewModel

    override fun getTitle(): Int {
        return R.string.paging_title
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
        layoutBinding = PagingLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[PagingViewModel::class.java]

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {

        characterAdapter = CharacterAdapter()

        binding?.pagingRecyclerView?.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    private fun loadData() {

        lifecycleScope.launch {
            viewModel.listData.collect {
                characterAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}