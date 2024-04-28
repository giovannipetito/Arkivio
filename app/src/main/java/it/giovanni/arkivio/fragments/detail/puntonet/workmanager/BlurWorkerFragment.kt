package it.giovanni.arkivio.fragments.detail.puntonet.workmanager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.BlurWorkerLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

/**
 * BlurWorkerFragment utilizza WorkManager per eseguire del lavoro in background. In dettaglio,
 * gestisce la UI e le interazioni per uno scenario di lavoro in background basato su WorkManager,
 * utilizzando il ViewModel BlurWorkerViewModel per gestire il lavoro e osservarne i cambiamenti di stato.
 */
class BlurWorkerFragment : DetailFragment() {

    private var layoutBinding: BlurWorkerLayoutBinding? = null
    private val binding get() = layoutBinding

    /**
     * La variabile viewModel è un'istanza di BlurWorkerViewModel ottenuta utilizzando il delegate
     * "by viewModels". Usa WorkerViewModelProviderFactory per creare l'istanza del ViewModel,
     * passando currentActivity.application come argomento. Ciò consente al fragment di avere
     * accesso all'istanza di ViewModel condivisa.
     */
    private val viewModel: BlurWorkerViewModel by viewModels {
        WorkerViewModelProviderFactory(currentActivity.application)
    }

    override fun getTitle(): Int {
        return R.string.blur_worker_title
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
        layoutBinding = BlurWorkerLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        binding?.buttonRunWork?.setOnClickListener {
            viewModel.applyBlur(3)
        }

        binding?.buttonCancel?.setOnClickListener {
            viewModel.cancelWork()
        }

        binding?.buttonSeeFile?.setOnClickListener {
            viewModel.outputUri?.let { currentUri ->
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                actionView.resolveActivity(currentActivity.packageManager)?.run {
                    startActivity(actionView)
                }
            }
        }

        viewModel.workInfos.observe(viewLifecycleOwner, workInfosObserver())
    }

    /**
     * La funzione workInfosObserver restituisce un Observer che osserva le modifiche nella lista
     * degli oggetti WorkInfo. Gestisce gli stati e le azioni relative al lavoro in background,
     * come l'aggiornamento della UI in base allo stato del lavoro e la gestione dei dati di output.
     */
    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            // Se non ci sono work info corrispondenti, non fare nulla.
            if (listOfWorkInfo.isEmpty()) {
                return@Observer
            }

            // Ci interessa solo lo stato di un output.
            // Ogni continuation ha un solo worker con tag TAG_OUTPUT
            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()

                // Normalmente questa elaborazione, che non è direttamente correlata alla costruzione
                // della view, dovrebbe stare nel ViewModel. Per semplicità lo teniamo qui.
                val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                // Se c'è un file di output, mostra il tasto "See File".
                if (!outputImageUri.isNullOrEmpty()) {
                    viewModel.setOutputUri(outputImageUri)
                    binding?.buttonSeeFile?.isEnabled = true
                }
            } else {
                showWorkInProgress()
            }
        }
    }

    /**
     * Le funzioni showWorkInProgress e showWorkFinished aggiornano i componenti della UI
     * (progress bar e buttons) in base allo stato del lavoro.
     * - showWorkInProgress gestisce la visibilità e lo stato delle view mentre l'applicazione
     *   sta elaborando l'immagine.
     * - showWorkFinished gestisce la visibilità e lo stato delle view quando l'applicazione
     *   ha terminato l'elaborazione dell'immagine.
     */
    private fun showWorkInProgress() {
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.buttonCancel?.isEnabled = true
        binding?.buttonRunWork?.isEnabled = false
        binding?.buttonSeeFile?.isEnabled = false
    }

    private fun showWorkFinished() {
        binding?.progressBar?.visibility = View.INVISIBLE
        binding?.buttonCancel?.isEnabled = false
        binding?.buttonRunWork?.isEnabled = true
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        if (isDarkMode) {
            binding?.buttonRunWork?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonCancel?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonSeeFile?.style(R.style.ButtonNormalDarkMode)
        } else {
            binding?.buttonRunWork?.style(R.style.ButtonNormalLightMode)
            binding?.buttonCancel?.style(R.style.ButtonNormalLightMode)
            binding?.buttonSeeFile?.style(R.style.ButtonNormalLightMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}