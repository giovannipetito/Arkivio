package it.giovanni.arkivio.fragments.detail.drop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DropLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class DropFragment : DetailFragment(), DropAdapter.OnClickListener {

    private var layoutBinding: DropLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var dropListTop: RecyclerView
    private lateinit var dropListBottom: RecyclerView

    private lateinit var dropAdapterTop: DropAdapter
    private lateinit var dropAdapterBottom: DropAdapter

    private val dragListener = DropListener()
    private val topList = mutableListOf<Any>("cat", "dog", "rabbit", "horse", "elephant", "eagle", "bear", "cow", "chicken", "dear")
    private val bottomList = mutableListOf<Any>("fish", "jellyfish", "whale", "turtle", "seahorse", "coral", "octopus", "frog", "screw", "starfish")

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
        layoutBinding = DropLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        dropAdapterTop = DropAdapter()
        dropAdapterTop.setClickListener(this)
        dropAdapterTop.setDragListener(dragListener)

        dropAdapterBottom = DropAdapter()
        dropAdapterBottom.setClickListener(this)
        dropAdapterBottom.setDragListener(dragListener)

        dropListTop = binding?.recyclerviewLeft!!
        dropListTop
            .apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                layoutManager = GridLayoutManager(requireContext(), 4)

                // specify an viewAdapter (see also next example)
                adapter = dropAdapterTop
            }

        dropListBottom = binding?.recyclerviewRight!!
        dropListBottom
            .apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                layoutManager = GridLayoutManager(requireContext(), 4)

                // specify an viewAdapter (see also next example)
                adapter = dropAdapterBottom
            }

        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        dropAdapterTop.setData(topList)
        dropAdapterBottom.setData(bottomList)
    }

    override fun recyclerviewClick(name: String) {
        Toast.makeText(requireContext(), "clicked $name", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}