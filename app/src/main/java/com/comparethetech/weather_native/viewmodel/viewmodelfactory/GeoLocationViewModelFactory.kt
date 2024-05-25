package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.GeoLocationRepository
import com.comparethetech.weather_native.viewmodel.GeoLocationViewModel

class GeoLocationViewModelFactory(private val repository: GeoLocationRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GeoLocationViewModel(repository) as T
    }
}