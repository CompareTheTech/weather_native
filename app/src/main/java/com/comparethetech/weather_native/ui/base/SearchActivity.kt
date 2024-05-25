package com.comparethetech.weather_native.ui.base

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.comparethetech.weather_native.BuildConfig
import com.comparethetech.weather_native.R
import com.comparethetech.weather_native.adapter.SearchCitiesAdapter
import com.comparethetech.weather_native.data.RetrofitHelper
import com.comparethetech.weather_native.data.local.LocationSharedPrefService
import com.comparethetech.weather_native.data.remote.GeoLocationService
import com.comparethetech.weather_native.data.repository.GeoLocationRepository
import com.comparethetech.weather_native.data.repository.LocationSharedPrefRepository
import com.comparethetech.weather_native.databinding.ActivitySearchBinding
import com.comparethetech.weather_native.model.CityLocationData
import com.comparethetech.weather_native.util.AppConstants
import com.comparethetech.weather_native.util.InternetConnection
import com.comparethetech.weather_native.viewmodel.GeoLocationViewModel
import com.comparethetech.weather_native.viewmodel.LocationSharedPrefViewModel
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.GeoLocationViewModelFactory
import com.comparethetech.weather_native.viewmodel.viewmodelfactory.LocationSharedPrefViewModelFactory
import java.text.Normalizer

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var geoLocationViewModel: GeoLocationViewModel

    private lateinit var citiesList: CityLocationData

    private lateinit var locationSharedPrefViewModel: LocationSharedPrefViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Changing status bar color to black
        setStatusBarColor()

        initLocationSharedPrefThing()
        initGeoLocationAPIThing()

        //Getting location data from sharedPref
        val locationSharedPrefData = locationSharedPrefViewModel.getData()

        //Observing LiveData from GeoLocationViewModel
        geoLocationViewModel.locationLiveData.observe(this@SearchActivity) {
            if(!it.isEmpty()) {
                citiesList = it

                for(city in citiesList) {
                    if(locationSharedPrefData != null &&
                        areEqualIgnoringDiacritics(city.name, locationSharedPrefData.city) &&
                        areEqualIgnoringDiacritics(city.country, locationSharedPrefData.country)
                    ) {
                        if(city.state.isNullOrEmpty() || areEqualIgnoringDiacritics(city.state, locationSharedPrefData.region)) {
                            city.alreadyExist = true
                            break
                        }
                    }
                }

                setSearchCitiesAdapter()
                binding.searchPlaceholderTV.visibility = View.GONE
                binding.citiesRecyclerView.visibility = View.VISIBLE
            } else {
                binding.searchPlaceholderTV.text = getString(R.string.no_results)
                binding.searchPlaceholderTV.visibility = View.VISIBLE
                binding.citiesRecyclerView.visibility = View.GONE
            }
        }

        //On Press Back Navigation Button
        onBackPressedDispatcher.addCallback(this) {
            finish()
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            } else {
                overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, R.anim.fadein, R.anim.fadeout)
            }
        }

        //On Press Back ImageButton
        binding.cancelSearch.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            } else {
                overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, R.anim.fadein, R.anim.fadeout)
            }
        }

        //SearchView Text Listener
        binding.searchCity.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.citiesRecyclerView.visibility = View.GONE

                if(query != null) {
                    binding.searchPlaceholderTV.visibility = View.VISIBLE

                    if(query.trim().isEmpty()) {
                        binding.searchPlaceholderTV.visibility = View.GONE
                    } else if(query.trim().length <= 2) {
                        binding.searchPlaceholderTV.text = getString(R.string.search_your_city)
                    } else {
                        binding.searchPlaceholderTV.text = getString(R.string.searching)
                        lifecycleScope.launch {
                            if (InternetConnection.isNetworkAvailable(this@SearchActivity)) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    geoLocationViewModel.getLocation(
                                        query,
                                        AppConstants.CITY_LIMITS,
                                        BuildConfig.apiKey
                                    )
                                }
                            } else {
                                val snackBar =
                                    Snackbar.make(
                                        binding.root,
                                        "No internet connection.",
                                        Snackbar.LENGTH_INDEFINITE
                                    )
                                snackBar.setAction(R.string.try_again) {
                                    lifecycleScope.launch {
                                        if (InternetConnection.isNetworkAvailable(this@SearchActivity)) {
                                            lifecycleScope.launch {
                                                geoLocationViewModel.getLocation(
                                                    query,
                                                    AppConstants.CITY_LIMITS,
                                                    BuildConfig.apiKey
                                                )
                                            }
                                        } else {
                                            Toast.makeText(
                                                this@SearchActivity,
                                                "No internet connection. Please try later.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }.show()
                            }
                        }
                    }
                } else {
                    binding.searchPlaceholderTV.visibility = View.GONE
                }

                return false
            }

            override fun onQueryTextChange(newCity: String?): Boolean {
                binding.citiesRecyclerView.visibility = View.GONE

                if(newCity != null) {
                    binding.searchPlaceholderTV.visibility = View.VISIBLE

                    if(newCity.trim().isEmpty()) {
                        binding.searchPlaceholderTV.visibility = View.GONE
                    } else if(newCity.trim().length <= 2) {
                        binding.searchPlaceholderTV.text = getString(R.string.search_your_city)
                    } else {
                        binding.searchPlaceholderTV.text = getString(R.string.click_search)
                    }
                } else {
                    binding.citiesRecyclerView.visibility = View.GONE
                }

                return false
            }
        })
    }

    private fun removeDiacritics(input: String): String {
        if(input.isEmpty()) return ""
        val normalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(normalizedString, "")
    }

    private fun areEqualIgnoringDiacritics(str1: String, str2: String): Boolean {
        val normalizedStr1 = removeDiacritics(str1)
        val normalizedStr2 = removeDiacritics(str2)
        return normalizedStr1.equals(normalizedStr2, ignoreCase = true)
    }

    private fun initLocationSharedPrefThing() {
        val locationSharedPrefService = LocationSharedPrefService(this)
        val locationSharedPrefRepository = LocationSharedPrefRepository(locationSharedPrefService)

        locationSharedPrefViewModel = ViewModelProvider(this@SearchActivity, LocationSharedPrefViewModelFactory(locationSharedPrefRepository))[LocationSharedPrefViewModel::class.java]
    }

    private fun initGeoLocationAPIThing() {
        //Initialization of GeoLocationRepository and GeoLocationServices
        val geoLocationService = RetrofitHelper.getInstance(AppConstants.OpenWeatherMap_API_BASE_URL).create(GeoLocationService::class.java)
        val geoLocationRepository = GeoLocationRepository(geoLocationService)

        //Initialization of GeoLocationViewModel
        geoLocationViewModel = ViewModelProvider(this@SearchActivity, GeoLocationViewModelFactory(geoLocationRepository))[GeoLocationViewModel::class.java]
    }

    private fun setStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun setSearchCitiesAdapter() {
        binding.citiesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val adapter = SearchCitiesAdapter(citiesList)
        binding.citiesRecyclerView.adapter = adapter
    }

}