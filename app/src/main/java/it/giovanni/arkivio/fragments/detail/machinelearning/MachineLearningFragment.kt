package it.giovanni.arkivio.fragments.detail.machinelearning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.MlMachineLearningLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class MachineLearningFragment : DetailFragment() {

    private var layoutBinding: MlMachineLearningLayoutBinding? = null
    private val binding get() = layoutBinding

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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = MlMachineLearningLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelTextRecognition?.setOnClickListener {
            currentActivity.openDetail(Globals.TEXT_RECOGNITION, null)
        }
        binding?.labelImageLabeling?.setOnClickListener {
            currentActivity.openDetail(Globals.IMAGE_LABELING, null)
        }
        binding?.labelFacialDetection?.setOnClickListener {
            currentActivity.openDetail(Globals.FACIAL_DETECTION, null)
        }
        binding?.labelObjectDetectionAndTracking?.setOnClickListener {
            currentActivity.openDetail(Globals.OBJECT_DETECTION, null)
        }
        binding?.labelBarcodeScanning?.setOnClickListener {
            currentActivity.openDetail(Globals.BARCODE_SCANNING, null)
        }
        binding?.labelLanguageId?.setOnClickListener {
            currentActivity.openDetail(Globals.LANGUAGE_ID, null)
        }
        binding?.labelTranslationOnDevice?.setOnClickListener {
            currentActivity.openDetail(Globals.TRANSLATION, null)
        }
        binding?.labelQuickAnswer?.setOnClickListener {
            currentActivity.openDetail(Globals.QUICK_ANSWER, null)
        }
        binding?.labelAutomlVisionEdge?.setOnClickListener {
            currentActivity.openDetail(Globals.AUTOML_VISION_EDGE, null)
        }
        binding?.labelRecognitionOfLandmarks?.setOnClickListener {
            currentActivity.openDetail(Globals.LANDMARKS_RECOGNITION, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}