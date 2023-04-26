package it.giovanni.arkivio.model.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class User: Serializable {

    @SerializedName("nome")
    @Expose
    var nome: String? = null

    @SerializedName("cognome")
    @Expose
    var cognome: String? = null

    @SerializedName("fisso")
    @Expose
    var fisso: String? = null

    @SerializedName("cellulare")
    @Expose
    var cellulare: String? = null

    @SerializedName("emails")
    @Expose
    var emails : ArrayList<String>? = null

    @SerializedName("indirizzo")
    @Expose
    var indirizzo: String? = null

    @SerializedName("occupazione")
    @Expose
    var occupazione: String? = null

    @SerializedName("isVisible")
    @Expose
    var isVisible: Boolean? = null

    constructor(nome: String,
                cognome: String,
                fisso: String,
                cellulare: String,
                emails: ArrayList<String>,
                indirizzo: String,
                occupazione: String,
                isVisible: Boolean) {

        this.nome = nome
        this.cognome = cognome
        this.fisso = fisso
        this.cellulare = cellulare
        this.emails = emails
        this.indirizzo = indirizzo
        this.occupazione = occupazione
        this.isVisible = isVisible
    }
}