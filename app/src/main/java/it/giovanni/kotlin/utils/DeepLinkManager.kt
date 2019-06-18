package it.giovanni.kotlin.utils

import android.net.Uri

class DeepLinkManager {

    companion object {

        val DEEP_LINK = "DEEP_LINK"
        val DEEP_LINK_ACTION = "DEEP_LINK_ACTION"
        val DEEP_LINK_URI = "DEEP_LINK_URI"

        var URI_GC3 = "gc3" //waw3://gc3
        var URI_CINEMA = "cinema" //waw3://cinema
        var URI_CONTACTS = "contacts" //waw3://cinema
        var URI_OPENAPP = "openapp" //waw3://openapp/{xyz}
        var URI_VIEW = "view" //waw3://openapp/{xyz}
        var URI_LOGOUT = "logout" //waw3://logout

        var URI_HOME_PAGE = "homepage" //waw3://homePage
        var URI_HOME_WORK_PAGE = "workpage" //waw3://workPage
        var URI_HOME_ADMIN_PAGE = "adminpage" //waw3://adminPage

        var URI_CALL = "call"
    }

    var deeplink: Uri? = null
}