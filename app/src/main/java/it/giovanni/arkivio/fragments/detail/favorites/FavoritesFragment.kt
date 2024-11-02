package it.giovanni.arkivio.fragments.detail.favorites

import android.os.Bundle
import android.util.Log
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

var personalsRecyclerView: RecyclerView? = null
var availablesRecyclerView: RecyclerView? = null

class FavoritesFragment : DetailFragment(), OnAdapterListener {

    /*
    companion object {
        private var personalsRecyclerView: RecyclerView? = null
        private var availablesRecyclerView: RecyclerView? = null

        var PERSONALS_RECYCLERVIEW_ID: Int? = personalsRecyclerView?.id
        var AVAILABLES_RECYCLERVIEW_ID: Int? = availablesRecyclerView?.id
    }
    */

    private var layoutBinding: FavoritesLayoutBinding? = null
    private val binding get() = layoutBinding

    private val viewModel: FavoritesViewModel by viewModels()

    private lateinit var personalsAdapter: FavoritesAdapter
    private lateinit var availablesAdapter: FavoritesAdapter

    private var isEditMode = false

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

        binding?.personalsRecyclerview?.setOnDragListener(personalsAdapter.dragListener)
        binding?.availablesRecyclerview?.setOnDragListener(availablesAdapter.dragListener)

        viewModel.personals.observe(viewLifecycleOwner) { personals ->
            Log.i("[FAVORITES]", "personals.size: " + personals.size)
            personalsAdapter.submitList(personals)
            personalsAdapter.notifyDataSetChanged()
        }

        viewModel.availables.observe(viewLifecycleOwner) { availables ->
            Log.i("[FAVORITES]", "availables.size: " + availables.size)
            availablesAdapter.submitList(availables)
            availablesAdapter.notifyDataSetChanged()
        }

        return binding?.root
    }

    private fun setupRecyclerViews() {

        personalsRecyclerView = binding?.personalsRecyclerview
        availablesRecyclerView = binding?.availablesRecyclerview

        personalsAdapter = FavoritesAdapter(true, onAdapterListener = this)
        personalsRecyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = personalsAdapter
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

        synchronizeScrollListeners()
    }

    private fun synchronizeScrollListeners() {
        personalsRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Scroll the other RecyclerView with the same distance.
                availablesRecyclerView?.scrollBy(dx, dy)
            }
        })

        availablesRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Scroll the other RecyclerView with the same distance.
                personalsRecyclerView?.scrollBy(dx, dy)
            }
        })
    }

    override fun onSet(isPersonal: Boolean, targetIndex: Int, sourceIndex: Int) {
        viewModel.onSet(isPersonal, targetIndex, sourceIndex)
    }

    override fun onSwap(isPersonal: Boolean, from: Int, to: Int) {
        viewModel.onSwap(isPersonal, from, to)
    }

    override fun onEditModeChanged(isEditMode: Boolean) {
        this.isEditMode = isEditMode
        personalsAdapter.setEditMode(isEditMode)
        availablesAdapter.setEditMode(isEditMode)
    }

    override fun onEditModeRemoved(position: Int) {
        viewModel.removeEditItem(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}