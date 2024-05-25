package com.comparethetech.weather_native.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.comparethetech.weather_native.R
import com.comparethetech.weather_native.data.local.SettingsSharedPrefService
import com.comparethetech.weather_native.data.repository.SettingsSharedPrefRepository
import com.comparethetech.weather_native.databinding.ActivitySettingBinding
import com.comparethetech.weather_native.model.SettingsData
import com.comparethetech.weather_native.ui.liveDate.SettingsLiveData
import com.comparethetech.weather_native.viewmodel.SettingsSharedPrefViewModel
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.SettingsSharedPrefViewModelFactory

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    private lateinit var settingsSharedPrefViewModel: SettingsSharedPrefViewModel
    private var settingsData: SettingsData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()

        initSettingsSharedPrefThing()
        //Getting data from Settings ViewModel
        settingsData = settingsSharedPrefViewModel.getData()
        if (settingsData != null) {
            setDataToUI()
        } else {
            settingsData = SettingsData()
        }

        binding.temperatureUnit.setOnClickListener { showTemperatureUnitPopup(binding.temperatureSpinner) }
        binding.windSpeedUnit.setOnClickListener { showWindSpeedUnitPopup(binding.windSpeedSpinner) }
        binding.atmosphericPressureUnit.setOnClickListener {
            showAtmosphericPressureUnitPopup(
                binding.atmosphericPressureSpinner
            )
        }

        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initSettingsSharedPrefThing() {
        val settingsSharedPrefService = SettingsSharedPrefService(this)
        val settingsSharedPrefRepository = SettingsSharedPrefRepository(settingsSharedPrefService)

        settingsSharedPrefViewModel = ViewModelProvider(
            this@SettingActivity,
            SettingsSharedPrefViewModelFactory(settingsSharedPrefRepository)
        )[SettingsSharedPrefViewModel::class.java]
    }

    private fun setDataToUI() {
        binding.temperatureSpinner.text = settingsData?.temperatureUnit
        binding.windSpeedSpinner.text = settingsData?.windSpeedUnit
        binding.atmosphericPressureSpinner.text = settingsData?.atmosphericPressureUnit
    }

    private fun sendDataToSharedPref() {
        SettingsLiveData.updateSettingsData(settingsData!!)
        settingsSharedPrefViewModel.sendData(settingsData!!)
    }

    private fun showTemperatureUnitPopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.temperature_unit_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.celsius -> {
                    binding.temperatureSpinner.text = item.title

                    settingsData?.temperatureUnit = item.title.toString()
                    sendDataToSharedPref()
                }

                R.id.fahrenheit -> {
                    binding.temperatureSpinner.text = item.title

                    settingsData?.temperatureUnit = item.title.toString()
                    sendDataToSharedPref()
                }
            }

            true
        }

        popup.show()
    }

    private fun showWindSpeedUnitPopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.windsped_unit_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.kilometers -> {
                    binding.windSpeedSpinner.text = item.title

                    settingsData?.windSpeedUnit = item.title.toString()
                    sendDataToSharedPref()
                }

                R.id.meters -> {
                    binding.windSpeedSpinner.text = item.title

                    settingsData?.windSpeedUnit = item.title.toString()
                    sendDataToSharedPref()
                }

                R.id.miles -> {
                    binding.windSpeedSpinner.text = item.title

                    settingsData?.windSpeedUnit = item.title.toString()
                    sendDataToSharedPref()
                }
            }

            true
        }

        popup.show()
    }

    private fun showAtmosphericPressureUnitPopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.atmospheric_pressure_unit_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.hpa -> {
                    binding.atmosphericPressureSpinner.text = item.title

                    settingsData?.atmosphericPressureUnit = item.title.toString()
                    sendDataToSharedPref()
                }

                R.id.atm -> {
                    binding.atmosphericPressureSpinner.text = item.title

                    settingsData?.atmosphericPressureUnit = item.title.toString()
                    sendDataToSharedPref()
                }
            }

            true
        }

        popup.show()
    }

    private fun openURL(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun setStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

}