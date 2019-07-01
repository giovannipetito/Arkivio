package it.giovanni.kotlin.utils

import it.giovanni.kotlin.bean.LinkMenu

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
        var listaLinkMenu : ArrayList<LinkMenu>? = null
        private var matricola : String? = null
        private var isLogged = false

        fun clear() {
            email = ""
            password = ""
            avatarImage = null
            listaLinkMenu = null
            matricola = ""
            isLogged = false
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