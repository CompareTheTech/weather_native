package com.comparethetech.weather_native.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.comparethetech.weather_native.R
import com.comparethetech.weather_native.data.RetrofitHelper
import com.comparethetech.weather_native.data.local.SettingsSharedPrefService
import com.comparethetech.weather_native.data.local.UpcomingDaysSharedPrefService
import com.comparethetech.weather_native.data.remote.NextSevenDaysWeatherService
import com.comparethetech.weather_native.data.repository.NextSevenDaysWeatherRepository
import com.comparethetech.weather_native.data.repository.SettingsSharedPrefRepository
import com.comparethetech.weather_native.data.repository.UpcomingDaysSharedPrefRepository
import com.comparethetech.weather_native.databinding.FragmentUpcomingDaysBinding
import com.comparethetech.weather_native.model.SettingsData
import com.comparethetech.weather_native.model.nextweathermodel.nextsevendays.NextSevenDaysWeather
import com.comparethetech.weather_native.ui.liveDate.SettingsLiveData
import com.comparethetech.weather_native.util.AppConstants
import com.comparethetech.weather_native.util.InternetConnection
import com.comparethetech.weather_native.util.TimeUtil
import com.comparethetech.weather_native.util.WeatherCodeToIcon
import com.comparethetech.weather_native.viewmodel.NextSevenDaysWeatherViewModel
import com.comparethetech.weather_native.viewmodel.SettingsSharedPrefViewModel
import com.comparethetech.weather_native.viewmodel.UpcomingDaysSharedPrefViewModel
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.NextSevenDaysWeatherViewModelFactory
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.SettingsSharedPrefViewModelFactory
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.UpcomingDaysSharedPrefViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.TimeZone

class UpcomingDaysFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingDaysBinding

    private lateinit var next7DaysWeatherViewModel: NextSevenDaysWeatherViewModel
    private lateinit var upcomingDaysSharedPrefViewModel: UpcomingDaysSharedPrefViewModel

    private lateinit var settingsData: SettingsData
    private lateinit var settingsDataObserver: Observer<SettingsData>
    private lateinit var settingSharedPrefViewModel: SettingsSharedPrefViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpcomingDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSettingsSharedPrefThing()

        settingsData = settingSharedPrefViewModel.getData() ?: SettingsData()

        settingsDataObserver = Observer {
            settingsData = it
        }

        SettingsLiveData.getSettingsLiveData().observe(requireActivity(), settingsDataObserver)


        val args: UpcomingDaysFragmentArgs by navArgs()
        val lat = args.lat
        val lon = args.lon

        initNext7DaysWeather()
        initUpcomingDaysSharedPref()

        callingNext7DaysWeatherAPI(lat, lon)

        val weatherData = getUpcomingWeatherSharedPrefData()

        if (weatherData != null) {
            setNext7DaysWeatherToUI(weatherData)
        }

        next7DaysWeatherViewModel.weatherLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                setNext7DaysWeatherToUI(it.daily)
                sendDataToSharedPref(it.daily)
            }
        }

        binding.back.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun getUpcomingWeatherSharedPrefData(): NextSevenDaysWeather? {
        return upcomingDaysSharedPrefViewModel.getData()
    }

    private fun initUpcomingDaysSharedPref() {
        val service = UpcomingDaysSharedPrefService(requireActivity())
        val repository = UpcomingDaysSharedPrefRepository(service)

        upcomingDaysSharedPrefViewModel = ViewModelProvider(
            requireActivity(),
            UpcomingDaysSharedPrefViewModelFactory(repository)
        )[UpcomingDaysSharedPrefViewModel::class.java]
    }

    private fun initNext7DaysWeather() {
        val service = RetrofitHelper.getInstance(AppConstants.OpenMeteo_API_BASE_URL).create(
            NextSevenDaysWeatherService::class.java
        )
        val repository = NextSevenDaysWeatherRepository(service)

        next7DaysWeatherViewModel = ViewModelProvider(
            requireActivity(),
            NextSevenDaysWeatherViewModelFactory(repository)
        )[NextSevenDaysWeatherViewModel::class.java]
    }

    private fun callingNext7DaysWeatherAPI(lat: String, lon: String) {
        val dailyParameters =
            listOf("weathercode", "temperature_2m_max", "temperature_2m_min", "sunrise", "sunset")

        lifecycleScope.launch {
            if (InternetConnection.isNetworkAvailable(requireActivity())) {
                lifecycleScope.launch {
                    next7DaysWeatherViewModel.getWeather(
                        lat,
                        lon,
                        dailyParameters,
                        TimeZone.getDefault().id,
                        AppConstants.NEXT7DAYS_WEATHER_DAYS_LIMIT
                    )
                }
            } else {
                val snackBar =
                    Snackbar.make(
                        requireView(),
                        "No internet connection.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                snackBar.setAction(R.string.try_again) {
                    lifecycleScope.launch {
                        if (InternetConnection.isNetworkAvailable(requireActivity())) {
                            lifecycleScope.launch {
                                next7DaysWeatherViewModel.getWeather(
                                    lat,
                                    lon,
                                    dailyParameters,
                                    TimeZone.getDefault().id,
                                    AppConstants.NEXT7DAYS_WEATHER_DAYS_LIMIT
                                )
                            }
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "No internet connection. Please try later.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.show()
            }
        }
    }


    private fun initSettingsSharedPrefThing() {
        val settingsSharedPrefService = SettingsSharedPrefService(requireActivity())
        val settingsSharedPrefRepository = SettingsSharedPrefRepository(settingsSharedPrefService)

        settingSharedPrefViewModel = ViewModelProvider(
            requireActivity(),
            SettingsSharedPrefViewModelFactory(settingsSharedPrefRepository)
        )[SettingsSharedPrefViewModel::class.java]
    }

    private fun sendDataToSharedPref(weatherData: NextSevenDaysWeather) {
        upcomingDaysSharedPrefViewModel.sendData(weatherData)
    }

    private fun setNext7DaysWeatherToUI(weather: NextSevenDaysWeather) {
        val tomorrowTemp =
            "${getTempByUnit(weather.temperature_2m_min[1])} / ${getTempByUnit(weather.temperature_2m_max[1])}"
        binding.tomorrowWeatherValue.text = tomorrowTemp
        val tomorrowWeatherIcon = WeatherCodeToIcon.getWeatherIcon(weather.weathercode[1])
        binding.tomorrowWeatherIcon.setImageResource(tomorrowWeatherIcon)
        binding.tomorrowSunrise.text = TimeUtil.extractTimeFromString(weather.sunrise[1])
        binding.tomorrowSunset.text = TimeUtil.extractTimeFromString(weather.sunset[1])


        val day2Temp =
            "${getTempByUnit(weather.temperature_2m_min[2])} / ${getTempByUnit(weather.temperature_2m_max[2])}"
        binding.weather1Value.text = day2Temp
        val weather2Icon = WeatherCodeToIcon.getWeatherIcon(weather.weathercode[2])
        binding.weather1Icon.setImageResource(weather2Icon)
        binding.weather1Day.text = TimeUtil.convertDateStringToDay(weather.time[2])

        val day3Temp =
            "${getTempByUnit(weather.temperature_2m_min[3])} / ${getTempByUnit(weather.temperature_2m_max[3])}"
        binding.weather2Value.text = day3Temp
        val weather3Icon = WeatherCodeToIcon.getWeatherIcon(weather.weathercode[3])
        binding.weather2Icon.setImageResource(weather3Icon)
        binding.weather2Day.text = TimeUtil.convertDateStringToDay(weather.time[3])

        val day4Temp =
            "${getTempByUnit(weather.temperature_2m_min[4])} / ${getTempByUnit(weather.temperature_2m_max[4])}"
        binding.weather3Value.text = day4Temp
        val weather4Icon = WeatherCodeToIcon.getWeatherIcon(weather.weathercode[4])
        binding.weather3Icon.setImageResource(weather4Icon)
        binding.weather3Day.text = TimeUtil.convertDateStringToDay(weather.time[4])

        val day5Temp =
            "${getTempByUnit(weather.temperature_2m_min[5])} / ${getTempByUnit(weather.temperature_2m_max[5])}"
        binding.weather4Value.text = day5Temp
        val weather5Icon = WeatherCodeToIcon.getWeatherIcon(weather.weathercode[5])
        binding.weather4Icon.setImageResource(weather5Icon)
        binding.weather4Day.text = TimeUtil.convertDateStringToDay(weather.time[5])

        val day6Temp =
            "${getTempByUnit(weather.temperature_2m_min[6])} / ${getTempByUnit(weather.temperature_2m_max[6])}"
        binding.weather5Value.text = day6Temp
        val weather6Icon = WeatherCodeToIcon.getWeatherIcon(weather.weathercode[6])
        binding.weather5Icon.setImageResource(weather6Icon)
        binding.weather5Day.text = TimeUtil.convertDateStringToDay(weather.time[6])

        val day7Temp =
            "${getTempByUnit(weather.temperature_2m_min[7])} / ${getTempByUnit(weather.temperature_2m_max[7])}"
        binding.weather6Value.text = day7Temp
        val weather7Icon = WeatherCodeToIcon.getWeatherIcon(weather.weathercode[7])
        binding.weather6Icon.setImageResource(weather7Icon)
        binding.weather6Day.text = TimeUtil.convertDateStringToDay(weather.time[7])
    }

    private fun getTempByUnit(temp: Double): String {
        if (settingsData.temperatureUnit == resources.getString(R.string.celsius)) {
            return temp.toInt().toString() + resources.getString(R.string.celsius)
        }
        return ((temp * 9 / 5) + 32).toInt()
            .toString() + resources.getString(R.string.fahrenheit)
    }

    override fun onDestroy() {
        super.onDestroy()

        SettingsLiveData.getSettingsLiveData().removeObserver(settingsDataObserver)
    }
}