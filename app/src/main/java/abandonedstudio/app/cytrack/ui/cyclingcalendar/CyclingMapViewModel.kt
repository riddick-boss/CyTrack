package abandonedstudio.app.cytrack.ui.cyclingcalendar

import abandonedstudio.app.cytrack.model.CyclingRideRepository
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CyclingMapViewModel @Inject constructor(private val repository: CyclingRideRepository): ViewModel() {

}