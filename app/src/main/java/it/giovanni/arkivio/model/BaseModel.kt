package it.giovanni.arkivio.model

import android.content.Context
import it.giovanni.arkivio.App

abstract class BaseModel {
    var context: Context = App.getInstance().context
    abstract fun cancelRequest()
}