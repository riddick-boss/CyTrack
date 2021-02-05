package abandonedstudio.app.cytrack.ui.cyclingaddride

import abandonedstudio.app.cytrack.model.CyclingRide
import abandonedstudio.app.cytrack.model.CyclingRideRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CyclingAddRideViewModel @Inject constructor(private val repository: CyclingRideRepository) : ViewModel() {

    val distinctDestinations = repository.distinctDestinations

    fun insert(cyclingRide: CyclingRide) = viewModelScope.launch {
        repository.insert(cyclingRide)
    }
}