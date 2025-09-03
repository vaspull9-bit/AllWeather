package com.example.allweather.data.repository

import com.example.allweather.data.api.RetrofitInstance
import com.example.allweather.data.model.WeatherData

class WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherData {
        return RetrofitInstance.api.getCurrentWeather(lat, lon)
    }
}