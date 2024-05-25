package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.GeoLocationRepository
import com.comparethetech.weather_native.model.CityLocationData

class GeoLocationViewModel(private val repository: GeoLocationRepository) : ViewModel() {

    suspend fun getLocation(cityName: String, limit: Int, appID: String) {
        repository.getLocation(cityName, limit, appID)
    }

    val locationLiveData: LiveData<CityLocationData>
        get() = repository.locationLiveData

}