package it.giovanni.kotlin.fragments.detail

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment
import kotlinx.android.synthetic.main.layout_manage_layout.*

class LayoutManageFragment: DetailFragment() {

    private var viewFragment: View? = null
    private var viewParent: View? = null
    private var progressBarContainer: RelativeLayout? = null

    override fun getLayout(): Int {
        return R.layout.layout_manage_layout
    }

    override fun getTitle(): Int {
        return R.string.layout_manage_title
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

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewGroup = viewFragment?.findViewById(R.id.progress_bar_viewgroup) as ViewGroup
        viewParent = layoutInflater.inflate(R.layout.layout_manage_layout, viewGroup, false)

        // val inflater = LayoutInflater.from(context)
        // val viewParent = inflater.inflate(R.layout.layout_manage_layout, null)
        // NOTA: viewParent e viewFragment sono equivalenti.

        val progressBarContent = viewFragment?.findViewById(R.id.progress_bar_content) as View
        progressBarContainer = progressBarContent.findViewById(R.id.progress_bar_container)
        val progressBar = progressBarContent.findViewById(R.id.progress_bar) as RelativeLayout
        val bar = progressBarContent.findViewById(R.id.bar) as ImageView

        val drawableBar = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(resources.getColor(R.color.red),
                resources.getColor(R.color.yellow),
                resources.getColor(R.color.azzurro))
        )

        drawableBar.cornerRadius = 50f
        progressBar.setBackgroundDrawable(drawableBar)

        /*
        var progressbarwidth: Int? = null
        viewParent?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (progressBarContainer != null)
                    progressbarwidth = progressBarContainer?.width!! * 1000 / 5
            }
        })
        viewParent?.requestLayout()
        */

        multi_spinner_view.setValues(104F, 140F, 104F, 20F)
        number_picker_1.minValue = 104
        number_picker_1.maxValue = 167
        number_picker_1.value = 104
        // number_picker_1.setBackgroundColor(context?.resources!!.getColor(R.color.colorPrimary))
        number_picker_1.setOnValueChangedListener { _, _, newVal ->

            multi_spinner_view.setValues(newVal.toFloat(), 140F, 104F, 20F)
        }

        number_picker_2.minValue = 0
        number_picker_2.maxValue = 350
        number_picker_2.setOnValueChangedListener { _, _, newVal ->

            bar.translationX = newVal.toFloat()
        }

        icon_1.background = resources.getDrawable(R.drawable.giovanni)

        Glide.with(context!!)
            .load(R.drawable.giovanni)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
            .into(icon_2)

        Glide.with(context!!)
            .load(R.drawable.giovanni)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(54)))
            .into(icon_3)

        Glide.with(context!!)
            .load(R.drawable.giovanni)
            .apply(RequestOptions()
                .circleCrop()
                .placeholder(R.mipmap.ic_launcher))
            .into(icon_4)

        Glide.with(context!!)                            // Passing context.
            .load(R.drawable.giovanni)                   // Passing your url to load image.
            .placeholder(R.mipmap.ic_launcher)           // The default image. It would be loaded at initial time and it will replace with your loaded image once Glide successfully load image using url.
            .error(R.mipmap.ic_launcher)                 // In case of any Glide exception or not able to download then this image will be appear.
            .diskCacheStrategy(DiskCacheStrategy.ALL)    // Using to load into cache, then second time it will load fast.
            .apply(RequestOptions.circleCropTransform()) // This CircleTransform class help to crop an image as circle.
            // .animate(R.anim.fade_in)                  // When image (url) will be loaded by Glide, then this face in animation help to replace url image in the place of placeHolder image.
            .into(icon_5)                                // Passing imageView reference to appear the image.
    }
}