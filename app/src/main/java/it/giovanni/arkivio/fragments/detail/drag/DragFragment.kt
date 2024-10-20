package it.giovanni.arkivio.fragments.detail.drag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DragLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
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

        // binding?.tvEmptyListTop?.visibility = View.GONE
        // binding?.tvEmptyListBottom?.visibility = View.GONE

        return binding?.root
    }

    private fun setTopRecyclerView() {

        val topList: MutableList<String> = ArrayList()
        topList.add("A")
        topList.add("B")
        topList.add("C")
        topList.add("D")
        topList.add("E")
        topList.add("F")

        val topListAdapter = MainAdapter(topList, this)
        binding?.rvTop?.setHasFixedSize(true)
        binding?.rvTop?.layoutManager = GridLayoutManager(requireContext(), 4)
        binding?.rvTop?.adapter = topListAdapter

        binding?.tvEmptyListTop?.setOnDragListener(topListAdapter.dragInstance)
        binding?.rvTop?.setOnDragListener(topListAdapter.dragInstance)
    }

    private fun setBottomRecyclerView() {

        val bottomList: MutableList<String> = ArrayList()
        bottomList.add("1")
        bottomList.add("2")
        bottomList.add("3")
        bottomList.add("4")
        bottomList.add("5")
        bottomList.add("6")

        val bottomListAdapter = MainAdapter(bottomList, this)
        binding?.rvBottom?.setHasFixedSize(true)
        binding?.rvBottom?.layoutManager = GridLayoutManager(requireContext(), 4)
        binding?.rvBottom?.adapter = bottomListAdapter

        binding?.tvEmptyListBottom?.setOnDragListener(bottomListAdapter.dragInstance)
        binding?.rvBottom?.setOnDragListener(bottomListAdapter.dragInstance)
    }

    override fun setEmptyListTop(visibility: Boolean) {
        Toast.makeText(requireContext(), "Top List Empty", Toast.LENGTH_SHORT).show()
        binding?.tvEmptyListTop?.visibility = if (visibility) View.VISIBLE else View.GONE
        binding?.rvTop?.visibility = if (visibility) View.GONE else View.VISIBLE
    }

    override fun setEmptyListBottom(visibility: Boolean) {
        Toast.makeText(requireContext(), "Bottom List Empty", Toast.LENGTH_SHORT).show()
        binding?.tvEmptyListBottom?.visibility = if (visibility) View.VISIBLE else View.GONE
        binding?.rvBottom?.visibility = if (visibility) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}