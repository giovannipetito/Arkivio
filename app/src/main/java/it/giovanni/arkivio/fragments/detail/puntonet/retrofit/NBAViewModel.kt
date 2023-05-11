package it.giovanni.arkivio.fragments.detail.puntonet.retrofit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * NBAViewModel estende la classe ViewModel dal package androidx.lifecycle. Ciò significa che avrà
 * un ciclo di vita legato a una activity o a un fragment e sarà in grado di conservare i dati ed
 * eseguire azioni che sopravvivono alle modifiche della configurazione (come le rotazioni dello schermo).
 *
 * Questo ViewModel è responsabile del recupero di una lista di team NBA da una API e del loro mapping
 * (trasformazione) in una lista di elementi di dati dell'interfaccia utente (UI data Items).
 *
 * La classe ha un'istanza MutableLiveData di una lista di AllTeamsDataItem. Questa lista è privata
 * ed è accessibile tramite un'istanza pubblica di LiveData denominata list. Ciò consente ad altri
 * componenti di osservare le modifiche alla lista.
 *
 * Il metodo fetchTeams è responsabile del recupero della lista dei team dall'API. Avvia una coroutine
 * utilizzando la property viewModelScope dal package kotlinx.coroutines. Questo scope è legato al
 * ciclo di vita del ViewModel e annulla automaticamente tutte le coroutine quando il ViewModel non
 * è più in uso.
 *
 * All'interno della coroutine, chiama il metodo ApiServiceFactory.getAllTeams(page) per recuperare i
 * dati dall'API. Questo metodo restituisce un oggetto Result, che può essere un'istanza Success o Error.
 *
 * Se il risultato è un'istanza Success, mappa l'elenco dei team in un elenco di AllTeamsDataItem
 * chiamando il metodo mapTeamsDataItem. Questo metodo crea una nuova lista di data items mappando
 * ogni team a un'istanza di AllTeamsDataItem. Questo nuovo elenco viene quindi impostato sulla
 * variabile _list utilizzando la property value.
 *
 * Se il risultato è un'istanza Error, non modifica la lista e può potenzialmente mostrare un messaggio
 * di errore (che è attualmente commentato).
 */

class NBAViewModel : ViewModel() {

    private val _list: MutableLiveData<List<AllTeamsDataItem>> = MutableLiveData<List<AllTeamsDataItem>>()
    val list: LiveData<List<AllTeamsDataItem>>
        get() = _list

    private val _listSer: MutableLiveData<List<Team>> = MutableLiveData<List<Team>>()
    val listSer: LiveData<List<Team>>
        get() = _listSer

    fun fetchTeams(page: Int) {
        viewModelScope.launch {
            when (val result = ApiServiceFactory.getAllTeams(page)) {
                is Result.Success<AllTeamsResponse> -> {

                    _listSer.value = result.data.teams

                    result.data.teams?.let { mapTeamsDataItem(it) }
                }
                is Result.Error -> {
                    // todo: show error message
                }
            }
        }
    }

    private fun mapTeamsDataItem(teams: List<Team>) {
        _list.value = teams.map {
            AllTeamsDataItem(
                it.id!!,
                it.abbreviation,
                it.city,
                it.conference,
                it.division,
                it.full_name,
                it.name
            )
        }
    }
}