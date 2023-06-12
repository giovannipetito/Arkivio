package it.giovanni.arkivio

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import it.giovanni.arkivio.utils.Typekit

/**
 * La classe App è una classe Application personalizzata per la nostra applicazione Android, è
 * annotata con @HiltAndroidApp che indica che Hilt deve gestire l'inserimento delle dipendenze
 * per questa applicazione.
 *
 * La classe Application è una classe base fornita dal framework Android che rappresenta lo stato
 * globale di un'applicazione Android. Funge da punto di ingresso e mantiene il ciclo di vita
 * complessivo dell'applicazione.
 *
 * L'annotazione @HiltAndroidApp è fornita da Hilt. Viene utilizzato per contrassegnare la classe
 * Application personalizzata come punto di ingresso per dependency injection di Hilt. Questa
 * annotazione consente a Hilt di generare il codice necessario per l'inserimento delle dipendenze
 * in tutta l'applicazione.
 *
 * Utilizzando l'annotazione @HiltAndroidApp, la classe App diventa la classe dell'applicazione
 * di base per il setup della dependency injection di Hilt. Consente a Hilt di generare e inserire
 * automaticamente dipendenze in tutta l'applicazione in base ai moduli, ai componenti e alle
 * annotazioni di Hilt definiti.
 */
@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var context: Context

        fun getInstance(): Companion {
            return this
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Typekit.getInstance()
            .addNormal(Typekit.createFromAsset(this, "fonts/fira_light.ttf"))
            .addBold(Typekit.createFromAsset(this, "fonts/fira_medium.ttf"))
            .addItalic(Typekit.createFromAsset(this, "fonts/fira_regular.ttf"))
    }
}