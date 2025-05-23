package it.giovanni.arkivio.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import it.giovanni.arkivio.databinding.DialogFlowLayoutBinding
import it.giovanni.arkivio.fragments.detail.rubrica.RubricaListFragment
import it.giovanni.arkivio.utils.Globals
import java.util.*

class DialogFlowFragment : BaseFragment(SectionType.DIALOG_FLOW) {

    private var layoutBinding: DialogFlowLayoutBinding? = null
    private val binding get() = layoutBinding

    private var handler: Handler? = null
    private val delayTime: Long = 5000

    private val visibilityRunnable: Runnable = Runnable {
        if (binding?.speechContainer != null && binding?.suggestionsContainer != null) {
            binding?.speechContainer?.visibility = View.GONE
            binding?.suggestionsContainer?.visibility = View.VISIBLE
        }
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = DialogFlowLayoutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler(Looper.getMainLooper())

        binding?.icoCloseContainer?.setOnClickListener {
            currentActivity.onBackPressed()
        }

        handler?.postDelayed(visibilityRunnable, delayTime)

        binding?.buttonVoiceContainer?.setOnClickListener {

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            // intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ciao, come posso aiutarti?")

            if (intent.resolveActivity(currentActivity.packageManager) != null)
                launcher.launch(intent)
            else
                Toast.makeText(context, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show()
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val array: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding?.speechText?.text = array!![0]

            binding?.speechContainer?.visibility = View.VISIBLE
            binding?.suggestionsContainer?.visibility = View.GONE

            if (array!![0].contains("rubrica")) {
                currentActivity.openDetail(Globals.RUBRICA_REALTIME, null)
            } else if (array[0].contains("Giovanni")) {
                val contact = Bundle()
                contact.putString(RubricaListFragment.KEY_SPEECH_USERS, array[0])
                currentActivity.openDetail(Globals.RUBRICA_LIST, contact)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        handler?.removeCallbacks(visibilityRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}