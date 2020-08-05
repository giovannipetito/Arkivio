package it.giovanni.arkivio.activities

import androidx.appcompat.app.AppCompatActivity

open class BaseView : AppCompatActivity() {

    // Il blocco seguente fa in modo che il font fira sia applicato automaticamente in tutta l'app.
    /*
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase))
    }
    */
}