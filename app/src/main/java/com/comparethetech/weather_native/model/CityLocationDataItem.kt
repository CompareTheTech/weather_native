package com.comparethetech.weather_native.model

data class CityLocationDataItem(
    val country: String,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String,
    val state: String,
    var alreadyExist: Boolean = false
)