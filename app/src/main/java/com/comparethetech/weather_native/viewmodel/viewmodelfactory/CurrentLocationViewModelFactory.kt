package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.CurrentLocationRepository
import com.comparethetech.weather_native.viewmodel.CurrentLocationViewModel

class CurrentLocationViewModelFactory(private val repository: CurrentLocationRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CurrentLocationViewModel(repository) as T
    }

}