package it.giovanni.arkivio.fragments.detail.drag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DragLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.model.favorite.Favorite
import it.giovanni.arkivio.presenter.DarkModePresenter

class DragFragment : DetailFragment(), DragAdapter.OnAdapterListener {

    private var layoutBinding: DragLayoutBinding? = null
    private val binding get() = layoutBinding

    private val viewModel: DragViewModel by viewModels()

    private var favoritesRecyclerview: RecyclerView? = null
    private var availablesRecyclerView: RecyclerView? = null

    private lateinit var favoriteAdapter: DragAdapter
    private lateinit var availableAdapter: DragAdapter

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
        layoutBinding = DragLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        // setFavoriteRecyclerView()
        // setAvailableRecyclerView()

        setupRecyclerViews()

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favoriteAdapter.submitList(favorites)
        }

        viewModel.availables.observe(viewLifecycleOwner) { availables ->
            availableAdapter.submitList(availables)
        }

        return binding?.root
    }

    /*
    private fun setFavoriteRecyclerView() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            val dragAdapter = DragAdapter(
                true,
                this
            )
            binding?.favoritesRecyclerview?.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 4)
                adapter = dragAdapter
            }
            dragAdapter.submitList(favorites)
        }
    }

    private fun setAvailableRecyclerView() {
        viewModel.availables.observe(viewLifecycleOwner) { availables ->
            val dragAdapter = DragAdapter(
                false,
                this
            )
            binding?.availablesRecyclerview?.apply {
                setHasFixedSize(true)
                val gridLayoutManager = GridLayoutManager(requireContext(), 5)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (dragAdapter.getItemViewType(position)) {
                            DragAdapter.VIEW_TYPE_HEADER -> gridLayoutManager.spanCount // Span all columns for headers
                            else -> 1 // Normal items take 1 span
                        }
                    }
                }

                layoutManager = gridLayoutManager

                adapter = dragAdapter
            }
            dragAdapter.submitList(availables)
        }
    }
    */

    private fun setupRecyclerViews() {

        favoritesRecyclerview = binding?.favoritesRecyclerview
        availablesRecyclerView = binding?.availablesRecyclerview

        // Set up favorite RecyclerView
        favoriteAdapter = DragAdapter(true, onAdapterListener = this)
        favoritesRecyclerview?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = favoriteAdapter
        }

        // Set up available RecyclerView
        availableAdapter = DragAdapter(false, onAdapterListener = this)
        availablesRecyclerView?.apply {
            setHasFixedSize(true)
            val gridLayoutManager = GridLayoutManager(requireContext(), 5)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (availableAdapter.getItemViewType(position)) {
                        DragAdapter.VIEW_TYPE_HEADER -> gridLayoutManager.spanCount // Span all columns for headers
                        else -> 1 // Normal items take 1 span
                    }
                }
            }
            layoutManager = gridLayoutManager
            adapter = availableAdapter
        }
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

    override fun onSwap(isFavorite: Boolean, from: Int, to: Int) {
        viewModel.onSwap(isFavorite, from, to)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}