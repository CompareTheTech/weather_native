package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.CurrentLocationRepository
import com.comparethetech.weather_native.model.CurrentLocationData

class CurrentLocationViewModel(private val currentLocationRepository: CurrentLocationRepository) : ViewModel() {
    suspend fun getLocation() {
        currentLocationRepository.getLocation()
    }

    val approximateLocationLiveData: LiveData<CurrentLocationData>
        get() = currentLocationRepository.locationLiveData
}