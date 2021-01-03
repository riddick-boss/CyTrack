package abandonedstudio.app.cytrack.ui.cyclingaddride

import abandonedstudio.app.cytrack.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class CyclingAddRideFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cycling_add_ride_fragment, container, false)
    }

}