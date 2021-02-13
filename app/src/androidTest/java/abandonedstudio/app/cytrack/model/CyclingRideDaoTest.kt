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
        val avgSpeed = dao.getAvgSpeedKmH().getOrAwaitValue()
//        Truth.assertThat(avgSpeed).isEqualTo(22f)
        val cyclingRide2 = CyclingRide(11f, 60, currentTime)
        dao.insert(cyclingRide2)
        val avgSpeed2 = dao.getAvgSpeedKmH().getOrAwaitValue()
        Truth.assertThat(avgSpeed2).isEqualTo(16.5f)
    }

}