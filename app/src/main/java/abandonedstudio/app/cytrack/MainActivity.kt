package abandonedstudio.app.cytrack

import abandonedstudio.app.cytrack.databinding.ActivityMainBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
//    private val binding by viewBinding (ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cyclingChooseSportCardView.setOnClickListener(this)
        binding.runningChooseSportCardView.setOnClickListener(this)
        binding.appnameMainTextView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.cycling_choose_sport_cardView -> {
                startActivity(Intent(this, CyclingActivity::class.java))
            }
            R.id.running_choose_sport_cardView->{
                // TODO: add start activity running
            }
/*            R.id.appname_main_textView -> {
                Toast.makeText(this, "toast", Toast.LENGTH_SHORT).show()
            }*/
        }
    }
}