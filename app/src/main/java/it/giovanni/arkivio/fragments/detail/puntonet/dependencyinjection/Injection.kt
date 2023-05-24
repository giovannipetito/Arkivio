package it.giovanni.arkivio.fragments.detail.puntonet.dependencyinjection

/**
 * Dependency Injection (DI) è un design pattern utilizzato nello sviluppo software per migliorare
 * la qualità e la manutenibilità del codice. DI è un modo per strutturare il codice, in modo che
 * sia più facile mantenerlo, testarlo e modificarlo nel tempo. Il suo scopo principale è ridurre
 * l'accoppiamento tra diversi componenti nel codice. Ciò significa che invece di creare e gestire
 * oggetti direttamente all'interno del codice, si delega tale responsabilità a un componente
 * separato chiamato Dependency Injection Container (DIP) che si occupa di creare e gestire tutti
 * i diversi oggetti di cui il tuo codice ha bisogno, permettendoti di concentrarti sulla scrittura
 * della business logic. Quindi, una classe richiederà i riferimenti di altre classi per funzionare.
 *
 * Con l'implementazione della classe Standard, le classi PC e Motherboard sono strettamente
 * accoppiate e non è possibile utilizzare facilmente sottoclassi o implementazioni alternative,
 * quindi è qui che entra in gioco la Dependency Injection. Con DI possiamo modificare il codice
 * rimuovendo l'istanza della classe Motherboard dalla classe PC stessa e spostando invece quella
 * classe all'interno del costruttore. In questo modo, quando stiamo costruendo il PC, avremmo la
 * possibilità di personalizzare il tipo di Motherboard che vogliamo costruire con un PC, piuttosto
 * che usarne una sola. Ciò rende il nostro codice molto più testabile e gestibile. Inoltre possiamo
 * sostituire la vera scheda madre con una falsa solo per testare la nostra classe PC.
 *
 * Esistono diversi modi per implementare la Dependency Injection nel codice:
 * - Constructor Injection (CI): le dipendenze di un componente vengono passate come parametro della
 *   classe attuale.
 * - Field Injection (FI): le activity e i fragment sono istanziati dal sistema stesso, quindi il CI
 *   non è possibile.
 */

object Injection {

    fun run(motherBoard: Motherboard): String {
        val pc = PC(motherBoard = motherBoard)
        return pc.start()
    }

    class PC(private val motherBoard: Motherboard? = null) {
        fun start(): String {
            return motherBoard?.powerOn()!!
        }
    }

    class AsusMotherboard: Motherboard {
        override fun powerOn(): String {
            return "Asus Motherboard turning on the computer..."
        }
    }

    class TestMotherboard: Motherboard {
        override fun powerOn(): String {
            return "Test Motherboard turning on the computer..."
        }
    }

    interface Motherboard {
        fun powerOn(): String
    }
}