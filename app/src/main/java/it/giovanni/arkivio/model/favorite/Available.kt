package it.giovanni.arkivio.model.favorite

data class Available(
    val domain: String,
    val identifier: String,
    val kind: String,
    val rootIdentifier: String,
    val title: String,
    val children: List<Child>
)