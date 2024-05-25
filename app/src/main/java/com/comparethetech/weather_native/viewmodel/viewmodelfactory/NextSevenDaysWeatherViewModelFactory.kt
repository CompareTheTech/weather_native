package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.NextSevenDaysWeatherRepository
import com.comparethetech.weather_native.viewmodel.NextSevenDaysWeatherViewModel

class NextSevenDaysWeatherViewModelFactory(private val repository: NextSevenDaysWeatherRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NextSevenDaysWeatherViewModel(repository) as T
    }

}