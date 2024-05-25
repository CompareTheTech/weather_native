package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.WeatherSharedPrefRepository
import com.comparethetech.weather_native.model.weathermodel.WeatherData

class WeatherSharedPrefViewModel(private val weatherRepository: WeatherSharedPrefRepository) :
    ViewModel() {

    fun getData(): WeatherData? {
        return weatherRepository.getData()
    }

    fun sendData(weatherData: WeatherData) {
        weatherRepository.sendData(weatherData)
    }

}