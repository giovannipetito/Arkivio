package it.giovanni.arkivio.fragments.detail.rubrica

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.user.User
import it.giovanni.arkivio.bean.user.UserResponse
import it.giovanni.arkivio.customview.Brick
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.adapter.UsersAdapter
import it.giovanni.arkivio.viewinterfaces.IFlexBoxCallback
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.Utils
import kotlinx.android.synthetic.main.rubrica_list_layout.*
import kotlinx.android.synthetic.main.detail_layout.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt

@Suppress("PrivatePropertyName")
class RubricaListFragment: DetailFragment(), UsersAdapter.OnItemViewClicked, IFlexBoxCallback {

    private var TRANSITION_TIME: Long = 275
    private var FLEXBOX_CONTAINER_HEIGHT: Int? = 80

    private var viewFragment: View? = null
    private lateinit var adapter: UsersAdapter
    private var list: ArrayList<User>? = null
    private var filtered: ArrayList<User>? = null
    private var updatedList: ArrayList<User>? = null
    private lateinit var customDialog: CustomDialogPopup
    private var reallyGoOut: Boolean = false
    private var labelIsClicked: Boolean = false
    private var sentence: String? = null
    private var isOpen = false

    companion object {
        var KEY_USERS: String = "KEY_USERS"
        var KEY_SPEECH_USERS: String = "KEY_SPEECH_USERS"
    }

    override fun getLayout(): Int {
        return R.layout.rubrica_list_layout
    }

    override fun getTitle(): Int {
        return R.string.rubrica_list_title
    }

    override fun getActionTitle(): Int {
        return R.string.rubrica_list_action_label
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

    override fun beforeClosing(): Boolean {
        if (!labelIsClicked) {
            if (!reallyGoOut) {
                customDialog = CustomDialogPopup(currentActivity, R.style.PopupTheme)
                customDialog.setCancelable(false)
                customDialog.setTitle("Rubrica", "Prima di uscire...")
                customDialog.setMessage("Confermi di voler annullare l'inserimento dei contatti?")

                customDialog.setButtons(resources.getString(R.string.button_confirm), {
                    reallyGoOut = true
                    customDialog.dismiss()
                    currentActivity.onBackPressed()
                },
                    resources.getString(R.string.button_cancel), {
                        customDialog.dismiss()
                    }
                )
                customDialog.show()
                return false
            } else {
                return true
            }
        }
        return true
    }

    override fun getResultBack(): Intent {
        val backIntent = Intent()
        if (labelIsClicked) {
            if (flexbox_users.childCount <= 0) {
                return backIntent
            } else {
                hideSoftKeyboard()
                list = ArrayList()
                val count = flexbox_users.childCount
                if (count > 0) {
                    for (index in 1..count) {
                        val brick: Brick = (flexbox_users.getChildAt(index - 1) as Brick)
                        if (brick.isVisible()) {
                            val user = User(brick.getName(), brick.getEmail(), "", "", ArrayList(), "", "", true)
                            list!!.add(user)
                        }
                    }
                }
                backIntent.putExtra(Globals.BACK_PARAM_KEY_USER_SEARCH, list)
                return backIntent
            }
        } else {
            return backIntent
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)

        list = init()
        adapter = UsersAdapter(this)

        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TODO("Not yet implemented")
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_users.setHasFixedSize(true)
        recyclerview_users.layoutManager = LinearLayoutManager(activity)
        recyclerview_users.adapter = adapter
        adapter.setList(list)
        adapter.notifyDataSetChanged()

        arrow_go_back.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_back_rvd))

