package it.giovanni.arkivio.puntonet.cleanarchitecture.data.response

import it.giovanni.arkivio.puntonet.cleanarchitecture.data.model.RickMorty

data class RickMortyResponse(

    val results: List<RickMorty>
)

/*
class RickMortyResponse {

    @SerializedName("results")
    var results: List<RickMorty>? = null
}
*/