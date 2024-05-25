package com.comparethetech.weather_native.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.comparethetech.weather_native.data.remote.CurrentLocationService
import com.comparethetech.weather_native.model.CurrentLocationData

class CurrentLocationRepository(private val currentLocationService: CurrentLocationService) {

    private val approximateLocationLiveData = MutableLiveData<CurrentLocationData>()

    val locationLiveData: LiveData<CurrentLocationData>
        get() = approximateLocationLiveData

    suspend fun getLocation() {
        val result = currentLocationService.getApproximateLocation()

        if(result.body() != null) {
            approximateLocationLiveData.postValue(result.body())
        }
    }

}