package it.giovanni.kotlin.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import it.giovanni.kotlin.R
import it.giovanni.kotlin.viewinterfaces.IFlexBoxCallback

class Brick : RelativeLayout {

    enum class ModeType {
        EDIT,
        VIEW
    }

    private var mode: ModeType = ModeType.VIEW
    private var mInflater: LayoutInflater? = null
    private var employeeName: TextView? = null
    private var employeeEmail: TextView? = null
    var container: RelativeLayout? = null
    private var icon: ImageView? = null
    private var isSelected: Boolean? = null
    var position: Int = -1
    var callback: IFlexBoxCallback? = null
    var visible: Boolean = true

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
        val v = mInflater?.inflate(R.layout.brick, this, true)
        if (v != null) {
            employeeName = v.findViewById(R.id.partecipante) as TextView
            employeeEmail = v.findViewById(R.id.emailPartecipante) as TextView
            container = v.findViewById(R.id.container) as RelativeLayout
            icon = v.findViewById(R.id.icon) as ImageView
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
            container!!.setOnClickListener {
                if (!isSelected!!) {
                    isSelected = true
                    // change color
                    container!!.setBackgroundColor(ContextCompat.getColor(context, R.color.rosso_1))
                    icon!!.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ico_close_rvd))
                } else {
                    // remove it!
                    if (position != -1 && callback != null) {
                        callback!!.flexBoxRemoved(position)
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
        employeeName?.text = name
    }

    fun setEmail(email: String) {
        employeeEmail?.text = email
    }

    fun getName(): String {
        return employeeName?.text.toString()
    }

    fun getEmail(): String {
        return employeeEmail?.text.toString()
    }
}