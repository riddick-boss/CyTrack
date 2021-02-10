package abandonedstudio.app.cytrack.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CyclingRide::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MyDatabase: RoomDatabase() {

    abstract fun cyclingRideDao(): CyclingRideDao

}