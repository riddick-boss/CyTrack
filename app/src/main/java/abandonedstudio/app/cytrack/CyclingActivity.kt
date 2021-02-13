package abandonedstudio.app.cytrack

import abandonedstudio.app.cytrack.databinding.CyclingActivityBinding
import abandonedstudio.app.cytrack.utils.Constants.ACTION_SHOW_MAP_FRAGMENT
import android.content.Intent
import android.os.Bundle
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CyclingActivity : AppCompatActivity() {

    private lateinit var cyclingAppBarConfiguration: AppBarConfiguration

    private lateinit var binding: CyclingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CyclingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToMapFragmentIfNeeded(intent)

        setSupportActionBar(binding.cyclingToolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.cycling_nav_host) as NavHostFragment
        val cyclingNavController = navHostFragment.navController
        val topDestinations = setOf(R.id.CyclingStatsFragment, R.id.CyclingMapFragment, R.id.CyclingAddRideFragment)
        cyclingAppBarConfiguration = AppBarConfiguration(topDestinations, binding.cyclingDrawerLayout)
        setupActionBarWithNavController(cyclingNavController, cyclingAppBarConfiguration)
        binding.navView.setupWithNavController(cyclingNavController)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.cycling_nav_host) as NavHostFragment
        val cyclingNavController = navHostFragment.navController
        return cyclingNavController.navigateUp(cyclingAppBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToMapFragmentIfNeeded(intent)
    }

//    navigating to mapFragment if user clicks tracking notification
    private fun navigateToMapFragmentIfNeeded(intent: Intent?){
        if (intent?.action == ACTION_SHOW_MAP_FRAGMENT){
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.cycling_nav_host) as NavHostFragment
            navHostFragment.findNavController().navigate(R.id.action_global_to_cyclingMapFragment)
        }
    }
}