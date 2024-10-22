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

        val topList: MutableList<Favorite> = arrayListOf(
            Favorite("Fantascienza", "Film", true),
            Favorite("Azione", "Film", true),
            Favorite("Commedia", "Serie", true),
            Favorite("Formula 1", "Sport", true),
            Favorite("Storia", "Rubriche/Docu", true),
            Favorite("Musica", "Intrattenimento", true),
            Favorite("Scuola", "Bambini", true)
        )

        val topListAdapter = DragAdapter(topList, this)
        binding?.topRecyclerview?.setHasFixedSize(true)
        binding?.topRecyclerview?.layoutManager = GridLayoutManager(requireContext(), 4)
        binding?.topRecyclerview?.adapter = topListAdapter

        binding?.topRecyclerviewContainer?.setOnDragListener(topListAdapter.dragInstance)
        binding?.topRecyclerview?.setOnDragListener(topListAdapter.dragInstance)
    }

    private fun setAvailableRecyclerView() {

        val bottomList: MutableList<Favorite> = arrayListOf(
            Favorite("Poliziesco", "Film", false),
            Favorite("Avventura", "Film", false),
            Favorite("Fantascienza", "Film", false),
            Favorite("Horror", "Film", false),
            Favorite("Western", "Film", false),
            Favorite("Fantascienza", "Serie", false),
            Favorite("Fantasy", "Serie", false),
            Favorite("Giallo", "Serie", false),
            Favorite("Sci", "Sport", false),
            Favorite("Basket", "Sport", false),
            Favorite("Pallavolo", "Sport", false),
            Favorite("Drammatico", "Serie", false),
            Favorite("Medico", "Serie", false),
            Favorite("Telenovela", "Serie", false),
            Favorite("Azione", "Serie", false),
            Favorite("Horror", "Serie", false),
            Favorite("Reality", "Rubriche/Docu", false),
            Favorite("Lifestyle", "Rubriche/Docu", false),
            Favorite("Cucina", "Rubriche/Docu", false),
            Favorite("Sentimentale", "Film", false),
            Favorite("Thriller", "Film", false),
            Favorite("Fantasy", "Film", false),
            Favorite("Quiz", "Intrattenimento", false),
            Favorite("Umorismo", "Intrattenimento", false),
            Favorite("Musica", "Intrattenimento", false),
            Favorite("Documentario", "Rubriche/Docu", false),
            Favorite("Reportage", "Rubriche/Docu", false),
            Favorite("Cultura", "Rubriche/Docu", false),
            Favorite("Tecnologia", "Rubriche/Docu", false),
            Favorite("Storia", "Rubriche/Docu", false),
            Favorite("Commedia", "Film", false),
            Favorite("Drammatico", "Film", false),
            Favorite("Azione", "Film", false),
            Favorite("Spettacolo", "Intrattenimento", false),
            Favorite("Reality", "Intrattenimento", false),
            Favorite("Talk show", "Intrattenimento", false),
            Favorite("Commedia", "Serie", false),
            Favorite("Crime", "Serie", false),
            Favorite("Soap", "Serie", false),
            Favorite("Calcio", "Sport", false),
            Favorite("Formula 1", "Sport", false),
            Favorite("Tennis", "Sport", false),
            Favorite("Atletica", "Sport", false),
            Favorite("Viaggi", "Rubriche/Docu", false),
            Favorite("Scienza", "Rubriche/Docu", false),
            Favorite("Natura", "Rubriche/Docu", false),
            Favorite("Serie", "Bambini", false),
            Favorite("Film", "Bambini", false),
            Favorite("Programma", "Bambini", false),
            Favorite("Scuola", "Bambini", false),
            Favorite("Concerto", "Intrattenimento", false),
            Favorite("Teatro", "Intrattenimento", false),
            Favorite("Opera", "Intrattenimento", false),
            Favorite("Rugby", "Sport", false),
            Favorite("Ciclismo", "Sport", false),
            Favorite("Motociclismo", "Sport", false),
            Favorite("Golf", "Sport", false)
        )

        val bottomListAdapter = DragAdapter(bottomList, this)
        binding?.bottomRecyclerview?.setHasFixedSize(true)
        binding?.bottomRecyclerview?.layoutManager = GridLayoutManager(requireContext(), 5)
        binding?.bottomRecyclerview?.adapter = bottomListAdapter

        binding?.bottomRecyclerviewContainer?.setOnDragListener(bottomListAdapter.dragInstance)
        binding?.bottomRecyclerview?.setOnDragListener(bottomListAdapter.dragInstance)
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