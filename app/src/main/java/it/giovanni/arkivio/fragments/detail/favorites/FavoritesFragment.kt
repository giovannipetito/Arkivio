package it.giovanni.arkivio.fragments.detail.favorites

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
import it.giovanni.arkivio.model.favorite.Favorite
import it.giovanni.arkivio.presenter.DarkModePresenter

class FavoritesFragment : DetailFragment(), OnAdapterListener {

    private var layoutBinding: FavoritesLayoutBinding? = null
    private val binding get() = layoutBinding

    private val viewModel: FavoritesViewModel by viewModels()

    private var favoritesRecyclerview: RecyclerView? = null
    private var availablesRecyclerView: RecyclerView? = null

    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var availablesAdapter: FavoritesAdapter

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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = FavoritesLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        setupRecyclerViews()

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favoritesAdapter.submitList(favorites)
        }

        viewModel.availables.observe(viewLifecycleOwner) { availables ->
            availablesAdapter.submitList(availables)
        }

        return binding?.root
    }

    private fun setupRecyclerViews() {

        favoritesRecyclerview = binding?.favoritesRecyclerview
        availablesRecyclerView = binding?.availablesRecyclerview

        favoritesAdapter = FavoritesAdapter(true, onAdapterListener = this)
        favoritesRecyclerview?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = favoritesAdapter
        }

        availablesAdapter = FavoritesAdapter(false, onAdapterListener = this)
        availablesRecyclerView?.apply {
            setHasFixedSize(true)
            val gridLayoutManager = GridLayoutManager(requireContext(), 5)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (availablesAdapter.getItemViewType(position)) {
                        FavoritesAdapter.VIEW_TYPE_HEADER -> gridLayoutManager.spanCount // Span all columns for headers
                        else -> 1 // Normal items take 1 span
                    }
                }
            }
            layoutManager = gridLayoutManager
            adapter = availablesAdapter
        }
    }

    override fun onSet(targetIndex: Int, sourceIndex: Int, targetFavorite: Favorite) {
        val isFavorite = targetFavorite.availableTitle == null
        viewModel.onSet(isFavorite, targetIndex, sourceIndex, targetFavorite)
    }

    override fun onAdd(favorite: Favorite) {
        val isFavorite = favorite.availableTitle == null
        viewModel.onAdd(isFavorite, favorite)
    }

    override fun onRemove(favorite: Favorite) {
        val isFavorite = favorite.availableTitle == null
        viewModel.onRemove(isFavorite, favorite)
    }

    override fun onSwap(isFavorite: Boolean, from: Int, to: Int) {
        viewModel.onSwap(isFavorite, from, to)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}