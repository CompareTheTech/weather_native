package com.comparethetech.weather_native.data.repository

import com.comparethetech.weather_native.data.local.UpcomingDaysSharedPrefService
import com.comparethetech.weather_native.model.nextweathermodel.nextsevendays.NextSevenDaysWeather

class UpcomingDaysSharedPrefRepository(private val service: UpcomingDaysSharedPrefService) {

    fun sendData(weatherData: NextSevenDaysWeather) {
        service.sendData(weatherData)
    }

    fun getData(): NextSevenDaysWeather? {
        return service.getData()
    }

}