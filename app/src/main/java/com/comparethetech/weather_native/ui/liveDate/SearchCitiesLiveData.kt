package com.comparethetech.weather_native.ui.liveDate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.comparethetech.weather_native.model.CityLocationDataItem

object SearchCitiesLiveData {
    private val citiesLiveData = MutableLiveData<CityLocationDataItem>()

    fun getCitiesLiveData(): LiveData<CityLocationDataItem> = citiesLiveData

    fun updateCitiesLiveData(newCitiesData: CityLocationDataItem) {
        citiesLiveData.value = newCitiesData
    }
}