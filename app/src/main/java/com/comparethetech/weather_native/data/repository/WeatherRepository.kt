package com.comparethetech.weather_native.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.comparethetech.weather_native.data.remote.WeatherService
import com.comparethetech.weather_native.model.weathermodel.WeatherData

class WeatherRepository(private val weatherService: WeatherService) {

    private val weatherLiveData = MutableLiveData<WeatherData>()

    val weatherData: LiveData<WeatherData>
        get() = weatherLiveData

    suspend fun getWeather(lat: String, lon: String, appId: String) {
        val result = weatherService.getWeather(lat, lon, appId)

        if(result.body() != null) {
            weatherLiveData.postValue(result.body())
        }
    }

}