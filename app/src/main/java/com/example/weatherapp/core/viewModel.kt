package com.example.weatherapp.core

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.core.domain.models.WeatherModel
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
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather by location", e)
            }
        }
    }
}