package it.giovanni.arkivio.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.NavigationActivityBinding

/*
class NavigationActivity : BaseActivity() {

    private var layoutBinding: NavigationActivityBinding? = null
    val binding: NavigationActivityBinding? get() = layoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutBinding = NavigationActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}
*/

class NavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)
    }
}