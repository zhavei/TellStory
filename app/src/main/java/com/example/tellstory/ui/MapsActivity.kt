package com.example.tellstory.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tellstory.R

class MapsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
    }


    companion object {
        const val MAPS_EXTRA = "maps_activity"
        const val MAPS_LOCATION = "maps_location"
        const val MAPS_ADDRESS = "maps_address"
    }

}