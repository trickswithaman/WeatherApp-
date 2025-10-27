package com.example.weatherapp.core.domain.models.forcastModel

data class ForcastWeather(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Item0>,
    val message: Int
)