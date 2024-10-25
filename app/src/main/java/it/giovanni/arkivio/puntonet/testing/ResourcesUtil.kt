package it.giovanni.arkivio.puntonet.testing

import android.content.Context

class ResourcesUtil {

    fun isEqual(context: Context, resId: Int, appName: String): Boolean {
        val result: Boolean = context.getString(resId) == appName
        return result
    }
}