package it.giovanni.arkivio.fragments.detail.machinelearning.facialdetection

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.GraphicOverlay
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Utils
import kotlin.math.max

class FacialDetectionFragment : DetailFragment(), OnItemSelectedListener {

    private var viewFragment: View? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var mlImageView: ImageView? = null
    private var mlButton: Button? = null
    private var selectedImage: Bitmap? = null
    private var imageMaxWidth: Int? = null
    private var imageMaxHeight: Int? = null

    override fun getLayout(): Int {
        return R.layout.ml_facial_detection_layout
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graphicOverlay = viewFragment?.findViewById(R.id.graphic_overlay)
        mlImageView = viewFragment?.findViewById(R.id.ml_imageview)
        mlButton = viewFragment?.findViewById(R.id.ml_button)

        val spinner: Spinner = viewFragment?.findViewById(R.id.ml_spinner)!!
        val items = arrayOf("Gio & Tara 1", "Gio & Tara 2", "Gio & Tara 3")
        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        mlButton!!.setOnClickListener {
            runFaceContourDetection()
        }
    }

    private fun runFaceContourDetection() {
        val image = InputImage.fromBitmap(selectedImage!!, 0)
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()
        mlButton!!.isEnabled = false
        val detector = FaceDetection.getClient(options)
        detector.process(image).addOnSuccessListener { faces ->
            mlButton!!.isEnabled = true
            processFaceContourDetectionResult(faces)
        }.addOnFailureListener { e -> // Task failed with an exception
            mlButton!!.isEnabled = true
            e.printStackTrace()
        }
    }

    private fun processFaceContourDetectionResult(faces: List<Face>) {
        // Task completed successfully
        if (faces.isEmpty()) {
            Toast.makeText(context, "No face found", Toast.LENGTH_SHORT).show()
            return
        }
        graphicOverlay!!.clear()
        for (i in faces.indices) {
            val face = faces[i]
            val faceGraphic = FaceContourGraphic(graphicOverlay)
            graphicOverlay!!.add(faceGraphic)
            faceGraphic.updateFace(face)
        }
    }

    // Functions for loading images from app assets.
    // Returns max image width, always for portrait mode. Caller needs to swap width / height for landscape mode.
    private fun getImageMaxWidth(): Int? {
        if (imageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            imageMaxWidth = mlImageView!!.width
        }
        return imageMaxWidth
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for landscape mode.
    private fun getImageMaxHeight(): Int? {
        if (imageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            imageMaxHeight = mlImageView!!.height
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
        graphicOverlay!!.clear()
        when (position) {
            0 -> selectedImage = Utils.getBitmapFromAsset(context!!, "gio_tara_1.jpg")
            1 -> selectedImage = Utils.getBitmapFromAsset(context!!, "gio_tara_2.jpg")
            2 -> selectedImage = Utils.getBitmapFromAsset(context!!, "gio_tara_3.jpg")
        }
        if (selectedImage != null) {
            // Get the dimensions of the View
            val targetedSize = getTargetedWidthHeight()
            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = max(
                selectedImage?.width!!.toFloat() / targetWidth.toFloat(),
                selectedImage?.height!!.toFloat() / maxHeight.toFloat()
            )
            val resizedBitmap = Bitmap.createScaledBitmap(
                selectedImage!!,
                (selectedImage?.width!! / scaleFactor).toInt(),
                (selectedImage?.height!! / scaleFactor).toInt(),
                true
            )
            mlImageView!!.setImageBitmap(resizedBitmap)
            selectedImage = resizedBitmap
        }
    }
}