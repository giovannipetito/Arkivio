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
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class DragFragment : DetailFragment(), DragAdapter.OnClickListener {

    private var layoutBinding: DragLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var adapterLeft: DragAdapter
    private lateinit var adapterRight: DragAdapter

    private val dragListener = DragListener()

    private val listLeft = mutableListOf<Any>("cat", "dog", "rabbit", "horse", "elephant", "eagle", "bear", "cow", "chicken", "dear")
    private val listRight = mutableListOf<Any>("fish", "clam", "whale", "turtle", "dolphin", "coral", "octopus", "frog", "screw", "shark")

    override fun getTitle(): Int {
        return R.string.drag_favorites_title
    }

    override fun getActionTitle(): Int {
        return R.string.action_label_title
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

    override fun onActionClickListener() {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = DragLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        adapterLeft = DragAdapter()
        adapterLeft.setClickListener(this)
        adapterLeft.setDragListener(dragListener)

        adapterRight = DragAdapter()
        adapterRight.setClickListener(this)
        adapterRight.setDragListener(dragListener)

        binding?.recyclerViewLeft?.apply {
            setHasFixedSize(false)
            layoutManager = GridLayoutManager(requireContext(), 5)
            adapter = adapterLeft
        }

        binding?.recyclerViewRight?.apply {
            setHasFixedSize(false)
            layoutManager = GridLayoutManager(requireContext(), 5)
            adapter = adapterRight
        }

        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        adapterLeft.setData(listLeft)
        adapterRight.setData(listRight)
    }

    override fun recyclerviewClick(name: String) {
        Toast.makeText(activity, name, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}