package com.example.weatherapp.weather.presentaion.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.core.domain.models.forcastModel.City
import com.example.weatherapp.core.domain.models.forcastModel.Item0
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ForecastForCurrentTimeToNext24Hours(
    forecast: List<Item0>,
    city: City
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(forecast) { item ->
            val timezoneOffsetMillis = (city.timezone ?: 0) * 1000L

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            val utcDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .parse(item.dt_txt) ?: return@items

            val localTime = Date(utcDate.time + timezoneOffsetMillis)
            val calendar = Calendar.getInstance().apply { time = localTime }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val roundedHour = (hour / 3) * 3
            calendar.set(Calendar.HOUR_OF_DAY, roundedHour)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val time = sdf.format(calendar.time)
//            val time = sdf.format(localTime)

            val weather = item.weather.firstOrNull()
            val isNight = weather?.icon?.endsWith("n") == true
            val isRain = weather?.main?.contains("Rain", ignoreCase = true) == true
            val isCloud = weather?.main?.contains("Cloud", ignoreCase = true) == true
            val isSnow = weather?.main?.contains("Snow", ignoreCase = true) == true

            val background = when {
                isRain -> Brush.verticalGradient(listOf(Color(0xFF4A90E2), Color(0xFF50E3C2)))
                isCloud -> Brush.verticalGradient(listOf(Color(0xFF90A4AE), Color(0xFFCFD8DC)))
                isSnow -> Brush.verticalGradient(listOf(Color(0xFFE0E0E0), Color(0xFFB3E5FC)))
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
                    Text(text = time, color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Image(
                        painter = rememberAsyncImagePainter(
                            "https://openweathermap.org/img/wn/${weather?.icon}@2x.png"
                        ),
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
fun ForecastForNextThreeHourTo24hours(
    forecast: List<Item0>,
    city: City
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(forecast) { item ->
            val timezoneOffsetMillis = (city.timezone ?: 0) * 1000L

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            val utcDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .parse(item.dt_txt)

            val localTime = Date(utcDate?.time?.plus(timezoneOffsetMillis) ?: 0)
            val time = sdf.format(localTime)

            val weather = item.weather.firstOrNull()
            val isNight = weather?.icon?.endsWith("n") == true
            val isRain = weather?.main?.contains("Rain", ignoreCase = true) == true
            val isCloud = weather?.main?.contains("Cloud", ignoreCase = true) == true
            val isSnow = weather?.main?.contains("Snow", ignoreCase = true) == true

            // 🌙 Choose background dynamically
            val background = when {
                isRain -> Brush.verticalGradient(listOf(Color(0xFF4A90E2), Color(0xFF50E3C2)))
                isCloud -> Brush.verticalGradient(listOf(Color(0xFF90A4AE), Color(0xFFCFD8DC)))
                isSnow -> Brush.verticalGradient(listOf(Color(0xFFE0E0E0), Color(0xFFB3E5FC)))
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
                        text = time,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Image(
                        painter = rememberAsyncImagePainter(
                            "https://openweathermap.org/img/wn/${weather?.icon}@2x.png"
                        ),
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

//My Old code
/*
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
}*/