package it.giovanni.arkivio.activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.detail.navigation.model.User

class NavigationActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var listener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // navController = findNavController(R.id.nav_host_fragment) // Inizializzo il navController passandogli l'id del NavHostFragment
        drawerLayout = findViewById(R.id.navigation_drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setupWithNavController(navController)

        // Inizializzo appBarConfiguration passandogli due parametri, il primo è navController.graph
        // che usa navController per passare il navigation graph, il secondo parametro è drawerLayout
        // che è il root layout di navigation_activity.
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)

        listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.firstFragment) {
                supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.red_A700)))
            } else if (destination.id == R.id.secondFragment) {
                supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.light_green_A700)))
            } else if (destination.id == R.id.argsFragment) {
                supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.light_green_A700)))

                val user = User("Giovanni", "Petito")
                arguments?.putParcelable("user", user)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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