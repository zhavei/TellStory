package com.example.tellstory.ui.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.tellstory.R
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.databinding.ActivityMapsBinding
import com.example.tellstory.ui.detail.DetailsActivity
import com.example.tellstory.ui.viewmodel.MapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import okio.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding by lazy {
        ActivityMapsBinding.inflate(layoutInflater)
    }

    private val addNewStoryViewModel: MapsViewModel by viewModels() {
        ViewModelFactories.getInstance(application)
    }

    private lateinit var mMap: GoogleMap
    private var selectedStoryId: String? = null
    private var selectedMark: Marker? = null
    private var prevSelectedMark: Marker? = null
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isPickLocation: Boolean = false
    private val initialMarker: MutableList<LatLng> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        isPickLocation = intent.getBooleanExtra(MAPS_PICKED_LATLON, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Enable compass and zoom controls on the map.
        mMap.uiSettings.run {
            isCompassEnabled = true
            isZoomControlsEnabled = true
        }

        // Reset the selected story ID and hide the detail and location pick buttons when the map is clicked.
        mMap.setOnMapClickListener {
            selectedStoryId = null
            binding.btnViewDetail.isVisible = false
            binding.btnPickLocation.isVisible = false
        }

        // Show the selected story's info window and update the UI when a marker is clicked.
        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            selectedStoryId = marker.tag?.toString()
            prevSelectedMark = selectedMark
            selectedMark = marker
            if (isPickLocation) {
                binding.btnPickLocation.isVisible = true
            }
            binding.btnViewDetail.isVisible = true
            if (selectedStoryId.isNullOrEmpty() || selectedStoryId == "null") {
                binding.btnViewDetail.visibility = View.INVISIBLE
            }
            return@setOnMarkerClickListener true
        }

        // If the user is picking a location, add a new marker when the map is long clicked.
        if (isPickLocation) {
            binding.btnPickLocation.isVisible = true
            mMap.setOnMapLongClickListener { addNewMarker(it) }
        }

        // Set the map style, and observe the loading state and snackbar text from the view model.
        setMapStyle()
        addNewStoryViewModel.loading.observe(this, ::showLoading)

        // Load the stories and add markers for them.
        addNewStoryViewModel.mapsStory.observe(this) { stories ->
            if (stories.isNotEmpty()) addMarkers(
                stories
            )
        }
        addNewStoryViewModel.getMapsStories()

        // Show the story details when the "View Detail" button is clicked, and let the user pick a location when the "Pick Location" button is clicked.
        binding.btnViewDetail.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DetailsActivity::class.java
                ).apply { putExtra(DetailsActivity.DETAILS_EXTRA_WITH_ID, selectedStoryId) })
        }
        binding.btnPickLocation.setOnClickListener { onBackPressedCallback.handleOnBackPressed() }

        // Get the user's location.
        getUserLocation()
    }


    private fun addMarkers(stories: List<MainStory>) {
        // Add markers for each story that has a valid latitude and longitude.
        stories.forEach { story ->
            story.lat?.let { lat ->
                story.lon?.let { lon ->
                    val coordinate = LatLng(lat, lon)
                    initialMarker.add(coordinate)
                    mMap.addMarker(
                        MarkerOptions().position(coordinate).title(story.name).snippet(story.description)
                    )?.tag = story.id
                    boundsBuilder.include(coordinate)
                }
            }
        }

        // Create a bounds object that includes all the markers, and animate the camera to show them all.
        val bounds = boundsBuilder.build()
        val padding = resources.displayMetrics.widthPixels / 4
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    private fun addNewMarker(latLng: LatLng) {
        // Remove the previously selected marker if it exists.
        selectedMark?.let { prevSelectedMark ->
            if (initialMarker.contains(prevSelectedMark.position)) {
                prevSelectedMark.remove()
            } else {
                selectedMark?.position
            }
        }

        // Create a new marker at the specified position with a green color and a title and snippet that describe the address.
        val newMarker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Your Location")
                .snippet(getAddress(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )

        // Show the info window for the new marker.
        newMarker?.showInfoWindow()

        // Set the new marker as the selected marker.
        selectedMark = newMarker
    }

    private fun getUserLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    if (isPickLocation) {
                        addNewMarker(latLng)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    }
                } ?: run {
                    Toast.makeText(this, "Last location is not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                getUserLocation()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Location permission is required to use this feature.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mapsProgress.visibility = View.VISIBLE
        } else {
            binding.mapsProgress.visibility = View.GONE
        }
    }

    private fun getAddress(lat: Double?, lon: Double?): String {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            if (lat != null && lon != null) {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    addressName = list[0].getAddressLine(0)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return addressName ?: "($lat, $lon)"
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this, "Failed parse map style", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(this, "Can't find map style: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            intent.putExtras(
                Bundle().apply {
                    putParcelable(
                        MAPS_LOCATION,
                        selectedMark?.position
                    )
                    putString(
                        MAPS_ADDRESS,
                        getAddress(
                            selectedMark?.position?.latitude,
                            selectedMark?.position?.longitude
                        )
                    )
                }
            )
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedCallback.handleOnBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        const val MAPS_PICKED_LATLON = "extra_maps_activity"
        const val MAPS_LOCATION = "extra_maps_location"
        const val MAPS_ADDRESS = "extra_maps_address"
    }

}