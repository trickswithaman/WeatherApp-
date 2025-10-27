package com.example.weatherapp.weather.navigation

import okhttp3.Route

sealed class Screen (val route: String) {
    data object SplashScreen: Screen("Splash_screen")
    data object WeatherScreen: Screen("weather_screen")
}