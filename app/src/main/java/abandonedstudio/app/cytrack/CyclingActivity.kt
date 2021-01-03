package abandonedstudio.app.cytrack

import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment

class CyclingActivity : AppCompatActivity() {

    private lateinit var cyclingAppBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cycling_activity)
        val cyclingToolbar: Toolbar = findViewById(R.id.cycling_toolbar)
        setSupportActionBar(cyclingToolbar)

        val cyclingDrawerLayout: DrawerLayout = findViewById(R.id.cycling_drawer_layout)
        val cyclingNavView: NavigationView = findViewById(R.id.nav_view)
//        val cyclingNavController = findNavController(R.id.cycling_nav_host)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.cycling_nav_host) as NavHostFragment
        val cyclingNavController = navHostFragment.navController
        val topDestinations = setOf(R.id.CyclingStatsFragment, R.id.CyclingCalendarFragment, R.id.CyclingAddRideFragment)
        cyclingAppBarConfiguration = AppBarConfiguration(topDestinations, cyclingDrawerLayout)
        setupActionBarWithNavController(cyclingNavController, cyclingAppBarConfiguration)
        cyclingNavView.setupWithNavController(cyclingNavController)
    }

/*    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.cycling, menu)
        return true
    }*/

    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.cycling_nav_host)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.cycling_nav_host) as NavHostFragment
        val cyclingNavController = navHostFragment.navController
        return cyclingNavController.navigateUp(cyclingAppBarConfiguration) || super.onSupportNavigateUp()
    }
}