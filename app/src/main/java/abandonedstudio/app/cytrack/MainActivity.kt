package abandonedstudio.app.cytrack

import abandonedstudio.app.cytrack.databinding.ActivityMainBinding
import abandonedstudio.app.cytrack.ui.settingsmain.SettingsMainActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cyclingChooseSportCardView.setOnClickListener(this)
        binding.runningChooseSportCardView.setOnClickListener(this)
        binding.settingsImageButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.cycling_choose_sport_cardView -> {
                startActivity(Intent(this, CyclingActivity::class.java))
            }
            R.id.running_choose_sport_cardView->{
                // TODO: add start activity running
            }
            R.id.settings_imageButton -> {
                startActivity(Intent(this, SettingsMainActivity::class.java))
            }
        }
    }
}