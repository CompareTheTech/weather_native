package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.WeatherRepository
import com.comparethetech.weather_native.model.weathermodel.WeatherData

class WeatherViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    suspend fun getWeather(lat: String, lon: String, appId: String) {
        weatherRepository.getWeather(lat, lon, appId)
    }

    val weatherLiveData: LiveData<WeatherData>
        get() = weatherRepository.weatherData

}