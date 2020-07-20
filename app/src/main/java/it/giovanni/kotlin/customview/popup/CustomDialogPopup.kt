package it.giovanni.kotlin.customview.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import it.giovanni.kotlin.utils.Utils
import it.giovanni.kotlin.R
import java.util.*
import kotlin.collections.ArrayList

open class CustomDialogPopup : AlertDialog.Builder {

    private val DIALOG_INTERFACE_KEY = 1258932039
    private lateinit var elementList: LinearLayout
    protected lateinit var labelList: LinearLayout
    protected lateinit var titleDialog: TextView
    protected lateinit var subtitleDialog: TextView
    protected lateinit var messageDialog: TextView
    private lateinit var containerLeftButton: RelativeLayout
    private lateinit var containerCenterButton: RelativeLayout
    protected lateinit var buttonsContainer: LinearLayout
    private lateinit var rightButton: AppCompatButton
    private lateinit var centerButton: AppCompatButton
    private lateinit var leftButton: AppCompatButton
    private var type: Int = TYPE_INFO
    private var title: String? = null
    private var subtitle: String? = null
    private var message: String? = null
    private var isBottom: Boolean = false

    private lateinit var dialogView: View
    protected var activity: Context

    private var leftAction: View.OnClickListener? = null
    private var centerAction: View.OnClickListener? = null
    private var rightAction: View.OnClickListener? = null
    private lateinit var leftText: String
    private lateinit var centerText: String
    private lateinit var rightText: String

    private var buttonNumber = 0
    private lateinit var dialog: AlertDialog

    private var isVisible = false

    companion object {
        var TYPE_INFO: Int = 0
        var TYPE_ERROR: Int = 1
    }

    constructor(activity: Context) : super(activity) {
        this.activity = activity
        prepare()
    }

    constructor(activity: Activity, @StyleRes themeResId: Int) : super(activity, themeResId) {
        this.activity = activity
        prepare()
    }

    @SuppressLint("InflateParams")
    protected fun prepare() {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        dialogView = inflater.inflate(R.layout.custom_dialog_popup, null)

        elementList = dialogView.findViewById(R.id.element_list) as LinearLayout
        labelList = dialogView.findViewById(R.id.label_list) as LinearLayout

        titleDialog = dialogView.findViewById(R.id.title_dialog) as TextView
        subtitleDialog = dialogView.findViewById(R.id.subtitle_dialog) as TextView
        messageDialog = dialogView.findViewById(R.id.message_dialog) as TextView

        buttonsContainer = dialogView.findViewById(R.id.buttons_container) as LinearLayout
        containerLeftButton = dialogView.findViewById(R.id.container_left_button) as RelativeLayout
        containerCenterButton = dialogView.findViewById(R.id.container_center_button) as RelativeLayout

        leftButton = dialogView.findViewById(R.id.left_button) as AppCompatButton
        centerButton = dialogView.findViewById(R.id.center_button) as AppCompatButton
        rightButton = dialogView.findViewById(R.id.right_button) as AppCompatButton
    }

    fun setType(type: Int) {
        this.type = type
    }

    fun setTitle(mTitle: String, mSubtitle: String) {
        if (mTitle == "")
            titleDialog.visibility = View.GONE
        if (mSubtitle == "")
            subtitleDialog.visibility = View.GONE
        this.title = mTitle
        this.subtitle = mSubtitle
    }

    fun setMessage(mMessage: String) {
        this.message = mMessage
    }

    fun setGravityBottom(isBottom: Boolean) {
        this.isBottom = isBottom
    }

