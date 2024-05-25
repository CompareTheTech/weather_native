package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.HourlyWeatherRepository
import com.comparethetech.weather_native.viewmodel.HourlyWeatherViewModel

class HourlyWeatherViewModelFactory(private val repository: HourlyWeatherRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HourlyWeatherViewModel(repository) as T
    }

}