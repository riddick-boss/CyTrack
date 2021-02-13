package abandonedstudio.app.cytrack.ui.cyclingstats

import abandonedstudio.app.cytrack.model.CyclingRideRepository
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CyclingStatsViewModel @Inject constructor(private val repository: CyclingRideRepository): ViewModel(){

    val totalTime = repository.totalRideTime

    val totalRideDistance = repository.totalDistance

    val avgSpeed = repository.getAvgSpeedKmH()

    val mostFrequentDestinations = repository.distinctDestinations

}