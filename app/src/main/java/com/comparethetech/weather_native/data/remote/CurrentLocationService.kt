package com.comparethetech.weather_native.data.remote

import com.comparethetech.weather_native.model.CurrentLocationData
import retrofit2.Response
import retrofit2.http.GET

interface CurrentLocationService {

    @GET("/json")
    suspend fun getApproximateLocation(): Response<CurrentLocationData>

}