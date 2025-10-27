package com.example.weatherapp.core.domain.repository

import com.example.weatherapp.core.domain.models.WeatherApp
import com.example.weatherapp.core.domain.models.forcastModel.ForcastWeather
import com.example.weatherapp.data.ApiInterface

open class WeatherRepository(
    private val api : ApiInterface
) {
    open suspend fun getWeather(city: String): WeatherApp = api.getWeatherData(city, "2c320404cdf5bff418d6952bd1887af4", "metric")

    open suspend fun getWeatherByLocation(lat: Double, lon: Double): WeatherApp = api.getWeatherByCoordinates(lat = lat, lon = lon, appid =  "2c320404cdf5bff418d6952bd1887af4", units = "metric")

    open suspend fun forcastWeather(lat: Double, lon: Double): ForcastWeather = api.getForecastByCoordinates(lat = lat, lon = lon, appid =  "2c320404cdf5bff418d6952bd1887af4", units = "metric")
}