package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.LocationSharedPrefRepository
import com.comparethetech.weather_native.model.CurrentLocationData

class LocationSharedPrefViewModel(private val locationSharedPrefRepository: LocationSharedPrefRepository): ViewModel() {

    fun getData(): CurrentLocationData? {
        return locationSharedPrefRepository.getData()
    }

    fun sendData(locationData: CurrentLocationData) {
        locationSharedPrefRepository.sendData(locationData)
    }

}