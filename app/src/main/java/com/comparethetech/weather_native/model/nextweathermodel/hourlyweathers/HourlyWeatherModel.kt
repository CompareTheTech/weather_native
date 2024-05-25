package com.comparethetech.weather_native.model.nextweathermodel.hourlyweathers

data class HourlyWeatherModel(
    val elevation: Double,
    val generationtime_ms: Double,
    val hourly: HourlyWeather,
    val hourly_units: HourlyWeatherUnits,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val timezone_abbreviation: String,
    val utc_offset_seconds: Int
)