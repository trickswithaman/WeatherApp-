package com.example.weatherapp.core.domain.models.forcastModel

import com.example.weatherapp.core.domain.models.Clouds
import com.example.weatherapp.core.domain.models.Main
import com.example.weatherapp.core.domain.models.Weather
import com.example.weatherapp.core.domain.models.Wind

data class Item0(
    val clouds: Clouds,
    val dt: Int,
    val dt_txt: String,
    val main: Main,
    val pop: Int,
    val sys: Sys,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)