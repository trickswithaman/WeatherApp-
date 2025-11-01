package com.example.weatherapp.core


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.core.domain.models.WeatherModel
import com.example.weatherapp.core.domain.models.forcastModel.City
import com.example.weatherapp.core.domain.models.forcastModel.Item0
import com.example.weatherapp.core.domain.usecase.WeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherUseCase: WeatherUseCase) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherModel?>(null)
    val weatherState: StateFlow<WeatherModel?> = _weatherState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _getWeatherbyLocation = MutableLiveData<WeatherModel>()
    val getWeatherbyLocation: LiveData<WeatherModel> = _getWeatherbyLocation

    fun getWeather(city: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                val result = weatherUseCase(city)
                _weatherState.value = result
                getForecast(result.latitude, result.longitude)
            } catch (e: retrofit2.HttpException) {
                _errorMessage.value = "City not found. Please check the name."
                _weatherState.value = null
            } catch (e: java.net.UnknownHostException) {
                _errorMessage.value = "No internet connection. Try again later."
                _weatherState.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong."
                _weatherState.value = null
            }
        }
    }

    fun getWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val result = weatherUseCase.getWeatherbyLocation(lat, lon)
                _getWeatherbyLocation.value = result
                getForecast(lat, lon)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather by location", e)
            }
        }
    }

    private val _next24Hours = MutableStateFlow<List<Item0>>(emptyList())
    val next24Hours: StateFlow<List<Item0>> = _next24Hours

    private val _next7Days = MutableStateFlow<List<Item0>>(emptyList())
    val next7Days: StateFlow<List<Item0>> = _next7Days

    private val _city = MutableStateFlow<City?>(null)
    val city: StateFlow<City?> = _city

    fun getForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = weatherUseCase.getForcastWeather(lat, lon)
                _city.value = response.city

                _next24Hours.value = getNext24HourForecast(response.list)
                _next7Days.value = getNext7DaysForecast(response.list)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /* private fun getNext24HourForecast(allForecasts: List<Item0>): List<Item0> {
         val now = System.currentTimeMillis() / 1000
         val next24h = now + 24 * 60 * 60
         return allForecasts.filter { it.dt in now..next24h }
     }*/
    private fun getNext24HourForecast(allForecasts: List<Item0>): List<Item0> {
        val now = System.currentTimeMillis() / 1000
        val next24h = now + 24 * 60 * 60
        return allForecasts.filter { it.dt in now..next24h }
    }

    private fun getNext7DaysForecast(allForecasts: List<Item0>): List<Item0> {
        // Filter for one forecast per day (e.g., midday)
        return allForecasts.filter { it.dt_txt.contains("12:00:00") }.take(7)
    }


    /*private fun getNext7DaysForecast(allForecasts: List<Item0>): List<Item0> {
        return allForecasts
            .filter { it.dt_txt.contains("12:00:00") } // Pick only mid-day forecast
            .take(7)
    }*/
}