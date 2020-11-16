package it.giovanni.arkivio.bean

import java.io.Serializable

class SelectedDay : Serializable, Cloneable {

    var year: String? = null
    var month: String? = null
    var dayOfMonth: String? = null

    constructor()

    constructor(year: String, month: String, dayOfMonth: String) {
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
    }

    fun cloneList(): SelectedDay {

        val selectedDay = SelectedDay()

        selectedDay.year = year
        selectedDay.month = month
        selectedDay.dayOfMonth = dayOfMonth

        return selectedDay
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Any {
        return super.clone()
    }
}