package com.comparethetech.weather_native.viewmodel

import androidx.lifecycle.ViewModel
import com.comparethetech.weather_native.data.repository.SettingsSharedPrefRepository
import com.comparethetech.weather_native.model.SettingsData

class SettingsSharedPrefViewModel(private val settingsRepository: SettingsSharedPrefRepository) :
    ViewModel() {

    fun getData(): SettingsData? {
        return settingsRepository.getData()
    }

    fun sendData(settingsData: SettingsData) {
        settingsRepository.sendData(settingsData)
    }
}