package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.SettingsSharedPrefRepository
import com.comparethetech.weather_native.viewmodel.SettingsSharedPrefViewModel

class SettingsSharedPrefViewModelFactory(private val repository: SettingsSharedPrefRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsSharedPrefViewModel(repository) as T
    }

}