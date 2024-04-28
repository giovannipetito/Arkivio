package it.giovanni.arkivio.fragments.detail.puntonet.coroutines

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.CoroutineScopesLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Ogni coroutine deve essere eseguita all'interno di uno scope. CoroutineScope tiene traccia di
 * tutte le coroutine che vengono eseguite al suo interno e prende un CoroutineContext come parametro.
 * Il coroutine context è un insieme di vari elementi e i principali sono:
 * - Job
 * - Dispatcher
 * - CoroutineName
 *
 * CoroutineScope e CoroutineContext sono strettamente correlati, CoroutineScope formalizza il
 * modo in cui il CoroutineContext viene ereditato. Quindi, ad esempio, quando una coroutine viene
 * lanciata all'interno di un CoroutineScope di un'altra coroutine, ne eredita il context e il job
 * della nuova coroutine diventa un job figlio della coroutine padre.
 * Quindi, quando la coroutine padre viene cancellata, anche tutti i suoi figli vengono cancellati
 * e la coroutine padre attende sempre la concorrenza (competition) di tutti i suoi figli per
 * impostazione predefinita.
 *
 * Il GlobalScope viene utilizzato per avviare una coroutine di primo livello (top-level) e dura
 * finché vive l'intera app, il che significa che se utilizziamo il GlobalScope in activity o
 * fragment, esso non verrà cancellato anche se l'activity o il fragment vengono distrutti.
 * Questo comportamento di GlobalScope può causare memory leak.
 *
 * Ogni CoroutineScope dovrebbe essere legato ad un ciclo di vita specifico del componente dell'app
 * e anche se lo stesso CoroutineScope fornisce un modo appropriato per cancellare automaticamente
 * le operazioni di lunga durata, esistono alcuni CoroutineScope incorporati (built-in) che dovrebbero
 * essere usati regolarmente. Essi sono:
 * - lifecycleScope: viene usato all'interno di activity e fragment ed è legato ai loro cicli di vita.
 * - viewModelScope: viene usato all'interno dei viewModels ed è legato al loro ciclo di vita.
 *
 * viewModelScope gestisce automaticamente tutte le cancellazioni, quindi ogni volta che utilizziamo
 * viewModelScope, esso si ripulisce automaticamente quando viene chiamato un metodo non cancellato,
 * evitando così problemi di memory leak.
 */
class CoroutineScopesFragment : DetailFragment() {

    private var layoutBinding: CoroutineScopesLayoutBinding? = null
    private val binding get() = layoutBinding

    /**
     * Definiamo un CoroutineScope passando come parametro un CoroutineContext che, come sappiamo,
     * è un insieme di elementi (Job, Dispatcher, CoroutineName), tuttavia possiamo specificare
     * anche solo il CoroutineName (in questo caso MyScope sarà il nome del nostro coroutine scope).
     * Se non specifichiamo il Dispatcher viene utilizzato Dispatchers.Default
     */
    private val scope: CoroutineScope = CoroutineScope(context = CoroutineName("MyScope"))
    // val scope: CoroutineScope = CoroutineScope(context = Dispatchers.IO + CoroutineName("MyScope"))

    override fun getTitle(): Int {
        return R.string.coroutine_scopes_title
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
        layoutBinding = CoroutineScopesLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        // Lanciamo la nostra coroutine attraverso la funzione coroutine builder launch che
        // restituisce un oggetto di tipo Job:
        val job: Job = scope.launch {
            Log.i("[Coroutine]", "coroutineContext: " + this.coroutineContext.toString())
            // coroutineContext: [CoroutineName(MyScope), StandaloneCoroutine{Active}@e38ad7, Dispatchers.Default]
            // - CoroutineName(MyScope) indica il nome del nostro scope, o della nostra coroutine (MyScope)
            // - StandaloneCoroutine{Active}@e38ad7 è il Job e @e38ad7 è il suo ID univoco.
            // - Dispatchers.Default

            // All'interno di questa coroutine aggiungiamo un'altra coroutine attraverso il launch builder:
            launch {
                Log.i("[Coroutine]", "coroutineContext: " + this.coroutineContext.toString())
                // coroutineContext: [CoroutineName(MyScope), StandaloneCoroutine{Active}@e79cfc4, Dispatchers.Default]

                // La coroutine innestata ha ereditato lo stesso scope (MyScope) della prima coroutine
                // builder, l'unica differenza è l'ID diverso.
            }
        }

        // Definiamo ora un GlobalScope e dimostriamo che esso vivrà finché la nostra app vivrà:
        GlobalScope.launch {
            while (true) {
                delay(1000L)
                logger("GlobalScope") // Il Log verrà eseguito ogni secondo.
            }
        }

        // Definiamo ora un lifecycleScope e dimostriamo che la coroutine verrà distrutta col fragment:
        lifecycleScope.launch {
            while (true) {
                delay(1000L)
                logger("lifecycleScope") // Il Log verrà eseguito ogni secondo.
            }
        }
    }

    private fun logger(coroutine: String) {
        Log.i("[Coroutine]", "$coroutine is running...")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }

    override fun onPause() {
        Log.i("[Coroutine]", "onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.i("[Coroutine]", "onStop()")
        super.onStop()
    }

    override fun onResume() {
        Log.i("[Coroutine]", "onResume()")
        super.onResume()
    }

    override fun onDestroy() {
        Log.i("[Coroutine]", "onDestroy()")
        super.onDestroy()
    }
}