    override fun show(): AlertDialog {

        titleDialog.text = Utils.fromHtml(title)
        titleDialog.movementMethod = LinkMovementMethod.getInstance()
        titleDialog.isClickable = true

        subtitleDialog.text = Utils.fromHtml(subtitle)
        subtitleDialog.movementMethod = LinkMovementMethod.getInstance()
        subtitleDialog.isClickable = true

        messageDialog.text = Utils.fromHtml(message)
        messageDialog.movementMethod = LinkMovementMethod.getInstance()
        messageDialog.isClickable = true

        dialog = create()
        dialog.setView(dialogView)
        dialog.setOnDismissListener { isVisible = false }

        if (isBottom) {
            val window = dialog.window
            window?.setGravity(Gravity.BOTTOM)
            // window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND) // TODO: NON FUNZIONA
        }

        when (type) {
            TYPE_INFO -> {
                leftButton.setTextColor(ContextCompat.getColor(context, R.color.grey_3))
                centerButton.setTextColor(ContextCompat.getColor(context, R.color.grey_3))
                rightButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            TYPE_ERROR -> {
                leftButton.setTextColor(ContextCompat.getColor(context, R.color.grey_3))
                centerButton.setTextColor(ContextCompat.getColor(context, R.color.grey_3))
                rightButton.setTextColor(ContextCompat.getColor(context, R.color.rosso_1))
            }
        }

        showButton()
        dialog.show()

        isVisible = true
        return dialog
    }

    // CUSTOM BUTTON
    fun setButton(text: String?, listener: View.OnClickListener) {
        buttonNumber = 1

        if (text != null)
            rightText = text.toString().toUpperCase(Locale.ITALY)

        leftAction = null
        centerAction = null
        rightAction = listener
        setButtonColors()
    }

    fun setButtons(textRight: String, listenerRight: View.OnClickListener,
                   textLeft: String, listenerLeft: View.OnClickListener) {
        buttonNumber = 2

        leftAction = null

        centerText = textLeft
        centerAction = listenerLeft

        rightText = textRight
        rightAction = listenerRight

        setButtonColors()
    }

    fun setButtons(textLeft: String, listenerLeft: View.OnClickListener,
                   textCenter: String, listenerCenter: View.OnClickListener,
                   textRight: String, listenerRight: View.OnClickListener) {
        buttonNumber = 3

        leftText = textLeft
        leftAction = listenerLeft

        centerText = textCenter
        centerAction = listenerCenter

        rightText = textRight
        rightAction = listenerRight

        setButtonColors()
    }

    override fun setPositiveButton(text: CharSequence, listener: DialogInterface.OnClickListener): AlertDialog.Builder {
        throw RuntimeException("it's possible to define positive button!")
    }

    override fun setNegativeButton(text: CharSequence, listener: DialogInterface.OnClickListener): AlertDialog.Builder {
        throw RuntimeException("it's possible to define negative button!")
    }

    override fun setNeutralButton(text: CharSequence, listener: DialogInterface.OnClickListener): AlertDialog.Builder {
        throw RuntimeException("it's possible to define neutral button!")
    }

    override fun setTitle(mTitle: CharSequence?): AlertDialog.Builder {
        if (mTitle.toString() == "") {
            titleDialog.visibility = View.GONE
            subtitleDialog.visibility = View.GONE
        }
        this.title = mTitle.toString()
        return super.setTitle(null)
    }

    override fun setMessage(mMessage: CharSequence?): AlertDialog.Builder {
        this.message = mMessage.toString()
        return super.setMessage(null)
    }

    private fun setButtonColors() {
        val colors: ArrayList<Int> = ArrayList()
        colors.add(R.color.black)
        colors.add(R.color.grey_2)
        colors.add(R.color.rosso_1)
        if (buttonNumber > 0) {
            when (buttonNumber) {
                1 -> {
                    if (colors.size < 1)
                        return
                    leftButton.setTextColor(colors[0])
                }
                2 -> {
                    if (colors.size < 2)
                        return
                    leftButton.setTextColor(colors[1])
                    centerButton.setTextColor(colors[2])
                }
                3 -> {
                    if (colors.size < 3)
                        return
                    leftButton.setTextColor(colors[1])
                    centerButton.setTextColor(colors[1])
                    rightButton.setTextColor(colors[2])
                }
            }
        }
    }

    private fun showButton() {
        if (buttonNumber > 0) {
            when (buttonNumber) {

                1 -> {
                    buttonsContainer.weightSum = 1f

                    containerLeftButton.visibility = View.GONE

                    containerCenterButton.visibility = View.GONE

                    rightButton.visibility = View.VISIBLE
                    rightButton.text = rightText
                    rightButton.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    rightButton.setOnClickListener(rightAction)
                }

                2 -> {
                    buttonsContainer.weightSum = 2f

                    containerLeftButton.visibility = View.GONE

                    containerCenterButton.visibility = View.VISIBLE
                    centerButton.text = centerText
                    centerButton.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    centerButton.setOnClickListener(centerAction)

                    rightButton.visibility = View.VISIBLE
                    rightButton.text = rightText
                    rightButton.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    rightButton.setOnClickListener(rightAction)
                }

                3 -> {
                    buttonsContainer.weightSum = 3f

                    containerLeftButton.visibility = View.VISIBLE
                    leftButton.text = leftText
                    leftButton.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    leftButton.setOnClickListener(leftAction)

                    containerCenterButton.visibility = View.VISIBLE
                    centerButton.text = centerText
                    centerButton.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    centerButton.setOnClickListener(centerAction)

                    rightButton.visibility = View.VISIBLE
                    rightButton.text = rightText
                    rightButton.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    rightButton.setOnClickListener(rightAction)
                }
            }
        } else
            throw RuntimeException("No action setted!")
    }

    fun dismiss() {
        dialog.dismiss()
    }
}