package it.giovanni.arkivio.fragments.homepage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
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
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.R
import it.giovanni.arkivio.model.SelectedDay
import it.giovanni.arkivio.databinding.HomePageLayoutBinding
import it.giovanni.arkivio.fragments.HomeFragment
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.DateManager
import it.giovanni.arkivio.utils.Utils.convertDpToPixel
import it.giovanni.arkivio.utils.Utils.getBatteryCapacity
import it.giovanni.arkivio.utils.Utils.getHashKey
import it.giovanni.arkivio.utils.Utils.getRoundBitmap
import it.giovanni.arkivio.utils.Utils.getVersionNameLong
import it.giovanni.arkivio.utils.Utils.turnArrayListToString
import it.giovanni.arkivio.utils.Utils.turnArrayToString
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HomePageFragment : HomeFragment() {

    companion object {
        private val TAG = HomePageFragment::class.java.simpleName

        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): HomePageFragment {
            caller = c
            return HomePageFragment()
        }
    }

    private var layoutBinding: HomePageLayoutBinding? = null
    private val binding get() = layoutBinding

    private val delayTime: Long = 3000
    private var handler: Handler? = null
    private val currentHours = Date().hours

    private var list: ArrayList<String>? = null
    private var array: Array<String>? = null

    private val avatarRunnable: Runnable = Runnable {
        val avatar: Bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.giovanni)
        val roundAvatar: Bitmap = getRoundBitmap(avatar, avatar.width)
        binding?.icoAvatar?.setImageBitmap(roundAvatar)
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = HomePageLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intro = MediaPlayer.create(context, R.raw.intro)
        intro.start()

        handler = Handler(Looper.getMainLooper())

        list = ArrayList() // Oppure: ArrayList<String>()
        list?.add("Hi")
        list?.add("how")
        list?.add("are")
        list?.add("you?")

        // Come convertire una lista di stringhe in un array di stringhe:
        array = list?.toTypedArray()

        // Come convertire un array di stringhe in una stringa:
        val message1 = turnArrayToString(array!!)
        Log.i(TAG, message1)

        // Come convertire una lista di stringhe in una stringa:
        val message2 = turnArrayListToString(list!!)
        Log.i(TAG, message2)

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
        Log.i(TAG, "newList: " + turnArrayListToString(newList))

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
                Log.i(TAG, sdf.format(date))
            }
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        // ------------ END SORT ----------- //

        val date = Date()

        var dayOfWeek = SimpleDateFormat("EEEE", Locale.ITALY).format(date)
        dayOfWeek = dayOfWeek.substring(0, 1).uppercase() + dayOfWeek.substring(1)
        binding?.labelDay?.text = dayOfWeek

        val currentMonth = SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(date).substring(0, 3) +
                SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(date).substring(3, 4).uppercase() +
                SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(date).substring(4, SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(date).length)
        binding?.labelDate?.text = currentMonth

        binding?.labelTime?.text = DateManager(Date()).getFormatTime()

        handler?.postDelayed(avatarRunnable, delayTime)

        binding?.icoAvatar?.setOnClickListener {
            pickFromGallery()
        }

        val pixel = convertDpToPixel(requireContext(), 24F)
        Log.i(TAG, "pixel: $pixel")

        val versionName = BuildConfig.VERSION_NAME
        Log.i(TAG, "versionName: " + getVersionNameLong(versionName))

        val hashKey = getHashKey(requireContext())
        Log.i(TAG, "Hash Key: $hashKey")

        if (currentHours in 5..17) {
            binding?.lottieSun?.visibility = View.VISIBLE
            binding?.lottieMoon?.visibility = View.GONE
            binding?.labelGreeting?.setText(R.string.good_moorning_title)
        } else {
            binding?.lottieSun?.visibility = View.GONE
            binding?.lottieMoon?.visibility = View.VISIBLE
            binding?.labelGreeting?.setText(R.string.good_evening_title)
        }

        binding?.labelGreetingShimmer?.startShimmer()
        binding?.labelGiovanniShimmer?.startShimmer()

        val batteryLevel = "Livello della batteria: " + getBatteryCapacity(requireContext()) + " %"
        binding?.labelBatteryLevel?.text = batteryLevel
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (null != data) run {
                try {
                    if (data.data != null) {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(requireContext().contentResolver, data.data!!)
                        val avatar: Bitmap = ImageDecoder.decodeBitmap(source)
                        val roundAvatar: Bitmap = getRoundBitmap(avatar, avatar.width)
                        binding?.icoAvatar?.setImageBitmap(roundAvatar)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        handler?.removeCallbacks(avatarRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}