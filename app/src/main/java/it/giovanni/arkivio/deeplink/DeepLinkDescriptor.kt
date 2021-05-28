package it.giovanni.arkivio.deeplink

import android.net.Uri

class DeepLinkDescriptor {

    companion object {

        const val DEEP_LINK = "DEEP_LINK"
        const val DEEP_LINK_ACTION = "DEEP_LINK_ACTION"
        const val DEEP_LINK_URI = "DEEP_LINK_URI"

        var URI_CONTACTS = "contacts" // waw3://contacts
        var URI_OPENAPP = "openapp" // waw3://openapp/{xyz}
        var URI_VIEW = "view" // waw3://openapp/{xyz}
        var URI_LOGOUT = "logout" // waw3://logout

        var URI_HOME_PAGE = "homepage_layout" // waw3://homePage
        var URI_HOME_WORK_PAGE = "workpage" // waw3://workPage
        var URI_HOME_ADMIN_PAGE = "adminpage" // waw3://adminPage

        var URI_CALL = "call"
    }

    var deeplink: Uri? = null
}