package abandonedstudio.app.cytrack.services

import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.utils.Constants.ACTION_END_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.ACTION_PAUSE_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.ACTION_START_OR_RESUME_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.FASTEST_SEVEN_SEC_LOCATION_UPDATE_PERIOD
import abandonedstudio.app.cytrack.utils.Constants.FASTEST_THREE_SEC_LOCATION_UPDATE_PERIOD
import abandonedstudio.app.cytrack.utils.Constants.FASTEST_TWELVE_SEC_LOCATION_UPDATE_PERIOD
import abandonedstudio.app.cytrack.utils.Constants.LOCATION_UPDATE_PERIOD_KEY
import abandonedstudio.app.cytrack.utils.Constants.SEVEN_SEC_LOCATION_UPDATE_PERIOD
import abandonedstudio.app.cytrack.utils.Constants.SEVEN_SEC_LOCATION_UPDATE_PERIOD_DS
import abandonedstudio.app.cytrack.utils.Constants.THREE_SEC_LOCATION_UPDATE_PERIOD
import abandonedstudio.app.cytrack.utils.Constants.THREE_SEC_LOCATION_UPDATE_PERIOD_DS
import abandonedstudio.app.cytrack.utils.Constants.TRACKING_NOTIFICATION_ID
import abandonedstudio.app.cytrack.utils.Constants.TWELVE_SEC_LOCATION_UPDATE_PERIOD
import abandonedstudio.app.cytrack.utils.Constants.TWELVE_SEC_LOCATION_UPDATE_PERIOD_DS
import abandonedstudio.app.cytrack.utils.ConvertersUI
import abandonedstudio.app.cytrack.utils.TrackingUtil
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService: LifecycleService() {

//    necessary to receive location updates
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var updatedNotificationBuilder: NotificationCompat.Builder

    private val Context.dataStore by preferencesDataStore(name = "global_settings")

//    this is to check if user has started completely new training or if he is continuing another (resuming)
    private var isFirstPartOfTraining = true

    private var isServiceKilled = false

//    private val trainingTimeInMinutes= MutableLiveData<Long>()
    private val trainingTimeInMillis = MutableLiveData<Long>()
    private var lapTime = 0L
    private var startTime = 0L
    private var totalTime = 0L
    private var minuteTimestamp = 0L

    private var locationUpdateInterval = THREE_SEC_LOCATION_UPDATE_PERIOD
    private var fastestLocationUpdateInterval = FASTEST_THREE_SEC_LOCATION_UPDATE_PERIOD

    companion object{
        val isTrackingActive = MutableLiveData<Boolean>()

        val trainingTimeInMinutes= MutableLiveData<Int>()

        //        this is list of lists of coordinates; user might stop tracking and resume later on after changing location,
//        so to prevent counting distance when tracking was paused, each line is stored as separate list of coordinates;
//        all this lines together create real distance which was ridden during training
//        it's typed as MutableLiveData, so it is easy to observe changes
        val pathCoordinates = MutableLiveData<MutableList<MutableList<LatLng>>>()
    }


    override fun onCreate() {
        super.onCreate()

//        if loading async then settings might not be loaded properly
//        timeout 5s, if not able to access settings -> 3sec period will be set
        runBlocking {
            withTimeout(5000L){
                loadTrackingIntervals()
            }
        }

        updatedNotificationBuilder = baseNotificationBuilder
        initializeValues()

        isTrackingActive.observe(this, {
            updateTracking(it)
            updateNotification(it)
        })
    }

    private suspend fun loadTrackingIntervals() {
        val  dataStoreKey = stringPreferencesKey(LOCATION_UPDATE_PERIOD_KEY)
        val  preferences = dataStore.data.first()
        when(preferences[dataStoreKey]) {
            THREE_SEC_LOCATION_UPDATE_PERIOD_DS -> {
                locationUpdateInterval = THREE_SEC_LOCATION_UPDATE_PERIOD
                fastestLocationUpdateInterval = FASTEST_THREE_SEC_LOCATION_UPDATE_PERIOD
            }
            SEVEN_SEC_LOCATION_UPDATE_PERIOD_DS -> {
                locationUpdateInterval = SEVEN_SEC_LOCATION_UPDATE_PERIOD
                fastestLocationUpdateInterval = FASTEST_SEVEN_SEC_LOCATION_UPDATE_PERIOD
            }
            TWELVE_SEC_LOCATION_UPDATE_PERIOD_DS -> {
                locationUpdateInterval = TWELVE_SEC_LOCATION_UPDATE_PERIOD
                fastestLocationUpdateInterval = FASTEST_TWELVE_SEC_LOCATION_UPDATE_PERIOD
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_TRACKING_SERVICE -> {
                    if (isFirstPartOfTraining){
                        startForegroundService()
                        isFirstPartOfTraining = false
                    } else {
                        startTimerAndStartTracking()
                    }
                }
                ACTION_PAUSE_TRACKING_SERVICE -> {
                    pauseService()
                }
                ACTION_END_TRACKING_SERVICE -> {
                    endService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun initializeValues(){
        isTrackingActive.postValue(false)
        pathCoordinates.postValue(mutableListOf())
        trainingTimeInMinutes.postValue(0)
        trainingTimeInMillis.postValue(0L)
    }

//    after resuming tracking it is needed to add new empty list at the end for next part of training path coordinates
    private fun addNextCoordinatesList() = pathCoordinates.value?.apply {
        add(mutableListOf())
        pathCoordinates.postValue(this)
    } ?: pathCoordinates.postValue(mutableListOf(mutableListOf()))

//    add new coordinates
    private fun addPathPoint(location: Location?){
        location?.let {
            val coordinates = LatLng(it.latitude, it.longitude)
            pathCoordinates.value?.apply {
                last().add(coordinates)
                pathCoordinates.postValue(this)
            }
        }
    }

//    retrieving location coordinates (changes)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTrackingActive.value!!){
                result.locations.let {
                    for (location in it){
                        addPathPoint(location)
                    }
                }
            }
        }
    }

//    starting and stopping retrieving location updates (pause/resume training)
//    permissions are checked
    @SuppressLint("MissingPermission")
    private fun updateTracking(isTracking: Boolean){
        if (isTracking){
            if (TrackingUtil.checkLocationPermissions(this)){
                val locationRequest = LocationRequest.create().apply {
                    interval = locationUpdateInterval
                    fastestInterval = fastestLocationUpdateInterval
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

//    pausing tracking
    private fun pauseService(){
        isTrackingActive.postValue(false)
    }

//    ending tracking
    private fun endService(){
        isServiceKilled=true
        isFirstPartOfTraining=true
        pauseService()
        initializeValues()
        stopForeground(true)
        stopSelf()
    }

//    starting timer, creating new set of coordinates, starting tracking again, updating timer
    private fun startTimerAndStartTracking(){
        addNextCoordinatesList()
        isServiceKilled=false
        // this is starting tracking
        isTrackingActive.postValue(true)
        startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isTrackingActive.value!!){
                lapTime = System.currentTimeMillis() - startTime
                trainingTimeInMillis.postValue(totalTime + lapTime)
//                checking if next whole minute has passed - if true updating notification
                if (trainingTimeInMillis.value!! >= minuteTimestamp + 60000L){
                    trainingTimeInMinutes.postValue(trainingTimeInMinutes.value!! + 1)
                    minuteTimestamp += 60000L
                }
                delay(10000L)
            }
            totalTime += lapTime
        }
    }

//     starting foreground service
    private fun startForegroundService(){
        startTimerAndStartTracking()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        TrackingUtil.createNotificationChannel(notificationManager)

        startForeground(TRACKING_NOTIFICATION_ID, baseNotificationBuilder.build())

        trainingTimeInMinutes.observe(this, {
            if (!isServiceKilled){
                val notification = updatedNotificationBuilder
                    .setContentText(ConvertersUI.formatTimeFromMinutes(it))
                notificationManager.notify(TRACKING_NOTIFICATION_ID, notification.build())
            }
        })
    }

//    update notification
    @SuppressLint("RestrictedApi")
    private fun updateNotification(isTrackingNow: Boolean){
        val pendingIntentStartStop = PendingIntent.getService(
            this,
            if (isTrackingNow) 1 else 2,
            Intent(this, TrackingService::class.java).apply {
                action = if (isTrackingNow) ACTION_PAUSE_TRACKING_SERVICE else ACTION_START_OR_RESUME_TRACKING_SERVICE
            },
            FLAG_UPDATE_CURRENT
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        updatedNotificationBuilder.mActions.clear()
        if (!isServiceKilled){
            updatedNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_round_pause_24, if (isTrackingNow) "Pause" else "Resume", pendingIntentStartStop)
            notificationManager.notify(TRACKING_NOTIFICATION_ID, updatedNotificationBuilder.build())
        }
    }

}