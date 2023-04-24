package it.giovanni.arkivio.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.detail.navigation.model.User

class NavigationActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var listener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController // Inizializzo il navController passandogli l'id del NavHostFragment

        drawerLayout = findViewById(R.id.navigation_drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setupWithNavController(navController)

        listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.firstFragment -> {
                    // todo
                }
                R.id.secondFragment -> {
                    // todo
                }
                R.id.argsFragment -> {
                    val user = User("Giovanni", "Petito")
                    arguments?.putParcelable("user", user)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listener)
    }
}