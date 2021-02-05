package abandonedstudio.app.cytrack.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CyclingRideDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cyclingRide: CyclingRide)

    @Query("SELECT DISTINCT UPPER(destination) FROM cycling_rides")
    fun getDistinctDestinations(): LiveData<List<String>>

    @Query("SELECT SUM(duration) FROM cycling_rides")
    fun getTotalRideTime(): LiveData<Int>

    @Query("SELECT SUM(distance) FROM cycling_rides")
    fun getTotalDistance(): LiveData<Float>

    @Query("SELECT CASE WHEN duration=0 THEN 0 ELSE (SUM(distance) / CAST( duration as FLOAT))*60 END FROM cycling_rides")
    fun getAvgSpeedKmH(): LiveData<Float>

    @Query("SELECT COUNT(*) FROM cycling_rides")
    fun countRowsLD(): LiveData<Int>

}