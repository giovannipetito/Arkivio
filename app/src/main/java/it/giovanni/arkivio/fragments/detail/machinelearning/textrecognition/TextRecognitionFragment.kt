package it.giovanni.arkivio.fragments.detail.machinelearning.textrecognition

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
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.GraphicOverlay
import it.giovanni.arkivio.databinding.MlTextRecognitionLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import it.giovanni.arkivio.utils.Utils
import kotlin.math.max

class TextRecognitionFragment : DetailFragment(), OnItemSelectedListener {

    private var layoutBinding: MlTextRecognitionLayoutBinding? = null
    private val binding get() = layoutBinding

    private var selectedImage: Bitmap? = null
    private var imageMaxWidth: Int? = null
    private var imageMaxHeight: Int? = null

    override fun getTitle(): Int {
        return R.string.text_recognition_title
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
        layoutBinding = MlTextRecognitionLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        val items = arrayOf("Test Text")
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding?.mlSpinner?.adapter = adapter
        binding?.mlSpinner?.onItemSelectedListener = this

        binding?.mlButton?.setOnClickListener {
            runTextRecognition()
        }
    }

    private fun runTextRecognition() {
        val image = InputImage.fromBitmap(selectedImage!!, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        binding?.mlButton?.isEnabled = false
        recognizer.process(image).addOnSuccessListener { texts ->
            binding?.mlButton?.isEnabled = true
            processTextRecognitionResult(texts)
        }.addOnFailureListener { e -> // Task failed with an exception
            binding?.mlButton?.isEnabled = true
            e.printStackTrace()
        }
    }

    private fun processTextRecognitionResult(texts: Text) {
        val blocks = texts.textBlocks
        if (blocks.size == 0) {
            Toast.makeText(context, "No text found", Toast.LENGTH_SHORT).show()
            return
        }
        binding?.graphicOverlay?.clear()
        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {
                    val textGraphic: GraphicOverlay.Graphic = TextGraphic(binding?.graphicOverlay, elements[k])
                    binding?.graphicOverlay?.add(textGraphic)
                }
            }
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
    private fun getTargetedWidthHeight(): Pair<Int?, Int?> {
        val targetWidth: Int?
        val targetHeight: Int?
        val maxWidthForPortraitMode = getImageMaxWidth()
        val maxHeightForPortraitMode = getImageMaxHeight()
        targetWidth = maxWidthForPortraitMode
        targetHeight = maxHeightForPortraitMode
        return Pair(targetWidth, targetHeight)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        binding?.graphicOverlay?.clear()
        when (position) {
            0 -> selectedImage = Utils.getBitmapFromAsset(requireContext(), "grass_text.jpg")
        }
        if (selectedImage != null) {
            // Get the dimensions of the View
            val targetedSize = getTargetedWidthHeight()
            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = max(
                selectedImage?.width?.toFloat()!! / targetWidth?.toFloat()!!,
                selectedImage?.height?.toFloat()!! / maxHeight?.toFloat()!!
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