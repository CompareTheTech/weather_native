package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.UpcomingDaysSharedPrefRepository
import com.comparethetech.weather_native.model.nextweathermodel.nextsevendays.NextSevenDaysWeather

class UpcomingDaysSharedPrefViewModel(private val repository: UpcomingDaysSharedPrefRepository) :
    ViewModel() {

    fun getData(): NextSevenDaysWeather? {
        return repository.getData()
    }

    fun sendData(weatherData: NextSevenDaysWeather) {
        repository.sendData(weatherData)
    }

}