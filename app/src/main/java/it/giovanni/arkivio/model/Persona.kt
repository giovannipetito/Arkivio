package it.giovanni.arkivio.model

import java.io.Serializable

class Persona : Serializable, Cloneable {

    var nome: String? = null
    var cognome: String? = null
    var fisso: String? = null
    var cellulare: String? = null
    var email: String? = null
    var indirizzo: String? = null
    var occupazione: String? = null
    var isVisible: Boolean? = null
    var tipo: Int? = null
    private var checked: Boolean? = null

    companion object {
        const val HEADER_TYPE = 0
        const val ITEM_TYPE = 1
    }

    constructor()

    constructor(nome: String, cognome: String, tipo: Int) {
        this.nome = nome
        this.cognome = cognome
        this.tipo = tipo
    }

    constructor(nome: String, cognome: String, cellulare: String, checked: Boolean) {
        this.nome = nome
        this.cognome = cognome
        this.cellulare = cellulare
        this.checked = checked
    }

    fun cloneObject(): Persona { // Su W3B, studiare le classi Servizi e Phx.

        val persona = Persona()

        persona.nome = nome
        persona.cognome = cognome
        persona.fisso = fisso
        persona.cellulare = cellulare
        persona.email = email
        persona.indirizzo = indirizzo
        persona.occupazione = occupazione
        persona.checked = checked
        persona.isVisible = isVisible

        return persona
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Any {
        return super.clone()
    }
}