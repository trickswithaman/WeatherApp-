package com.example.weatherapp.weather.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.core.WeatherViewModel
import com.example.weatherapp.weather.presentaion.mainScreen.WeatherScreen
import com.example.weatherapp.weather.presentaion.splashScreen.SplashScreen


@Composable
fun Navigation(
    weatherViewModel: WeatherViewModel
) {

    val navController = rememberNavController()



//    val weatherViewModel = viewModel <WeatherViewModel>()
    NavHost(
        navController = navController, startDestination = Screen.SplashScreen.route,
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.WeatherScreen.route) {
            WeatherScreen(
                navController = navController,
                viewModel = weatherViewModel
            )
        }

    }
    
}