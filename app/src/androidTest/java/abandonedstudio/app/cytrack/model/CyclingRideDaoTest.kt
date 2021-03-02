package abandonedstudio.app.cytrack.model

import abandonedstudio.app.cytrack.getOrAwaitValue
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CyclingRideDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: MyDatabase
    private lateinit var dao: CyclingRideDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MyDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.cyclingRideDao()
    }

    @After
    fun shutdown() {
        db.close()
    }

    @Test
    fun avgSpeedDivByZero() = runBlockingTest {
        val cyclingRide = CyclingRide(2f, 0, currentTime)
        dao.insert(cyclingRide)
        val avgSpeed = dao.getAvgSpeedKmH().getOrAwaitValue()
        Truth.assertThat(avgSpeed).isEqualTo(0f)
    }

    @Test
    fun avgSpeed() = runBlockingTest {
        val cyclingRide = CyclingRide(20f, 120, currentTime)
        dao.insert(cyclingRide)
        val avgSpeed = dao.getAvgSpeedKmH().getOrAwaitValue()
        Truth.assertThat(avgSpeed).isEqualTo(10f)
    }

    @Test
    fun countRows() = runBlockingTest {
        val rows = dao.countRowsLD().getOrAwaitValue()
        Truth.assertThat(rows).isEqualTo(0)
    }

    @Test
    fun avgSpeedAfterMultipleInserts() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, currentTime)
        dao.insert(cyclingRide)
