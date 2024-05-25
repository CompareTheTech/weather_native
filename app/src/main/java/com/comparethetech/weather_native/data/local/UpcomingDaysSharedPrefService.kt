package com.comparethetech.weather_native.data.local

import android.content.Context
import com.google.gson.Gson
import com.comparethetech.weather_native.model.nextweathermodel.nextsevendays.NextSevenDaysWeather
import com.comparethetech.weather_native.util.AppConstants

class UpcomingDaysSharedPrefService(context: Context) {
    private val sharedPref = context.getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun sendData(weatherData: NextSevenDaysWeather) {
        val data = gson.toJson(weatherData)
        val editor = sharedPref.edit()
        editor.putString(AppConstants.NEXT_SEVEN_WEATHER_SHARED_PREF, data)
        editor.apply()
    }

    fun getData(): NextSevenDaysWeather? {
        val data = sharedPref.getString(AppConstants.NEXT_SEVEN_WEATHER_SHARED_PREF, null)
        return gson.fromJson(data, NextSevenDaysWeather::class.java)
    }

}