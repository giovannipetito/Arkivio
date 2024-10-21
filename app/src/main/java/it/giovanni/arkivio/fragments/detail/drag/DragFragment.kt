package it.giovanni.arkivio.fragments.detail.drag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        layoutBinding = DragLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        setTopRecyclerView()
        setBottomRecyclerView()

        return binding?.root
    }

    private fun setTopRecyclerView() {

        val topList: MutableList<Favorite> = arrayListOf(
            Favorite("A", false),
            Favorite("B", false),
            Favorite("C", false),
            Favorite("D", false),
            Favorite("E", false),
            Favorite("F", false)
        )

        val topListAdapter = MainAdapter(topList, this)
        binding?.topList?.setHasFixedSize(true)
        binding?.topList?.layoutManager = GridLayoutManager(requireContext(), 4)
        binding?.topList?.adapter = topListAdapter

        binding?.topListContainer?.setOnDragListener(topListAdapter.dragInstance)
        binding?.topList?.setOnDragListener(topListAdapter.dragInstance)
    }

    private fun setBottomRecyclerView() {

        val bottomList: MutableList<Favorite> = arrayListOf(
            Favorite("1", false),
            Favorite("2", false),
            Favorite("3", false),
            Favorite("4", false),
            Favorite("5", false),
            Favorite("6", false)
        )

        val bottomListAdapter = MainAdapter(bottomList, this)
        binding?.bottomList?.setHasFixedSize(true)
        binding?.bottomList?.layoutManager = GridLayoutManager(requireContext(), 4)
        binding?.bottomList?.adapter = bottomListAdapter

        binding?.bottomListContainer?.setOnDragListener(bottomListAdapter.dragInstance)
        binding?.bottomList?.setOnDragListener(bottomListAdapter.dragInstance)
    }

    override fun notifyTopListEmpty() {
        Toast.makeText(requireContext(), "Top list is empty.", Toast.LENGTH_SHORT).show()
    }

    override fun notifyBottomListEmpty() {
        Toast.makeText(requireContext(), "Bottom list is empty.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}