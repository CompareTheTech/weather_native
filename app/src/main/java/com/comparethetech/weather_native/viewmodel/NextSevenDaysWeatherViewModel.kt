package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.NextSevenDaysWeatherRepository
import com.comparethetech.weather_native.model.nextweathermodel.nextsevendays.NextSevenDaysWeatherModel

class NextSevenDaysWeatherViewModel(private val repository: NextSevenDaysWeatherRepository) :
    ViewModel() {

    suspend fun getWeather(
        latitude: String,
        longitude: String,
        dailyParameters: List<String>,
        timezone: String,
        forecastDays: Int
    ) {
        repository.getWeather(latitude, longitude, dailyParameters, timezone, forecastDays)
    }

    val weatherLiveData: LiveData<NextSevenDaysWeatherModel>
        get() = repository.weatherLiveData

}