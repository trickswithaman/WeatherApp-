package com.example.weatherapp.core.domain.usecase

import com.example.weatherapp.core.domain.models.WeatherModel
import com.example.weatherapp.core.domain.repository.WeatherRepository


class WeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String): WeatherModel {
        val response = repository.getWeather(city)
        return WeatherModel(
            city = response.name,
            temperature = response.main.temp,
            description = response.weather.firstOrNull()?.description ?: "N/A",
            latitude = response.coord.lat,
            longitude = response.coord.lon
        )
    }

    suspend fun getWeatherbyLocation(lat: Double, lon: Double): WeatherModel {
        val response = repository.getWeatherByLocation(lat, lon)
        return WeatherModel(
            city = response.name,
            temperature = response.main.temp,
            description = response.weather.firstOrNull()?.description ?: "N/A",
            latitude = response.coord.lat,
            longitude = response.coord.lon
        )
    }
}