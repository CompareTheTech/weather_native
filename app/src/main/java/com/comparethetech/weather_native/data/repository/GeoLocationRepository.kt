package com.comparethetech.weather_native.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.comparethetech.weather_native.data.remote.GeoLocationService
import com.comparethetech.weather_native.model.CityLocationData

class GeoLocationRepository(private val geoLocationService: GeoLocationService) {

    private val geoLocationLiveData = MutableLiveData<CityLocationData>()

    val locationLiveData: LiveData<CityLocationData>
        get() = geoLocationLiveData

    suspend fun getLocation(q: String, limit: Int, appId: String) {
        val result = geoLocationService.getLocation(q, limit, appId)

        if (result.body() != null) {
            geoLocationLiveData.postValue(result.body())
        }
    }

}