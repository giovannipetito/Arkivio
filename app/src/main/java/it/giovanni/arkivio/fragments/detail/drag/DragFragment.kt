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
import it.giovanni.arkivio.fragments.detail.dragdrop.Favorite
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

        setPersonalRecyclerView()
        setAvailableRecyclerView()

        return binding?.root
    }

    private fun setPersonalRecyclerView() {

        viewModel.personalFavorites.observe(viewLifecycleOwner) { personalFavorites ->
            val topListAdapter = DragAdapter(personalFavorites.toMutableList(), this)
            binding?.topRecyclerview?.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 4)
                adapter = topListAdapter
                setOnDragListener(topListAdapter.dragInstance)
            }
            binding?.topRecyclerviewContainer?.setOnDragListener(topListAdapter.dragInstance)
        }
    }

    private fun setAvailableRecyclerView() {

        viewModel.availableFavorites.observe(viewLifecycleOwner) { availableFavorites ->
            val bottomListAdapter = DragAdapter(availableFavorites.toMutableList(), this)
            binding?.bottomRecyclerview?.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 5)
                adapter = bottomListAdapter
                setOnDragListener(bottomListAdapter.dragInstance)
            }
            binding?.bottomRecyclerviewContainer?.setOnDragListener(bottomListAdapter.dragInstance)
        }
    }

    override fun notifyPersonalFavoritesEmpty() {
        Toast.makeText(requireContext(), "Top list is empty.", Toast.LENGTH_SHORT).show()
    }

    override fun notifyAvailableFavoritesEmpty() {
        Toast.makeText(requireContext(), "Bottom list is empty.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}