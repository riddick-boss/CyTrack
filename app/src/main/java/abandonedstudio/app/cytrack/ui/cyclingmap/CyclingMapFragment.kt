package abandonedstudio.app.cytrack.ui.cyclingmap

import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.databinding.CyclingMapFragmentBinding
import abandonedstudio.app.cytrack.model.CyclingRide
import abandonedstudio.app.cytrack.services.TrackingService
import abandonedstudio.app.cytrack.utils.Constants.ACTION_END_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.ACTION_PAUSE_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.ACTION_START_OR_RESUME_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import abandonedstudio.app.cytrack.utils.Constants.MAP_ZOOM
import abandonedstudio.app.cytrack.utils.Constants.POLYLINE_WIDTH_ON_MAP
import abandonedstudio.app.cytrack.utils.ConvertersUI
import abandonedstudio.app.cytrack.utils.TrackingUtil
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.roundToInt

@AndroidEntryPoint
class CyclingMapFragment: Fragment(), EasyPermissions.PermissionCallbacks {

    private val viewModel: CyclingMapViewModel by viewModels()

    private var _binding: CyclingMapFragmentBinding? = null
    private val binding get() = _binding!!

    private var isTrackingNow = false
    private var pathPoints = mutableListOf<MutableList<LatLng>>()

//    this actually holds map on which actions (tracking, etc) are performed (mapView just displays it)
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CyclingMapFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        location permissions
        requestPermissions()

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            allPolyline()
        }

//        start/resume/pause tracking
        binding.startStopButton.setOnClickListener {
            if (isLocationEnabled(requireContext())){
                toggleTracking()
            } else{
                Snackbar.make(requireView(), "Turn on location!", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.finishButton.setOnClickListener {
            it.visibility = View.INVISIBLE
            endTrackingAndSave()
        }

        binding.cancelTrackingImageButton.setOnClickListener {
            cancelTracking()
        }

        subscribeToServiceData()

    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    /************************************************************************/

//    drawing a line on map
//    connecting 2 last coordinates
    private fun latestPolyline(){
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val lastCoordinate = pathPoints.last().last()
            val preLastCoordinate = pathPoints.last()[pathPoints.last().size-2]
            val polylineOptions = PolylineOptions()
                .color(Color.BLUE)
                .width(POLYLINE_WIDTH_ON_MAP)
                .add(preLastCoordinate)
                .add(lastCoordinate)
            map?.addPolyline(polylineOptions)
        }
    }

//    connecting all coordinates from training start - in case of device rotation, etc...
    private fun allPolyline(){
        for (coordinate in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(Color.BLUE)
                .width(POLYLINE_WIDTH_ON_MAP)
                .addAll(coordinate)
            map?.addPolyline(polylineOptions)
        }
    }

//    camera movement handling
    private fun moveCameraToCurrentLocation(){
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

//    check if location is turned on
    private fun isLocationEnabled(context: Context): Boolean{
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

//    updating UI elements
    private fun toggleUI(isTracking: Boolean){
        this.isTrackingNow = isTracking
        if (isTracking){
            binding.startStopButton.text = getString(R.string.pause)
            binding.finishButton.visibility = View.VISIBLE
        } else {
            binding.startStopButton.text = getString(R.string.start)
        }
    }

    @Suppress("SameParameterValue")
    private fun toggleUI(isTracking: Boolean, end: Boolean){
        this.isTrackingNow = isTracking
        if (isTracking){
            binding.startStopButton.text = getString(R.string.pause)
            binding.finishButton.visibility = View.VISIBLE
        } else {
            binding.startStopButton.text = getString(R.string.start)
            if(end){
                binding.finishButton.visibility = View.INVISIBLE
            }
        }
    }

//    toggling tracking service (start/pause/resume)
    private fun toggleTracking(){
        if (isTrackingNow){
            deliverActionToService(ACTION_PAUSE_TRACKING_SERVICE)
        } else{
            deliverActionToService(ACTION_START_OR_RESUME_TRACKING_SERVICE)
        }
    }

//    end tracking service WITH saving to db
    private fun endTrackingAndSave(){
        zoomToWholeTrack()
        deliverActionToService(ACTION_PAUSE_TRACKING_SERVICE)
        map?.snapshot {
            val distanceInKm = (TrackingUtil.calculateDistance(pathPoints) / 10).roundToInt() /100f
            val date = System.currentTimeMillis()
            var duration = TrackingService.trainingTimeInMinutes.value ?: 1
//            setting duration as at least 1 -> 0 will cause errors whit calculating avg speed (div by 0)
            if (duration < 1) {
                duration = 1
            }
            val view = View.inflate(requireContext(), R.layout.cycling_map_destination_dialog, null) as View
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setNeutralButton(resources.getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.ok){ _, _ ->
                    // prevent adding empty string and nullPointerException
                    var destination = view.findViewById<EditText>(R.id.destination_editText)?.text?.toString()
                        ?.trim()
                        ?: "Mysterious"
                    if (destination.isEmpty()){
                        destination = "Mysterious"
                    }
                    viewModel.insert(CyclingRide(distanceInKm, duration, date, destination, it))
                    deliverActionToService(ACTION_END_TRACKING_SERVICE)
                    map?.clear()
                    pathPoints.clear()
                    Snackbar.make(requireView(), "Remember to turn off location", Snackbar.LENGTH_SHORT).show()
                }.show()
        }
    }

//    end tracking service WITHOUT saving to db
    private fun cancelTracking(){
        if(isTrackingNow){
            deliverActionToService(ACTION_PAUSE_TRACKING_SERVICE)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.confirm_canceling_tracking))
                .setNeutralButton(resources.getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.ok)){ _, _ ->
                    toggleUI(isTracking = false, end = true)
                    deliverActionToService(ACTION_END_TRACKING_SERVICE)
                    map?.clear()
                    pathPoints.clear()
                    Snackbar.make(requireView(), "Remember to turn off location", Snackbar.LENGTH_SHORT).show()
                }.show()
        }
    }

    private fun zoomToWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for (set in pathPoints){
            for (coordinates in set){
                bounds.include(coordinates)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }

//    observing service data
    @SuppressLint("SetTextI18n")
    private fun subscribeToServiceData(){
        TrackingService.isTrackingActive.observe(viewLifecycleOwner, {
            toggleUI(it)
        })

        TrackingService.pathCoordinates.observe(viewLifecycleOwner, {
            pathPoints=it
            latestPolyline()
            moveCameraToCurrentLocation()
//            rounding to 2 decimal places
            binding.distanceTextView.text = "${((TrackingUtil.calculateDistance(pathPoints) / 10).roundToInt() /100f)} km"
        })

        TrackingService.trainingTimeInMinutes.observe(viewLifecycleOwner, {
            binding.durationTextView.text = ConvertersUI.formatTimeFromMinutes(it)
        })
    }

//     requesting permissions for location tracking
    private fun requestPermissions() {
        if (TrackingUtil.checkLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.location_permissions),
                LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.location_permissions),
                LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    //    if permission denied or permanently denied showing dialog and navigating to device-level settings, so user is able to give this permission easily
//     without this permissions app wouldn't work properly (tracking functionality)
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) { /*no operation*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//         re-directing parameters to EasyPermissions framework
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

//      deliver intent to tracking service (not starting service every time function is called)
//    use like: btn.onClick{ deliverActionToService(ACTION_TAG) }
    private fun deliverActionToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

}