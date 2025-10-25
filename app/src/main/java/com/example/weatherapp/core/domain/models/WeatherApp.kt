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
    val temperature: Double,
    val description: String,
    val latitude: Double,
    val longitude: Double,

)