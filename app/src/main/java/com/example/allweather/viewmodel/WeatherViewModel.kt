package com.example.allweather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allweather.data.model.WeatherData
import com.example.allweather.data.repository.WeatherRepository
import com.example.allweather.service.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository()
    private val locationService = LocationService(application)

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    init {
        _weatherState.value = WeatherState.Loading
    }

    fun loadWeather() {
        _weatherState.value = WeatherState.Loading
        viewModelScope.launch {
            try {
                val location = getLocationSuspend()
                if (location != null) {
                    val weather = weatherRepository.getCurrentWeather(
                        location.latitude,
                        location.longitude
                    )
                    _weatherState.postValue(WeatherState.Success(weather))
                } else {
                    _weatherState.postValue(WeatherState.Error("Не удалось получить местоположение"))
                }
            } catch (e: Exception) {
                _weatherState.postValue(WeatherState.Error(e.message ?: "Ошибка загрузки погоды"))
            }
        }
    }

    private suspend fun getLocationSuspend(): android.location.Location? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                locationService.getCurrentLocation { location ->
                    continuation.resume(location)
                }
            }
        }
    }

    sealed class WeatherState {
        object Loading : WeatherState()
        data class Success(val weather: WeatherData) : WeatherState()
        data class Error(val message: String) : WeatherState()
    }
}