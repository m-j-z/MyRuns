package com.michael_zhu.myruns.ui.history

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.database.*
import com.michael_zhu.myruns.misc.Utility

class DisplayEntryActivity : AppCompatActivity() {
    private var id: Long = 0

    private lateinit var historyDatabase: HistoryDatabase
    private lateinit var historyDatabaseDao: HistoryDatabaseDao
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyViewModelFactory: HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var entry: LiveData<Entry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_entry)

        val extras = intent.extras
        if (extras == null) {
            finish()
        }
        id = extras!!.getLong("id")

        historyDatabase = HistoryDatabase.getInstance(this)
        historyDatabaseDao = historyDatabase.historyDatabaseDao
        historyRepository = HistoryRepository(historyDatabaseDao)
        historyViewModelFactory = HistoryViewModelFactory(historyRepository)
        historyViewModel = ViewModelProvider(
            this, historyViewModelFactory
        )[HistoryViewModel::class.java]

        val inputTypeEditText: EditText = findViewById(R.id.input_type_et)
        val activityTypeEditText: EditText = findViewById(R.id.activity_type_et)
        val datetimeEditText: EditText = findViewById(R.id.datetime_et)
        val durationEditText: EditText = findViewById(R.id.duration_et)
        val distanceEditText: EditText = findViewById(R.id.distance_et)
        val caloriesEditText: EditText = findViewById(R.id.calories_et)
        val heartRateEditText: EditText = findViewById(R.id.heartRate_et)

        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val unitPref = preference.getString("unit_preference", "km").toString()

        entry = historyViewModel.getEntry(id)
        entry.observe(this) {
            val date = Utility.longToDate(it.date)
            val time = Utility.longToTime(it.time)
            val datetime = "$time $date"
            val distance = Utility.convertUnits(unitPref, it.unitSavedAs, it.distance).toString() + " $unitPref"
            val calories = it.calories.toString() + " cal"
            val heartRate = it.heartRate.toString() + " bpm"

            inputTypeEditText.setText(it.inputType)
            activityTypeEditText.setText(it.activityType)
            datetimeEditText.setText(datetime)
            durationEditText.setText(Utility.longToDuration(it.duration.toLong()))
            distanceEditText.setText(distance)
            caloriesEditText.setText(calories)
            heartRateEditText.setText(heartRate)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.display_entry_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                entry.removeObservers(this)
                historyViewModel.delete(id)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}