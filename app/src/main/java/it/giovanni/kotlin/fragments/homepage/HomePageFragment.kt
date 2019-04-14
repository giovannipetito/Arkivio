package it.giovanni.kotlin.fragments.homepage

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.kotlin.fragments.HomeFragment
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.utils.Utils.Companion.getRoundBitmap
import it.giovanni.kotlin.R
import kotlinx.android.synthetic.main.home_layout.*
import java.text.SimpleDateFormat
import java.util.*

class HomePageFragment : HomeFragment() {

    private val GALLERY_CODE = 201
    private val DELAY_TIME: Long = 3000
    var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.home_layout
    }

    override fun getTitle(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        var caller: MainFragment? = null
        fun newInstance(c: MainFragment): HomePageFragment {
            caller = c
            return HomePageFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    @SuppressLint("SimpleDateFormat")
    override fun onFragmentReady() {
        super.onFragmentReady()

        val date = Date()

        val dayOfWeek = SimpleDateFormat("EEEE").format(date).substring(0, 1).toUpperCase() +
                SimpleDateFormat("EEEE").format(date).substring(1, SimpleDateFormat("EEEE").format(date).length)
        day.text = dayOfWeek

        val currentMonth = SimpleDateFormat("dd MMMM").format(date).substring(0, 3) +
                SimpleDateFormat("dd MMMM").format(date).substring(3, 4).toUpperCase() +
                SimpleDateFormat("dd MMMM").format(date).substring(4, SimpleDateFormat("dd MMMM").format(date).length)
        month.text = currentMonth

        year.text = SimpleDateFormat("yyyy").format(date)

        Handler().postDelayed({
            val avatar : Bitmap = BitmapFactory.decodeResource(context!!.resources, R.drawable.giovanni)
            val roundAvatar : Bitmap = getRoundBitmap(avatar, avatar.width)
            ico_avatar.setImageBitmap(roundAvatar)
        }, DELAY_TIME)

        ico_avatar.setOnClickListener {
            pickFromGallery()
        }
    }

    private fun pickFromGallery() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) run {
            try {
                if (data.data != null) {
                    val avatar : Bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, data.data)
                    val roundAvatar : Bitmap = getRoundBitmap(avatar, avatar.width)
                    ico_avatar.setImageBitmap(roundAvatar)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}