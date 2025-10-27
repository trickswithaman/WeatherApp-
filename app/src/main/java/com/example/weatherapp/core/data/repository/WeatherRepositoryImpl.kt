package com.example.weatherapp.core.data.repository

import com.example.weatherapp.core.domain.models.WeatherApp
import com.example.weatherapp.core.domain.models.forcastModel.ForcastWeather
import com.example.weatherapp.core.domain.repository.WeatherRepository
import com.example.weatherapp.data.ApiInterface

class WeatherRepositoryImpl (
    private val api: ApiInterface
) : WeatherRepository(api) {


   override suspend fun getWeather(city: String): WeatherApp =
       api.getWeatherData(
            city = city,
            appid = "2c320404cdf5bff418d6952bd1887af4", // Replace with your OpenWeatherMap key
            units = "metric"
       )


    override suspend fun getWeatherByLocation(lat: Double, lon: Double): WeatherApp =
        api.getWeatherByCoordinates(
            lat = lat,
            lon = lon,
            appid = "2c320404cdf5bff418d6952bd1887af4", // Replace with your OpenWeatherMap key
            units = "metric"
        )

    override suspend fun forcastWeather(lat: Double,lon: Double): ForcastWeather =
        api.getForecastByCoordinates(
            lat = lat,
            lon = lon,
            appid = "2c320404cdf5bff418d6952bd1887af4", // Replace with your OpenWeatherMap key
            units = "metric"
        )
}