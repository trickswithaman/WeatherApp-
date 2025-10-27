package com.example.weatherapp.core.domain.models


//https://api.openweathermap.org/data/2.5/weather?q=Aligarh&appid=2c320404cdf5bff418d6952bd1887af4
data class WeatherApp(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)

data class WeatherModel(
    val city: String,
    val timezone: Int,
    val temperature: Double,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val humidity: Int,
    val windSpeed: Double,
    val icon: String,
    val pressure: Int,
    val visibility: Int,
    val country: String,
    val sunrise: Int,
    val sunset: Int,
    val all: Int,
    val feels_like: Double,
    val grnd_level: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_kf: Double,
    val temp_max: Double,
    val temp_min: Double,
    val id: Int,
    val main: String


)