package it.giovanni.arkivio.model.favorite

data class Favorite(
    var domain: String? = null,
    var identifier: String? = null,
    var kind: String? = null,
    var title: String? = null,
    var contentPath: String? = null,
    var images: List<Image>? = null,
    var availableTitle: String? = null
)