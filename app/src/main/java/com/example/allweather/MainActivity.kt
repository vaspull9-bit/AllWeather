package com.example.allweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.allweather.databinding.ActivityMainBinding
import com.example.allweather.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                viewModel.loadWeather()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewModel.loadWeather()
            }
            else -> {
                Toast.makeText(this, "Разрешение на местоположение не предоставлено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Установите Toolbar как ActionBar
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        setupObservers()
        checkLocationPermissions()
    }
    private fun setupObservers() {
        viewModel.weatherState.observe(this) { state ->
            when (state) {
                is WeatherViewModel.WeatherState.Loading -> showLoading()
                is WeatherViewModel.WeatherState.Success -> showWeather(state.weather)
                is WeatherViewModel.WeatherState.Error -> showError(state.message)
            }
        }
    }

    private fun checkLocationPermissions() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted && coarseLocationGranted) {
            viewModel.loadWeather()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showLoading() {
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.errorTextView.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
        binding.currentWeatherCard.visibility = View.GONE
    }

    private fun showWeather(weather: com.example.allweather.data.model.WeatherData) {
        binding.loadingProgressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
        binding.currentWeatherCard.visibility = View.VISIBLE

        binding.cityNameTextView.text = weather.cityName
        binding.temperatureTextView.text = "${weather.main.temperature.toInt()}°C"
        binding.weatherDescriptionTextView.text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""
        binding.feelsLikeTextView.text = "Ощущается как: ${weather.main.feelsLike.toInt()}°C"
        binding.pressureTextView.text = "${weather.main.pressure} мм рт.ст."
        binding.humidityTextView.text = "${weather.main.humidity}%"
        binding.windTextView.text = "${weather.wind.speed} м/с, ${getWindDirection(weather.wind.degree)}"

        val iconUrl = "https://openweathermap.org/img/wn/${weather.weather.firstOrNull()?.icon}@2x.png"
        binding.weatherIconImageView.load(iconUrl)
    }

    private fun showError(message: String) {
        binding.loadingProgressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.retryButton.visibility = View.VISIBLE
        binding.currentWeatherCard.visibility = View.GONE

        binding.errorTextView.text = message
        binding.retryButton.setOnClickListener {
            checkLocationPermissions()
        }
    }

    private fun getWindDirection(degrees: Int): String {
        return when {
            degrees >= 337.5 || degrees < 22.5 -> "С"
            degrees >= 22.5 && degrees < 67.5 -> "СВ"
            degrees >= 67.5 && degrees < 112.5 -> "В"
            degrees >= 112.5 && degrees < 157.5 -> "ЮВ"
            degrees >= 157.5 && degrees < 202.5 -> "Ю"
            degrees >= 202.5 && degrees < 247.5 -> "ЮЗ"
            degrees >= 247.5 && degrees < 292.5 -> "З"
            else -> "СЗ"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                checkLocationPermissions()
                true
            }
            R.id.action_info -> {
                showInfoDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle("Информация о приложении")
            .setMessage("VShargin (C) 2025\nvaspull9@gmail.com\nAllWeather, v2.0\n\nВсе о погоде")
            .setPositiveButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}