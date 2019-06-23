package it.giovanni.kotlin.bean

import java.io.Serializable

class Persona : Serializable {

    var nome: String? = null
    var cognome: String? = null

    var fisso: String? = null
    var cellulare: String? = null
    var email: String? = null
    var indirizzo: String? = null
    var occupazione: String? = null

    var isVisible: Boolean? = null

    constructor(nome: String, cognome: String) {
        this.nome = nome
        this.cognome = cognome
    }

    constructor(nome: String,
                cognome: String,
                fisso: String,
                cellulare: String,
                email: String,
                indirizzo: String,
                occupazione: String,
                isVisible: Boolean) {

        this.nome = nome
        this.cognome = cognome
        this.fisso = fisso
        this.cellulare = cellulare
        this.email = email
        this.indirizzo = indirizzo
        this.occupazione = occupazione
        this.isVisible = isVisible
    }
}