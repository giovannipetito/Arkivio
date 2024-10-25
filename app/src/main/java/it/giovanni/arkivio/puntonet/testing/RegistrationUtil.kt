package it.giovanni.arkivio.puntonet.testing

object RegistrationUtil {

    private val existingUsers = listOf("Mario", "Andrea", "Luca", "Antonio")

    /**
     * L'input non è valido se...
     * 1. username o password sono empty
     * 2. username già esistente
     * 3. la password di conferma non corrisponde alla password
     * 4. la password contiene meno di 2 caratteri
     */
    fun validateRegistrationInput(
        username: String,
        password: String,
        confirmedPassword: String
    ): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            return false
        }
        if (username in existingUsers) {
            return false
        }
        if (password != confirmedPassword) {
            return false
        }
        if (password.count {it.isDigit()} < 2) {
            return false
        }
        return true
    }
}