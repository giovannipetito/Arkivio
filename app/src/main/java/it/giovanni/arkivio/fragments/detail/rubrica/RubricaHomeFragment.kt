package it.giovanni.arkivio.fragments.detail.rubrica

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.paris.extensions.style
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import it.giovanni.arkivio.R
import it.giovanni.arkivio.model.user.Response
import it.giovanni.arkivio.model.user.User
import it.giovanni.arkivio.model.user.UserResponse
import it.giovanni.arkivio.customview.Brick
import it.giovanni.arkivio.databinding.RubricaHomeLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.restclient.realtime.IRealtime
import it.giovanni.arkivio.restclient.realtime.MyRealtimeClient.Companion.callRealtimeDatabase
import it.giovanni.arkivio.viewinterfaces.IFlexBoxCallback
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadUsersFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.saveUsersToPreferences
import it.giovanni.arkivio.utils.Utils
import kotlin.math.roundToInt

class RubricaHomeFragment: DetailFragment(), IFlexBoxCallback, IRealtime {

    private var layoutBinding: RubricaHomeLayoutBinding? = null
    private val binding get() = layoutBinding

    private var mResponse: Response? = null
    private var users: ArrayList<User>? = null

    override fun getTitle(): Int {
        return R.string.rubrica_home_title
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = RubricaHomeLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        mResponse = Response()
        users = ArrayList()

        binding?.initButton?.setOnClickListener {
            mResponse?.users = initUsers()
            saveUsersToPreferences(mResponse)
        }

        binding?.jsonButton?.setOnClickListener {
            mResponse?.users = getUsersFromJson()
            saveUsersToPreferences(mResponse)
        }

        binding?.realtimeButton?.setOnClickListener {
            showProgressDialog()
            callRealtimeDatabase(this)
        }

        binding?.usersContainer?.setOnClickListener {

            val bundle = Bundle()
            val brickUsers = ArrayList<User>()
            val count = binding?.flexboxLayoutUsers?.childCount!!
            if (count > 0) {
                for (index in 1..count) {
                    val brick = (binding?.flexboxLayoutUsers?.getChildAt(index - 1) as Brick)
                    val brickUser = User(brick.getName(), brick.getName(), "", "", ArrayList(), "", "", true)
                    brickUsers.add(brickUser)
                }
            }
            bundle.putSerializable(RubricaListFragment.KEY_BRICKS, brickUsers)

            if (loadUsersFromPreferences() != null)
                currentActivity.openDetail(Globals.RUBRICA_LIST, bundle, this@RubricaHomeFragment, Globals.REQUEST_CODE_EVENT_USER_SEARCH)
            else
                Toast.makeText(context, "Inizializza la lista di utenti.", Toast.LENGTH_LONG).show()
        }

        binding?.flexboxLayoutUsers?.setOnClickListener {

            val bundle = Bundle()
            val brickUsers = ArrayList<User>()
            val count = binding?.flexboxLayoutUsers?.childCount!!
            if (count > 0) {
                for (index in 1..count) {
                    val brick = (binding?.flexboxLayoutUsers?.getChildAt(index - 1) as Brick)
                    val brickUser = User(brick.getName(), brick.getName(), "", "", ArrayList(), "", "", true)
                    brickUsers.add(brickUser)
                }
            }
            bundle.putSerializable(RubricaListFragment.KEY_BRICKS, brickUsers)
            currentActivity.openDetail(Globals.RUBRICA_LIST, bundle, this@RubricaHomeFragment, Globals.REQUEST_CODE_EVENT_USER_SEARCH)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Globals.REQUEST_CODE_EVENT_USER_SEARCH && data != null && data.hasExtra(Globals.BACK_PARAM_KEY_USER_SEARCH)) {

            users = data.getSerializableExtra(Globals.BACK_PARAM_KEY_USER_SEARCH) as ArrayList<User>

            if (users != null) {
                binding?.flexboxLayoutUsers?.removeAllViews()
                for (user in users!!) {
                    val brick = Brick(requireContext())
                    brick.mode(Brick.ModeType.VIEW)
                    brick.setName(user.nome!!)
                    brick.callback(this)
                    binding?.flexboxLayoutUsers?.addView(brick)
                }
                setFlexBoxHeight(binding?.flexboxLayoutUsers?.childCount!!)
            }
        }
    }

    private fun setFlexBoxHeight(brickCount: Int) {
        val density = resources.displayMetrics.density
        if (brickCount == 0) {
            binding?.flexboxLayoutUsers?.visibility = View.GONE
            binding?.usersText?.visibility = View.VISIBLE
            binding?.flexboxLayoutContainer?.layoutParams?.height = (50.toFloat() * density).roundToInt()
        } else {
            binding?.flexboxLayoutUsers?.visibility = View.VISIBLE
            binding?.usersText?.visibility = View.GONE
            if (brickCount == 1)
                binding?.flexboxLayoutContainer?.layoutParams?.height = (50.toFloat() * density).roundToInt()
            if (brickCount == 2)
                binding?.flexboxLayoutContainer?.layoutParams?.height = (90.toFloat() * density).roundToInt()
            if (brickCount == 3 || brickCount > 3)
                binding?.flexboxLayoutContainer?.layoutParams?.height = (130.toFloat() * density).roundToInt()
        }
    }

