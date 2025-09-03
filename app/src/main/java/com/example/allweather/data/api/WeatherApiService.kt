package com.example.allweather.data.api

import com.example.allweather.data.model.ForecastResponse
import com.example.allweather.data.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru"
    ): WeatherData

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru",
        @Query("cnt") count: Int = 40
    ): ForecastResponse
}