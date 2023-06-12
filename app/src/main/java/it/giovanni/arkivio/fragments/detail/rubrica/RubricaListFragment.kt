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
import it.giovanni.arkivio.R
import it.giovanni.arkivio.model.user.User
import it.giovanni.arkivio.customview.Brick
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.databinding.RubricaListLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.adapter.UsersAdapter
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.viewinterfaces.IFlexBoxCallback
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadUsersFromPreferences
import it.giovanni.arkivio.utils.Utils
import kotlin.collections.ArrayList
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt

class RubricaListFragment: DetailFragment(), UsersAdapter.OnItemViewClicked, IFlexBoxCallback {

    companion object {
        private var TRANSITION_TIME: Long = 275
        private var FLEXBOX_CONTAINER_HEIGHT: Int? = 80
        var KEY_BRICKS: String = "KEY_BRICKS"
        var KEY_SPEECH_USERS: String = "KEY_SPEECH_USERS"
    }

    private var layoutBinding: RubricaListLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var adapter: UsersAdapter
    private var list: ArrayList<User>? = null
    private var filtered: ArrayList<User>? = null
    private var updatedList: ArrayList<User>? = null
    private lateinit var customDialogPopup: CustomDialogPopup
    private var reallyGoOut: Boolean = false
    private var labelIsClicked: Boolean = false
    private var sentence: String? = null
    private var isOpen = false

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
                customDialogPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
                customDialogPopup.setCancelable(false)
                customDialogPopup.setTitle("Rubrica", "Prima di uscire...")
                customDialogPopup.setMessage("Confermi di voler annullare l'inserimento dei contatti?")

