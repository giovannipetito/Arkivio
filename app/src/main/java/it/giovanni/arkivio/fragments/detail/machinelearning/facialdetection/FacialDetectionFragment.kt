package it.giovanni.arkivio.fragments.detail.machinelearning.facialdetection

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.airbnb.paris.extensions.style
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.MlFacialDetectionLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import it.giovanni.arkivio.utils.Utils
import kotlin.math.max

class FacialDetectionFragment : DetailFragment(), OnItemSelectedListener {

    private var layoutBinding: MlFacialDetectionLayoutBinding? = null
    private val binding get() = layoutBinding

    private var selectedImage: Bitmap? = null
    private var imageMaxWidth: Int? = null
    private var imageMaxHeight: Int? = null

    override fun getTitle(): Int {
        return R.string.facial_detection_title
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
        layoutBinding = MlFacialDetectionLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        val items = arrayOf("Gio & Tara 1", "Gio & Tara 2", "Gio & Tara 3")
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding?.mlSpinner?.adapter = adapter
        binding?.mlSpinner?.onItemSelectedListener = this

        binding?.mlButton?.setOnClickListener {
            runFaceContourDetection()
        }
    }

    private fun runFaceContourDetection() {
        val image = InputImage.fromBitmap(selectedImage!!, 0)
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()
        binding?.mlButton?.isEnabled = false
        val detector = FaceDetection.getClient(options)
        detector.process(image).addOnSuccessListener { faces ->
            binding?.mlButton?.isEnabled = true
            processFaceContourDetectionResult(faces)
        }.addOnFailureListener { e -> // Task failed with an exception
            binding?.mlButton?.isEnabled = true
            e.printStackTrace()
        }
    }

    private fun processFaceContourDetectionResult(faces: List<Face>) {
        // Task completed successfully
        if (faces.isEmpty()) {
            Toast.makeText(context, "No face found", Toast.LENGTH_SHORT).show()
            return
        }
        binding?.graphicOverlay?.clear()
        for (i in faces.indices) {
            val face = faces[i]
            val faceGraphic = FaceContourGraphic(binding?.graphicOverlay)
            binding?.graphicOverlay?.add(faceGraphic)
            faceGraphic.updateFace(face)
        }
    }

    // Functions for loading images from app assets.
    // Returns max image width, always for portrait mode. Caller needs to swap width / height for landscape mode.
    private fun getImageMaxWidth(): Int? {
        if (imageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            imageMaxWidth = binding?.mlImageView?.width
        }
        return imageMaxWidth
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for landscape mode.
    private fun getImageMaxHeight(): Int? {
        if (imageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            imageMaxHeight = binding?.mlImageView?.height
        }
        return imageMaxHeight
    }

    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        val targetWidth: Int
        val targetHeight: Int
        val maxWidthForPortraitMode = getImageMaxWidth()
        val maxHeightForPortraitMode = getImageMaxHeight()
        targetWidth = maxWidthForPortraitMode!!
        targetHeight = maxHeightForPortraitMode!!
        return Pair(targetWidth, targetHeight)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        binding?.graphicOverlay?.clear()
        when (position) {
            0 -> selectedImage = Utils.getBitmapFromAsset(requireContext(), "gio_tara_1.jpg")
            1 -> selectedImage = Utils.getBitmapFromAsset(requireContext(), "gio_tara_2.jpg")
            2 -> selectedImage = Utils.getBitmapFromAsset(requireContext(), "gio_tara_3.jpg")
        }
        if (selectedImage != null) {
            // Get the dimensions of the View
            val targetedSize = getTargetedWidthHeight()
            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = max(
                selectedImage?.width?.toFloat()!! / targetWidth.toFloat(),
                selectedImage?.height?.toFloat()!! / maxHeight.toFloat()
            )
            val resizedBitmap = Bitmap.createScaledBitmap(
                selectedImage!!,
                (selectedImage?.width!! / scaleFactor).toInt(),
                (selectedImage?.height!! / scaleFactor).toInt(),
                true
            )
            binding?.mlImageView?.setImageBitmap(resizedBitmap)
            selectedImage = resizedBitmap
        }
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
        if (isDarkMode)
            binding?.mlButton?.style(R.style.ButtonNormalDarkMode)
        else
            binding?.mlButton?.style(R.style.ButtonNormalLightMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}