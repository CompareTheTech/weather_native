package com.comparethetech.weather_native.model.nextweathermodel.hourlyweathers

data class HourlyWeather(
    val temperature_2m: List<Double>,
    val time: List<String>,
    val weathercode: List<Int>
)