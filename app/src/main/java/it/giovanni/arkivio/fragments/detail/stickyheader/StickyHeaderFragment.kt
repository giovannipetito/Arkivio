package it.giovanni.arkivio.fragments.detail.stickyheader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.Persona
import it.giovanni.arkivio.bean.Persona.Companion.HEADER_TYPE
import it.giovanni.arkivio.bean.Persona.Companion.ITEM_TYPE
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.adapter.StickyHeaderAdapter
import it.giovanni.arkivio.utils.StickyHeaderItemDecoration

class StickyHeaderFragment : DetailFragment() {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.sticky_header_layout
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = viewFragment?.findViewById(R.id.recyclerview_sticky_header) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val list = init()
        val adapter = StickyHeaderAdapter()
        adapter.setList(list)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        // recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.addItemDecoration(StickyHeaderItemDecoration())
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

        return list
    }
}