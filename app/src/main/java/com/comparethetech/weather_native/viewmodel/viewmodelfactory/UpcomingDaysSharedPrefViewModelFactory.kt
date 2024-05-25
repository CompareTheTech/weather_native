package com.comparethetech.weather_native.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.data.repository.UpcomingDaysSharedPrefRepository
import com.comparethetech.weather_native.viewmodel.UpcomingDaysSharedPrefViewModel

class UpcomingDaysSharedPrefViewModelFactory(private val repository: UpcomingDaysSharedPrefRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UpcomingDaysSharedPrefViewModel(repository) as T
    }

}