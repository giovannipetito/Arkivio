package it.giovanni.arkivio.fragments.detail.puntonet.dependencyinjection

object Standard {

    fun run(): String {
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
            return "Motherboard turning on the computer..."
        }
    }
}