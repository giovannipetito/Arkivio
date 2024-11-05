package it.giovanni.arkivio.deeplink

import android.net.Uri

object DeepLinkDescriptor {

    const val DEEP_LINK = "DEEP_LINK"
    const val DEEP_LINK_ACTION = "DEEP_LINK_ACTION"
    const val DEEP_LINK_URI = "DEEP_LINK_URI"

    var URI_CONTACTS = "contacts" // waw3://contacts
    var URI_OPEN_APP = "openapp" // waw3://openapp/{xyz}
    var URI_VIEW = "view" // waw3://openapp/{xyz}
    var URI_LOGOUT = "logout" // waw3://logout

    var URI_HOMEPAGE = "homepage" // waw3://homepage
    var URI_LEARNING = "learning" // waw3://learning
    var URI_TRAINING = "training" // waw3://training

    var URI_CALL = "call"

    var deeplink: Uri? = null
}