                customDialogPopup.setButtons(resources.getString(R.string.button_confirm), {
                    reallyGoOut = true
                    customDialogPopup.dismiss()
                    currentActivity.onBackPressed()
                },
                    resources.getString(R.string.button_cancel), {
                        customDialogPopup.dismiss()
                    }
                )
                customDialogPopup.show()
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
            if (binding?.flexboxUsers?.childCount!! <= 0) {
                return backIntent
            } else {
                hideSoftKeyboard()
                list = ArrayList()
                val count = binding?.flexboxUsers?.childCount!!
                if (count > 0) {
                    for (index in 1..count) {
                        val brick: Brick = (binding?.flexboxUsers?.getChildAt(index - 1) as Brick)
                        if (brick.isVisible()) {
                            val user = User(brick.getName(), brick.getEmail(), "", "", ArrayList(), "", "", true)
                            list?.add(user)
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = RubricaListLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val response = loadUsersFromPreferences()
        list = response?.users
        adapter = UsersAdapter(this)

        binding?.recyclerviewUsers?.setHasFixedSize(true)
        binding?.recyclerviewUsers?.layoutManager = LinearLayoutManager(activity)
        binding?.recyclerviewUsers?.adapter = adapter
        adapter.setList(list)
        adapter.notifyDataSetChanged()

        detailLayoutBinding?.arrowGoBack?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ico_back_rvd))

        binding?.flexboxUsers?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            binding?.scrollviewFlexbox?.scrollTo(0, binding?.flexboxUsers?.height!!)
        }

        if (arguments != null) {
            var brickUsers: ArrayList<User> = ArrayList()
            if (requireArguments().getSerializable(KEY_BRICKS) != null)
                brickUsers = requireArguments().getSerializable(KEY_BRICKS) as ArrayList<User>
            if (brickUsers.size > 0) {
                binding?.flexboxUsers?.removeAllViews()
                for ((i, user) in brickUsers.withIndex()) {
                    val brick = Brick(requireContext())
                    brick.mode(Brick.ModeType.EDIT)
                    brick.setName(user.nome!!)
                    if (user.emails?.isEmpty()!!)
                        brick.setEmail("")
                    else
                        brick.setEmail(user.emails!![0])
                    brick.callback(this)
                    brick.position(i)
                    binding?.flexboxUsers?.addView(brick)
                }
                actionLabelState(true)
            }

            sentence = requireArguments().getString(KEY_SPEECH_USERS)
            if (sentence != null) {
                binding?.editSearch?.setText(sentence)
                binding?.editSearch?.requestFocus()
                showSoftKeyboard(binding?.editSearch!!)
                filter()
            }
        }

        binding?.switchCompat?.setOnClickListener {

            binding?.closeAction?.visibility = View.GONE
            binding?.editSearch?.setText("")
            startAnimation()

            if (binding?.switchCompat?.isChecked!!) {
                binding?.editSearch?.requestFocus()
                showSoftKeyboard(binding?.editSearch!!)
                binding?.recyclerviewUsers?.visibility = View.VISIBLE
            } else {
                binding?.editSearch?.clearFocus()
                hideSoftKeyboard()
                binding?.recyclerviewUsers?.visibility = View.GONE
            }
        }

        binding?.editSearch?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (binding?.editSearch?.text?.isNotEmpty()!!) {
                    binding?.closeAction?.visibility = View.VISIBLE
                    filter()

                    if (binding?.editSearch?.text?.endsWith(",")!! || binding?.editSearch?.text?.endsWith(" ")!!) {
                        var email: String = binding?.editSearch?.text.toString().lowercase()
                        email = email.substring(0, email.length - 1)
                        addBrick(email, email)
                        binding?.editSearch?.setText("")
                        /*
                        email = email.substring(0, email.length - 1)
                        if (Utils.checkEmail(email)) {
                            if (list?.size != 0) {
                                if (list != null && list?.isNotEmpty()!!) {

                                    if (email == list!![0].email) {
                                        addBrick(list!![0].nome, email)
                                        binding?.editSearch?.setText("")
                                    }
                                }
                            } else {
                                addBrick(email, email)
                                binding?.editSearch?.setText("")
                            }
                        }
                        */
                    }
                } else if (binding?.editSearch?.text?.isEmpty()!!) {
                    binding?.closeAction?.visibility = View.GONE
                    resetList()
                }

                val imm = currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive)
                    Toast.makeText(activity, "SoftKeyboard is active", Toast.LENGTH_SHORT).show()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding?.editSearch?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var email: String = binding?.editSearch?.text.toString().lowercase()
                email = email.substring(0, email.length)
                if (Utils.checkEmail(email)) {
                    if (list?.size != 0) {
                        if (email == list!![0].emails!![0]) {
                            addBrick(list!![0].nome!!, email)
                            binding?.editSearch?.setText("")
                        }
                    } else {
                        addBrick(email, email)
                        binding?.editSearch?.setText("")
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        binding?.closeAction?.setOnClickListener {
            binding?.editSearch?.setText("")
            resetList()
        }
    }

    private fun addBrick(name: String, email: String) {
        if (!isAdded(email)) {
            val brick = Brick(requireContext())
            brick.mode(Brick.ModeType.EDIT)
            brick.setName(name)
            brick.setEmail(email)
            brick.callback(this)
            brick.position(binding?.flexboxUsers?.childCount!!)
            binding?.flexboxUsers?.addView(brick)
        }
    }

    private fun isAdded(email: String): Boolean {
        val count = binding?.flexboxUsers?.childCount!!
        if (count > 0) {
            for (index in 1..count) {
                val brick = (binding?.flexboxUsers?.getChildAt(index - 1) as Brick)
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
        if (binding?.flexboxContainer?.measuredHeight!! >= (FLEXBOX_CONTAINER_HEIGHT?.toFloat()!! * density).roundToInt())
            binding?.flexboxContainer?.layoutParams?.height = (FLEXBOX_CONTAINER_HEIGHT?.toFloat()!! * density).roundToInt()

        if (list != null) {
            for (notChosen in list) {
                if (notChosen.emails!![0] != user.emails!![0])
                    updatedList?.add(notChosen)
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
        binding?.flexboxUsers?.getChildAt(position)?.visibility = View.GONE
    }

    private fun filter() {
        /*
        if (sentence != null && sentence.equals(binding?.editSearch?.text.toString(), ignoreCase = true))
            return
        */
        sentence = binding?.editSearch?.text.toString()
        if (sentence?.isEmpty()!!) {
            sentence = null
            clearFilteredList()
            binding?.noResult?.visibility = View.GONE
            binding?.recyclerviewUsers?.visibility = View.VISIBLE
            adapter.setList(list)
            adapter.notifyDataSetChanged()
            return
        }
        clearFilteredList()
        if (list == null || list?.size == 0) {
            binding?.recyclerviewUsers?.visibility = View.GONE
            binding?.noResult?.visibility = View.VISIBLE
            binding?.noResult?.setText(R.string.no_list)
            return
        }
        for (user in list!!) {
            if (user.nome?.lowercase()?.contains(sentence?.lowercase()!!)!! ||
                user.cognome?.lowercase()?.contains(sentence?.lowercase()!!)!! ||
                (user.nome?.lowercase() + " " + user.cognome?.lowercase())
                    .contains(sentence?.lowercase()!!) ||
                user.cellulare?.lowercase()?.contains(sentence?.lowercase()!!)!!)

                filtered?.add(user)
        }
        if (filtered == null || filtered?.size == 0) {
            binding?.recyclerviewUsers?.visibility = View.GONE
            binding?.noResult?.visibility = View.VISIBLE
        } else {
            binding?.noResult?.visibility = View.GONE
            binding?.recyclerviewUsers?.visibility = View.VISIBLE
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
        list = loadUsersFromPreferences()?.users
        adapter.setList(list)
        adapter.notifyDataSetChanged()
        binding?.noResult?.visibility = View.GONE
        binding?.recyclerviewUsers?.visibility = View.VISIBLE
    }

    private fun startAnimation() {

        if (!isOpen) {
            val centerX = binding?.switchCompat?.x?.toInt()!! + binding?.switchCompat?.width!!/2
            val centerY = binding?.switchCompat?.y?.toInt()!! + binding?.switchCompat?.height!!/2
            val startRadius = 0
            val endRadius = hypot(binding?.container?.width?.toDouble()!!, binding?.container?.height?.toDouble()!!).toInt()

            val anim = ViewAnimationUtils.createCircularReveal(binding?.content, centerX, centerY, startRadius.toFloat(), endRadius.toFloat())
            anim.duration = TRANSITION_TIME
            anim.start()

            binding?.content?.visibility = View.VISIBLE
            isOpen = true
        } else {
            val centerX = binding?.switchCompat?.x?.toInt()!! + binding?.switchCompat?.width!!/2
            val centerY = binding?.switchCompat?.y?.toInt()!! + binding?.switchCompat?.height!!/2
            val startRadius = max(binding?.container?.width!!, binding?.container?.height!!)
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(binding?.content, centerX, centerY, startRadius.toFloat(), endRadius.toFloat())

            anim.addListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    binding?.content?.visibility = View.GONE
                }

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })

            anim.duration = TRANSITION_TIME
            anim.start()

            isOpen = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}