package com.comparethetech.weather_native.data.repository

import com.comparethetech.weather_native.data.local.SettingsSharedPrefService
import com.comparethetech.weather_native.model.SettingsData

class SettingsSharedPrefRepository(private val settingsService: SettingsSharedPrefService) {

    fun getData(): SettingsData? {
        return settingsService.getData()
    }

    fun sendData(settingsData: SettingsData) {
        settingsService.setData(settingsData)
    }
}