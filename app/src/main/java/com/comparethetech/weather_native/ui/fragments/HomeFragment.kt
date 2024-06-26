package com.comparethetech.weather_native.ui.fragments

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.comparethetech.weather_native.BuildConfig
import com.comparethetech.weather_native.R
import com.comparethetech.weather_native.adapter.HorizontalWeatherAdapter
import com.comparethetech.weather_native.data.RetrofitHelper
import com.comparethetech.weather_native.data.local.LocationSharedPrefService
import com.comparethetech.weather_native.data.local.SettingsSharedPrefService
import com.comparethetech.weather_native.data.local.WeatherSharedPrefService
import com.comparethetech.weather_native.data.remote.CurrentLocationService
import com.comparethetech.weather_native.data.remote.HourlyWeatherService
import com.comparethetech.weather_native.data.remote.WeatherService
import com.comparethetech.weather_native.data.repository.CurrentLocationRepository
import com.comparethetech.weather_native.data.repository.HourlyWeatherRepository
import com.comparethetech.weather_native.data.repository.LocationSharedPrefRepository
import com.comparethetech.weather_native.data.repository.SettingsSharedPrefRepository
import com.comparethetech.weather_native.data.repository.WeatherRepository
import com.comparethetech.weather_native.data.repository.WeatherSharedPrefRepository
import com.comparethetech.weather_native.databinding.FragmentHomeBinding
import com.comparethetech.weather_native.model.CityLocationDataItem
import com.comparethetech.weather_native.model.CurrentLocationData
import com.comparethetech.weather_native.model.SettingsData
import com.comparethetech.weather_native.model.WeatherHourlyCardData
import com.comparethetech.weather_native.model.nextweathermodel.hourlyweathers.HourlyWeatherModel
import com.comparethetech.weather_native.model.weathermodel.WeatherData
import com.comparethetech.weather_native.model.weathermodel.WeatherType
import com.comparethetech.weather_native.ui.base.SearchActivity
import com.comparethetech.weather_native.ui.base.SettingActivity
import com.comparethetech.weather_native.ui.liveDate.SearchCitiesLiveData
import com.comparethetech.weather_native.ui.liveDate.SettingsLiveData
import com.comparethetech.weather_native.util.AppConstants
import com.comparethetech.weather_native.util.CountryNameByCode
import com.comparethetech.weather_native.util.InternetConnection
import com.comparethetech.weather_native.util.NavigateFragmentUtil
import com.comparethetech.weather_native.util.TimeUtil
import com.comparethetech.weather_native.util.WeatherCodeToIcon
import com.comparethetech.weather_native.viewmodel.CurrentLocationViewModel
import com.comparethetech.weather_native.viewmodel.HourlyWeatherViewModel
import com.comparethetech.weather_native.viewmodel.LocationSharedPrefViewModel
import com.comparethetech.weather_native.viewmodel.SettingsSharedPrefViewModel
import com.comparethetech.weather_native.viewmodel.WeatherSharedPrefViewModel
import com.comparethetech.weather_native.viewmodel.WeatherViewModel
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.CurrentLocationViewModelFactory
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.HourlyWeatherViewModelFactory
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.LocationSharedPrefViewModelFactory
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.SettingsSharedPrefViewModelFactory
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.WeatherSharedPrefViewModelFactory
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.WeatherViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var weatherCardData: ArrayList<WeatherHourlyCardData>

    private lateinit var settingsData: SettingsData
    private lateinit var hourlyWeatherData: HourlyWeatherModel

    private lateinit var settingsDataObserver: Observer<SettingsData>
    private lateinit var citiesDataObserver: Observer<CityLocationDataItem>

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var hourlyWeatherViewModel: HourlyWeatherViewModel
    private lateinit var currentLocationViewModel: CurrentLocationViewModel
    private lateinit var locationSharedPrefViewModel: LocationSharedPrefViewModel
    private lateinit var settingSharedPrefViewModel: SettingsSharedPrefViewModel
    private lateinit var weatherSharedPrefViewModel: WeatherSharedPrefViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setting data in UI
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        binding.date.text = dateFormat.format(currentDate)

        binding.hourlyShimmerLayout.startShimmer()

        //First get location then set the location data in local and in UI after that get weather data from API
        initCurrentLocationThing()
        initLocationSharedPrefThing()
        initWeatherSharedPrefThing()
        initSettingsSharedPrefThing()
        initWeatherAPIThing()
        initHourlyWeatherAPIThing()

        //Getting and setting data from LocationSharedPrefViewModel to UI
        var locationSharedPrefData = locationSharedPrefViewModel.getData()
        locationSharedPrefData?.let { setLocationDataToUI(it) }

        //Getting Data from Settings SharedPref
        settingsData = settingSharedPrefViewModel.getData() ?: SettingsData()
        setSettingDataToUI(settingsData)


        //Observing the livedata from WeatherAPI
        weatherViewModel.weatherLiveData.observe(requireActivity()) {
            if (it != null) {
                setWeatherDataToUI(it)
                weatherSharedPrefViewModel.sendData(it)
            }
        }

        //Getting and setting data from WeatherSharedPref to UI
        val weatherData = weatherSharedPrefViewModel.getData()
        weatherData?.let { setWeatherDataToUI(it) }

        //Observing the changes in settings
        settingsDataObserver = Observer {
            settingsData = it
            setSettingDataToUI(it)
            changeWeatherToToday()
        }

        SettingsLiveData.getSettingsLiveData().observe(requireActivity(), settingsDataObserver)

        //Observing LiveData from CurrentLocationViewModel
        currentLocationViewModel.approximateLocationLiveData.observe(requireActivity()) {
            if (it != null) {
                if (locationSharedPrefData == null || locationSharedPrefData!!.loc != it.loc && locationSharedPrefData!!.ip.isNotEmpty()) {
                    locationSharedPrefViewModel.sendData(it)
                    locationSharedPrefData = it
                    setLocationDataToUI(it)
                    callingWeatherAPI(it)
                    callingHourlyWeatherAPI(it)
                }
            }
        }

        citiesDataObserver = Observer {
            val state = it.state
            val data =
                CurrentLocationData(it.name, it.country, "", "${it.lat},${it.lon}", "", state, "")
            locationSharedPrefViewModel.sendData(data)
            locationSharedPrefData = locationSharedPrefViewModel.getData()
            setLocationDataToUI(data)
            callingWeatherAPI(data)
            callingHourlyWeatherAPI(data)
        }
        SearchCitiesLiveData.getCitiesLiveData().observe(requireActivity(), citiesDataObserver)

        //Observing the livedata from HourlyWeatherAPI
        hourlyWeatherViewModel.weatherLiveData.observe(requireActivity()) {
            if (it != null) {
                hourlyWeatherData = it
                changeWeatherToToday()
            }
        }

        callingWeatherAPI(locationSharedPrefData)
        callingHourlyWeatherAPI(locationSharedPrefData)

        binding.settings.setOnClickListener { moveToSettings() }
        binding.search.setOnClickListener { moveToSearch() }
        binding.next7days.setOnClickListener {
            locationSharedPrefData?.loc?.let { it1 ->
                navToUpcomingDaysFrag(
                    it1
                )
            }
        }
        binding.today.setOnClickListener { changeWeatherToToday() }
        binding.tomorrow.setOnClickListener { changeWeatherToTomorrow() }
    }

    private fun callingHourlyWeatherAPI(locationData: CurrentLocationData?) {
        val loc = locationData?.loc?.split(",")
        if (loc != null) {
            val lat = loc[0]
            val lon = loc[1]
            lifecycleScope.launch {
                if (InternetConnection.isNetworkAvailable(requireActivity())) {
                    lifecycleScope.launch {
                        hourlyWeatherViewModel.getWeather(
                            lat,
                            lon,
                            AppConstants.HOURLY_WEATHER_QUERY,
                            AppConstants.HOURLY_WEATHER_CODE,
                            AppConstants.HOURLY_WEATHER_DAYS_LIMIT,
                            TimeZone.getDefault().id
                        )
                    }
                } else {
                    val snackBar = Snackbar.make(
                        requireView(),
                        "No internet connection.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackBar.setAction(R.string.try_again) {
                        lifecycleScope.launch {
                            if (InternetConnection.isNetworkAvailable(requireActivity())) {
                                lifecycleScope.launch {
                                    hourlyWeatherViewModel.getWeather(
                                        lat,
                                        lon,
                                        AppConstants.HOURLY_WEATHER_QUERY,
                                        AppConstants.HOURLY_WEATHER_CODE,
                                        AppConstants.HOURLY_WEATHER_DAYS_LIMIT,
                                        TimeZone.getDefault().id
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
    }

    private fun callingWeatherAPI(locationData: CurrentLocationData?) {
        val loc = locationData?.loc?.split(",")
        if (loc != null) {
            val lat = loc[0]
            val lon = loc[1]

            lifecycleScope.launch {
                if (InternetConnection.isNetworkAvailable(requireActivity())) {
                    lifecycleScope.launch {
                        weatherViewModel.getWeather(lat, lon, BuildConfig.apiKey)
                    }
                } else {
                    val snackBar = Snackbar.make(
                        requireView(),
                        "No internet connection.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackBar.setAction(R.string.try_again) {
                        lifecycleScope.launch {
                            if (InternetConnection.isNetworkAvailable(requireActivity())) {
                                lifecycleScope.launch {
                                    weatherViewModel.getWeather(
                                        lat,
                                        lon,
                                        BuildConfig.apiKey
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
    }

    private fun setLocationDataToUI(currentLocation: CurrentLocationData) {
        binding.cityName.text = currentLocation.city
        binding.countryName.text =
            CountryNameByCode.getCountryNameByCode(requireActivity(), currentLocation.country)
    }

    private fun setWeatherDataToUI(weatherData: WeatherData) {
        var temp = weatherData.main.temp

        if (settingsData.temperatureUnit == resources.getString(R.string.fahrenheit)) {
            temp = (temp - 273.15) * 1.8 + 32
        } else {
            temp -= 273.15
        }

        if (settingsData.atmosphericPressureUnit == resources.getString(R.string.atm)) {
            val pressure = weatherData.main.pressure.toDouble() / 1013.25
            binding.atmosphericPressureValue.text = String.format("%.4f", pressure)
        } else {
            binding.atmosphericPressureValue.text =
                String.format("%.0f", weatherData.main.pressure.toDouble())
        }

        if (settingsData.windSpeedUnit == resources.getString(R.string.miles_per_hour)) {
            val speed = weatherData.wind.speed * 2.237
            binding.windValue.text = String.format("%.2f", speed)
        } else if (settingsData.windSpeedUnit == resources.getString(R.string.kilometers_per_hour)) {
            val speed = weatherData.wind.speed * 3.6
            binding.windValue.text = String.format("%.2f", speed)
        } else {
            binding.windValue.text = weatherData.wind.speed.toString()
        }

        setWeatherIcon(weatherData.weather.first())

        binding.weatherNumericValue.text = String.format("%.0f", temp)
        binding.humidityValue.text = weatherData.main.humidity.toString()
        binding.weatherType.text = weatherData.weather.first().main
    }

    private fun setHourlyWeatherDataToUI(
        hourlyWeatherData: HourlyWeatherModel,
        isToday: Boolean
    ): Int {
        val hourlyWeather = hourlyWeatherData.hourly

        val time = hourlyWeather.time
        val weather = hourlyWeather.temperature_2m
        val weatherCode = hourlyWeather.weathercode

        weatherCardData = ArrayList()

        var position = 0

        if (isToday) {
            for (i in 0..23) {
                var currTime = TimeUtil.extractTimeFromString(time[i])
                val currWeather = weather[i]
                val weatherIcon = WeatherCodeToIcon.getWeatherIcon(weatherCode[i])
                val isCurrentTime = isCurrentLocalTime(currTime)

                if (isCurrentTime) {
                    currTime = "now"
                    position = i
                }

                val currTimeData = WeatherHourlyCardData(
                    currTime,
                    weatherIcon,
                    String.format("%.0f", this.getTempByUnit(currWeather)),
                    isCurrentTime,
                    settingsData.temperatureUnit
                )

                weatherCardData.add(currTimeData)
            }

            return position
        } else {
            for (i in 24..47) {
                val currTime = TimeUtil.extractTimeFromString(time[i])
                val currWeather = weather[i]
                val weatherIcon = WeatherCodeToIcon.getWeatherIcon(weatherCode[i])

                val currTimeData = WeatherHourlyCardData(
                    currTime,
                    weatherIcon,
                    String.format("%.0f", this.getTempByUnit(currWeather)),
                    false,
                    settingsData.temperatureUnit
                )

                weatherCardData.add(currTimeData)
            }

            return 0
        }
    }

    private fun getTempByUnit(temp: Double): Double {
        if (settingsData.temperatureUnit == resources.getString(R.string.celsius)) {
            return temp
        }
        return (temp * 9 / 5) + 32
    }


    private fun setSettingDataToUI(settingsData: SettingsData) {
        var temp = binding.weatherNumericValue.text.toString().toDouble()

        if (binding.weatherUnit.text.toString() != settingsData.temperatureUnit) {
            if (settingsData.temperatureUnit == resources.getString(R.string.celsius)) {
                binding.weatherUnit.text = resources.getString(R.string.celsius)
                temp = (temp - 32) * 5 / 9
            } else {
                binding.weatherUnit.text = resources.getString(R.string.fahrenheit)
                temp = (temp * 9 / 5) + 32
            }
            binding.weatherNumericValue.text = String.format("%.0f", temp)
        }

        if (binding.atmosphericPressureUnit.text.toString() != settingsData.atmosphericPressureUnit) {
            if (settingsData.atmosphericPressureUnit == resources.getString(R.string.atm)) {
                val pressure = binding.atmosphericPressureValue.text.toString().toDouble() / 1013.25
                binding.atmosphericPressureValue.text = String.format("%.4f", pressure)
            } else {
                val pressure = binding.atmosphericPressureValue.text.toString().toDouble() * 1013.25
                binding.atmosphericPressureValue.text = String.format("%.0f", pressure)
            }
            binding.atmosphericPressureUnit.text = settingsData.atmosphericPressureUnit
        }

        if (binding.windUnit.text.toString() != settingsData.windSpeedUnit) {
            val prevWindUnit = binding.windUnit.text.toString()
            val prevWindSpeed = binding.windValue.text.toString().toDouble()
            val newWindSpeed: Double
            if (settingsData.windSpeedUnit == resources.getString(R.string.miles_per_hour)) {
                newWindSpeed =
                    if (prevWindUnit == resources.getString(R.string.kilometers_per_hour)) {
                        prevWindSpeed * 0.621371
                    } else {
                        prevWindSpeed * 2.23694
                    }
            } else if (settingsData.windSpeedUnit == resources.getString(R.string.kilometers_per_hour)) {
                newWindSpeed = if (prevWindUnit == resources.getString(R.string.miles_per_hour)) {
                    prevWindSpeed * 1.60934
                } else {
                    prevWindSpeed * 3.6
                }
            } else {
                newWindSpeed =
                    if (prevWindUnit == resources.getString(R.string.kilometers_per_hour)) {
                        prevWindSpeed / 3.6
                    } else {
                        prevWindSpeed / 2.23694
                    }
            }

            binding.windValue.text = String.format("%.2f", newWindSpeed)
        }

        binding.windUnit.text = settingsData.windSpeedUnit
    }

    private fun setWeatherIcon(weatherType: WeatherType) {
        if (weatherType.id in 200..232) { //Thunderstorm
            binding.weatherIcon.setImageResource(R.drawable.icon_weather_thunderstorm_cloud)
        } else if (weatherType.id in 300..321) { //Drizzle
            binding.weatherIcon.setImageResource(R.drawable.icon_weather_rain_cloud)
        } else if (weatherType.id in 500..531) { //Rain
            binding.weatherIcon.setImageResource(R.drawable.icon_weather_sun_rain_cloud)
        } else if (weatherType.id in 701..781) { //Atmosphere
            binding.weatherIcon.setImageResource(R.drawable.icon_weather_cloud_sun)
        } else if (weatherType.id in 600..622) { //Snow
            binding.weatherIcon.setImageResource(R.drawable.icon_weather_snow_cloud)
        } else if (weatherType.id == 800) { //Clear
            binding.weatherIcon.setImageResource(R.drawable.icon_weather_sun)
        } else if (weatherType.id in 801..804) { //Cloud
            binding.weatherIcon.setImageResource(R.drawable.icon_weather_cloud)
        } else {
            binding.weatherIcon.setImageResource(R.drawable.icon_weather_sun)
        }
    }

    @SuppressWarnings("deprecation")
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun isCurrentLocalTime(timeString: String): Boolean {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = Calendar.getInstance()
        val parsedTime = sdf.parse(timeString)

        if (parsedTime != null) {
            val parsedCalendar = Calendar.getInstance()
            parsedCalendar.time = parsedTime

            return currentTime.get(Calendar.HOUR_OF_DAY) == parsedCalendar.get(Calendar.HOUR_OF_DAY)
        }

        return false
    }

    private fun changeWeatherToToday() {
        val todayTypeface: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Inter-Bold.ttf")
        val tomorrowTypeface: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Inter-Regular.ttf")

        binding.today.typeface = todayTypeface
        binding.tomorrow.typeface = tomorrowTypeface

        binding.today.setTextColor(requireActivity().getColor(R.color.textColor))
        binding.tomorrow.setTextColor(Color.parseColor("#D6996B"))

        changeIndicatorDotPosition(R.id.today)

        //Adding Today's Weather Data in array and adapter
        val position = setHourlyWeatherDataToUI(hourlyWeatherData, true)
        setHorizontalWeatherViewAdapter()

        binding.hourlyShimmerLayoutContainer.visibility = View.GONE
        binding.hourlyShimmerLayout.stopShimmer()
        binding.smallWeatherCardView.visibility = View.VISIBLE

        binding.smallWeatherCardView.layoutManager?.scrollToPosition(position)
    }

    private fun changeWeatherToTomorrow() {
        if (::hourlyWeatherData.isInitialized) {
            val tomorrowTypeface: Typeface =
                Typeface.createFromAsset(requireActivity().assets, "fonts/Inter-Bold.ttf")
            val todayTypeface: Typeface =
                Typeface.createFromAsset(requireActivity().assets, "fonts/Inter-Regular.ttf")

            binding.tomorrow.typeface = tomorrowTypeface
            binding.today.typeface = todayTypeface

            binding.tomorrow.setTextColor(requireActivity().getColor(R.color.textColor))
            binding.today.setTextColor(Color.parseColor("#D6996B"))

            changeIndicatorDotPosition(R.id.tomorrow)

            //Adding Tomorrow's Weather Data in array and adapter
            setHourlyWeatherDataToUI(hourlyWeatherData, false)

            binding.hourlyShimmerLayoutContainer.visibility = View.GONE
            binding.hourlyShimmerLayout.stopShimmer()
            binding.smallWeatherCardView.visibility = View.VISIBLE

            setHorizontalWeatherViewAdapter()
        }
    }

    private fun changeIndicatorDotPosition(viewId: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.parentLayout)
        constraintSet.connect(R.id.indicator, ConstraintSet.START, viewId, ConstraintSet.START, 0)
        constraintSet.connect(R.id.indicator, ConstraintSet.END, viewId, ConstraintSet.END, 0)
        constraintSet.applyTo(binding.parentLayout)
    }

    private fun setHorizontalWeatherViewAdapter() {
        binding.smallWeatherCardView.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)

        val adapter = HorizontalWeatherAdapter(weatherCardData)
        binding.smallWeatherCardView.adapter = adapter
    }

    private fun moveToSettings() {
        val intent = Intent(requireActivity(), SettingActivity::class.java)
        startActivity(intent)
    }

    private fun moveToSearch() {
        val intent = Intent(requireActivity(), SearchActivity::class.java)
        startActivity(intent)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        } else {
            requireActivity().overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                R.anim.fadein,
                R.anim.fadeout
            )
        }
    }

    private fun navToUpcomingDaysFrag(cord: String) {
        val loc = cord.split(",")
        val action =
            HomeFragmentDirections.navHomeFragToUpcomingDaysFrag(loc[0].trim(), loc[1].trim())
        val navHelper = NavigateFragmentUtil()
        navHelper.navigateToFragmentWithAction(requireView(), action)
    }

    private fun initWeatherAPIThing() {
        val weatherService =
            RetrofitHelper.getInstance(AppConstants.OpenWeatherMap_API_BASE_URL).create(
                WeatherService::class.java
            )
        val weatherRepository = WeatherRepository(weatherService)

        weatherViewModel = ViewModelProvider(
            requireActivity(),
            WeatherViewModelFactory(weatherRepository)
        )[WeatherViewModel::class.java]
    }

    private fun initHourlyWeatherAPIThing() {
        val hourlyWeatherService =
            RetrofitHelper.getInstance(AppConstants.OpenMeteo_API_BASE_URL).create(
                HourlyWeatherService::class.java
            )
        val hourlyWeatherRepository = HourlyWeatherRepository(hourlyWeatherService)

        hourlyWeatherViewModel = ViewModelProvider(
            requireActivity(),
            HourlyWeatherViewModelFactory(hourlyWeatherRepository)
        )[HourlyWeatherViewModel::class.java]
    }

    private fun initCurrentLocationThing() {
        //Initialization of CurrentLocationRepository and CurrentLocationServices
        val currentLocationService =
            RetrofitHelper.getInstance(AppConstants.CURRENT_LOCATION_API_BASE_URL)
                .create(CurrentLocationService::class.java)
        val currentLocationRepository = CurrentLocationRepository(currentLocationService)

        //Initialization of CurrentLocationViewModel
        currentLocationViewModel = ViewModelProvider(
            requireActivity(),
            CurrentLocationViewModelFactory(currentLocationRepository)
        )[CurrentLocationViewModel::class.java]

        //Calling API to get current location
        lifecycleScope.launch {
            if (InternetConnection.isNetworkAvailable(requireActivity())) {
                lifecycleScope.launch {
                    currentLocationViewModel.getLocation()
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
                                currentLocationViewModel.getLocation()
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

    private fun initLocationSharedPrefThing() {
        val locationSharedPrefService = LocationSharedPrefService(requireActivity())
        val locationSharedPrefRepository = LocationSharedPrefRepository(locationSharedPrefService)

        locationSharedPrefViewModel = ViewModelProvider(
            requireActivity(),
            LocationSharedPrefViewModelFactory(locationSharedPrefRepository)
        )[LocationSharedPrefViewModel::class.java]
    }

    private fun initSettingsSharedPrefThing() {
        val settingsSharedPrefService = SettingsSharedPrefService(requireActivity())
        val settingsSharedPrefRepository = SettingsSharedPrefRepository(settingsSharedPrefService)

        settingSharedPrefViewModel = ViewModelProvider(
            requireActivity(),
            SettingsSharedPrefViewModelFactory(settingsSharedPrefRepository)
        )[SettingsSharedPrefViewModel::class.java]
    }

    private fun initWeatherSharedPrefThing() {
        val weatherSharedPrefService = WeatherSharedPrefService(requireActivity())
        val weatherSharedPrefRepository = WeatherSharedPrefRepository(weatherSharedPrefService)

        weatherSharedPrefViewModel = ViewModelProvider(
            requireActivity(),
            WeatherSharedPrefViewModelFactory(weatherSharedPrefRepository)
        )[WeatherSharedPrefViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()

        SettingsLiveData.getSettingsLiveData().removeObserver(settingsDataObserver)
        SearchCitiesLiveData.getCitiesLiveData().removeObserver(citiesDataObserver)
    }

}