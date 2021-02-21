package abandonedstudio.app.cytrack.model

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject

class CyclingRideRepository @Inject constructor(private val cyclingRideDao: CyclingRideDao) {

    @WorkerThread
    suspend fun insert(cyclingRide: CyclingRide) {
        cyclingRideDao.insert(cyclingRide)
    }

    @WorkerThread
    suspend fun delete(cyclingRide: CyclingRide){
        cyclingRideDao.delete(cyclingRide)
    }

    val distinctDestinations: LiveData<List<String>> = cyclingRideDao.getDistinctDestinations()

    val totalRideTime: LiveData<Int> = cyclingRideDao.getTotalRideTime()

    val totalDistance: LiveData<Float> = cyclingRideDao.getTotalDistance()

    val rows: LiveData<Int> = cyclingRideDao.countRowsLD()

    fun getAvgSpeedKmH() = cyclingRideDao.getAvgSpeedKmH()

    suspend fun getDistinctYears() = cyclingRideDao.getDistinctYears()

    suspend fun getTotalTimeRideInYear(year: Int) = cyclingRideDao.getTotalTimeRideInYear(year)

    suspend fun getTotalDistanceInYear(year: Int) = cyclingRideDao.getTotalDistanceInYear(year)

    suspend fun getMostFrequentDestinationInYear(year: Int) = cyclingRideDao.getMostFrequentDestinationInYear(year)

    suspend fun getActiveDaysInYear(year: Int) = cyclingRideDao.getActiveDaysInYear(year)

    suspend fun getAllRidesInDay(milliseconds: Long) = cyclingRideDao.getAllRidesInDay(milliseconds)

    fun getDistinctAllActiveDays() = cyclingRideDao.getDistinctAllActiveDays()

}