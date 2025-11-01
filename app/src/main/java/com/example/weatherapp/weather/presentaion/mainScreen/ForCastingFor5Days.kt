package com.example.weatherapp.weather.presentaion.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.core.domain.models.forcastModel.Item0
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                    val date = inputFormat.parse(forecast.dt_txt)
                    val formattedDate = outputFormat.format(date)
                    // Day name (e.g., Monday)
                    Text(
                        text = "$formattedDate $dayName",
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
                            text = "${forecast.main.temp_min.toInt()}° / ${forecast.main.temp_max.toInt()}°",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Next5DaysSection(next5Days: List<Item0>) {
    // Group forecasts by date (e.g., "2025-10-28")
    val groupedByDate = next5Days.groupBy { item ->
        item.dt_txt.substringBefore(" ")
    }

    // Convert each group to a daily summary
    val dailyForecasts = groupedByDate.map { (dateStr, items) ->
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(date ?: Date())

        val minTemp = items.minOf { it.main.temp_min }
        val maxTemp = items.maxOf { it.main.temp_max }
        val weatherIcon = items.firstOrNull()?.weather?.firstOrNull()?.icon ?: "01d"

        Triple(Triple(dayName, dateStr, weatherIcon), "$minTemp° / $maxTemp°", weatherIcon)
    }

    // 🔹 Skip today's forecast (index 0)
    val upcomingDays = dailyForecasts.drop(1).take(5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Next 5 Days",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        upcomingDays.forEach { (dayInfo, tempRange, icon) ->
            val (dayName, dateStr, weatherIcon) = dayInfo
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
                    // 🗓️ Day name (e.g., Tuesday)
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                    val date = inputFormat.parse(dateStr)
                    val formattedDate = date?.let { outputFormat.format(it) } ?: ""
                    // Day name (e.g., Monday)
                    Text(
                        text = "$formattedDate $dayName",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    // 🌦️ Weather icon and temperature
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                "https://openweathermap.org/img/wn/${icon}@2x.png"
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = tempRange,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
