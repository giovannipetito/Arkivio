package it.giovanni.kotlin.utils

import it.giovanni.kotlin.bean.Link
import it.giovanni.kotlin.bean.LinkSide
import java.util.*
import kotlin.collections.ArrayList

class UserFactory {

    companion object {

        private var user: User = User()
        fun getInstance(): User {
            return user
        }
    }

    class User {

        lateinit var email: String
        lateinit var password: String
        var avatarImage:ByteArray? = null
        var listLink : ArrayList<Link>? = null
        var listLinkSide : ArrayList<LinkSide>? = null
        var matricola : String? = null
        var isLogged = false
        var oAuthToken: String? = null
        var contacts: String? = null

        fun clear() {
            email = ""
            password = ""
            avatarImage = null
            listLink = null
            listLinkSide = null
            matricola = ""
            isLogged = false
            oAuthToken = ""
            contacts = ""
        }

        fun getUserName(): String {
            return if (email.isEmpty())
                ""
            else {
                email = email.substring(0, 1).toUpperCase(Locale.ITALY) + "" + email.substring(1, email.length)
                val partialEmail = email.split("@")[0]
                val split = partialEmail.split(".")
                if (split[0].isNotEmpty()) split[0]
                else partialEmail
            }
        }
    }
}