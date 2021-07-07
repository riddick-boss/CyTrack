package abandonedstudio.app.cytrack.di

import abandonedstudio.app.cytrack.CyclingActivity
import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.utils.Constants
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @SuppressLint("VisibleForTests")
    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ) = FusedLocationProviderClient(context)

//    pending intent, so user will be navigated to this (CyclingMap) fragment when notification clicked
    @ServiceScoped
    @Provides
    fun provideCyclingMapPendingIntent(
        @ApplicationContext context: Context
    ) = PendingIntent.getActivity(
        context,
        0,
        Intent(context, CyclingActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_MAP_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )!!

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context, Constants.TRACKING_NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_baseline_directions_bike_100)
        .setContentTitle(context.getString(R.string.ridding))
        .setContentText(context.getString(R.string.total_time_content))
        .setContentIntent(pendingIntent)

}