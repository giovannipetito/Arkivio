package it.giovanni.arkivio.fragments.detail.nearby.game

import java.util.Random

class PlayerGenerator {

    companion object {

        private val generator = Random()

        private val PLAYERS = arrayOf(
            "Player 1",
            "Player 2",
            "Player 3",
            "Player 4",
            "Player 5",
            "Player 6",
            "Player 7",
            "Player 8",
            "Player 9",
            "Player 10"
        )

        fun generate(): String? {
            return PLAYERS[generator.nextInt(PLAYERS.size)]
        }
    }
}