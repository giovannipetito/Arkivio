package it.giovanni.kotlin.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import it.giovanni.kotlin.utils.typekit.TypekitContextWrapper

open class BaseView : AppCompatActivity() {

    // Il blocco seguente fa in modo che il font fira sia applicato automaticamente in tutta l'app.
    /*
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase))
    }
    */
}