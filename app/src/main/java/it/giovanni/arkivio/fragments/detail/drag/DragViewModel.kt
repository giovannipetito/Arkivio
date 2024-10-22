package it.giovanni.arkivio.fragments.detail.drag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.fragments.detail.dragdrop.Favorite

class DragViewModel : ViewModel() {

    private val _personalFavorites = MutableLiveData<List<Favorite>>()
    val personalFavorites: LiveData<List<Favorite>> get() = _personalFavorites

    private val _availableFavorites = MutableLiveData<List<Favorite>>()
    val availableFavorites: LiveData<List<Favorite>> get() = _availableFavorites

    init {
        _personalFavorites.value = listOf(
            Favorite("Fantascienza", "Film", true),
            Favorite("Azione", "Film", true),
            Favorite("Commedia", "Serie", true),
            Favorite("Formula 1", "Sport", true),
            Favorite("Storia", "Rubriche/Docu", true),
            Favorite("Musica", "Intrattenimento", true),
            Favorite("Scuola", "Bambini", true)
        )

        _availableFavorites.value = listOf(
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
    }
}