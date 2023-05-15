package it.giovanni.arkivio.fragments.detail.puntonet.paging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RickMortyLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RickMortyFragment : DetailFragment() {

    private var layoutBinding: RickMortyLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: RickMortyViewModel

    private lateinit var characterAdapter: CharacterAdapter

    private var job: Job? = null

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
        layoutBinding = RickMortyLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[RickMortyViewModel::class.java]

        setupAdapter()
        loadData()

        binding?.includeErrorSmile?.errorSmile?.setOnClickListener {
            characterAdapter.refresh()
        }
    }

    private fun setupAdapter() {

        characterAdapter = CharacterAdapter {
            onItemClicked(it)
        }

        binding?.pagingRecyclerView?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }

        binding?.pagingRecyclerView?.adapter = characterAdapter.withLoadStateFooter(
            footer = FooterAdapter {
                retry()
            }
        )

        characterAdapter.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.Loading) {
                    if (characterAdapter.snapshot().isEmpty()) {
                        showProgressDialog()
                    }
                    binding?.includeErrorSmile?.errorSmile?.visibility = View.GONE
                } else {
                    hideProgressDialog()

                    val error = when {
                        loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                        loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                        loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error

                        else -> null
                    }
                    error?.let {
                        if (characterAdapter.snapshot().isEmpty()) {
                            binding?.includeErrorSmile?.errorSmile?.visibility = View.VISIBLE
                            binding?.includeErrorSmile?.errorMessageSmile?.text = it.error.localizedMessage
                        }
                    }
                }

        }
    }

    /**
     * Il metodo loadData() carica i dati in un PagingDataAdapter utilizzando le coroutine Kotlin e
     * il lifecycleScope. Nel dettaglio, imposta una coroutine che ascolta le modifiche nel PagingData
     * emesso dal ViewModel e invia tali dati al PagingDataAdapter quando cambia.

     * loadData() avvia una nuova coroutine utilizzando lifecycleScope.launch. All'interno della
     * coroutine, raccoglie i PagingData emessi da viewModel.listData utilizzando la funzione collect.
     *
     * Quando collect riceve un nuovo valore, lo assegna a una variabile data di tipo PagingData<RickMorty>.
     * Quindi, chiama la funzione submitData di characterAdapter con i dati come argomento.
     */
    private fun loadData() {
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.getDataFlow().collectLatest {
                val pagingData: PagingData<RickMorty> = it
                characterAdapter.submitData(pagingData)
            }
        }
    }

    private fun retry() {
        characterAdapter.retry()
    }

    private fun onItemClicked(rickMorty: RickMorty) {
        Toast.makeText(requireContext(), rickMorty.name, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}