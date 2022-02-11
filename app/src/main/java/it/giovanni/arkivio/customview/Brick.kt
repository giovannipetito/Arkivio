package it.giovanni.arkivio.customview

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.BrickBinding
import it.giovanni.arkivio.viewinterfaces.IFlexBoxCallback

class Brick(context: Context) : RelativeLayout(context) {

    enum class ModeType {
        EDIT,
        VIEW
    }

    private var callback: IFlexBoxCallback? = null
    private var mode: ModeType = ModeType.VIEW
    private var userName: TextView? = null
    private var userEmail: TextView? = null
    private var container: RelativeLayout? = null
    private var icon: ImageView? = null
    private var isSelected: Boolean? = null
    private var position: Int = -1
    private var visible: Boolean = true

    init {
        init()
    }

    private fun init() {
        val brickBinding = BrickBinding.inflate(LayoutInflater.from(context), this, true)
        userName = brickBinding.partecipante
        userEmail = brickBinding.emailPartecipante
        container = brickBinding.container
        icon = brickBinding.icon
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