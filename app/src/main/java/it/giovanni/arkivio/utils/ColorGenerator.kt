package it.giovanni.arkivio.utils

import it.giovanni.arkivio.R
import java.util.*

class ColorGenerator {

    companion object {

        private val generator = Random()

        private val COLORS = arrayOf(
            R.color.blu,
            R.color.verde,
            R.color.rosso,
            R.color.arancio
        )

        fun generate(): Int {
            return COLORS[generator.nextInt(COLORS.size)]
        }
    }
}