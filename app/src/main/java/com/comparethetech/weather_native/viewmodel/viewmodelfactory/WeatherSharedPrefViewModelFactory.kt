package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.WeatherSharedPrefRepository
import com.comparethetech.weather_native.viewmodel.WeatherSharedPrefViewModel

class WeatherSharedPrefViewModelFactory(private val repository: WeatherSharedPrefRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherSharedPrefViewModel(repository) as T
    }

}