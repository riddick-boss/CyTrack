package abandonedstudio.app.cytrack.ui.cyclingstats

import abandonedstudio.app.cytrack.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class CyclingStatsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cycling_stats_fragment, container, false)
    }
}