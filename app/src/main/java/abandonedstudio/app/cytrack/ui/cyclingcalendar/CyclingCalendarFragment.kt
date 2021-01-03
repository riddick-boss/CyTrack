package abandonedstudio.app.cytrack.ui.cyclingcalendar

import abandonedstudio.app.cytrack.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class CyclingCalendarFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cycling_calendar_fragment, container, false)
    }
}