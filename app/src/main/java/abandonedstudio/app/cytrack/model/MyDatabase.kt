package abandonedstudio.app.cytrack.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CyclingRide::class], version = 1, exportSchema = false)
abstract class MyDatabase: RoomDatabase() {

    abstract fun cyclingRideDao(): CyclingRideDao

/*    companion object{

        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabaseClient(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                MyDatabase::class.java,
                "myDatabase").build()
                INSTANCE = instance
                instance
            }
        }
    }*/
}