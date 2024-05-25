package com.comparethetech.weather_native.data.repository

import com.comparethetech.weather_native.data.local.LocationSharedPrefService
import com.comparethetech.weather_native.model.CurrentLocationData

class LocationSharedPrefRepository(private val locationSharedPrefService: LocationSharedPrefService) {

    fun getData(): CurrentLocationData? {
        return locationSharedPrefService.getData()
    }

    fun sendData(locationData: CurrentLocationData) {
        locationSharedPrefService.sendData(locationData)
    }

}