package com.example.tellstory.ui.maps

import android.Manifest
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
import com.example.tellstory.R
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.databinding.ActivityMapsBinding
import com.example.tellstory.ui.viewmodel.MapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import okio.IOException
import java.util.*

class MapsActivity : AppCompatActivity() {

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
    }


    private fun getUserLocation() {
        TODO("Not yet implemented")
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
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_2))
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