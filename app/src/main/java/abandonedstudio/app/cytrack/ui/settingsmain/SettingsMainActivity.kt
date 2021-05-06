package abandonedstudio.app.cytrack.ui.settingsmain

import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.databinding.SettingsMainBinding
import abandonedstudio.app.cytrack.utils.Constants.LOCATION_UPDATE_PERIOD_KEY
import abandonedstudio.app.cytrack.utils.Constants.SEVEN_SEC_LOCATION_UPDATE_PERIOD_DS
import abandonedstudio.app.cytrack.utils.Constants.THREE_SEC_LOCATION_UPDATE_PERIOD_DS
import abandonedstudio.app.cytrack.utils.Constants.TWELVE_SEC_LOCATION_UPDATE_PERIOD_DS
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsMainActivity : AppCompatActivity() {

    private lateinit var binding: SettingsMainBinding

    private val Context.dataStore by preferencesDataStore(name = "global_settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSavedSettings()

        binding.threeSecPeriodButton.setOnClickListener {
            binding.locationPeriodDescTextView.text = resources.getText(R.string.best_tracking_desc)
        }

        binding.sevenSecPeriodButton.setOnClickListener {
            binding.locationPeriodDescTextView.text = resources.getText(R.string.medium_tracking_desc)
        }

        binding.twelveSecPeriodButton.setOnClickListener {
            binding.locationPeriodDescTextView.text = resources.getText(R.string.worst_tracking_desc)
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                when {
                    binding.threeSecPeriodButton.isChecked -> {
                        saveToDataStore(LOCATION_UPDATE_PERIOD_KEY, THREE_SEC_LOCATION_UPDATE_PERIOD_DS)
                    }
                    binding.sevenSecPeriodButton.isChecked -> {
                        saveToDataStore(LOCATION_UPDATE_PERIOD_KEY, SEVEN_SEC_LOCATION_UPDATE_PERIOD_DS)
                    }
                    binding.twelveSecPeriodButton.isChecked -> {
                        saveToDataStore(LOCATION_UPDATE_PERIOD_KEY, TWELVE_SEC_LOCATION_UPDATE_PERIOD_DS)
                    }
                }
            }
            Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private suspend fun saveToDataStore(key: String, value: String){
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { globalSettings ->
            globalSettings[dataStoreKey] = value
        }
    }

    private suspend fun readDataStore(key: String): String? {
        val  dataStoreKey = stringPreferencesKey(key)
        val  preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    private fun loadSavedSettings(){
        lifecycleScope.launch {
            when {
                readDataStore(LOCATION_UPDATE_PERIOD_KEY) == THREE_SEC_LOCATION_UPDATE_PERIOD_DS -> {
                    binding.threeSecPeriodButton.isChecked = true
                    binding.locationPeriodDescTextView.text = resources.getText(R.string.best_tracking_desc)
                }
                readDataStore(LOCATION_UPDATE_PERIOD_KEY) == SEVEN_SEC_LOCATION_UPDATE_PERIOD_DS -> {
                    binding.sevenSecPeriodButton.isChecked = true
                    binding.locationPeriodDescTextView.text = resources.getText(R.string.medium_tracking_desc)
                }
                readDataStore(LOCATION_UPDATE_PERIOD_KEY) == TWELVE_SEC_LOCATION_UPDATE_PERIOD_DS -> {
                    binding.twelveSecPeriodButton.isChecked = true
                    binding.locationPeriodDescTextView.text = resources.getText(R.string.worst_tracking_desc)
                }
            }
        }
    }

}