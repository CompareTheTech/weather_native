package com.comparethetech.weather_native.data.local

import android.content.Context
import com.comparethetech.weather_native.model.weathermodel.WeatherData
import com.comparethetech.weather_native.util.AppConstants
import com.google.gson.Gson

class WeatherSharedPrefService(context: Context) {

    private val sharedPref =
        context.getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getData(): WeatherData? {
        val data = sharedPref.getString(AppConstants.WEATHER_SHARED_PREF, null)
        return gson.fromJson(data, WeatherData::class.java)
    }

    fun sendData(weatherData: WeatherData) {
        val editor = sharedPref.edit()
        val data = gson.toJson(weatherData)
        editor.putString(AppConstants.WEATHER_SHARED_PREF, data)
        editor.apply()
    }

}