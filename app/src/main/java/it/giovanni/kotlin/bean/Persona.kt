package it.giovanni.kotlin.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Persona : Serializable, Cloneable {

    @SerializedName("nome")
    @Expose
    var nome: String? = null
    var cognome: String? = null
    var fisso: String? = null
    var cellulare: String? = null
    var email: String? = null
    var indirizzo: String? = null
    var occupazione: String? = null
    var checked: Boolean? = null
    var isVisible: Boolean? = null

    constructor()

    constructor(nome: String, cognome: String) {
        this.nome = nome
        this.cognome = cognome
    }

    constructor(nome: String, cognome: String, cellulare: String, checked: Boolean) {
        this.nome = nome
        this.cognome = cognome
        this.cellulare = cellulare
        this.checked = checked
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

    fun cloneList(): Persona { // Su W3B, studiare le classi Servizi e Phx.

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