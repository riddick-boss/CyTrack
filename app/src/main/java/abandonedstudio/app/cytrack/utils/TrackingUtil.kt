package abandonedstudio.app.cytrack.utils

import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.utils.Constants.TRACKING_NOTIFICATION_CHANNEL_ID
import abandonedstudio.app.cytrack.utils.Constants.TRACKING_NOTIFICATION_CHANNEL_NAME
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import pub.devrel.easypermissions.EasyPermissions

object TrackingUtil {

    fun checkLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
                )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

//    creating channel for tracking notification
    fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            TRACKING_NOTIFICATION_CHANNEL_ID,
            TRACKING_NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }



}