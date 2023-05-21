package it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.UsersPagingLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import kotlinx.coroutines.launch

class UsersPagingFragment : DetailFragment() {

    private var layoutBinding: UsersPagingLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: UsersViewModel
    private lateinit var usersPagingDataAdapter: UsersPagingDataAdapter

    override fun getTitle(): Int {
        return R.string.users_paging_title
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
        layoutBinding = UsersPagingLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[UsersViewModel::class.java]

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {

        usersPagingDataAdapter = UsersPagingDataAdapter()

        binding?.usersPagingRecyclerView?.apply {
            adapter = usersPagingDataAdapter
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    /**
     * Il metodo loadData() carica i dati in un PagingDataAdapter utilizzando le coroutine Kotlin e
     * il lifecycleScope. Nel dettaglio, imposta una coroutine che ascolta le modifiche nel PagingData
     * emesso dal ViewModel e invia tali dati al PagingDataAdapter quando cambia.

     * loadData() avvia una nuova coroutine utilizzando lifecycleScope.launch. All'interno della
     * coroutine, raccoglie i PagingData emessi da viewModel.listData utilizzando la funzione collect.
     *
     * Quando collect riceve un nuovo valore, lo assegna a una variabile data di tipo PagingData<Data>.
     * Quindi, chiama la funzione submitData di adapter con i dati come argomento.
     */
    private fun loadData() {
        lifecycleScope.launch {
            viewModel.listData.collect {
                val data: PagingData<Data> = it
                usersPagingDataAdapter.submitData(data)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}