package it.giovanni.arkivio.utils

import java.util.regex.Pattern

object Patterns {

    const val emailRegexOld = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
            "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
            "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|" +
            "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"

    const val emailRegex: String = "(" +
            "([a-zA-Z0-9]+\\.)+[a-zA-Z0-9]+|" +
            "([a-zA-Z0-9]+\\_)+[a-zA-Z0-9]+|" +
            "([a-zA-Z0-9]+\\-)+[a-zA-Z0-9]+|" +
            "[a-zA-Z0-9]" +
            "){2,256}" +
            "@" +
            "[a-zA-Z0-9]{0,64}" +
            "(\\.[a-zA-Z0-9]{0,25})"

    val emailPattern: Pattern = Pattern.compile(emailRegex)
}