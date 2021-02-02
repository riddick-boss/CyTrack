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
}