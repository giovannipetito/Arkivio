package it.giovanni.arkivio.fragments.homepage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.fragments.HomeFragment
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.utils.Utils.Companion.getRoundBitmap
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.SelectedDay
import it.giovanni.arkivio.databinding.HomePageLayoutBinding
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.DateManager
import it.giovanni.arkivio.utils.Utils.Companion.convertDpToPixel
import it.giovanni.arkivio.utils.Utils.Companion.getBatteryCapacity
import it.giovanni.arkivio.utils.Utils.Companion.getHashKey
import it.giovanni.arkivio.utils.Utils.Companion.getVersionNameLong
import it.giovanni.arkivio.utils.Utils.Companion.turnArrayListToString
import it.giovanni.arkivio.utils.Utils.Companion.turnArrayToString
import kotlinx.android.synthetic.main.home_page_layout.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomePageFragment : HomeFragment() {

    private val mTag = HomePageFragment::class.java.simpleName

    // private val galleryCode = 201
    private val delayTime: Long = 3000
    private var viewFragment: View? = null
    private val currentHours = Date().hours

    private var list: ArrayList<String>? = null
    private var array: Array<String>? = null

    override fun getLayout(): Int {
        return NO_LAYOUT
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    companion object {
        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): HomePageFragment {
            caller = c
            return HomePageFragment()
        }
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: HomePageLayoutBinding? = DataBindingUtil.inflate(inflater, R.layout.home_page_layout, container, false)
        viewFragment = binding?.root

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.temp = model
        binding?.presenter = darkModePresenter

        val intro = MediaPlayer.create(context, R.raw.intro)
        intro.start()

        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list = ArrayList() // Oppure: ArrayList<String>()
        list?.add("Hi")
        list?.add("how")
        list?.add("are")
        list?.add("you?")

        // Come convertire una lista di stringhe in un array di stringhe:
        array = list?.toTypedArray()

        // Come convertire un array di stringhe in una stringa:
        val message1 = turnArrayToString(array!!)
        Log.i(mTag, message1)

        // Come convertire una lista di stringhe in una stringa:
        val message2 = turnArrayListToString(list!!)
        Log.i(mTag, message2)

        // Se ho bisogno di un'array di stringhe di cui conosco la dimensione, posso inizializzarlo nel modo seguente:
        array = arrayOf("", "", "")
        array?.set(0, "Ciao")
        array?.set(1, "come")
        array?.set(2, "stai?")

        val list: ArrayList<String> = ArrayList()
        list.add("Ciao")
        list.add("mi")
        list.add("mi")
        list.add("chiamo")
        list.add("Ciao")
        list.add("Giovanni")
        list.add("mi")
        list.add("e")
        list.add("ho")
        list.add("32")
        list.add("anni")
        list.add("anni")

        // Dato un ArrayList di String, il codice seguente restituisce un ArrayList di String senza occorrenze.
        val newList: ArrayList<String> = ArrayList()
        for (i in 0 until list.size) { // NOTA: posso anche scrivere: for (i in list.indices) {}
            var present = false
            for (j in 0 until newList.size) {
                if (newList[j] == list[i]) {
                    present = true
                    break
                }
            }
            if (!present) {
                newList.add(list[i])
            }
        }
        Log.i(mTag, "newList: " + turnArrayListToString(newList))

        // ----------- START SORT ---------- //
        // Dato un ArrayList di Date (o anche di SelectedDay come in questo caso), restituisco un
        // ArrayList di Date ordinato.
        val items: ArrayList<SelectedDay> = ArrayList()
        items.add(SelectedDay("2020", "11", "10"))
        items.add(SelectedDay("2020", "11", "02"))
        items.add(SelectedDay("2021", "02", "04"))
        items.add(SelectedDay("2021", "01", "05"))
        items.add(SelectedDay("2020", "10", "16"))
        items.add(SelectedDay("2020", "11", "01"))
        items.add(SelectedDay("2020", "12", "24"))
        items.add(SelectedDay("2020", "12", "25"))
        items.add(SelectedDay("2021", "01", "05"))
        items.add(SelectedDay("2020", "11", "30"))
        items.add(SelectedDay("2020", "11", "16"))
        items.add(SelectedDay("2021", "02", "06"))

        val dateItems = ArrayList<Date>()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        for (item in items) {
            dateItems.add(sdf.parse(item.dayOfMonth + "/" + item.month + "/" + item.year)!!)
        }

        try {
            dateItems.sort()
            for (date in dateItems) {
                Log.i(mTag, sdf.format(date))
            }
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        // ------------ END SORT ----------- //

        val date = Date()

        val dayOfWeek = SimpleDateFormat("EEEE", Locale.ITALY).format(date).capitalize(Locale.getDefault())
        label_day.text = dayOfWeek

        val currentMonth = SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(date).substring(0, 3) +
                SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(date).substring(3, 4).uppercase() +
                SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(date).substring(4, SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(date).length)
        label_date.text = currentMonth

        label_time.text = DateManager(Date()).getFormatTime()

        Handler(Looper.getMainLooper()).postDelayed({
            val avatar: Bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.giovanni)
            val roundAvatar: Bitmap = getRoundBitmap(avatar, avatar.width)
            ico_avatar.setImageBitmap(roundAvatar)
        }, delayTime)

        ico_avatar.setOnClickListener {
            pickFromGallery()
        }

        val pixel = convertDpToPixel(requireContext(), 24F)
        Log.i(mTag, "pixel: $pixel")

        val versionName = BuildConfig.VERSION_NAME
        Log.i(mTag, "versionName: " + getVersionNameLong(versionName))

        val hashKey = getHashKey(requireContext())
        Log.i(mTag, "Hash Key: $hashKey")

        if (currentHours in 5..17) {
            lottie_sun.visibility = View.VISIBLE
            lottie_moon.visibility = View.GONE
            label_greeting.setText(R.string.good_moorning_title)
        } else {
            lottie_sun.visibility = View.GONE
            lottie_moon.visibility = View.VISIBLE
            label_greeting.setText(R.string.good_evening_title)
        }

        val batteryLevel = "Livello della batteria: " + getBatteryCapacity(requireContext()) + " %"
        label_battery_level.text = batteryLevel
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // startActivityForResult(intent, galleryCode)
        launcher.launch(intent)
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryCode && resultCode == RESULT_OK && null != data) run {
            try {
                if (data.data != null) {
                    val avatar: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, data.data)
                    val roundAvatar: Bitmap = getRoundBitmap(avatar, avatar.width)
                    ico_avatar.setImageBitmap(roundAvatar)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    */

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data

            if (null != data) run {
                try {
                    if (data.data != null) {
                        val avatar: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, data.data)
                        val roundAvatar: Bitmap = getRoundBitmap(avatar, avatar.width)
                        ico_avatar.setImageBitmap(roundAvatar)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}