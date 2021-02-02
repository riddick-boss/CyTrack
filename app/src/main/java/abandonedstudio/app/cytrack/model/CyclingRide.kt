package abandonedstudio.app.cytrack.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cycling_rides")
data class CyclingRide(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rideId")
    val rideId: Int? = null,

    @ColumnInfo(name = "distance")
    var distance: Float,

    @ColumnInfo(name = "duration")
    var duration: Int,

    @ColumnInfo(name = "destination")
    var destination: String,

    @ColumnInfo(name = "date")
    var date: Long
)