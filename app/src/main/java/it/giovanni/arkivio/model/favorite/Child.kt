package it.giovanni.arkivio.model.favorite

data class Child(
    val domain: String,
    val identifier: String,
    val kind: String,
    val title: String,
    val contentPath: String,
    val images: List<Image>
)