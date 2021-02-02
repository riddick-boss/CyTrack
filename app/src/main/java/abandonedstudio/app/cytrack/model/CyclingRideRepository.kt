package abandonedstudio.app.cytrack.model

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject

class CyclingRideRepository @Inject constructor(private val cyclingRideDao: CyclingRideDao) {

    val distinctDestinations: LiveData<List<String>> = cyclingRideDao.getDistinctDestinations()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(cyclingRide: CyclingRide) {
        cyclingRideDao.insert(cyclingRide)
    }

}