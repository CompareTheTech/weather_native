package com.comparethetech.weather_native.data.repository

import com.comparethetech.weather_native.data.local.WeatherSharedPrefService
import com.comparethetech.weather_native.model.weathermodel.WeatherData

class WeatherSharedPrefRepository(private val weatherService: WeatherSharedPrefService) {

    fun getData(): WeatherData? {
        return weatherService.getData()
    }

    fun sendData(weatherData: WeatherData) {
        weatherService.sendData(weatherData)
    }

}