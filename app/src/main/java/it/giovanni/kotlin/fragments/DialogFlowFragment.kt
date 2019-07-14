package it.giovanni.kotlin.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import kotlinx.android.synthetic.main.dialog_flow_layout.*
import java.util.*

class DialogFlowFragment : BaseFragment(SectionType.DIALOG_FLOW) {

    private var REQUEST_CODE: Int = 10

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_voice.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            /*
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, 10)
            else
                Toast.makeText(context, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show()
            */
            startActivityForResult(intent, REQUEST_CODE) // TODO: Da cancellare se risolvo getPackageManager()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE -> if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                written_speech_text!!.text = result[0]
            }
        }
    }
}