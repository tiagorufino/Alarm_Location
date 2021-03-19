package com.tiagorufino.android.alarmlocation.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.SphericalUtil
import com.tiagorufino.android.alarmlocation.R
import com.tiagorufino.android.alarmlocation.main.MainActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //Map
    private lateinit var mMap: GoogleMap

    //permission, provider location and places
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    //request of the user location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    //check the distance
    private lateinit var lastLocation: Location
    private lateinit var addressLocation: LatLng
    private lateinit var userLocation: LatLng
    private var radius: Int = 0
    private var isGettingClose: Boolean = false
    private var distance: Double = 0.0

    //Constrants
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_oncreate))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        radius = getString(R.string.distance).toInt()

        Places.initialize(applicationContext, getString(R.string.maps_key))
        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        isUserReachRadius()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        createLocationRequest()
    }

    /**
     * Check if the user already reach the radius
     */
    private fun isUserReachRadius(){
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_isUserReachRadius))

        locationCallback = object : LocationCallback() {
            @SuppressLint("StringFormatMatches")
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                userLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                distance = SphericalUtil.computeDistanceBetween(addressLocation, userLocation)

                if (distance < ((radius /2) + radius) && distance > radius && !isGettingClose){
                    Toast.makeText(applicationContext,
                        getString(R.string.closed_to_destination, distance.toInt()),
                        Toast.LENGTH_LONG).show()
                    isGettingClose = true
                } else if (distance < radius){
                    cancelUserLocationUpdate()
                    callAlarmScreen()
                }
            }
        }
    }

    /**
     * Get the updates and set the user location update
     */
    private fun getPermissionsForUpdate() {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_getPermissionsForUpdate))

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        locationPermissionGranted = true
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    /**
     * Create the request for the user location. Its used for update the user position
     */
    private fun createLocationRequest() {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_createLocationRequest))

        locationRequest = LocationRequest()
        locationRequest.interval = 8000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            getPermissionsForUpdate()
        }
    }

    /**
     * Load the moviment of the user by the GPS location
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_onActivityResult))

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                getPermissionsForUpdate()
            }
        }
    }

    /**
     * Reload for calculate the position of the user
     */
    public override fun onResume() {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_onresume))

        super.onResume()
        if (!locationUpdateState) {
            getPermissionsForUpdate()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_onmapready))

        googleMap.also { mMap = it }

        val addresses: List<Address> = Geocoder(this).getFromLocationName(
            intent.getStringExtra("addressClient"), 1)

        addressLocation = LatLng(addresses[0].latitude, addresses[0].longitude)

        mMap.addMarker(MarkerOptions().position(addressLocation)
            .title(R.string.position_tittle.toString()))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addressLocation, 15F))
        mMap.addCircle(
            CircleOptions()
                .center(addressLocation)
                .radius(radius.toDouble())
                .strokeColor(R.color.blue_selection_screen)
                .fillColor(R.color.blue_selection_screen))

        mMap.isTrafficEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        // Prompt the user for permission.
        getPermissionsForUpdate()
        updateLocationUI()
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    private fun updateLocationUI() {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_updateLocationUI))

        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings?.isMyLocationButtonEnabled = false
                getPermissionsForUpdate()
            }
        } catch (e: SecurityException) {
            Log.e(R.string.exception.toString(), e.message, e)
        }
    }

    /**
     * Call the main page and cancel the map
     */
    fun callMainScreen(view: View?) {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_main_screen ))

        cancelUserLocationUpdate()
        val intent = Intent(this, MainActivity::class.java).apply {  }
        startActivity(intent)
    }

    /**
     * Call the alarm screen after reachs the radius
     */
    fun callAlarmScreen() {
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_alarm_screen))

        val intent = Intent(this, AlarmActivity::class.java).apply {  }
        startActivity(intent)
    }

    /**
     * Cancel the user location update
     */
    fun cancelUserLocationUpdate(){
        Log.i(R.string.maps_screen.toString(),getString(R.string.call_cancelUserLocationUpdate))

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}