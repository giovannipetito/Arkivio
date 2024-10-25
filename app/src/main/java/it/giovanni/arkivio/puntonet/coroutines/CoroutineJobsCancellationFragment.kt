package it.giovanni.arkivio.puntonet.coroutines

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.CoroutineJobsCancellationLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

/**
 * Il Job è responsabile del ciclo di vita della coroutine e delle relazioni tra padre e figlio,
 * quindi possiamo utilizzare l'oggetto Job per cancellare la coroutine.
 *
 * Da documentazione: "Cancellation of coroutine code needs to be cooperative.", ciò significa che
 * dobbiamo fare in modo che la nostra coroutine sia "Cancelable", e per fare ciò ci sono due modi
 * possibili:
 * 1. Controllando se la tua coroutine è ancora attiva prima di eseguire qualsiasi logica
 *    all'interno del suo body.
 * 2. Chiamando qualsiasi funzione di sospensione (suspending function) dalla libreria
 *    kotlinx.coroutines (delay, yield(), ecc.).
 *
 * Queste funzioni di sospensione rendono la nostra coroutine Cancelable controllando se la coroutine
 * che le chiama è cancellata, e in caso affermativo lanciano una CancellationException.
 *
 * Il Job State più importante è isActive perché, per rendere la nostra coroutine Cancelable,
 * prima del blocco di codice che vogliamo eseguire all'interno della coroutine, dobbiamo prima
 * controllare se la coroutine è attiva o no.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *                                 Job States                                    *
 *                                                                               *
 *  State                                  isActive   isCompleted   isCancelled  *
 *                                                                               *
 *  New         (optional initial state)   false      false         false        *
 *  Active      (default initial state)    true       false         false        *
 *  Completing  (transient state)          true       false         false        *
 *  Cancelling  (transient state)          false      false         true         *
 *  Cancelled   (final state)              false      true          true         *
 *  Completed   (final state)              false      true          false        *
 *                                                                               *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */
class CoroutineJobsCancellationFragment : DetailFragment() {

    private var layoutBinding: CoroutineJobsCancellationLayoutBinding? = null
    private val binding get() = layoutBinding

    private val scope: CoroutineScope = CoroutineScope(CoroutineName("MyScope"))

    override fun getTitle(): Int {
        return R.string.coroutine_jobs_cancellation_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = CoroutineJobsCancellationLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        /*
        val job: Job = lifecycleScope.launch {
            delayCustom(1000L)
            Log.i("[Coroutine]", "Job is running...")
        }
        */

        /*
        scope.launch {
            val job1: Job = launch {
                while (isActive) {
                    Log.i("[Coroutine]", "Job1 is running...")
                }
            }

            delay(1000L) // Per non stampare immediatamente: Job 1 is canceling...

            // Per rendere job1 Cancelable:
            Log.i("[Coroutine]", "Job1 is canceling...")
            job1.cancel()
            job1.join()
            // La funzione join aspetterà fino a quando lo stato di job1 sarà isCompleted e solo
            // quando lo stato sarà isCompleted, verrà eseguito il codice successivo a job1.join().
            Log.i("[Coroutine]", "Job1 is canceled!")
        }
        */

        /*
        // C'è un secondo modo per controllare se la nostra coroutine è attiva:
        scope.launch {
            val job1: Job = launch {
                while (true) {
                    // Controlla se lo stato di job1 è isCancelled o no. Se lo stato è isCancelled,
                    // allora lancia una CancellationException che viene notificata allo scope.
                    ensureActive()
                    Log.i("[Coroutine]", "Job1 is running...")
                }
            }

            delay(1000L)

            Log.i("[Coroutine]", "Job1 is canceling...")
            job1.cancel()
            job1.join()
            Log.i("[Coroutine]", "Job1 is canceled!")
        }
        */

        /*
        // C'è un terzo modo per controllare se la nostra coroutine è attiva:
        scope.launch {
            val job1: Job = launch {
                while (true) {
                    // Chiamando qualsiasi suspending function che, come sappiamo, rende la nostra
                    // coroutine Cancelable.
                    delay(50L) // Oppure: yield()
                    Log.i("[Coroutine]", "Job1 is running...")
                }
            }

            delay(1000L)

            Log.i("[Coroutine]", "Job1 is canceling...")
            job1.cancel()
            job1.join()
            Log.i("[Coroutine]", "Job1 is canceled!")
        }
        */

        /**
         * Mostriamo che, in presenza di due job, quando cancelli un job, l'altro non sarà cancellato.
         * Se abbiamo quindi più job all'interno di un coroutine scope e uno di questi job figlio è
         * cancellato con CancellationException, ciò non vuol dire che gli altri job figlio saranno
         * cancellati. Discorso a parte se invece di CancellationException viene lanciata qualsiasi
         * altra eccezione, allora in quel caso anche gli altri job figlio verranno cancellati.
         * Se invece cancelliamo lo scope padre (mainJob), allora tutti i job figlio all'interno
         * dello scope verranno automaticamente cancellati.
         */

        val mainJob: Job = scope.launch {
            val job1: Job = launch {
                while (true) {
                    yield()
                    Log.i("[Coroutine]", "Job1 is running...")
                }
            }

            val job2: Job = launch {
                Log.i("[Coroutine]", "Job2 is running...")
            }

            // Cancelliamo job2:

            delay(1000L)

            Log.i("[Coroutine]", "Job2 is canceling...")
            // La suspend function cancelAndJoin() corrisponde a chiamare job2.cancel() & job2.join()
            job2.cancelAndJoin()
            Log.i("[Coroutine]", "Job2 is canceled!")
        }

        // runBlocking è una coroutine.
        runBlocking {
            delay(2000L)
            Log.i("[Coroutine]", "mainJob is canceling...")
            mainJob.cancelAndJoin()
            Log.i("[Coroutine]", "mainJob is canceled!")
        }
    }

    private suspend fun delayCustom(timeMillis: Long) {
        try {
            // Todo: write a code that throw a CancellationException if needed to make the job1 Cancelable.
        } catch (ex : CancellationException) {
            println(ex.message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}