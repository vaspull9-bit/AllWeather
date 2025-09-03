package com.example.allweather.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.allweather.data.model.WeatherData

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherData: WeatherData)

    @Query("SELECT * FROM weather_data WHERE id = :id")
    suspend fun getWeather(id: String = "current"): WeatherData?

    @Query("SELECT COUNT(*) FROM weather_data WHERE id = :id")
    suspend fun hasWeatherData(id: String = "current"): Int

    @Query("DELETE FROM weather_data WHERE id = :id")
    suspend fun deleteWeather(id: String = "current")
}