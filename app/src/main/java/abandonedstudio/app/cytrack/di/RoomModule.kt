package abandonedstudio.app.cytrack.di

import abandonedstudio.app.cytrack.model.CyclingRide
import abandonedstudio.app.cytrack.model.MyDatabase
import abandonedstudio.app.cytrack.utils.Constants.MY_DATABASE_NAME
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context:Context
    ) = Room.databaseBuilder(
        context,
        MyDatabase::class.java,
        MY_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideCyclingRideDao(db: MyDatabase) = db.cyclingRideDao()
}