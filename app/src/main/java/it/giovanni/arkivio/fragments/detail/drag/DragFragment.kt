package it.giovanni.arkivio.fragments.detail.drag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DragLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class DragFragment : DetailFragment(), Listener {

    private var layoutBinding: DragLayoutBinding? = null
    private val binding get() = layoutBinding

    private val viewModel: DragViewModel by viewModels()

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

        setFavoriteRecyclerView()
        setAvailableRecyclerView()

        return binding?.root
    }

    private fun setFavoriteRecyclerView() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            val dragAdapter = DragAdapter(
                true,
                favorites.toMutableList(),
                this
            )
            binding?.favoritesRecyclerview?.apply {
                // setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 4)
                adapter = dragAdapter
                setOnDragListener(dragAdapter.dragInstance)
            }
            binding?.favoritesContainer?.setOnDragListener(dragAdapter.dragInstance)
        }
    }

    private fun setAvailableRecyclerView() {
        viewModel.availables.observe(viewLifecycleOwner) { availables ->
            val dragAdapter = DragAdapter(
                false,
                availables.toMutableList(),
                this
            )
            binding?.availablesRecyclerview?.apply {
                // setHasFixedSize(true)
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
                setOnDragListener(dragAdapter.dragInstance)
            }
            binding?.availablesContainer?.setOnDragListener(dragAdapter.dragInstance)
        }
    }

    override fun notifyEmptyFavorites() {
        Toast.makeText(requireContext(), "Favorite list is empty.", Toast.LENGTH_SHORT).show()
    }

    override fun notifyEmptyAvailables() {
        Toast.makeText(requireContext(), "Available list is empty.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}