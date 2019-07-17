package it.giovanni.kotlin.model

import android.content.Context
import it.giovanni.kotlin.App

abstract class BaseModel {
    var context: Context = App.getInstance().context
    abstract fun cancelRequest()
}