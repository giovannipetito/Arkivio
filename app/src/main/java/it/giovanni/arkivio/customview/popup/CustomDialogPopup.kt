package it.giovanni.arkivio.customview.popup

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.utils.Utils
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.TextViewCustom
import java.util.*

open class CustomDialogPopup : AlertDialog.Builder {

    private val dialogInterfaceKey = 1258932039
    private lateinit var elementList: LinearLayout
    protected lateinit var labelList: LinearLayout
    protected lateinit var titleDialog: TextViewCustom
    protected lateinit var subtitleDialog: TextViewCustom
    protected lateinit var messageDialog: TextViewCustom
    private lateinit var containerLeftButton: RelativeLayout
    private lateinit var containerCenterButton: RelativeLayout
    protected lateinit var buttonsContainer: LinearLayout
    private lateinit var rightButton: TextViewCustom
    private lateinit var centerButton: TextViewCustom
    private lateinit var leftButton: TextViewCustom
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

    protected fun prepare() {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        dialogView = inflater.inflate(R.layout.custom_dialog_popup, null)

        elementList = dialogView.findViewById(R.id.element_list) as LinearLayout
        labelList = dialogView.findViewById(R.id.label_list) as LinearLayout

        titleDialog = dialogView.findViewById(R.id.title_dialog) as TextViewCustom
        subtitleDialog = dialogView.findViewById(R.id.subtitle_dialog) as TextViewCustom
        messageDialog = dialogView.findViewById(R.id.message_dialog) as TextViewCustom

        buttonsContainer = dialogView.findViewById(R.id.buttons_container) as LinearLayout
        containerLeftButton = dialogView.findViewById(R.id.container_left_button) as RelativeLayout
        containerCenterButton = dialogView.findViewById(R.id.container_center_button) as RelativeLayout

        leftButton = dialogView.findViewById(R.id.left_button) as TextViewCustom
        centerButton = dialogView.findViewById(R.id.center_button) as TextViewCustom
        rightButton = dialogView.findViewById(R.id.right_button) as TextViewCustom
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
                rightButton.setTextColor(ContextCompat.getColor(context, R.color.rosso))
            }
        }

        showButton()
        dialog.show()

        isVisible = true
        return dialog
    }

    fun setButtons(text: String?, listener: View.OnClickListener) {
        buttonNumber = 1

        if (text != null)
            rightText = text.toString().toUpperCase(Locale.ITALY)

        leftAction = null
        centerAction = null
        rightAction = listener
    }

    fun setButtons(textRight: String, listenerRight: View.OnClickListener,
                   textLeft: String, listenerLeft: View.OnClickListener) {
        buttonNumber = 2

        leftAction = null

        centerText = textLeft
        centerAction = listenerLeft

        rightText = textRight
        rightAction = listenerRight
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

    private fun showButton() {
        if (buttonNumber > 0) {
            when (buttonNumber) {

                1 -> {
                    buttonsContainer.weightSum = 1f

                    containerLeftButton.visibility = View.GONE

                    containerCenterButton.visibility = View.GONE

                    rightButton.visibility = View.VISIBLE
                    rightButton.text = rightText
                    rightButton.setTag(dialogInterfaceKey, dialog) // Used for dismiss on callback.
                    rightButton.setOnClickListener(rightAction)
                }

                2 -> {
                    buttonsContainer.weightSum = 2f

                    containerLeftButton.visibility = View.GONE

                    containerCenterButton.visibility = View.VISIBLE
                    centerButton.text = centerText
                    centerButton.setTag(dialogInterfaceKey, dialog) // Used for dismiss on callback.
                    centerButton.setOnClickListener(centerAction)

                    rightButton.visibility = View.VISIBLE
                    rightButton.text = rightText
                    rightButton.setTag(dialogInterfaceKey, dialog) // Used for dismiss on callback.
                    rightButton.setOnClickListener(rightAction)
                }

                3 -> {
                    buttonsContainer.weightSum = 3f

                    containerLeftButton.visibility = View.VISIBLE
                    leftButton.text = leftText
                    leftButton.setTag(dialogInterfaceKey, dialog) // Used for dismiss on callback.
                    leftButton.setOnClickListener(leftAction)

                    containerCenterButton.visibility = View.VISIBLE
                    centerButton.text = centerText
                    centerButton.setTag(dialogInterfaceKey, dialog) // Used for dismiss on callback.
                    centerButton.setOnClickListener(centerAction)

                    rightButton.visibility = View.VISIBLE
                    rightButton.text = rightText
                    rightButton.setTag(dialogInterfaceKey, dialog) // Used for dismiss on callback.
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