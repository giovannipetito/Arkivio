package it.giovanni.arkivio.fragments.detail.machinelearning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Globals
import kotlinx.android.synthetic.main.ml_machine_learning_layout.*

class MachineLearningFragment : DetailFragment() {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.ml_machine_learning_layout
    }

    override fun getTitle(): Int {
        return R.string.machine_learning_title
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

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        label_text_recognition.setOnClickListener {
            currentActivity.openDetail(Globals.TEXT_RECOGNITION, null)
        }
        label_image_labeling.setOnClickListener {
            currentActivity.openDetail(Globals.IMAGE_LABELING, null)
        }
        label_facial_detection.setOnClickListener {
            currentActivity.openDetail(Globals.FACIAL_DETECTION, null)
        }
        label_object_detection_and_tracking.setOnClickListener {
            currentActivity.openDetail(Globals.OBJECT_DETECTION, null)
        }
        label_barcode_scanning.setOnClickListener {
            currentActivity.openDetail(Globals.BARCODE_SCANNING, null)
        }
        label_language_id.setOnClickListener {
            currentActivity.openDetail(Globals.LANGUAGE_ID, null)
        }
        label_translation_on_device.setOnClickListener {
            currentActivity.openDetail(Globals.TRANSLATION, null)
        }
        label_quick_answer.setOnClickListener {
            currentActivity.openDetail(Globals.QUICK_ANSWER, null)
        }
        label_automl_vision_edge.setOnClickListener {
            currentActivity.openDetail(Globals.AUTOML_VISION_EDGE, null)
        }
        label_recognition_of_landmarks.setOnClickListener {
            currentActivity.openDetail(Globals.LANDMARKS_RECOGNITION, null)
        }
    }
}