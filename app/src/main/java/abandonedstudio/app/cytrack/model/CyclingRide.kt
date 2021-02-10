package abandonedstudio.app.cytrack.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cycling_rides")
data class CyclingRide(

    @ColumnInfo(name = "distance_in_km")
    var distance: Float,

    @ColumnInfo(name = "duration")
    var duration: Int,

    @ColumnInfo(name = "date")
    var date: Long,

    @ColumnInfo(name = "destination")
    var destination: String = "Mysterious",

    @ColumnInfo(name = "track_image")
    var img: Bitmap? = null,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rideId")
    var rideId: Int? = null

)