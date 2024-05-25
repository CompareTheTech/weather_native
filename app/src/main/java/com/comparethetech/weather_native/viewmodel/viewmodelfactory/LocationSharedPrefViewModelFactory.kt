package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.LocationSharedPrefRepository
import com.comparethetech.weather_native.viewmodel.LocationSharedPrefViewModel

class LocationSharedPrefViewModelFactory(private val repository: LocationSharedPrefRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationSharedPrefViewModel(repository) as T
    }

}