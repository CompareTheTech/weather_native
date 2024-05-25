package com.comparethetech.weather_native.data.local

import android.content.Context
import com.google.gson.Gson
import com.comparethetech.weather_native.model.CurrentLocationData
import com.comparethetech.weather_native.util.AppConstants

class LocationSharedPrefService(context: Context) {
    private val sharedPref = context.getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun sendData(locationData: CurrentLocationData) {
        val data = gson.toJson(locationData)
        val editor = sharedPref.edit()
        editor.putString(AppConstants.LOCATION_SHARED_PREF, data)
        editor.apply()
    }

    fun getData(): CurrentLocationData? {
        val data = sharedPref.getString(AppConstants.LOCATION_SHARED_PREF, null)
        return gson.fromJson(data, CurrentLocationData::class.java)
    }
}