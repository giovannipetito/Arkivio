package it.giovanni.kotlin.customview.popup

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.text.method.LinkMovementMethod
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

open class CustomDialogPopup : AlertDialog.Builder {

    private val DIALOG_INTERFACE_KEY = 1258932039
    private var popup: LinearLayout? = null
    protected var elementList: LinearLayout? = null
    protected var labelList: LinearLayout? = null
    protected var bodyContent: TextView? = null
    private var containerLeftButton: RelativeLayout? = null
    private var containerCenterButton: RelativeLayout? = null
    protected var buttonsContainer: LinearLayout? = null
    private var rightButton: AppCompatButton? = null
    private var centerButton: AppCompatButton? = null
    private var leftButton: AppCompatButton? = null
    private var type: Int = TYPE_INFO
    private var title: String? = null
    private var subtitle: String? = null
    private var message: String? = null

    private var dialogView: View? = null
    protected var activity: Context? = null

    private var leftAction: View.OnClickListener? = null
    private var leftText: String? = null
    private var centerAction: View.OnClickListener? = null
    private var centerText: String? = null
    private var rightAction: View.OnClickListener? = null
    private var rightText: String? = null

    private var buttonNumber = 0
    private var dialog: AlertDialog? = null

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
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        dialogView = inflater.inflate(R.layout.custom_dialog_popup, null)

        elementList = dialogView!!.findViewById(R.id.element_list) as LinearLayout
        labelList = dialogView!!.findViewById(R.id.label_list) as LinearLayout
        popup = dialogView!!.findViewById(R.id.popup) as LinearLayout

        bodyContent = dialogView!!.findViewById(R.id.body_content) as TextView

        buttonsContainer = dialogView!!.findViewById(R.id.buttons_container) as LinearLayout
        containerLeftButton = dialogView!!.findViewById(R.id.container_left_button) as RelativeLayout
        containerCenterButton = dialogView!!.findViewById(R.id.container_center_button) as RelativeLayout

        leftButton = dialogView!!.findViewById(R.id.left_button) as AppCompatButton
        centerButton = dialogView!!.findViewById(R.id.center_button) as AppCompatButton
        rightButton = dialogView!!.findViewById(R.id.right_button) as AppCompatButton
    }

    fun setType(type: Int) {
        this.type = type
    }

    fun setTitle(title: String, subtitle: String) {
        this.title = title
        this.subtitle = subtitle
    }

    fun setMessage(message: String) {
        this.message = message
    }

    override fun show(): AlertDialog {

        bodyContent!!.text = Utils.fromHtml(message)
        bodyContent!!.movementMethod = LinkMovementMethod.getInstance()
        bodyContent!!.isClickable = true

        dialog = create()
        dialog!!.setView(dialogView)
        dialog!!.setOnDismissListener { isVisible = false }

        when (type) {
            TYPE_INFO -> {
                leftButton!!.setTextColor(ContextCompat.getColor(context, R.color.grey))
                centerButton!!.setTextColor(ContextCompat.getColor(context, R.color.grey))
                rightButton!!.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            TYPE_ERROR -> {
                leftButton!!.setTextColor(ContextCompat.getColor(context, R.color.grey))
                centerButton!!.setTextColor(ContextCompat.getColor(context, R.color.grey))
                rightButton!!.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
        }

        showButton()
        dialog!!.show()

        isVisible = true
        return dialog as AlertDialog
    }

    // CUSTOM BUTTON
    fun setButton(text: String?, listener: View.OnClickListener) {
        buttonNumber = 1

        if (text != null)
            rightText = text.toString().toUpperCase()

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

    override fun setTitle(charTitle: CharSequence?): AlertDialog.Builder {
        title = charTitle!!.toString()
        return super.setTitle(null)
    }

    override fun setMessage(message: CharSequence?): AlertDialog.Builder {
        this.message = message!!.toString()
        return super.setMessage(null)
    }

    private fun setButtonColors() {
        val colors: ArrayList<Int> = ArrayList()
        colors.add(R.color.black)
        colors.add(R.color.dark_grey)
        colors.add(R.color.red)
        if (buttonNumber > 0) {
            when (buttonNumber) {
                1 -> {
                    if (colors.size < 1)
                        return
                    leftButton!!.setTextColor(colors[0])
                }
                2 -> {
                    if (colors.size < 2)
                        return
                    leftButton!!.setTextColor(colors[1])
                    centerButton!!.setTextColor(colors[2])
                }
                3 -> {
                    if (colors.size < 3)
                        return
                    leftButton!!.setTextColor(colors[1])
                    centerButton!!.setTextColor(colors[1])
                    rightButton!!.setTextColor(colors[2])
                }
            }
        }
    }

    private fun showButton() {
        if (buttonNumber > 0) {
            when (buttonNumber) {

                1 -> {
                    buttonsContainer!!.weightSum = 1f

                    containerLeftButton!!.visibility = View.GONE

                    containerCenterButton!!.visibility = View.GONE

                    rightButton!!.visibility = View.VISIBLE
                    rightButton!!.text = rightText
                    rightButton!!.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    rightButton!!.setOnClickListener(rightAction)
                }

                2 -> {
                    buttonsContainer!!.weightSum = 2f

                    containerLeftButton!!.visibility = View.GONE

                    containerCenterButton!!.visibility = View.VISIBLE
                    centerButton!!.text = centerText
                    centerButton!!.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    centerButton!!.setOnClickListener(centerAction)

                    rightButton!!.visibility = View.VISIBLE
                    rightButton!!.text = rightText
                    rightButton!!.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    rightButton!!.setOnClickListener(rightAction)
                }

                3 -> {
                    buttonsContainer!!.weightSum = 3f

                    containerLeftButton!!.visibility = View.VISIBLE
                    leftButton!!.text = leftText
                    leftButton!!.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    leftButton!!.setOnClickListener(leftAction)

                    containerCenterButton!!.visibility = View.VISIBLE
                    centerButton!!.text = centerText
                    centerButton!!.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    centerButton!!.setOnClickListener(centerAction)

                    rightButton!!.visibility = View.VISIBLE
                    rightButton!!.text = rightText
                    rightButton!!.setTag(DIALOG_INTERFACE_KEY, dialog) // Used for dismiss on callback.
                    rightButton!!.setOnClickListener(rightAction)
                }
            }
        } else
            throw RuntimeException("No action setted!")
    }

    fun dismiss() {
        dialog!!.dismiss()
    }
}