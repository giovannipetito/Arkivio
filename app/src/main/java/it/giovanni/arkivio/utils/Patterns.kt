package it.giovanni.arkivio.utils

import java.util.regex.Pattern

object Patterns {

    private const val emailRegex: String = "(" +
            "([a-zA-Z0-9]+\\.)+[a-zA-Z0-9]+|" +
            "([a-zA-Z0-9]+_)+[a-zA-Z0-9]+|" +
            "([a-zA-Z0-9]+-)+[a-zA-Z0-9]+|" +
            "[a-zA-Z0-9]" +
            "){2,256}" +
            "@" +
            "[a-zA-Z0-9]{0,64}" +
            "(\\.[a-zA-Z0-9]{0,25}" +
            ")"

    val emailPattern: Pattern = Pattern.compile(emailRegex)
}