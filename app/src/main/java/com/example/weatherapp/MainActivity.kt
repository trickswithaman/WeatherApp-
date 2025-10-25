package com.example.weatherapp


import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.core.WeatherViewModel
import com.example.weatherapp.core.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.core.domain.usecase.WeatherUseCase
import com.example.weatherapp.core.utils.RetrofitInstance
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels {
        val repository = WeatherRepositoryImpl(RetrofitInstance.api)
        val useCase = WeatherUseCase(repository)
        WeatherViewModelFactory(useCase)
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val fusedLocationClient = remember {
                LocationServices.getFusedLocationProviderClient(context)
            }

            val locationPermissionState = rememberPermissionState(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )

            val gpsLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) {
                if (isLocationEnabled(context)) {
                    getCurrentLocation(fusedLocationClient, viewModel)
                } else {
                    Toast.makeText(context, "Please enable location", Toast.LENGTH_SHORT).show()
                }
            }

            var gpsDialogShown by remember { mutableStateOf(false) }

            // 🔹 Automatically request location permission when app launches
            LaunchedEffect(Unit) {
                if (!locationPermissionState.status.isGranted) {
                    locationPermissionState.launchPermissionRequest()
                }
            }

            // 🔹 Once permission granted, fetch location or show GPS prompt
            LaunchedEffect(locationPermissionState.status.isGranted) {
                if (locationPermissionState.status.isGranted) {
                    while (true) {
                        if (isLocationEnabled(context)) {
                            gpsDialogShown = false
                            getCurrentLocation(fusedLocationClient, viewModel)
                            break
                        } else if (!gpsDialogShown) {
                            gpsDialogShown = true
                            requestEnableGPS(context, gpsLauncher)
                        } else {
                            delay(4000)
                            gpsDialogShown = false
                        }
                    }
                }
            }

            Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    WeatherScreen(viewModel = viewModel)
                }
            }
        }
    }

    private fun getCurrentLocation(
        fusedLocationClient: FusedLocationProviderClient, viewModel: WeatherViewModel
    ) {
        val request =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).setMaxUpdates(1).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation
                if (loc != null) {
                    viewModel.getWeatherByLocation(loc.latitude, loc.longitude)
                } else {
                    Toast.makeText(
                        this@MainActivity, "Unable to fetch location", Toast.LENGTH_SHORT
                    ).show()
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestEnableGPS(
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
}

@Composable
fun RequestScreen(onAllowClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Location permission is required to show weather for your area.")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onAllowClick) { Text("Allow") }
    }
}

@Composable
fun RationaleScreen(onGrantClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("We need location access to fetch your local weather.")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onGrantClick) { Text("Grant Access") }
    }
}


@Composable
fun PermissionRationaleScreen(onGrantClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("We need location permission to show weather for your area.")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onGrantClick) { Text("Grant Permission") }
    }
}

@Composable
fun PermissionRequestScreen(onAllowClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Location permission is required to detect your weather.")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onAllowClick) { Text("Allow Location Access") }
    }
}

// ✅ Your Weather UI stays the same
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val locationWeather by viewModel.getWeatherbyLocation.observeAsState()

    var city by remember { mutableStateOf("") }
    val currentWeather = weatherState ?: locationWeather

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter city name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (city.isNotBlank()) viewModel.getWeather(city)
            }, modifier = Modifier.align(Alignment.End)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(24.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        currentWeather?.let { weather ->
            Text("City: ${weather.city}", style = MaterialTheme.typography.titleLarge)
            Text("Temperature: ${weather.temperature}°C")
            Text("Condition: ${weather.description}")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Longitude: ${weather.longitude}, Latitude: ${weather.latitude}")
        }

        if (currentWeather == null && errorMessage == null) {
            Text(
                "Fetching weather for your location...",
                style = MaterialTheme.typography.bodyLarge
            )
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