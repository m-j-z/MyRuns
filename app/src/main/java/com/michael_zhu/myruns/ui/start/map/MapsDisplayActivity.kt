package com.michael_zhu.myruns.ui.start.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
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
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.math.abs

class MapsDisplayActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener, SensorEventListener {
    private var isBind: Boolean = false
    private var inserted: Boolean = false
    private var i: Long = 1

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

    private lateinit var locationList: LiveData<List<LocationEntry>>
    private lateinit var entry: LiveData<Entry>
    private lateinit var lastEntry: Flow<Long>

    // Sensors
    private lateinit var sensorManager: SensorManager
    private var x: Double = 0.0
    private var y: Double = 0.0
    private var z: Double = 0.0

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
            inputViewModel.id = extras.getLong("id", -1)
            inputViewModel.inputType = extras.getString("input_type", "GPS")
            inputViewModel.activityType = extras.getString("activity_type", "Running")
        }

        if (savedInstanceState != null) {
            isBind = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }

        if (inputViewModel.id == -1L && inputViewModel.inputType == "Automatic") {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    /**
     * Initializes and starts reproducing an activity Entry.
     */
    private fun displayEntry() {
        locationList = locationViewModel.getLocations(inputViewModel.id)
        entry = historyViewModel.getEntry(inputViewModel.id)

        locationList.observe(this) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.last().lat, it.last().lon),
                    17f
                )
            )
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

        val avgSpeed = entry.distance / entry.duration
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
            if (it.size == 1) {
                val location = it.first()
                val latLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(
                    markerOptions.position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )

                polylineOptions.add(latLng)
                mMap.addPolyline(polylineOptions)

                prevMarker = mMap.addMarker(
                    markerOptions.position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )!!

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                mMap.animateCamera(cameraUpdate)
            }

            if (it.isNotEmpty() && it.size > 1) {
                val location = it.last()
                val latLng = LatLng(location.latitude, location.longitude)

                prevMarker.remove()

                if (!mMap.projection.visibleRegion.latLngBounds.contains(latLng)) {
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                    mMap.animateCamera(cameraUpdate)
                }

                polylineOptions.add(latLng)
                mMap.addPolyline(polylineOptions)

                prevMarker = mMap.addMarker(
                    markerOptions.position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )!!

                calculateStats(it)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startForegroundService(intent)
            bindService()
        }
    }

    /**
     * Calculates stats dependent on the previous location and current location.
     */
    private fun calculateStats(locations: ArrayList<Location>?) {
        if (locations == null || locations.isEmpty() || locations.size < 2) return

        val prevLocation = locations[locations.size - 2]
        val currLocation = locations.last()

        val totalDuration = getDuration()
        val totalDistance = getDistance(locations)
        val avgSpeed = totalDistance / totalDuration * 3600


        var duration = totalDuration - inputViewModel.duration
        if (duration == 0.0) {
            duration = 1.0
        }
        val distance = prevLocation.distanceTo(currLocation) / 1000
        val curSpeed = distance / duration * 3600

        inputViewModel.duration = totalDuration
        inputViewModel.distance = totalDistance
        inputViewModel.calories += 0.05 * curSpeed
        inputViewModel.climb += abs(currLocation.altitude - prevLocation.altitude) / 1000

        val stats = EntryStatistics(this).apply {
            setActivityType(inputViewModel.activityType)
            setAverageSpeed(avgSpeed)
            setCurrentSpeed(curSpeed)
            setCalories(inputViewModel.calories)
            setClimb(inputViewModel.climb)
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

        if (inputViewModel.id != -1L) {
            displayEntry()
        } else {
            appContext = applicationContext
            markerOptions = MarkerOptions()

            val latLngList = trackingViewModel.getAll()
            if (latLngList.isNotEmpty()) {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            latLngList.last().latitude,
                            latLngList.last().longitude
                        ), 17f
                    )
                )
                recreateLiveList(latLngList)
            }
            recordEntry()
        }
    }

    /**
     * Recreates the map live as given by [latLngList].
     */
    private fun recreateLiveList(latLngList: ArrayList<Location>) {
        if (latLngList.isEmpty()) return

        polylineOptions = PolylineOptions()

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

    /**
     * Create a new entry and save it to database.
     */
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
        lastEntry.asLiveData().observe(this) {
            if (it != null && !inserted) {
                insertIntoLocationDatabase(it)
            }
        }
    }

    /**
     * Insert route data into the database with reference to [entryId].
     */
    private fun insertIntoLocationDatabase(entryId: Long) {
        val locationList = trackingViewModel.getAll()

        locationList.forEach {
            val locationEntry = LocationEntry(
                id = i, entryId = entryId, lat = it.latitude, lon = it.longitude
            )
            locationViewModel.insertLocationEntry(locationEntry)
            i += 1
        }
        inserted = true
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
        if (inputViewModel.id != -1L) {
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
                locationViewModel.deleteLocations(inputViewModel.id)
                entry.removeObservers(this)
                historyViewModel.delete(inputViewModel.id)
                Toast.makeText(
                    this,
                    "Removed entry with ID ${inputViewModel.id}.",
                    Toast.LENGTH_SHORT
                ).show()
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

    override fun onResume() {
        super.onResume()
        val latLngList = trackingViewModel.getAll()
        if (latLngList.isNotEmpty() && this::mMap.isInitialized) {
            mMap.clear()
            recreateLiveList(latLngList)
        }
    }

    override fun onPause() {
        super.onPause()
        if (inputViewModel.id == -1L && inputViewModel.inputType == "Automatic") {
            sensorManager.unregisterListener(this)
        }
    }

    // Sensors
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        x = (event.values[0] / SensorManager.GRAVITY_EARTH).toDouble()
        y = (event.values[1] / SensorManager.GRAVITY_EARTH).toDouble()
        z = (event.values[2] / SensorManager.GRAVITY_EARTH).toDouble()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    companion object {
        const val BIND_STATUS_KEY = "bind_status_key"
    }
}