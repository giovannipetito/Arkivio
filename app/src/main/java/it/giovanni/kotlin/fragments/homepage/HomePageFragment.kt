package it.giovanni.kotlin.fragments.homepage

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.kotlin.BuildConfig
import it.giovanni.kotlin.fragments.HomeFragment
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.utils.Utils.Companion.getRoundBitmap
import it.giovanni.kotlin.R
import it.giovanni.kotlin.utils.Utils.Companion.convertDpToPixel
import it.giovanni.kotlin.utils.Utils.Companion.getHashKey
import it.giovanni.kotlin.utils.Utils.Companion.getVersionNameLong
import kotlinx.android.synthetic.main.home_page_layout.*
import java.text.SimpleDateFormat
import java.util.*

class HomePageFragment : HomeFragment() {

    /*
    - Click on Gradle (from right side panel)
    - Click on your project
    - Click on Tasks
    - Click on Android
    - Double click on signingReport
    You will get SHA1 and MD5 in Run Tab:

    Variant: debug
    Config: debug
    Store: C:\Users\giova\.android\debug.keystore
    Alias: AndroidDebugKey
    MD5: 10:DF:F7:97:43:F9:2F:73:47:39:DE:D3:9C:93:2D:7E
    SHA1: 19:6F:40:61:7D:C4:56:A1:4A:03:B6:F6:BE:EA:74:16:5B:13:B6:92
    SHA-256: A1:0D:D2:8E:B7:AF:FE:6A:13:DA:3B:30:B6:19:02:A9:88:79:7F:DA:A0:A7:04:F2:AB:A0:A4:88:8F:F4:1B:5F
    Valid until: mercoledì 8 aprile 2048
    */

    private val GALLERY_CODE = 201
    private val DELAY_TIME: Long = 3000
    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.home_page_layout
    }

    override fun getTitle(): Int {
        return NO_TITLE
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val avatar: Bitmap = BitmapFactory.decodeResource(context!!.resources, R.drawable.giovanni)
            val roundAvatar: Bitmap = getRoundBitmap(avatar, avatar.width)
            ico_avatar.setImageBitmap(roundAvatar)
        }, DELAY_TIME)

        ico_avatar.setOnClickListener {
            pickFromGallery()
        }

        val pixel = convertDpToPixel(context!!, 24F)
        Log.i("HOMEPAGETAG", "pixel: $pixel")

        val versionName = BuildConfig.VERSION_NAME
        Log.i("HOMEPAGETAG", "versionName: " + getVersionNameLong(versionName))

        val hashKey = getHashKey(context!!)
        Log.i("HOMEPAGETAG", "Hash Key: $hashKey")
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
                    val avatar: Bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, data.data)
                    val roundAvatar: Bitmap = getRoundBitmap(avatar, avatar.width)
                    ico_avatar.setImageBitmap(roundAvatar)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}