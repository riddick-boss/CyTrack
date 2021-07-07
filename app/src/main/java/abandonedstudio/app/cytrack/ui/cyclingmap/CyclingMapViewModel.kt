package abandonedstudio.app.cytrack.ui.cyclingmap

import abandonedstudio.app.cytrack.model.CyclingRide
import abandonedstudio.app.cytrack.model.CyclingRideRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CyclingMapViewModel @Inject constructor(private val repository: CyclingRideRepository): ViewModel() {

    fun getDistinctDestinations(): List<String>{
        var destinations: List<String>
        runBlocking {
            destinations = repository.getDistinctDestinations()
        }
        return destinations
    }

    fun insert(cyclingRide: CyclingRide) = viewModelScope.launch {
        repository.insert(cyclingRide)
    }

}