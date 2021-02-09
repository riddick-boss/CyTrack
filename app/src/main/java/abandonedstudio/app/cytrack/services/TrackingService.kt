package abandonedstudio.app.cytrack.services

import abandonedstudio.app.cytrack.CyclingActivity
import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.utils.Constants
import abandonedstudio.app.cytrack.utils.Constants.ACTION_END_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.ACTION_PAUSE_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.ACTION_SHOW_MAP_FRAGMENT
import abandonedstudio.app.cytrack.utils.Constants.ACTION_START_OR_RESUME_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.TRACKING_NOTIFICATION_CHANNEL_ID
import abandonedstudio.app.cytrack.utils.Constants.TRACKING_NOTIFICATION_ID
import abandonedstudio.app.cytrack.utils.TrackingUtil
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

class TrackingService: LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var isTrackingActive = false

        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_TRACKING_SERVICE -> {
                    if (!isTrackingActive){
                        startForegroundService()
                        isTrackingActive = true
                    } else {
//                        TODO: resume tracking
                    }
                }
                ACTION_PAUSE_TRACKING_SERVICE -> {
//                    TODO: pause tracking
                }
                ACTION_END_TRACKING_SERVICE -> {
//                    TODO: stop tracking
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

//      pending intent, so user will be navigated to this (CyclingMap) fragment when notification clicked
    private fun getCyclingMapPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, CyclingActivity::class.java).also {
            it.action = ACTION_SHOW_MAP_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

//      starting foreground service
    private fun startForegroundService(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        TrackingUtil.createNotificationChannel(notificationManager)
        val notificationBuilder =  NotificationCompat.Builder(this, TRACKING_NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_100)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.total_time_content))
            .setContentIntent(getCyclingMapPendingIntent())

        startForeground(TRACKING_NOTIFICATION_ID, notificationBuilder.build())
    }

}