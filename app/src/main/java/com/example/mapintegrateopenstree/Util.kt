package com.example.mapintegrateopenstree

import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.osmdroid.util.GeoPoint
import java.util.Locale

fun Fragment.getLocationName(locationName: String) {
    try {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val geoResults: List<Address> =
            geocoder.getFromLocationName(locationName, 1) ?: listOf()
        if (geoResults.isNotEmpty()) {
            val addr = geoResults[0]
            val location = GeoPoint(addr.latitude, addr.longitude)
//            moveCameraMap(location)
        } else {
            Toast.makeText(requireContext(), "Location Not Found", Toast.LENGTH_LONG).show()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}