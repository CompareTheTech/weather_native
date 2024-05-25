package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.WeatherRepository
import com.comparethetech.weather_native.viewmodel.WeatherViewModel

class WeatherViewModelFactory(private val repository: WeatherRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(repository) as T
    }

}