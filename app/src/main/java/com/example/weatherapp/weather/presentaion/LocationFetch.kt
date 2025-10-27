package com.example.weatherapp.weather.presentaion

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.IntentSender
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.core.WeatherViewModel
import com.example.weatherapp.core.domain.usecase.WeatherUseCase
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: WeatherViewModel
) {
    val request =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).setMaxUpdates(1).build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation
            if (loc != null) {
                viewModel.getWeatherByLocation(loc.latitude, loc.longitude)
                viewModel.getForecast(loc.latitude, loc.longitude)

            } else {
                Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
            fusedLocationClient.removeLocationUpdates(this)
        }
    }

    if (ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }
}

fun isLocationEnabled(context: Context): Boolean {
    val lm = context.getSystemService(LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

fun requestEnableGPS(
    context: Context,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, androidx.activity.result.ActivityResult>
) {
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    val builder =
        LocationSettingsRequest.Builder().addLocationRequest(request).setAlwaysShow(true)

    val client = LocationServices.getSettingsClient(context)
    val task = client.checkLocationSettings(builder.build())

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                val intentSenderRequest =
                    IntentSenderRequest.Builder(exception.resolution).build()
                launcher.launch(intentSenderRequest)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(context, "Unable to access location settings", Toast.LENGTH_SHORT)
                .show()
        }
    }
}


class WeatherViewModelFactory(
    private val weatherUseCase: WeatherUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(weatherUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}