//        val avgSpeed = dao.getAvgSpeedKmH().getOrAwaitValue()
//        Truth.assertThat(avgSpeed).isEqualTo(22f)
        val cyclingRide2 = CyclingRide(11f, 60, currentTime)
        dao.insert(cyclingRide2)
        val avgSpeed2 = dao.getAvgSpeedKmH().getOrAwaitValue()
        Truth.assertThat(avgSpeed2).isEqualTo(16.5f)
    }

    @Test
    fun getAllCyclingRides() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, currentTime, destination = "A")
        dao.insert(cyclingRide)
        val cyclingRide2 = CyclingRide(11f, 60, currentTime, destination = "B")
        dao.insert(cyclingRide2)
        val list = dao.getAllCyclingRides().getOrAwaitValue()
        Truth.assertThat(list).isEqualTo(listOf(CyclingRide(22f, 60, currentTime, destination = "A", rideId = 1),
            CyclingRide(11f, 60, currentTime, destination = "B", rideId = 2)))
    }

    @Test
    fun getYears() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, 1613749194969, destination = "A")
        val cyclingRide2 = CyclingRide(11f, 60, 1546383600000, destination = "A")
        val cyclingRide3 = CyclingRide(22f, 20, 1646089200100, destination = "B")
        dao.insert(cyclingRide)
        dao.insert(cyclingRide2)
        dao.insert(cyclingRide3)
        val years = dao.getDistinctYears().toMutableList()
        years.sortDescending()
        Truth.assertThat(years).isEqualTo(listOf(2022, 2021, 2019))
    }

    @Test
    fun getAllRidesFromYearTest() = runBlockingTest{
        val cyclingRide = CyclingRide(22f, 60, 1646089200000)
        val cyclingRide2 = CyclingRide(22f, 60, 1613640406126)
        dao.insert(cyclingRide)
        dao.insert(cyclingRide2)
        val rides = dao.getAllRidesFromYear(2021)
        Truth.assertThat(rides).isEqualTo(listOf(CyclingRide(22f, 60, 1613640406126, rideId = 2)))
    }

    @Test
    fun getTotalTimeFromYearTest() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, 1646089200000)
        val cyclingRide2 = CyclingRide(22f, 32, 1646089210001)
        val cyclingRide3 = CyclingRide(22f, 20, 1613640406126)
        dao.insert(cyclingRide)
        dao.insert(cyclingRide2)
        dao.insert(cyclingRide3)
        val totalTime = dao.getTotalTimeRideInYear(2022)
        Truth.assertThat(totalTime).isEqualTo(92)
    }

    @Test
    fun getMostFrequentDestinationInYearTest() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, 1646089200000, destination = "A")
        val cyclingRide2 = CyclingRide(11f, 60, 1646089200000, destination = "A")
        val cyclingRide3 = CyclingRide(22f, 20, 1646089200100, destination = "B")
        val cyclingRide4 = CyclingRide(22f, 20, 1646089200010, destination = "B")
        val cyclingRide5 = CyclingRide(22f, 60, 1646089200000, destination = "B")
        dao.insert(cyclingRide)
        dao.insert(cyclingRide2)
        dao.insert(cyclingRide3)
        dao.insert(cyclingRide4)
        dao.insert(cyclingRide5)
        val dest = dao.getMostFrequentDestinationInYear(2022)
        Truth.assertThat(dest).isEqualTo("B")
    }

    @Test
    fun getActiveDaysInYearTest() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, 1646089200000, destination = "A")
        val cyclingRide2 = CyclingRide(11f, 60, 1658440800000, destination = "A")
        val cyclingRide3 = CyclingRide(22f, 20, 1613640406126, destination = "B")
        val cyclingRide5 = CyclingRide(22f, 60, 1658440800000, destination = "B")
        dao.insert(cyclingRide)
        dao.insert(cyclingRide2)
        dao.insert(cyclingRide3)
        dao.insert(cyclingRide5)
        val days = dao.getActiveDaysInYear(2022)
        Truth.assertThat(days).isEqualTo(2)
    }

    @Test
    fun getAllRidesFromDay() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, 1646089204000, destination = "A")
        val cyclingRide2 = CyclingRide(11f, 60, 1646089200100, destination = "A")
        val cyclingRide3 = CyclingRide(22f, 20, 1613640406126, destination = "B")
        dao.insert(cyclingRide)
        dao.insert(cyclingRide2)
        dao.insert(cyclingRide3)
        val rides = dao.getAllRidesInDay(16460892)
        Truth.assertThat(rides).isEqualTo(listOf(CyclingRide(22f, 60, 1646089204000, destination = "A", rideId = 1),
            CyclingRide(11f, 60, 1646089200100, destination = "A", rideId = 2)))
    }


    @Test
    fun getDistinctDestination() = runBlockingTest{
        val cyclingRide = CyclingRide(22f, 60, 1646089204000, destination = "B")
        val cyclingRide2 = CyclingRide(11f, 60, 1646089200100, destination = "a")
        val cyclingRide3 = CyclingRide(22f, 20, 1613640406126, destination = "a")
        val cyclingRide4 = CyclingRide(22f, 60, 1646089204000, destination = "a")
        val cyclingRide5 = CyclingRide(11f, 60, 1646089200100, destination = "A")
        val cyclingRide6 = CyclingRide(22f, 20, 1613640406126, destination = "B")
        dao.insert(cyclingRide)
        dao.insert(cyclingRide2)
        dao.insert(cyclingRide3)
        dao.insert(cyclingRide4)
        dao.insert(cyclingRide5)
        dao.insert(cyclingRide6)
        val dest = dao.getDistinctDestinations().getOrAwaitValue()
        Truth.assertThat(dest).isEqualTo(listOf("A", "B"))
    }

    @Test
    fun getDistinctAllActiveDays() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, 1613749194969, destination = "A")
        val cyclingRide2 = CyclingRide(11f, 60, 1646089200100, destination = "A")
        val cyclingRide3 = CyclingRide(22f, 20, 1613640406126, destination = "B")
        dao.insert(cyclingRide)
        dao.insert(cyclingRide2)
        dao.insert(cyclingRide3)
        val days = dao.getDistinctAllActiveDays().getOrAwaitValue()
        Truth.assertThat(days).isEqualTo(listOf("2021-02-19", "2022-02-28", "2021-02-18"))
    }

}