    override fun flexBoxRemoved(position: Int) {}

    private fun initUsers(): ArrayList<User> {

        val list = ArrayList<User>()
        list.add(User("Giovanni", "Petito (initUsers())", "", "3331582355", arrayListOf("giovanni.petito88@gmail.com", "gi.petito@gmail.com"), "Via Monte Grappa 15, Cologno Monzese (MI)", "Android Developer", false))
        list.add(User("Raffaele", "Petito", "0818183301", "3802689011", arrayListOf("raffaele.petito@gmail.com"), "Via Santa Maria a Cubito 19, Giugliano in Campania (NA)", "Fotografo", false))
        list.add(User("Teresa", "Petito", "", "3343540536", arrayListOf("teresa_petito@yahoo.it"), "Via Raffaele Carelli 8, Giugliano in Campania (NA)", "Commercialista", false))
        list.add(User("Salvatore", "Pragliola", "", "3384672609", arrayListOf("salvatore.pragliola@gmail.com"), "Via Raffaele Carelli 8, Giugliano in Campania (NA)", "Marmista", false))
        list.add(User("Angelina", "Basile", "0818183301", "3334392578", arrayListOf("basile.angela59@gmail.com"), "Via Santa Maria a Cubito 19, Giugliano in Campania (NA)", "Casalinga", false))
        list.add(User("Vincenzo", "Petito", "0818183301", "3666872262", arrayListOf("vincenzo.petito@gmail.com"), "Via Santa Maria a Cubito 19, Giugliano in Campania (NA)", "Impiegato", false))
        list.add(User("Giovanni", "Basile", "0818188619", "3884723340", arrayListOf("giovanni.basile@gmail.com"), "Via Santa Maria a Cubito 21, Giugliano in Campania (NA)", "Studente", false))
        list.add(User("Marco", "Basile", "0818188619", "3892148853", arrayListOf("marco.basile@gmail.com"), "Via Santa Maria a Cubito 21, Giugliano in Campania (NA)", "Studente", false))
        list.add(User("Antonio", "D'Ascia", "", "3315605694", arrayListOf("antonio.dascia@gmail.com"), "Corsico (MI)", "Impiegato", false))
        list.add(User("Giovanni", "D'Ascia", "024404881", "3331711437", arrayListOf("giovanni.dascia@gmail.com"), "Via 4 Novembre 19, Corsico (MI)", "Impiegato", false))
        list.add(User("Mariano", "Pinto", "", "3397016728", arrayListOf("mar.pinto3@gmail.com"), "", "Impiegato", false))
        list.add(User("Pasquale", "Amato", "", "3665917760", arrayListOf("pasquale.amato@gmail.com"), "", "Impiegato", false))
        list.add(User("Francesco", "Mongiello", "", "3299376402", arrayListOf("francesco.mongiello@gmail.com"), "", "Sacerdote", false))
        list.add(User("Gianluigi", "Santillo", "", "3386124867", arrayListOf("gianluigi.santillo@gmail.com"), "Via Conte Rosso 11, Milano (MI)", "Project Manager", false))
        list.add(User("Daniele", "Musacchia", "", "3494977374", arrayListOf("danielemusacchia@hotmail.it"), "Monza (MB)", "Impiegato", false))

        return list
    }

    private fun getUsersFromJson(): ArrayList<User> {

        val jsonObject: String? = Utils.getJsonFromAssets(requireContext(), "user.json")
        val gson: Gson? = GsonBuilder().serializeNulls().create()
        val userResponse: UserResponse? = gson?.fromJson(jsonObject, UserResponse::class.java)

        return userResponse?.response?.users!!
    }

    override fun onRealtimeSuccess(message: String?, response: Response?) {
        hideProgressDialog()
        mResponse = Response()
        mResponse?.users = response?.users
        saveUsersToPreferences(mResponse)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onRealtimeFailure(message: String?) {
        hideProgressDialog()
        mResponse = Response()
        mResponse?.users = getUsersFromJson()
        saveUsersToPreferences(mResponse)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
        if (isDarkMode) {
            binding?.initButton?.style(R.style.ButtonEmptyDarkMode)
            binding?.jsonButton?.style(R.style.ButtonEmptyDarkMode)
            binding?.realtimeButton?.style(R.style.ButtonEmptyDarkMode)
        }
        else {
            binding?.initButton?.style(R.style.ButtonEmptyLightMode)
            binding?.jsonButton?.style(R.style.ButtonEmptyLightMode)
            binding?.realtimeButton?.style(R.style.ButtonEmptyLightMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}