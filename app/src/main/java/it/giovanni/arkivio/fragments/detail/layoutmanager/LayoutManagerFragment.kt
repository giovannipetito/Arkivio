package it.giovanni.arkivio.fragments.detail.layoutmanager

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.TimelineView
import it.giovanni.arkivio.databinding.LayoutManagerLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Utils.checkEmail

class LayoutManagerFragment: DetailFragment(), TimelineView.TimelineViewListener {

    private var layoutBinding: LayoutManagerLayoutBinding? = null
    private val binding get() = layoutBinding

    private var progressBarContainer: RelativeLayout? = null
    private lateinit var list: List<String>

    override fun getTitle(): Int {
        return R.string.layout_manager_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return false
    }

    override fun closeAction(): Boolean {
        return true
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
        layoutBinding = LayoutManagerLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBarContainer = binding?.progressBarContent?.progressBarContainer
        val progressBar = binding?.progressBarContent?.progressBar
        val bar = binding?.progressBarContent?.bar

        val gradientDrawableBar = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.rosso),
                ContextCompat.getColor(requireContext(), R.color.giallo),
                ContextCompat.getColor(requireContext(), R.color.blu)
            )
        )

        gradientDrawableBar.cornerRadius = 50f
        val drawableBar: Drawable = gradientDrawableBar.current // Oppure: gradientDrawableBar.mutate()
        progressBar?.background = drawableBar

        binding?.multiSpinnerView?.setValues(104F, 140F, 104F, 20F)
        binding?.numberPicker1?.minValue = 104
        binding?.numberPicker1?.maxValue = 167
        binding?.numberPicker1?.value = 104
        binding?.numberPicker1?.setOnValueChangedListener { _, _, newVal ->

            binding?.multiSpinnerView?.setValues(newVal.toFloat(), 140F, 104F, 20F)
        }

        binding?.numberPicker2?.minValue = 0
        binding?.numberPicker2?.maxValue = 350
        binding?.numberPicker2?.setOnValueChangedListener { _, _, newVal ->
            bar?.translationX = newVal.toFloat()
        }

        binding?.icon1?.background = ResourcesCompat.getDrawable(resources, R.drawable.giovanni, null)

        Glide.with(requireContext())
            .load(R.drawable.giovanni)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
            .into(binding?.icon2!!)

        Glide.with(requireContext())
            .load(R.drawable.giovanni)
            .placeholder(R.drawable.logo_audioslave_blue)
            .error(R.drawable.logo_audioslave_blue)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(54)))
            .into(binding?.icon3!!)

        Glide.with(requireContext())
            .load(R.drawable.giovanni)
            .apply(RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.logo_audioslave_blue))
            .into(binding?.icon4!!)

        Glide.with(requireContext())                       // Passing context.
            .load(R.drawable.giovanni)                     // Passing your url to load image.
            .placeholder(R.drawable.logo_audioslave_blue)  // The default image. It would be loaded at initial time and it will replace with your loaded image once Glide successfully load image using url.
            .error(R.drawable.logo_audioslave_blue)        // In case of any Glide exception or not able to download then this image will be appear.
            .diskCacheStrategy(DiskCacheStrategy.ALL)      // Using to load into cache, then second time it will load fast.
            .apply(RequestOptions.circleCropTransform())   // This CircleTransform class help to crop an image as circle.
            // .animate(R.anim.fade_in)                    // When image (url) will be loaded by Glide, then this face in animation help to replace url image in the place of placeHolder image.
            .into(binding?.icon5!!)                        // Passing imageView reference to appear the image.

        val timelineView: TimelineView = view.findViewById(R.id.bar_tagli)
        list = listOf("10€", "20€", "30€", "40€", "50€")

        timelineView.setValues(list)
        timelineView.setTimelineViewListener(this)

        selectedValue = "20€"
        timelineView.setValue(selectedValue, TimelineView.DISCRETE)

        val buttonMinus: ImageView = view.findViewById(R.id.button_minus)
        val buttonPlus: ImageView = view.findViewById(R.id.button_plus)

        buttonMinus.setOnClickListener {
            if (list.isNotEmpty()) {
                selectedValue = getPreviousValue(list)
                timelineView.setValue(selectedValue, TimelineView.DISCRETE)
            }
        }

        buttonPlus.setOnClickListener {
            if (list.isNotEmpty()) {
                selectedValue = getNextValue(list)
                timelineView.setValue(selectedValue, TimelineView.DISCRETE)
            }
        }

        binding?.editEmail?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (checkEmail(p0.toString()))
                    binding?.editEmail?.setTextColor(ContextCompat.getColor(context!!, R.color.green_A700))
                else
                    binding?.editEmail?.setTextColor(ContextCompat.getColor(context!!, R.color.red_A700))
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onFinishDragging(var1: Int, var2: String) {
        selectedValue = var2
    }

    private var selectedValue : String = ""
        set(value) {
            field = value
            processSelectedValue()
        }

    private fun processSelectedValue() {
        if (selectedValue.isEmpty())
            return
    }

    private fun getPreviousValue(list: List<String>): String {
        for (i in list.indices) {
            if (list[i].equals(selectedValue, ignoreCase = true)) {
                return if (i == 0)
                    list[0]
                else
                    list[i - 1]
            }
        }
        return list[0]
    }

    private fun getNextValue(list: List<String>): String {
        for (i in list.indices) {
            if (list[i].equals(selectedValue, ignoreCase = true)) {
                return if (i == list.size - 1)
                    list[list.size - 1]
                else
                    list[i + 1]
            }
        }
        return list[0]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}