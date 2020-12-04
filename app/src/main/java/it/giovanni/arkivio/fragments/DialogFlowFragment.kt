package it.giovanni.arkivio.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import it.giovanni.arkivio.fragments.detail.rubrica.RubricaListFragment
import it.giovanni.arkivio.utils.Globals
import kotlinx.android.synthetic.main.dialog_flow_layout.*
import java.util.*

class DialogFlowFragment : BaseFragment(SectionType.DIALOG_FLOW) {

    private val DELAY_TIME: Long = 5000
    private var REQUEST_CODE: Int = 10

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ico_close_container.setOnClickListener {
            currentActivity.onBackPressed()
        }

        Handler().postDelayed({
            if (speech_container != null && suggestions_container != null) {
                speech_container.visibility = View.GONE
                suggestions_container.visibility = View.VISIBLE
            }
        }, DELAY_TIME)

        button_voice_container.setOnClickListener {

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            // intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ciao, come posso aiutarti?")

            if (intent.resolveActivity(currentActivity.packageManager) != null)
                startActivityForResult(intent, REQUEST_CODE)
            else
                Toast.makeText(context, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE -> if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                speech_text!!.text = result!![0]

                speech_container.visibility = View.VISIBLE
                suggestions_container.visibility = View.GONE
                if (result[0].contains("rubrica")) {
                    currentActivity.openDetail(Globals.RUBRICA_HOME, null)
                } else if (result[0].contains("Giovanni")) {
                    val contact = Bundle()
                    contact.putString(RubricaListFragment.KEY_SPEECH_USERS, result[0])
                    currentActivity.openDetail(Globals.RUBRICA_LIST, contact)
                }
            }
        }
    }
}