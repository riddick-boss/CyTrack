# CyTrack
#### Android app for outdoor sports enthusiasts (like cyclists) that helps track training sessions and provides statistics, allowing the user to keep an eye on personal progress.

Used:
- Kotlin
- Kotlin Coroutines
- MVVM Architecture
- Room Database
- LiveData
- Hilt
- Foreground service
- Google Maps API
- [Navigation drawer](https://material.io/components/navigation-drawer)
- DataStore
- Permissions
- Intents
- Notifications
- Fragments
- Dialogs
- Navigation Component
- ViewBinding
- Material Design
- [EasyPermissions](https://github.com/googlesamples/easypermissions)
- [Glide](https://github.com/bumptech/glide)
- Espresso

## General info

App is organized in MVVM structure (Entity -> DAO -> Repository -> ViewModel -> View). Navigation is implemented with usage of Navigation Drawer. Foreground Service is tracking user's location, to get information about distance, duration , avg. speed. When user finishes training, screenshot of Google Map with track on it is taken and then displayed in Statistics Fragment with usage of Glide. It is also possible to change location update period in [SettingsActivity](https://github.com/riddick-boss/CyTrack/blob/master/app/src/main/java/abandonedstudio/app/cytrack/ui/settingsmain/SettingsMainActivity.kt).

## UI

#### 1. Main Activity


<img src="/screenshots/main_activity.png" width="200">

This is opening activity. From here user might choose sport or open SettingActivity.

#### 2. SettingsMain Activity

<img src="/screenshots/global_settings.png" width="200">

Location update period might be changed here, in order to improve tracking precision (shorter period) or save battery (longer period). Information about it is stored with DataStore.

#### 3. Cycling Activity

<img src="/screenshots/drawer.png" width="200">

This is Navigation Drawer Activity with 3 fragments: MapFragment, AddRide (manually) and Statistics.

#### 3.1 Map Fragment

<img src="/screenshots/map_tracking.png" width="200">

Google Map is used to draw track. User is able to stop tracking with or without saving to database. If user decides to save it, then screenshot of map with drawn track is taken and MaterialAlertDialog (with custom layout) is shown, asking user to input training destination. Also, when tracking is started, Foreground Service is launched, so it is not neccessary for app to be open all time - tracking will continue even if app is in background and/or screen is turned off.

#### 3.2 AddRide Fragment

<img src="/screenshots/add_manually.png" width="200">

From here it is possible to add ride manually in case it was not done with usage of app's tracking service. There is DatePickerDialog to choose a date of ride, but only past dates (including today) are valid to choose :wink:

#### 3.3 Statistics Fragment

<img src="/screenshots/stats1.png" width="200"> <img src="/screenshots/stats2.png" width="200"> <img src="/screenshots/stats_3.png" width="200" height="356">

In this fragment there are multiple sections. First - overall - distance, avg. speed, duration, most frequent destination based on all tracks in database. Second - information from each year. They are displayed in recyclerView. Thanks to this section it is easy to see if you made progress since last year, check number of active days and feel like true sportsperson :smile: . Then there is last section - displaying information about each day. User is able to pick a date in DatePicker and information about this date will be displayed in recyclerView below. However it might be quite annoying to search previous day when training was performed , so I added buttons to jump from day to day (list of active days), so all user has to do is just click on them and next/previous day with at least 1 training will be set. Each item in this recyclerView has button, which allows user to delete this ride - in case it was e.g. added accidentally.

## Forground Service

Foreground service is used to track user's location even if app is background. To be able to do it permissions were needed:
``` xml
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```
Notification is updated once a minute and has button to pause/resume training session, so it is not neccessary to open app. I achieved this behaviour by using Intents:
``` kotlin
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
```
If notification is clicked user will be immediately navigated to MapFragment (also by using Intent).

## SQLite Room Database

Id of each row is autogenerated. Example of SQLite queries in [DAO](https://github.com/riddick-boss/CyTrack/blob/master/app/src/main/java/abandonedstudio/app/cytrack/model/CyclingRideDao.kt):
- to get number of active days in year:
``` SQL
    @Query("SELECT COUNT(DISTINCT strftime('%j', date / 1000, 'unixepoch')) FROM cycling_rides WHERE CAST(strftime('%Y', date / 1000, 'unixepoch') as integer) = :year")
    suspend fun getActiveDaysInYear(year: Int): Int 
```

- and to get all rides from selected day:
``` SQL
    @Query("SELECT  DISTINCT DATE(date/1000, 'unixepoch', 'start of day') FROM cycling_rides")
    fun getDistinctAllActiveDays(): LiveData<List<String>> 
```
Screenshot of map is passed to database as Bitmap, so I created [Converters](https://github.com/riddick-boss/CyTrack/blob/master/app/src/main/java/abandonedstudio/app/cytrack/model/Converters.kt), which are converting Bitmap to byteArray (accepted by Room) and vice versa (needed when loading photo with Glide in Statistics Fragment).

## Code samples

#### Dependency Injection with Hilt

Hilt Modules are declared in [di directory](https://github.com/riddick-boss/CyTrack/tree/master/app/src/main/java/abandonedstudio/app/cytrack/di).
Examples of usage: 
- Room module - creating database:
```kotlin
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context:Context
    ) = Room.databaseBuilder(
        context,
        MyDatabase::class.java,
        MY_DATABASE_NAME
    ).build(
```
- Hilt is also used with ViewModels and helps to inject repository, so I could safely get rid of ViewModelFactory.
```kotlin
    @HiltViewModel
    class CyclingStatsViewModel @Inject constructor(private val repository: CyclingRideRepository): ViewModel(){
```

#### Interface

Interface is used to pass Ride as fun parameter from [recyclerView](https://github.com/riddick-boss/CyTrack/blob/master/app/src/main/java/abandonedstudio/app/cytrack/ui/cyclingstats/StatsDayAdapter.kt). 
```kotlin
    interface OnDeleteItemListener{
        fun delete(ride: CyclingRide)
    }

    fun setOnDeleteItemListener(listener: OnDeleteItemListener){
        this.listener = listener
    }
    
    holder.binding.deleteRideImageButton.setOnClickListener {
        MaterialAlertDialogBuilder(holder.itemView.context)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete)
            .setNeutralButton(R.string.cancel){dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.ok){ _, _ ->
                    listener.delete(ride)
                    ridesInDayList.removeAt(position)
                    notifyItemRemoved(position)
                }.show()
        }
    }    
```
And delete it from [StatsFragment](https://github.com/riddick-boss/CyTrack/blob/master/app/src/main/java/abandonedstudio/app/cytrack/ui/cyclingstats/CyclingStatsFragment.kt) class, which has viewModel.
```kotlin
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dayAdapter.setOnDeleteItemListener(this)
    }

        override fun delete(ride: CyclingRide) {
        viewModel.delete(ride)
    }
```

#### Tests

I have created multiple InstrumentedTests to check if Queries are working properly, using Espresso Testing framework. Here in one of them:
```kotlin
    @Test
    fun getAllCyclingRides() = runBlockingTest {
        val cyclingRide = CyclingRide(22f, 60, currentTime, destination = "A")
        dao.insert(cyclingRide)
        val cyclingRide2 = CyclingRide(11f, 60, currentTime, destination = "B")
        dao.insert(cyclingRide2)
        val list = dao.getAllCyclingRides().getOrAwaitValue()
        Truth.assertThat(list).isEqualTo(listOf(CyclingRide(22f, 60, currentTime, destination = "A", rideId = 1),
            CyclingRide(11f, 60, currentTime, destination = "B", rideId = 2)))
    }
```
