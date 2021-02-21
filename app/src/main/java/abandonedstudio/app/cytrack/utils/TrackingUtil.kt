package abandonedstudio.app.cytrack.utils

import abandonedstudio.app.cytrack.utils.Constants.TRACKING_NOTIFICATION_CHANNEL_ID
import abandonedstudio.app.cytrack.utils.Constants.TRACKING_NOTIFICATION_CHANNEL_NAME
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.location.Location
import android.os.Build
import com.google.android.gms.maps.model.LatLng
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

    fun calculateDistance(sets: MutableList<MutableList<LatLng>>): Float{
        var distance = 0f
        for (set in sets){
            for (i in 0..set.size-2){
                val coordinates1 = set[i]
                val coordinates2 = set[i+1]

                val resultArray = FloatArray(1)
                Location.distanceBetween(
                    coordinates1.latitude,
                    coordinates1.longitude,
                    coordinates2.latitude,
                    coordinates2.longitude,
                    resultArray
                )
                distance += resultArray[0]
            }
        }
        return distance
    }

}