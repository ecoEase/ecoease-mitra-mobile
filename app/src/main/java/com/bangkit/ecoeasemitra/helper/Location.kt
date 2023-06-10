package com.bangkit.ecoeasemitra.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

fun FusedLocationProviderClient.getLastLocation(context: Context, onSuccess: (Location) -> Unit, onError: (Exception) -> Unit){

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        this.lastLocation.addOnSuccessListener { location ->
            if(location == null) {
                try {//if null using location manager
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val locationListener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            onSuccess(location)
                            locationManager.removeUpdates(this)
                        }
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                }catch (e: Exception){
                    onError( NullPointerException("Location is null!"))
                }
            } else {
                onSuccess(location)
            }
        }
    }else {
        onError(SecurityException("Location permission not granted!"))
    }
}