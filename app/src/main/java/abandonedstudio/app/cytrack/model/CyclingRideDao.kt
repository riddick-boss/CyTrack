package abandonedstudio.app.cytrack.model

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface CyclingRideDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cyclingRide: CyclingRide)

    @Delete
    suspend fun delete(cyclingRide: CyclingRide)

    @Query("SELECT DISTINCT UPPER(destination) FROM cycling_rides")
    fun getDistinctDestinations(): LiveData<List<String>>

    @Query("SELECT SUM(duration) FROM cycling_rides")
    fun getTotalRideTime(): LiveData<Int>

    @Query("SELECT SUM(distance_in_km) FROM cycling_rides")
    fun getTotalDistance(): LiveData<Float>

    @Query("SELECT CASE WHEN SUM(duration)=0 THEN 0 ELSE (SUM(distance_in_km) / CAST( SUM(duration) as FLOAT))*60 END FROM cycling_rides")
    fun getAvgSpeedKmH(): LiveData<Float>

    @Query("SELECT COUNT(*) FROM cycling_rides")
    fun countRowsLD(): LiveData<Int>

    @Query("SELECT * FROM cycling_rides")
    fun getAllCyclingRides(): LiveData<List<CyclingRide>>

    @Query("SELECT DISTINCT strftime('%Y', date / 1000, 'unixepoch') FROM cycling_rides")
    suspend fun getDistinctYears(): List<Int>

    @Query("SELECT * FROM cycling_rides WHERE CAST(strftime('%Y', date / 1000, 'unixepoch') as integer) = :year")
    suspend fun getAllRidesFromYear(year: Int): List<CyclingRide>

    @Query("SELECT SUM(duration) FROM cycling_rides WHERE CAST(strftime('%Y', date / 1000, 'unixepoch') as integer) = :year")
    suspend fun getTotalTimeRideInYear(year: Int): Int

    @Query("SELECT SUM(distance_in_km) FROM cycling_rides WHERE CAST(strftime('%Y', date / 1000, 'unixepoch') as integer) = :year")
    suspend fun getTotalDistanceInYear(year: Int): Float

    @Query("SELECT DISTINCT UPPER(destination) FROM cycling_rides WHERE CAST(strftime('%Y', date / 1000, 'unixepoch') as integer) = :year LIMIT 1")
    suspend fun getMostFrequentDestinationInYear(year: Int): String

    @Query("SELECT COUNT(DISTINCT strftime('%j', date / 1000, 'unixepoch')) FROM cycling_rides WHERE CAST(strftime('%Y', date / 1000, 'unixepoch') as integer) = :year")
    suspend fun getActiveDaysInYear(year: Int): Int

//    usage: milliseconds should be millis at start of day
    @Query("SELECT * FROM cycling_rides WHERE date BETWEEN :milliseconds AND :milliseconds+86400000")
    suspend fun getAllRidesInDay(milliseconds: Long): List<CyclingRide>

    @Query("SELECT  DISTINCT DATE(date/1000, 'unixepoch', 'start of day') FROM cycling_rides")
    fun getDistinctAllActiveDays(): LiveData<List<String>>

}