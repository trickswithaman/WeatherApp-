package com.example.weatherapp.weather.presentaion.mainScreen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.core.WeatherViewModel
import com.example.weatherapp.core.domain.models.forcastModel.Item0
import com.example.weatherapp.weather.presentaion.getCurrentLocation
import com.example.weatherapp.weather.presentaion.isLocationEnabled
import com.example.weatherapp.weather.presentaion.requestEnableGPS
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherScreen(navController: NavController, viewModel: WeatherViewModel) {

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
            getCurrentLocation(context, fusedLocationClient, viewModel)
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
                    getCurrentLocation(context, fusedLocationClient, viewModel)
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
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val locationWeather by viewModel.getWeatherbyLocation.observeAsState()
    var city by remember { mutableStateOf("") }
    val currentWeather = weatherState ?: locationWeather
    val forecast by viewModel.next24Hours.collectAsState()
    val forecast7Days by viewModel.next7Days.collectAsState()
    remember(currentWeather) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        hour < 1 || hour >= 18
    }





    LaunchedEffect(currentWeather) {
        currentWeather?.let { weather ->
            viewModel.getForecast(weather.latitude, weather.longitude)
        }
    }
    LaunchedEffect(currentWeather) {
        currentWeather?.let { weather ->
            viewModel.getForecastdays(weather.latitude, weather.longitude)
        }
    }

    MaterialTheme {


        val backgroundRes = remember(currentWeather?.description) {
            val icon = currentWeather?.icon
            val description = currentWeather?.description

            when {
                icon?.endsWith("n") == true -> R.drawable.night_backgroung
                description?.contains(
                    "rain",
                    ignoreCase = true
                ) == true -> R.drawable.rain_background

                description?.contains(
                    "cloud",
                    ignoreCase = true
                ) == true -> R.drawable.colud_background

                description?.contains(
                    "clear sky",
                    ignoreCase = true
                ) == true -> R.drawable.sunny_background

                description?.contains(
                    "night",
                    ignoreCase = true
                ) == true -> R.drawable.night_backgroung

                else -> R.drawable.sunny_background
            }
        }


        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = backgroundRes as Int),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                alpha = 0.85f
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                singleLine = true,
                placeholder = { Text("Search city name") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (city.isNotBlank()) viewModel.getWeather(city)
                    },

                    ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search, keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))



            if (errorMessage != null) {

                Spacer(modifier = Modifier.height(24.dp))
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                currentWeather?.let { weather ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.loctaion),
                            contentDescription = "Location",
                            modifier = Modifier.size(25.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = weather.city,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth()
                                .wrapContentHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    when {
                                        weather.icon.endsWith("n") -> R.raw.moon
                                        weather.description.contains(
                                            "cloud",
                                            ignoreCase = true
                                        ) -> R.raw.cloud

                                        weather.description.contains(
                                            "rain",
                                            ignoreCase = true
                                        ) -> R.raw.rain

                                        weather.description.contains(
                                            "snow",
                                            ignoreCase = true
                                        ) -> R.raw.snow

                                        else -> R.raw.sun
                                    }
                                )
                            )

                            // Animate it infinitely
                            val progress by animateLottieCompositionAsState(
                                composition, iterations = LottieConstants.IterateForever
                            )

                            LottieAnimation(
                                composition = composition,
                                progress = progress,
                                modifier = Modifier.size(100.dp)
                            )

                            Text(
                                text = "${weather.description}",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )/*?*/
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth()
                                .wrapContentHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                buildAnnotatedString {
                                    append("${weather.temperature}")
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 12.sp,
                                            baselineShift = BaselineShift.Superscript
                                        )
                                    ) {
                                        append("°C")
                                    }
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.SemiBold,
                            )

                            Text(
                                buildAnnotatedString {
                                    append("Min : ${weather.temp_min}")
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 12.sp,
                                            baselineShift = BaselineShift.Superscript
                                        )
                                    ) {
                                        append("°C")
                                    }
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                buildAnnotatedString {
                                    append("Max : ${weather.temp_max}")
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 12.sp,
                                            baselineShift = BaselineShift.Superscript
                                        )
                                    ) {
                                        append("°C")
                                    }
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )

                        }
                    }

                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                        val dayName = when (day) {
                            Calendar.SUNDAY -> "Sunday"
                            Calendar.MONDAY -> "Monday"
                            Calendar.TUESDAY -> "Tuesday"
                            Calendar.WEDNESDAY -> "Wednesday"
                            Calendar.THURSDAY -> "Thursday"
                            Calendar.FRIDAY -> "Friday"
                            Calendar.SATURDAY -> "Saturday"
                            else -> {}
                        }

                        Image(
                            painter = rememberAsyncImagePainter(
                                model = "https://openweathermap.org/img/wn/${weather.icon}@2x.png"
                            ), contentDescription = null, modifier = Modifier.size(100.dp)
                        )

                        Text(
                            text = "$dayName",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        val date =
                            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
                        Text(
                            text = "$date",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Next24HourForecastSection(forecast)


                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .border(
                                width = 1.dp, shape = RoundedCornerShape(16.dp), color = Color.White
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                        ),


                        ) {
                        Column(
                            Modifier
                                .wrapContentSize()
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White
                                        ),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Spacer(
                                            modifier = Modifier.height(3.dp)
                                        )
                                        Image(
                                            painter = painterResource(id = R.drawable.humidity),
                                            contentDescription = "humidity",
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "${weather.humidity}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                        Text(
                                            text = "humidity",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal
                                        )

                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White
                                        ),
                                    shape = RoundedCornerShape(10.dp),

                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {

                                        Image(
                                            painter = painterResource(id = R.drawable.wind),
                                            contentDescription = "wind",
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "${weather.windSpeed}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                        Text(
                                            text = "Wind Speed",

                                            fontSize = 14.sp, fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White
                                        ),
                                    shape = RoundedCornerShape(10.dp),

                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {

                                        Image(
                                            painter = painterResource(id = R.drawable.conditions),
                                            contentDescription = "rain",
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "${weather.description}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 15.sp,
                                            maxLines = 1,
                                            fontWeight = FontWeight.Normal
                                        )
                                        Text(
                                            text = "Condition",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White
                                        ),
                                    shape = RoundedCornerShape(10.dp),

                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {

                                        Image(
                                            painter = painterResource(id = R.drawable.sunrise),
                                            contentDescription = "sunrise",
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "${time(weather.sunrise.toLong())}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                        Text(
                                            text = "Sunrise",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White
                                        ),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {

                                        Image(
                                            painter = painterResource(id = R.drawable.sunset),
                                            contentDescription = "sunset",
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "${time(weather.sunset.toLong())}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                        Text(
                                            text = "Sunset",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White
                                        ),
                                    shape = RoundedCornerShape(10.dp),

                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {

                                        Image(
                                            painter = painterResource(id = R.drawable.sea),
                                            contentDescription = "sea",
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "${weather.sea_level}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                        Text(
                                            text = "sea",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                            }

                        }
                    }

                    viewModel.city.value
                    Next7DaysSection(forecast7Days)

                }

            }



            if (currentWeather == null && errorMessage == null) {

                Text(
                    "Fetching weather for your location...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }


}

fun time(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

@Composable
fun Next24HourForecastSection(forecast: List<Item0>) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(forecast) { item ->
            val time = remember(item.dt_txt) {
                val parts = item.dt_txt.split(" ")
                parts[1].substring(0, 5) // "HH:mm"
            }

            val isNight = item.weather.first().icon.endsWith("n")
            val isRain = item.weather.first().main.contains("Rain", ignoreCase = true)
            val isCloud = item.weather.first().main.contains("Cloud", ignoreCase = true)

            val background = when {
                isRain -> Brush.verticalGradient(listOf(Color(0xFF4A90E2), Color(0xFF50E3C2)))
                isCloud -> Brush.verticalGradient(listOf(Color(0xFF90A4AE), Color(0xFFCFD8DC)))
                isNight -> Brush.verticalGradient(listOf(Color(0xFF0D47A1), Color(0xFF311B92)))
                else -> Brush.verticalGradient(listOf(Color(0xFFFFC107), Color(0xFFFF9800)))
            }

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = background)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = time, color = Color.White, style = MaterialTheme.typography.bodySmall
                    )


                    Image(
                        painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${item.weather.first().icon}@2x.png"),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )

                    Text(
                        text = "${item.main.temp}°C",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun Next7DaysSection(next7Days: List<Item0>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Next 7 Days",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        next7Days.forEach { forecast ->
            // 🔹 Convert dt (seconds) to readable day name
            val date = Date(forecast.dt * 1000L)
            val sdf =
                SimpleDateFormat("EEEE", Locale.getDefault()) // gives full day name like "Monday"
            val dayName = sdf.format(date)

            val weatherMain = forecast.weather.firstOrNull()?.icon ?: ""
            when {
                weatherMain.contains("Rain", true) -> R.drawable.rain
                weatherMain.contains("Cloud", true) -> R.drawable.cloud_black
                weatherMain.contains("Clear", true) -> R.drawable.white_cloud
                else -> R.drawable.white_cloud
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Day name (e.g., Monday)
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )


                    // Weather icon and temperature
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${forecast.weather.first().icon}@2x.png"),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "${forecast.main.temp_max.toInt()}° / ${forecast.main.temp_min.toInt()}°",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

