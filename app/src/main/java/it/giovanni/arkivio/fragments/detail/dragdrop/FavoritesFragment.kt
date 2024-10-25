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

    private val viewModel: FavoritesViewModel by viewModels()

    private var favoritesRecyclerview: RecyclerView? = null
    private var availablesRecyclerView: RecyclerView? = null

    private lateinit var favoriteAdapter: FavoritesAdapter
    private lateinit var availableAdapter: FavoritesAdapter

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

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favoriteAdapter.submitList(favorites)
        }

        viewModel.availables.observe(viewLifecycleOwner) { availables ->
            availableAdapter.submitList(availables)
        }

        return binding?.root
    }

    private fun setupRecyclerViews() {

        favoritesRecyclerview = binding?.favoritesRecyclerview
        availablesRecyclerView = binding?.availablesRecyclerview

        // Set up favorite RecyclerView
        favoriteAdapter = FavoritesAdapter(onAdapterListener = this)
        favoritesRecyclerview?.layoutManager = GridLayoutManager(requireContext(), 4)
        favoritesRecyclerview?.adapter = favoriteAdapter

        // Set up available RecyclerView
        availableAdapter = FavoritesAdapter(onAdapterListener = this)
        availablesRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 4)
        availablesRecyclerView?.adapter = availableAdapter
    }

    // Implement OnAdapterListener methods
    override fun onAdd(favorite: OldFavorite) {
        viewModel.onAdd(favorite)
    }

    override fun onRemove(favorite: OldFavorite) {
        viewModel.onRemove(favorite)
    }

    override fun onSet(targetIndex: Int, sourceIndex: Int, favorite: OldFavorite) {
        viewModel.onSet(targetIndex, sourceIndex, favorite)
    }

    override fun onSwap(isFavorite: Boolean, from: Int, to: Int) {
        viewModel.onSwap(isFavorite, from, to)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}