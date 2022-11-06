package com.michael_zhu.myruns.ui.start.map

import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.database.history.*
import com.michael_zhu.myruns.database.location.*
import com.michael_zhu.myruns.databinding.ActivityMapsDisplayBinding
import com.michael_zhu.myruns.misc.Utility
import com.michael_zhu.myruns.ui.start.InputViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.properties.Delegates

class MapsDisplayActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener,
    LocationListener {
    private var initialAltitude by Delegates.notNull<Double>()
    private lateinit var prevLocation: Location
    private lateinit var mMap: GoogleMap
    private lateinit var prevMarker: Marker
    private lateinit var binding: ActivityMapsDisplayBinding
    private lateinit var viewModel: InputViewModel
    private lateinit var statsTextView: TextView
    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var locationManager: LocationManager
    private lateinit var historyDatabase: HistoryDatabase
    private lateinit var historyDatabaseDao: HistoryDatabaseDao
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyViewModelFactory: HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    /**
     * Sets the view of the activity, initializes the map, creates the input view model,
     * adds listeners to the buttons and creates the database instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

        viewModel = ViewModelProvider(this)[InputViewModel::class.java]
        val extras = intent.extras
        if (extras != null) {
            viewModel.activityType = extras.getString("activity_type").toString()
            viewModel.inputType = extras.getString("input_type").toString()
        }

        statsTextView = findViewById(R.id.stats)


        cancelBtn = findViewById(R.id.cancel_btn)
        saveBtn = findViewById(R.id.save_btn)
        cancelBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        historyDatabase = HistoryDatabase.getInstance(this)
        historyDatabaseDao = historyDatabase.historyDatabaseDao
        historyRepository = HistoryRepository(historyDatabaseDao)
        historyViewModelFactory = HistoryViewModelFactory(historyRepository)
        historyViewModel =
            ViewModelProvider(this, historyViewModelFactory)[HistoryViewModel::class.java]
    }

    /**
     * On map ready, set the map type and create the marker for the starting location.
     * Initialize the location manager if permissions were granted.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)

        if (Utility.checkPermissions(this)) {
            initLocationManager()
        }
    }

    /**
     * Initializes the location manager and gets the initial position.
     * Start requesting the location.
     */
    private fun initLocationManager() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider: String? = locationManager.getBestProvider(criteria, true)
        if (provider != null) {
            val location = locationManager.getLastKnownLocation(provider)

            if (location != null) {
                prevLocation = location
                initialAltitude = prevLocation.altitude

                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            location.latitude, location.longitude
                        )
                    ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
                prevMarker = mMap.addMarker(
                    MarkerOptions().position(LatLng(location.latitude, location.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )!!

                if (viewModel.latLng.isNotEmpty()) {
                    recreateMap()
                }
                onLocationChanged(location)
            }
            locationManager.requestLocationUpdates(provider, 0, 0f, this)
        }
    }

    /**
     * Recreates the path from previous data.
     */
    private fun recreateMap() {
        viewModel.latLng.forEach {
            prevMarker.remove()
            polylineOptions.add(LatLng(it.latitude, it.longitude))
            mMap.addPolyline(polylineOptions)
            prevMarker = mMap.addMarker(
                MarkerOptions().position(LatLng(it.latitude, it.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )!!
        }
    }

    /**
     * On location change, create a new marker at current position and update the stats.
     */
    override fun onLocationChanged(location: Location) {
        prevMarker.remove()
        val locLatLng = LatLng(location.latitude, location.longitude)
        viewModel.latLng.add(locLatLng)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(locLatLng, 17f)
        polylineOptions.add(locLatLng)
        mMap.addPolyline(polylineOptions)
        mMap.animateCamera(cameraUpdate)
        prevMarker = mMap.addMarker(
            MarkerOptions().position(LatLng(location.latitude, location.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )!!
        updateStats(location)
    }

    /**
     * Update stats depending on the previous location and current [location]
     */
    private fun updateStats(location: Location) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val unitPref = preferences.getString("unit_preference", "km").toString()

        val type = viewModel.activityType
        val climb = roundNumber(location.altitude - initialAltitude)
        val distance = roundNumber(
            Utility.convertUnits(
                viewModel.distanceSavedAsUnit, unitPref, calculateDistance()
            )
        )
        val avgSpeed = roundNumber((distance / getDuration()))
        val currSpeed = roundNumber(
            ((Utility.convertUnits(
                viewModel.distanceSavedAsUnit,
                unitPref,
                (prevLocation.distanceTo(location) / 1000).toDouble()
            )) / getDuration()) * 3600
        )
        val calories = 1.9 * currSpeed
        viewModel.calories += calories

        val status =
            "Type: $type\nAvg speed: $avgSpeed $unitPref/h\nCur speed: $currSpeed $unitPref/h\nClimb: $climb $unitPref\nCalorie: ${
                roundNumber(
                    viewModel.calories
                )
            } cal\nDistance: $distance $unitPref"
        statsTextView.text = status
        prevLocation = location
    }

    /**
     * Rounds [num] by 3 decimal places.
     */
    private fun roundNumber(num: Double): Double {
        return (num * 1000.0).toInt() / 1000.0
    }

    /**
     * Determines what to do on click of [v].
     */
    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            R.id.cancel_btn -> {
                Toast.makeText(this, "Entry discarded!", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.save_btn -> {
                saveNewEntry()
                Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Create and save a new entry for the history database.
     */
    private fun saveNewEntry() {
        viewModel.duration = getDuration()
        calculateDistance()

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider: String? = locationManager.getBestProvider(criteria, true)
        if (provider != null) {
            val location = locationManager.getLastKnownLocation(provider)!!
            val locLatLng = LatLng(location.latitude, location.longitude)
            viewModel.latLng.add(locLatLng)
        }

        val entry = Entry(
            inputType = viewModel.inputType,
            activityType = viewModel.activityType,
            date = viewModel.dateEpoch,
            time = viewModel.timeEpoch,
            duration = viewModel.duration,
            distance = viewModel.distance,
            calories = viewModel.calories
        )
        historyViewModel.insert(entry)
        enterLocationsIntoDatabase(entry.id)
    }

    /**
     * Enters the route to the location database with reference key [entryId].
     */
    private fun enterLocationsIntoDatabase(entryId: Long) {
        val locationDatabase = LocationDatabase.getInstance(this)
        val locationDatabaseDao = locationDatabase.locationDatabaseDao
        val locationRepository = LocationRepository(locationDatabaseDao)
        val locationViewModelFactory = LocationViewModelFactory(locationRepository)
        val locationViewModel =
            ViewModelProvider(this, locationViewModelFactory)[LocationViewModel::class.java]

        viewModel.latLng.forEach {
            val locationEntry =
                LocationEntry(entryId = entryId, lat = it.latitude, lon = it.longitude)
            locationViewModel.insertLocationEntry(locationEntry)
        }
    }

    /**
     * Calculates the distance from each location ping and sum it together.
     */
    private fun calculateDistance(): Double {
        viewModel.distanceSavedAsUnit = "km"
        if (viewModel.latLng.size < 1) {
            viewModel.distance = 0.0
            return 0.0
        }

        var prevLocation = Location("prevLocation")
        prevLocation.latitude = viewModel.latLng[0].latitude
        prevLocation.longitude = viewModel.latLng[0].longitude
        viewModel.latLng.forEach {
            val nextLocation = Location("nextLocation")
            nextLocation.latitude = it.latitude
            nextLocation.longitude = it.longitude
            viewModel.distance += prevLocation.distanceTo(nextLocation) / 1000
            prevLocation = nextLocation
        }
        return viewModel.distance
    }

    /**
     * Calculates the duration of the activity from the start of the activity till now.
     */
    private fun getDuration(): Double {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val date = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute)
        val currentDatetime =
            date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + second * 1000
        val initialDatetime = viewModel.dateEpoch + viewModel.timeEpoch * 1000

        return ((currentDatetime - initialDatetime) / 1000).toDouble()
    }

    /**
     * On destroy, remove location manager updates.
     */
    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }
}