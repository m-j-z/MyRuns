package com.michael_zhu.myruns.ui.start.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
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
import kotlin.math.abs

class MapsDisplayActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private var isBind: Boolean = false
    private var lastUpdate: Double = 0.0
    private var prevClimb: Double = 0.0

    private lateinit var appContext: Context
    private lateinit var trackingViewModel: TrackingViewModel
    private lateinit var inputViewModel: InputViewModel

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsDisplayBinding
    private lateinit var statsTextView: TextView
    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var polylineOptions: PolylineOptions

    private lateinit var historyDatabase: HistoryDatabase
    private lateinit var historyDatabaseDao: HistoryDatabaseDao
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyViewModelFactory: HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    private lateinit var locationDatabase: LocationDatabase
    private lateinit var locationDatabaseDao: LocationDatabaseDao
    private lateinit var locationRepository: LocationRepository
    private lateinit var locationViewModelFactory: LocationViewModelFactory
    private lateinit var locationViewModel: LocationViewModel

    private lateinit var prevMarker: Marker
    private lateinit var markerOptions: MarkerOptions

    private var id: Long = -1
    private lateinit var locationList: LiveData<List<LocationEntry>>
    private lateinit var entry: LiveData<Entry>
    private lateinit var lastEntry: LiveData<Long>

    /**
     * Sets the view of the activity, initializes the map, creates the input view model,
     * adds listeners to the buttons and creates the database instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputViewModel = ViewModelProvider(this)[InputViewModel::class.java]
        trackingViewModel = ViewModelProvider(this)[TrackingViewModel::class.java]

        initHistoryDatabase()
        initLocationDatabase()

        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

        statsTextView = findViewById(R.id.stats)

        val extras = intent.extras
        if (extras != null) {
            id = extras.getLong("id", -1)
            inputViewModel.inputType = extras.getString("input_type", "GPS")
            inputViewModel.activityType = extras.getString("activity_type", "Running")
        }

        if (savedInstanceState != null) {
            isBind = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }
    }

    /**
     * Initializes and starts reproducing an activity Entry.
     */
    private fun displayEntry() {
        locationList = locationViewModel.getLocations(id)
        entry = historyViewModel.getEntry(id)

        locationList.observe(this) {
            recreateMap(it)
        }

        entry.observe(this) {
            recalculateStats(it)
        }
    }

    /**
     * Recalculates the stats on completed recording of Entry.
     */
    private fun recalculateStats(entry: Entry?) {
        if (entry == null) return

        val avgSpeed = Utility.roundToDecimalPlaces(entry.distance / entry.duration, 5)
        val entryStatistics = EntryStatistics(this)
        entryStatistics.apply {
            setActivityType(entry.activityType)
            setAverageSpeed(avgSpeed)
            setClimb(entry.climb)
            setCalories(entry.calories)
            setDistance(entry.distance)
        }
        statsTextView.text = entryStatistics.getStats()
    }

    /**
     * Recreates an Entry as specified by [entries].
     */
    private fun recreateMap(entries: List<LocationEntry>) {
        if (entries.isEmpty()) return

        val firstEntry = entries.first()
        val firstLoc = LatLng(firstEntry.lat, firstEntry.lon)
        mMap.addMarker(
            MarkerOptions().position(firstLoc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )

        prevMarker = mMap.addMarker(
            MarkerOptions().position(firstLoc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )!!

        entries.forEach {
            val latLng = LatLng(it.lat, it.lon)

            prevMarker.remove()
            polylineOptions.add(latLng)
            mMap.addPolyline(polylineOptions)

            prevMarker = mMap.addMarker(
                MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )!!
        }

        val lastEntry = entries.last()
        val latLng = LatLng(lastEntry.lat, lastEntry.lon)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        mMap.animateCamera(cameraUpdate)
    }

    /**
     * Initializes and starts recording an Entry.
     */
    private fun recordEntry() {
        cancelBtn = findViewById(R.id.cancel_btn)
        saveBtn = findViewById(R.id.save_btn)

        cancelBtn.visibility = Button.VISIBLE
        saveBtn.visibility = Button.VISIBLE
        cancelBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        trackingViewModel.latLngList.observe(this) {
            if (it.isNotEmpty()) {
                val location = it.last()
                val latLng = LatLng(location.latitude, location.longitude)

                prevMarker.remove()

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                mMap.animateCamera(cameraUpdate)

                polylineOptions.add(latLng)
                mMap.addPolyline(polylineOptions)

                prevMarker = mMap.addMarker(
                    markerOptions.position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )!!

                calculateStats(it)
            }
        }

        intent.action = TrackingService.START
        startService(intent)
        bindService()
    }

    private fun calculateStats(locations: ArrayList<Location>?) {
        if (locations == null || locations.isEmpty() || locations.size < 2) return

        val prevLocation = locations[locations.size - 2]
        val currLocation = locations.last()

        val totalDuration = getDuration()
        val totalDistance = getDistance(locations)
        val avgSpeed = Utility.roundToDecimalPlaces((totalDistance / totalDuration) * 3600, 5)

        val duration = Utility.roundToDecimalPlaces(totalDuration - lastUpdate, 5)
        val distance = Utility.roundToDecimalPlaces(
            (prevLocation.distanceTo(currLocation) / 1000).toDouble(),
            5
        )
        val curSpeed = Utility.roundToDecimalPlaces((distance / duration) * 3600, 5)
        lastUpdate = duration

        inputViewModel.calories += 0.1 * curSpeed

        prevClimb += abs(currLocation.altitude - prevLocation.altitude) / 1000
        inputViewModel.climb = prevClimb

        val stats = EntryStatistics(this).apply {
            setActivityType(inputViewModel.activityType)
            setAverageSpeed(avgSpeed)
            setCurrentSpeed(curSpeed)
            setCalories(Utility.roundToDecimalPlaces(inputViewModel.calories, 5))
            setClimb(Utility.roundToDecimalPlaces(prevClimb, 5))
            setDistance(totalDistance)
        }

        statsTextView.text = stats.getStats()
    }

    /**
     * Calculates the duration from the start of the activity to now.
     * @return Double, in seconds.
     */
    private fun getDuration(): Double {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val localDate = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute)
        val now =
            localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + second * 1000

        return Utility.roundToDecimalPlaces(((now - inputViewModel.dateEpoch) / 1000).toDouble(), 5)
    }

    /**
     * Sums distance of all locations in [locations].
     * @return Double, in kilometer.
     */
    private fun getDistance(locations: ArrayList<Location>): Double {
        val prevLocation = locations.first()

        var totalDistance = 0.0
        locations.forEach {
            totalDistance += prevLocation.distanceTo(it)
        }

        return Utility.roundToDecimalPlaces(totalDistance / 1000, 5)
    }

    /**
     * Gets the starting location of the activity.
     */
    @SuppressLint("MissingPermission")
    private fun getStartingLocation(addStart: Boolean = true) {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: return

        val latLng = LatLng(location.latitude, location.longitude)

        if (addStart) {
            mMap.addMarker(
                markerOptions.position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }

        polylineOptions.add(latLng)
        mMap.addPolyline(polylineOptions)

        prevMarker = mMap.addMarker(
            markerOptions.position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )!!

        trackingViewModel.add(location)
        recordEntry()
    }

    /**
     * Initializes the History database.
     */
    private fun initHistoryDatabase() {
        historyDatabase = HistoryDatabase.getInstance(this)
        historyDatabaseDao = historyDatabase.historyDatabaseDao
        historyRepository = HistoryRepository(historyDatabaseDao)
        historyViewModelFactory = HistoryViewModelFactory(historyRepository)
        historyViewModel =
            ViewModelProvider(this, historyViewModelFactory)[HistoryViewModel::class.java]
    }

    /**
     * Initializes the Location database.
     */
    private fun initLocationDatabase() {
        locationDatabase = LocationDatabase.getInstance(this)
        locationDatabaseDao = locationDatabase.locationDatabaseDao
        locationRepository = LocationRepository(locationDatabaseDao)
        locationViewModelFactory = LocationViewModelFactory(locationRepository)
        locationViewModel =
            ViewModelProvider(this, locationViewModelFactory)[LocationViewModel::class.java]
    }

    /**
     * On map ready, set the map type and create the marker for the starting location.
     * Determines if it is to display an Entry or to create a new Entry.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        intent = Intent(this, TrackingService::class.java)

        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)

        if (id != -1L) {
            displayEntry()
        } else {
            appContext = applicationContext
            markerOptions = MarkerOptions()

            val latLngList = trackingViewModel.getAll()
            if (latLngList.isNotEmpty()) {
                recreateLiveList(latLngList)
            } else {
                getStartingLocation()
            }
        }
    }

    /**
     * Recreates the map live as given by [latLngList].
     */
    private fun recreateLiveList(latLngList: ArrayList<Location>) {
        if (latLngList.isEmpty()) return

        val firstEntry = latLngList.first()
        val firstLoc = LatLng(firstEntry.latitude, firstEntry.longitude)
        mMap.addMarker(
            MarkerOptions().position(firstLoc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )

        prevMarker = mMap.addMarker(
            MarkerOptions().position(firstLoc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )!!

        latLngList.forEach {
            val latLng = LatLng(it.latitude, it.longitude)

            prevMarker.remove()
            polylineOptions.add(latLng)
            mMap.addPolyline(polylineOptions)

            prevMarker = mMap.addMarker(
                MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )!!
        }
        recordEntry()
    }

    /**
     * Determines what to do on click of [v].
     */
    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            R.id.cancel_btn -> {
                unbindService()
                stopService(intent)
                trackingViewModel.latLngList.removeObservers(this)
                Toast.makeText(this, "Entry discarded!", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.save_btn -> {
                unbindService()
                stopService(intent)
                trackingViewModel.latLngList.removeObservers(this)
                saveEntry()
                Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun saveEntry() {
        inputViewModel.duration = getDuration()
        inputViewModel.distance = getDistance(trackingViewModel.getAll())

        val entry = Entry(
            inputType = inputViewModel.inputType,
            activityType = inputViewModel.activityType,
            date = inputViewModel.dateEpoch,
            time = inputViewModel.timeEpoch,
            duration = inputViewModel.duration,
            distance = inputViewModel.distance,
            calories = inputViewModel.calories,
            climb = inputViewModel.climb
        )

        historyViewModel.insert(entry)
        lastEntry = historyViewModel.getLastEntryId()
        lastEntry.observe(this) {
            insertIntoLocationDatabase(it)
        }
    }

    private fun insertIntoLocationDatabase(entryId: Long) {
        val locationList = trackingViewModel.getAll()

        locationList.forEach {
            val locationEntry = LocationEntry(
                entryId = entryId,
                lat = it.latitude,
                lon = it.longitude
            )
            locationViewModel.insertLocationEntry(locationEntry)
        }
        lastEntry.removeObservers(this)
    }

    /**
     * Bind service if it is not already bound.
     */
    private fun bindService() {
        if (!isBind) {
            appContext.bindService(intent, trackingViewModel, Context.BIND_AUTO_CREATE)
            isBind = true
        }
    }

    /**
     * Unbind service if it is bound.
     */
    private fun unbindService() {
        if (isBind) {
            appContext.unbindService(trackingViewModel)
            isBind = false
        }
    }

    /**
     * Add 'DELETE' to menu if entry is to be recreated.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (id != -1L) {
            menuInflater.inflate(R.menu.display_entry_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * On DELETE pressed.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                locationList.removeObservers(this)
                locationViewModel.deleteLocations(id)
                entry.removeObservers(this)
                historyViewModel.delete(id)
                Toast.makeText(this, "Removed entry with ID $id.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Save instance of variable isBind.
     */
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(BIND_STATUS_KEY, isBind)
    }

    companion object {
        const val BIND_STATUS_KEY = "bind_status_key"
    }
}