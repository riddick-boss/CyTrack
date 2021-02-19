package abandonedstudio.app.cytrack.ui.cyclingstats

import abandonedstudio.app.cytrack.model.CyclingRide
import abandonedstudio.app.cytrack.model.CyclingRideRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CyclingStatsViewModel @Inject constructor(private val repository: CyclingRideRepository): ViewModel(){

    fun delete(cyclingRide: CyclingRide) = viewModelScope.launch {
        repository.delete(cyclingRide)
    }

    val totalTime = repository.totalRideTime

    val totalRideDistance = repository.totalDistance

    val avgSpeed = repository.getAvgSpeedKmH()

    val mostFrequentDestinations = repository.distinctDestinations

    val rows = repository.rows

    val activeDays = repository.getDistinctAllActiveDays()

    fun getDistinctYears(): MutableList<Int>{
        var list: MutableList<Int>
        runBlocking {
            list = repository.getDistinctYears().toMutableList()
        }
        Log.d("years", list.toString())
        return list
    }

    fun getTotalTimeInYear(year: Int): Int{
        var time: Int
        runBlocking {
            time = repository.getTotalTimeRideInYear(year)
        }
        return time
    }

    fun getTotalDistanceInYear(year: Int): Float{
        var distance: Float
        runBlocking {
            distance = repository.getTotalDistanceInYear(year)
        }
        return distance
    }

    fun getMostFrequentDestinationInYear(year: Int): String {
        var destination: String

        runBlocking {
            destination = repository.getMostFrequentDestinationInYear(year)
        }
        return destination
    }

    fun getActiveDaysInYear(year: Int): Int{
        var days: Int
        runBlocking {
            days = repository.getActiveDaysInYear(year)
        }
        return days
    }

    fun getAllRidesInDay(millis: Long): List<CyclingRide> {
        var list: List<CyclingRide>
        runBlocking {
            list = repository.getAllRidesInDay(millis)
        }
        return list
    }

}