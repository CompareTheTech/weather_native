package com.comparethetech.weather_native.ui.liveDate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.comparethetech.weather_native.model.SettingsData

object SettingsLiveData {
    private val settingsLiveData = MutableLiveData<SettingsData>()

    fun getSettingsLiveData(): LiveData<SettingsData> = settingsLiveData

    fun updateSettingsData (newSettingsData: SettingsData) {
        settingsLiveData.value = newSettingsData
    }
}
