package it.giovanni.kotlin.fragments.homepage

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import it.giovanni.kotlin.fragments.HomeFragment
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.R
import it.giovanni.kotlin.utils.Globals
import it.giovanni.kotlin.utils.Utils.Companion.decodeBase64Url
import it.giovanni.kotlin.utils.Utils.Companion.encodeBase64Url
import kotlinx.android.synthetic.main.working_area_layout.*

class WorkingAreaFragment : HomeFragment() {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.working_area_layout
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    companion object {
        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): WorkingAreaFragment {
            caller = c
            return WorkingAreaFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        label_logcat.setOnClickListener {
            currentActivity.openDetail(Globals.LOGCAT_PROJECTS, null)
        }
        label_date_manager.setOnClickListener {
            currentActivity.openDetail(Globals.DATE_MANAGER, null)
        }
        label_rubrica.setOnClickListener {
            currentActivity.openDetail(Globals.RUBRICA_HOME, null)
        }

        label_pdf.setOnClickListener {

            val encodedUrl = encodeBase64Url("https://kotlinlang.org/docs/kotlin-docs.pdf")
            Log.i("TAGPDF", "Encoded url: $encodedUrl")

            val decodedUrl = decodeBase64Url("rO0ABXQAK2h0dHBzOi8va290bGlubGFuZy5vcmcvZG9jcy9rb3RsaW4tZG9jcy5wZGY=")
            Log.i("TAGPDF", "Decoded url: $decodedUrl")

            val intent = Intent(Intent.ACTION_VIEW)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            // intent.action = Intent.ACTION_VIEW
            intent.type = "application/pdf"
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(activity, "No application found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}