        flexbox_users?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            scrollview_flexbox?.scrollTo(0, flexbox_users.height)
        }

        if (arguments != null) {
            var users: ArrayList<User> = ArrayList()
            if (arguments!!.getSerializable(KEY_USERS) != null)
                users = arguments!!.getSerializable(KEY_USERS) as ArrayList<User>
            if (users.size > 0) {
                flexbox_users.removeAllViews()
                for ((i, user) in users.withIndex()) {
                    val brick = Brick(context!!)
                    brick.mode(Brick.ModeType.EDIT)
                    brick.setName(user.nome!!)
                    if (user.emails!!.isEmpty())
                        brick.setEmail("")
                    else
                        brick.setEmail(user.emails!![0])
                    brick.callback(this)
                    brick.position(i)
                    flexbox_users.addView(brick)
                }
                actionLabelState(true)
            }

            sentence = arguments!!.getString(KEY_SPEECH_USERS)
            if (sentence != null) {
                edit_search.setText(sentence)
                edit_search.requestFocus()
                showSoftKeyboard(edit_search)
                filter()
            }
        }

        switch_compat.setOnClickListener {

            close_action.visibility = View.GONE
            edit_search.setText("")
            startAnimation()

            if (switch_compat.isChecked) {
                edit_search.requestFocus()
                showSoftKeyboard(edit_search)
                recyclerview_users.visibility = View.VISIBLE
            } else {
                edit_search.clearFocus()
                hideSoftKeyboard()
                recyclerview_users.visibility = View.GONE
            }
        }

        edit_search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (edit_search.text?.isNotEmpty()!!) {
                    close_action.visibility = View.VISIBLE
                    filter()

                    if (edit_search.text.endsWith(",") || edit_search.text.endsWith(" ")) {
                        var email: String = edit_search.text.toString().toLowerCase(Locale.ITALIAN)
                        email = email.substring(0, email.length - 1)
                        addBrick(email, email)
                        edit_search.setText("")
                        /*
                        email = email.substring(0, email.length - 1)
                        if (Utils.checkEmail(email)) {
                            if (list?.size != 0) {
                                if (list != null && list?.isNotEmpty()!!) {

                                    if (email == list!![0].email) {
                                        addBrick(list!![0].nome, email)
                                        edit_search.setText("")
                                    }
                                }
                            } else {
                                addBrick(email, email)
                                edit_search.setText("")
                            }
                        }
                        */
                    }
                } else if (edit_search.text?.isEmpty()!!) {
                    close_action.visibility = View.GONE
                    resetList()
                }

                val imm = currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive)
                    Toast.makeText(activity, "SoftKeyboard is active", Toast.LENGTH_SHORT).show()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        edit_search.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var email: String = edit_search.text.toString().toLowerCase(Locale.ITALIAN)
                email = email.substring(0, email.length)
                if (Utils.checkEmail(email)) {
                    if (list?.size != 0) {
                        if (email == list!![0].emails!![0]) {
                            addBrick(list!![0].nome!!, email)
                            edit_search.setText("")
                        }
                    } else {
                        addBrick(email, email)
                        edit_search.setText("")
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        close_action.setOnClickListener {
            edit_search.setText("")
            resetList()
        }
    }

    private fun addBrick(name: String, email: String) {
        if (!isAdded(email)) {
            val brick = Brick(context!!)
            brick.mode(Brick.ModeType.EDIT)
            brick.setName(name)
            brick.setEmail(email)
            brick.callback(this)
            brick.position(flexbox_users.childCount)
            flexbox_users.addView(brick)
        }
    }

    private fun isAdded(email: String): Boolean {
        val count = flexbox_users.childCount
        if (count > 0) {
            for (index in 1..count) {
                val brick = (flexbox_users.getChildAt(index - 1) as Brick)
                if (brick.isVisible() && brick.getEmail() == email)
                    return true
            }
        }
        return false
    }

    override fun onItemInfoClicked(user: User, list: ArrayList<User>?) {

        updatedList = ArrayList()
        user.isVisible = false
        addBrick(user.nome!!, user.emails!![0])
        val density = resources.displayMetrics.density
        if (flexbox_container.measuredHeight >= (FLEXBOX_CONTAINER_HEIGHT?.toFloat()!! * density).roundToInt())
            flexbox_container.layoutParams.height = (FLEXBOX_CONTAINER_HEIGHT?.toFloat()!! * density).roundToInt()

        if (list != null) {
            for (notChosen in list) {
                if (notChosen.emails!![0] != user.emails!![0])
                    updatedList!!.add(notChosen)
            }
        }

        adapter.setList(updatedList)
        adapter.notifyDataSetChanged()

        actionLabelState(true)
    }

    override fun onItemClicked(user: User, color: Int?) {
        hideSoftKeyboard()
        val bundleUser = Bundle()
        bundleUser.putSerializable(RubricaDetailFragment.KEY_RUBRICA, user)
        bundleUser.putInt(RubricaDetailFragment.KEY_COLOR, color!!)
        currentActivity.openDetail(Globals.RUBRICA_DETAIL, bundleUser)
    }

    override fun onActionClickListener() {
        labelIsClicked = true
        currentActivity.onBackPressed()
    }

    override fun flexBoxRemoved(position: Int) {
        flexbox_users.getChildAt(position).visibility = View.GONE
    }

    private fun filter() {
        /*
        if (sentence != null && sentence.equals(edit_search.text.toString(), ignoreCase = true))
            return
        */
        sentence = edit_search.text.toString()
        if (sentence?.isEmpty()!!) {
            sentence = null
            clearFilteredList()
            no_result.visibility = View.GONE
            recyclerview_users.visibility = View.VISIBLE
            adapter.setList(list)
            adapter.notifyDataSetChanged()
            return
        }
        clearFilteredList()
        if (list == null || list?.size == 0) {
            recyclerview_users.visibility = View.GONE
            no_result.visibility = View.VISIBLE
            no_result.setText(R.string.no_list)
            return
        }
        for (user in list!!) {
            if (user.nome!!.toLowerCase(Locale.ITALIAN).contains(sentence?.toLowerCase(Locale.ITALIAN)!!) ||
                user.cognome!!.toLowerCase(Locale.ITALIAN).contains(sentence?.toLowerCase(Locale.ITALIAN)!!) ||
                (user.nome!!.toLowerCase(Locale.ITALIAN) + " " + user.cognome!!.toLowerCase(Locale.ITALIAN))
                    .contains(sentence?.toLowerCase(Locale.ITALIAN)!!) ||
                user.cellulare!!.toLowerCase(Locale.ITALIAN).contains(sentence?.toLowerCase(Locale.ITALIAN)!!))

                filtered?.add(user)
        }
        if (filtered == null || filtered?.size == 0) {
            recyclerview_users.visibility = View.GONE
            no_result.visibility = View.VISIBLE
        } else {
            no_result.visibility = View.GONE
            recyclerview_users.visibility = View.VISIBLE
            adapter.setList(filtered)
            adapter.notifyDataSetChanged()
        }
    }

    private fun clearFilteredList() {
        if (filtered == null)
            filtered = ArrayList()
        else
            filtered?.clear()
    }

    private fun resetList() {
        list = init()
        adapter.setList(list)
        adapter.notifyDataSetChanged()
        no_result.visibility = View.GONE
        recyclerview_users.visibility = View.VISIBLE
    }

    private fun startAnimation() {

        if (!isOpen) {
            val centerX = switch_compat.x.toInt() + switch_compat.width/2
            val centerY = switch_compat.y.toInt() + switch_compat.height/2
            val startRadius = 0
            val endRadius = hypot(container.width.toDouble(), container.height.toDouble()).toInt()

            val anim = ViewAnimationUtils.createCircularReveal(content, centerX, centerY, startRadius.toFloat(), endRadius.toFloat())
            anim.duration = TRANSITION_TIME
            anim.start()

            content.visibility = View.VISIBLE
            isOpen = true
        } else {
            val centerX = switch_compat.x.toInt() + switch_compat.width/2
            val centerY = switch_compat.y.toInt() + switch_compat.height/2
            val startRadius = max(container.width, container.height)
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(content, centerX, centerY, startRadius.toFloat(), endRadius.toFloat())
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    content.visibility = View.GONE
                }

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })
            anim.duration = TRANSITION_TIME
            anim.start()

            isOpen = false
        }
    }

    private fun init(): ArrayList<User> {

        val jsonObject: String? = Utils.getJsonFromAssets(context!!, "user.json")
        val gson: Gson? = GsonBuilder().serializeNulls().create()
        val response: UserResponse? = gson?.fromJson(jsonObject, UserResponse::class.java)

        return response?.users!!
    }
}