package abandonedstudio.app.cytrack.model

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject

class CyclingRideRepository @Inject constructor(private val cyclingRideDao: CyclingRideDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(cyclingRide: CyclingRide) {
        cyclingRideDao.insert(cyclingRide)
    }

    val distinctDestinations: LiveData<List<String>> = cyclingRideDao.getDistinctDestinations()

    val totalRideTime: LiveData<Int> = cyclingRideDao.getTotalRideTime()

    val totalDistance: LiveData<Float> = cyclingRideDao.getTotalDistance()

    val avgSpeedKmH: LiveData<Float> = cyclingRideDao.getAvgSpeedKmH()

}