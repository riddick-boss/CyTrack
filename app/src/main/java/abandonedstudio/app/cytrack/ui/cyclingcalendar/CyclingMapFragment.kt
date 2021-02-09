package abandonedstudio.app.cytrack.ui.cyclingcalendar

import abandonedstudio.app.cytrack.CyclingActivity
import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.databinding.CyclingMapFragmentBinding
import abandonedstudio.app.cytrack.services.TrackingService
import abandonedstudio.app.cytrack.utils.Constants
import abandonedstudio.app.cytrack.utils.Constants.ACTION_SHOW_MAP_FRAGMENT
import abandonedstudio.app.cytrack.utils.Constants.ACTION_START_OR_RESUME_TRACKING_SERVICE
import abandonedstudio.app.cytrack.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import abandonedstudio.app.cytrack.utils.TrackingUtil
import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class CyclingMapFragment: Fragment(), EasyPermissions.PermissionCallbacks {

    private val viewModel: CyclingMapViewModel by viewModels()

    private var _binding: CyclingMapFragmentBinding? = null
    private val binding get() = _binding!!

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
        }

//        start/stop tracking
        binding.startStopButton.setOnClickListener {
            deliverActionToService(ACTION_START_OR_RESUME_TRACKING_SERVICE)
            binding.startStopButton.text = getString(R.string.finish)
        }

    }

    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView?.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    //     fun to be able to cache map and avoid fully loading it every time
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }

    /************************************************************************/


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