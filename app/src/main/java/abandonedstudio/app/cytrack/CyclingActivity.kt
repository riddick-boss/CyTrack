package abandonedstudio.app.cytrack

import abandonedstudio.app.cytrack.databinding.CyclingActivityBinding
import android.os.Bundle
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CyclingActivity : AppCompatActivity() {

    private lateinit var cyclingAppBarConfiguration: AppBarConfiguration

    private lateinit var binding: CyclingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CyclingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.cycling_activity)
//        val cyclingToolbar: Toolbar = findViewById(R.id.cycling_toolbar)
        setSupportActionBar(binding.cyclingToolbar)

//        val cyclingDrawerLayout: DrawerLayout = findViewById(R.id.cycling_drawer_layout)
//        val cyclingNavView: NavigationView = findViewById(R.id.nav_view)
//        val cyclingNavController = findNavController(R.id.cycling_nav_host)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.cycling_nav_host) as NavHostFragment
        val cyclingNavController = navHostFragment.navController
        val topDestinations = setOf(R.id.CyclingStatsFragment, R.id.CyclingMapFragment, R.id.CyclingAddRideFragment)
        cyclingAppBarConfiguration = AppBarConfiguration(topDestinations, binding.cyclingDrawerLayout)
        setupActionBarWithNavController(cyclingNavController, cyclingAppBarConfiguration)
        binding.navView.setupWithNavController(cyclingNavController)
//        cyclingNavView.setupWithNavController(cyclingNavController)
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