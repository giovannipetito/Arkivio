package it.giovanni.arkivio.fragments.detail.stickyheader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.model.Persona
import it.giovanni.arkivio.model.Persona.Companion.HEADER_TYPE
import it.giovanni.arkivio.model.Persona.Companion.ITEM_TYPE
import it.giovanni.arkivio.databinding.StickyHeaderLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.adapter.StickyHeaderAdapter
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.StickyHeaderItemDecoration

class StickyHeaderFragment : DetailFragment() {

    private var layoutBinding: StickyHeaderLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.sticky_header_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return false
    }

    override fun closeAction(): Boolean {
        return true
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = StickyHeaderLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding?.recyclerviewStickyHeader
        recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val list = init()
        val adapter = StickyHeaderAdapter()
        adapter.setList(list)
        recyclerView?.adapter = adapter
        recyclerView?.setHasFixedSize(true)

        // recyclerView?.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView?.addItemDecoration(StickyHeaderItemDecoration())
    }

    private fun init(): ArrayList<Persona> {

        val list = ArrayList<Persona>()

        list.add(Persona("Title 1", "", HEADER_TYPE))
        list.add(Persona("Row 1", "Description 1", ITEM_TYPE))
        list.add(Persona("Row 2", "Description 2", ITEM_TYPE))
        list.add(Persona("Title 2", "", HEADER_TYPE))
        list.add(Persona("Row 3", "Description 3", ITEM_TYPE))
        list.add(Persona("Row 4", "Description 4", ITEM_TYPE))
        list.add(Persona("Row 5", "Description 5", ITEM_TYPE))

        list.add(Persona("Title 3", "", HEADER_TYPE))
        list.add(Persona("Row 6", "Description 6", ITEM_TYPE))
        list.add(Persona("Row 7", "Description 7", ITEM_TYPE))
        list.add(Persona("Title 4", "", HEADER_TYPE))
        list.add(Persona("Row 8", "Description 8", ITEM_TYPE))
        list.add(Persona("Row 9", "Description 9", ITEM_TYPE))
        list.add(Persona("Row 10", "Description 10", ITEM_TYPE))

        list.add(Persona("Title 5", "", HEADER_TYPE))
        list.add(Persona("Row 11", "Description 11", ITEM_TYPE))
        list.add(Persona("Row 12", "Description 12", ITEM_TYPE))
        list.add(Persona("Title 6", "", HEADER_TYPE))
        list.add(Persona("Row 13", "Description 13", ITEM_TYPE))
        list.add(Persona("Row 14", "Description 14", ITEM_TYPE))
        list.add(Persona("Row 15", "Description 15", ITEM_TYPE))

        list.add(Persona("Title 7", "", HEADER_TYPE))
        list.add(Persona("Row 16", "Description 16", ITEM_TYPE))
        list.add(Persona("Row 17", "Description 17", ITEM_TYPE))
        list.add(Persona("Title 8", "", HEADER_TYPE))
        list.add(Persona("Row 18", "Description 18", ITEM_TYPE))
        list.add(Persona("Row 19", "Description 19", ITEM_TYPE))
        list.add(Persona("Row 20", "Description 20", ITEM_TYPE))

        list.add(Persona("Title 9", "", HEADER_TYPE))
        list.add(Persona("Row 21", "Description 21", ITEM_TYPE))
        list.add(Persona("Row 22", "Description 22", ITEM_TYPE))
        list.add(Persona("Title 10", "", HEADER_TYPE))
        list.add(Persona("Row 23", "Description 23", ITEM_TYPE))
        list.add(Persona("Row 24", "Description 24", ITEM_TYPE))
        list.add(Persona("Row 25", "Description 25", ITEM_TYPE))

        list.add(Persona("Title 11", "", HEADER_TYPE))
        list.add(Persona("Row 26", "Description 26", ITEM_TYPE))
        list.add(Persona("Row 27", "Description 27", ITEM_TYPE))
        list.add(Persona("Title 12", "", HEADER_TYPE))
        list.add(Persona("Row 28", "Description 28", ITEM_TYPE))
        list.add(Persona("Row 29", "Description 29", ITEM_TYPE))
        list.add(Persona("Row 30", "Description 30", ITEM_TYPE))

        list.add(Persona("Title 13", "", HEADER_TYPE))
        list.add(Persona("Row 31", "Description 31", ITEM_TYPE))
        list.add(Persona("Row 32", "Description 32", ITEM_TYPE))
        list.add(Persona("Title 14", "", HEADER_TYPE))
        list.add(Persona("Row 33", "Description 33", ITEM_TYPE))
        list.add(Persona("Row 34", "Description 34", ITEM_TYPE))
        list.add(Persona("Row 35", "Description 35", ITEM_TYPE))

        list.add(Persona("Title 15", "", HEADER_TYPE))
        list.add(Persona("Row 36", "Description 36", ITEM_TYPE))
        list.add(Persona("Row 37", "Description 37", ITEM_TYPE))
        list.add(Persona("Title 16", "", HEADER_TYPE))
        list.add(Persona("Row 38", "Description 38", ITEM_TYPE))
        list.add(Persona("Row 39", "Description 39", ITEM_TYPE))
        list.add(Persona("Row 40", "Description 40", ITEM_TYPE))

        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}