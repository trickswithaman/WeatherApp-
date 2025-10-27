package com.example.weatherapp.core.domain.usecase

import com.example.weatherapp.core.domain.models.WeatherModel
import com.example.weatherapp.core.domain.models.forcastModel.ForcastWeather
import com.example.weatherapp.core.domain.repository.WeatherRepository


class WeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String): WeatherModel {
        val response = repository.getWeather(city)
        return WeatherModel(
            city = response.name,
            temperature = response.main.temp,
            description = response.weather.firstOrNull()?.description ?: "N/A",
            latitude = response.coord.lat,
            longitude = response.coord.lon,
            humidity = response.main.humidity,
            windSpeed = response.wind.speed ,
            pressure = response.main.pressure,
            icon = response.weather.firstOrNull()?.icon ?: "N/A",
            visibility = response.visibility,
            sunset = response.sys.sunset,
            sunrise = response.sys.sunrise,
            country = response.sys.country,
            feels_like = response.main.feels_like,
            temp_max = response.main.temp_max,
            temp_min = response.main.temp_min,
            timezone = response.timezone,
            grnd_level = response.main.grnd_level,
            sea_level = response.main.sea_level,
            id = response.id,
            main = response.weather.firstOrNull()?.main ?: "N/A",
            temp_kf = response.main.temp_kf,
            all = response.clouds.all,
            temp = response.main.temp
            )

    }

    suspend fun getWeatherbyLocation(lat: Double, lon: Double): WeatherModel {
        val response = repository.getWeatherByLocation(lat, lon)
        return WeatherModel(
            city = response.name,
            temperature = response.main.temp,
            description = response.weather.firstOrNull()?.description ?: "N/A",
            latitude = response.coord.lat,
            longitude = response.coord.lon,
            humidity = response.main.humidity,
            windSpeed = response.wind.speed ,
            pressure = response.main.pressure,
            icon = response.weather.firstOrNull()?.icon ?: "N/A",
            visibility = response.visibility,
            sunset = response.sys.sunset,
            sunrise = response.sys.sunrise,
            country = response.sys.country,
            feels_like = response.main.feels_like,
            temp_max = response.main.temp_max,
            temp_min = response.main.temp_min,
            timezone = response.timezone,
            grnd_level = response.main.grnd_level,
            sea_level = response.main.sea_level,
            id = response.id,
            main = response.weather.firstOrNull()?.main ?: "N/A",
            temp_kf = response.main.temp_kf,
            all = response.clouds.all,
            temp = response.main.temp

        )
    }

    suspend fun getForcastWeather(lat: Double ,lon:Double) : ForcastWeather {
        val response = repository.forcastWeather(lat, lon)
        return ForcastWeather(
            city = response.city,
            cnt = response.cnt,
            cod = response.cod,
            list = response.list,
            message = response.message

        )
    }
}