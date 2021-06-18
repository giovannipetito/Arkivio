package it.giovanni.arkivio.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R
import it.giovanni.arkivio.viewinterfaces.IFlexBoxCallback

class Brick : RelativeLayout {

    enum class ModeType {
        EDIT,
        VIEW
    }

    private var callback: IFlexBoxCallback? = null
    private var mode: ModeType = ModeType.VIEW
    private var mInflater: LayoutInflater? = null
    private var userName: TextView? = null
    private var userEmail: TextView? = null
    private var container: RelativeLayout? = null
    private var icon: ImageView? = null
    private var isSelected: Boolean? = null
    private var position: Int = -1
    private var visible: Boolean = true

    constructor(context: Context) : super(context) {
        mInflater = LayoutInflater.from(context)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mInflater = LayoutInflater.from(context)
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mInflater = LayoutInflater.from(context)
        init()
    }

    private fun init() {
        val view = mInflater?.inflate(R.layout.brick, this, true)
        if (view != null) {
            userName = view.findViewById(R.id.partecipante) as TextView
            userEmail = view.findViewById(R.id.emailPartecipante) as TextView
            container = view.findViewById(R.id.container) as RelativeLayout
            icon = view.findViewById(R.id.icon) as ImageView
        }
        isSelected = false
    }

    fun callback(c: IFlexBoxCallback) {
        callback = c
    }

    fun position(p: Int) {
        position = p
    }

    fun mode(type: ModeType) {
        this.mode = type
        if (mode == ModeType.EDIT) {
            container?.setOnClickListener {
                if (!isSelected!!) {
                    isSelected = true
                    // Change color
                    container?.setBackgroundColor(ContextCompat.getColor(context, R.color.rosso))
                    icon?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ico_close_rvd))
                } else {
                    // Remove it
                    if (position != -1 && callback != null) {
                        callback?.flexBoxRemoved(position)
                        visible = false
                    }
                }
            }
        }
    }

    fun isVisible(): Boolean {
        return visible
    }

    fun setName(name: String) {
        userName?.text = name
    }

    fun setEmail(email: String) {
        userEmail?.text = email
    }

    fun getName(): String {
        return userName?.text.toString()
    }

    fun getEmail(): String {
        return userEmail?.text.toString()
    }
}