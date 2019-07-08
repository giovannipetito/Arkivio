package it.giovanni.kotlin.utils

import it.giovanni.kotlin.bean.LinkSide

class UserFactory {

    companion object {

        private var user: User = User()
        fun getInstance(): User {
            return user
        }
    }

    class User {

        private lateinit var email: String
        private lateinit var password: String
        private var avatarImage:ByteArray? = null
        var listLinkSide : ArrayList<LinkSide>? = null
        private var matricola : String? = null
        private var isLogged = false
        var oAuthToken: String? = null

        fun clear() {
            email = ""
            password = ""
            avatarImage = null
            listLinkSide = null
            matricola = ""
            isLogged = false
            oAuthToken = ""
        }

        fun getUserName(): String {
            return if (email.isEmpty())
                ""
            else {
                email = email.substring(0, 1).toUpperCase() + "" + email.substring(1, email.length)
                val partialEmail = email.split("@")[0]
                val split = partialEmail.split(".")
                if (split[0].isNotEmpty()) split[0]
                else partialEmail
            }
        }
    }
}