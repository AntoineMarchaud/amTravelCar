package com.amarchaud.travelcar.data.remote

import com.amarchaud.travelcar.data.models.ApiCar
import retrofit2.Response
import retrofit2.http.GET

interface TravelCarApi {
    @GET("/ncltg/6a74a0143a8202a5597ef3451bde0d5a/raw/8fa93591ad4c3415c9e666f888e549fb8f945eb7/tc-test-ios.json")
    suspend fun getCars(): Response<List<ApiCar>>
}