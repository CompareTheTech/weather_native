package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.HourlyWeatherRepository
import com.comparethetech.weather_native.model.nextweathermodel.hourlyweathers.HourlyWeatherModel

class HourlyWeatherViewModel(private val repository: HourlyWeatherRepository) : ViewModel() {

    suspend fun getWeather(
        lat: String,
        lon: String,
        hourly: String,
        weatherCode: String,
        forecastDays: Int,
        timezone: String
    ) {
        repository.getWeather(lat, lon, hourly, weatherCode, forecastDays, timezone)
    }

    val weatherLiveData: LiveData<HourlyWeatherModel>
        get() = repository.weatherLiveData

}