package it.giovanni.arkivio.fragments.detail.dragdrop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.FavoritesLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class FavoritesFragment : DetailFragment(), FavoritesAdapter.OnAdapterListener {

    private var layoutBinding: FavoritesLayoutBinding? = null
    private val binding get() = layoutBinding

    private var topRecyclerView: RecyclerView? = null
    private var bottomRecyclerView: RecyclerView? = null

    private val viewModel: FavoritesViewModel by viewModels()

    private lateinit var topAdapter: FavoritesAdapter
    private lateinit var bottomAdapter: FavoritesAdapter

    override fun getTitle(): Int {
        return R.string.favorites_title
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
        layoutBinding = FavoritesLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        setupRecyclerViews()

        // Observe ViewModel data
        viewModel.topData.observe(viewLifecycleOwner) { topData ->
            topAdapter.submitList(topData)
        }

        viewModel.bottomData.observe(viewLifecycleOwner) { bottomData ->
            bottomAdapter.submitList(bottomData)
        }

        return binding?.root
    }

    private fun setupRecyclerViews() {

        topRecyclerView = binding?.topRecyclerView
        bottomRecyclerView = binding?.bottomRecyclerView

        // Set up top RecyclerView
        topAdapter = FavoritesAdapter(isSwappable = true, onAdapterListener = this)
        topRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 4)
        topRecyclerView?.adapter = topAdapter

        // Set up bottom RecyclerView
        bottomAdapter = FavoritesAdapter(isSwappable = true, onAdapterListener = this)
        bottomRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 4)
        bottomRecyclerView?.adapter = bottomAdapter
    }

    // Implement OnAdapterListener methods
    override fun onAdd(favorite: Favorite) {
        viewModel.onAdd(favorite)
    }

    override fun onRemove(favorite: Favorite) {
        viewModel.onRemove(favorite)
    }

    override fun onSet(targetIndex: Int, sourceIndex: Int, favorite: Favorite) {
        viewModel.onSet(targetIndex, sourceIndex, favorite)
    }

    override fun onSwap(isPersonal: Boolean, from: Int, to: Int) {
        viewModel.onSwap(isPersonal, from, to)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}