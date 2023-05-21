package it.giovanni.arkivio.fragments.detail.puntonet.dependencyinjection

import android.util.Log

object Standard {

    fun main(): String {
        val pc = PC()
        return pc.start()
    }

    class PC {
        private val motherBoard: Motherboard = Motherboard()
        fun start(): String {
            return motherBoard.powerOn()
        }
    }

    class Motherboard {
        fun powerOn(): String {
            val output = "Motherboard turning on the computer..."
            Log.i("[DI]", output)
            return output
        }
    }
}