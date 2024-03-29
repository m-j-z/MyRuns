package com.michael_zhu.myruns.ui.start.manual

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.database.history.*
import com.michael_zhu.myruns.ui.start.InputViewModel
import com.michael_zhu.myruns.ui.start.manual.dialogs.DateDialogListener
import com.michael_zhu.myruns.ui.start.manual.dialogs.RotatableDatePickerDialog
import com.michael_zhu.myruns.ui.start.manual.dialogs.RotatableTimePickerDialog
import com.michael_zhu.myruns.ui.start.manual.dialogs.TimeDialogListener

class ManualInputActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var viewModel: InputViewModel
    private lateinit var listView: ListView
    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var historyDatabase: HistoryDatabase
    private lateinit var historyDatabaseDao: HistoryDatabaseDao
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyViewModelFactory: HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    /**
     * Sets the view of the activity.
     * Reads the activity type.
     * Initializes the list view, buttons and history database.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)
        viewModel = ViewModelProvider(this)[InputViewModel::class.java]

        if (intent.extras != null) {
            viewModel.activityType = intent.extras!!.getString("activity_type").toString()
        }

        listView = findViewById(R.id.list_view)
        cancelBtn = findViewById(R.id.cancel_btn)
        saveBtn = findViewById(R.id.save_btn)
        historyDatabase = HistoryDatabase.getInstance(this)
        historyDatabaseDao = historyDatabase.historyDatabaseDao
        historyRepository = HistoryRepository(historyDatabaseDao)
        historyViewModelFactory = HistoryViewModelFactory(historyRepository)
        historyViewModel = ViewModelProvider(this, historyViewModelFactory)[HistoryViewModel::class.java]

        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> onDateClicked()
                1 -> onTimeClicked()
                2 -> onDurationClicked()
                3 -> onDistanceClicked()
                4 -> onCaloriesClicked()
                5 -> onHeartbeatClicked()
                6 -> onCommentClicked()
            }
        }

        cancelBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
    }

    /**
     * Creates the comment dialog.
     */
    private fun onCommentClicked() {
        val dialogView = layoutInflater.inflate(
            R.layout.comment_dialog,
            findViewById(androidx.appcompat.R.id.content)
        )
        val commentEditText: EditText = dialogView.findViewById(R.id.comment_et)

        if (viewModel.comment != "") {
            commentEditText.setText(viewModel.comment)
        }

        val listener = DialogInterface.OnClickListener { _: DialogInterface, which: Int ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    viewModel.comment = commentEditText.text.toString()
                }
            }
        }
        createDialog("Enter any comments.", dialogView, listener)
    }

    /**
     * Creates the heartbeat dialog.
     */
    private fun onHeartbeatClicked() {
        val dialogView = layoutInflater.inflate(
            R.layout.heart_rate_dialog,
            findViewById(androidx.appcompat.R.id.content)
        )
        val heartRateEditText: EditText = dialogView.findViewById(R.id.heartRate_et)

        if (viewModel.heartRate != 0.0) {
            heartRateEditText.setText(viewModel.heartRate.toString())
        }

        val listener = DialogInterface.OnClickListener { _: DialogInterface, which: Int ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    var heartRate = 0.0
                    if (heartRateEditText.text.toString() != "") {
                        heartRate = heartRateEditText.text.toString().toDouble()
                    }
                    viewModel.heartRate = heartRate
                }
            }
        }
        createDialog("Enter your heart rate.", dialogView, listener)
    }

    /**
     * Creates the calories dialog.
     */
    private fun onCaloriesClicked() {
        val dialogView = layoutInflater.inflate(
            R.layout.calories_dialog,
            findViewById(androidx.appcompat.R.id.content)
        )
        val caloriesEditText: EditText = dialogView.findViewById(R.id.calories_et)

        if (viewModel.calories != 0.0) {
            caloriesEditText.setText(viewModel.calories.toString())
        }

        val listener =
            DialogInterface.OnClickListener { _: DialogInterface, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        var calories = 0.0
                        if (caloriesEditText.text.toString() != "") {
                            calories = caloriesEditText.text.toString().toDouble()
                        }
                        viewModel.calories = calories
                    }
                }
            }
        createDialog("Enter the calories burnt.", dialogView, listener)
    }

    /**
     * Creates the distance dialog.
     */
    private fun onDistanceClicked() {
        val dialogView = layoutInflater.inflate(
            R.layout.distance_dialog,
            findViewById(androidx.appcompat.R.id.content)
        )
        val distanceEditText: EditText = dialogView.findViewById(R.id.distance_et)
        val unitTextView: TextView = dialogView.findViewById(R.id.unit_tv)

        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        unitTextView.text = preference.getString("unit_preference", "km")

        if (viewModel.distance != 0.0) {
            distanceEditText.setText(viewModel.distance.toString())
        }

        val listener = DialogInterface.OnClickListener { _: DialogInterface, which: Int ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    var distance = 0.0
                    if (distanceEditText.text.toString() != "") {
                        distance = distanceEditText.text.toString().toDouble()
                    }
                    viewModel.distance = distance
                    when (unitTextView.text.toString()) {
                        "km" -> viewModel.distanceSavedAsUnit = "km"
                        "mi" -> viewModel.distanceSavedAsUnit = "mi"
                    }
                }
            }
        }
        createDialog("Enter the distance traveled.", dialogView, listener)
    }

    /**
     * Creates the duration dialog.
     */
    private fun onDurationClicked() {
        val dialogView = layoutInflater.inflate(
            R.layout.duration_dialog,
            findViewById(androidx.appcompat.R.id.content)
        )
        val hourEditText: EditText = dialogView.findViewById(R.id.hour_et)
        val minuteEditText: EditText = dialogView.findViewById(R.id.minute_et)
        val secondEditText: EditText = dialogView.findViewById(R.id.second_et)

        if (viewModel.durationHour != 0) {
            hourEditText.setText(viewModel.durationHour.toString())
        }
        if (viewModel.durationMinute != 0) {
            minuteEditText.setText(viewModel.durationMinute.toString())
        }
        if (viewModel.durationSecond != 0) {
            secondEditText.setText(viewModel.durationSecond.toString())
        }

        val listener =
            DialogInterface.OnClickListener { _: DialogInterface, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        var hour = 0.0
                        var minute = 0.0
                        var second = 0.0
                        if (hourEditText.text.toString() != "") {
                            hour = hourEditText.text.toString().toDouble()
                        }
                        if (minuteEditText.text.toString() != "") {
                            minute = minuteEditText.text.toString().toDouble()
                        }
                        if (secondEditText.text.toString() != "") {
                            second = secondEditText.text.toString().toDouble()
                        }
                        viewModel.durationHour = hour.toInt()
                        viewModel.durationMinute = minute.toInt()
                        viewModel.durationSecond = second.toInt()
                        viewModel.duration = hour * 3600 + minute * 60 + second
                    }
                }
            }
        createDialog("Enter duration of activity.", dialogView, listener)
    }

    /**
     * Creates the time picker dialog.
     */
    private fun onTimeClicked() {
        val dialog = RotatableTimePickerDialog()
        val listener = TimeDialogListener()
        listener.viewModel = viewModel

        val args = Bundle()
        args.putInt("hour", viewModel.hourOfDay)
        args.putInt("minute", viewModel.minute)
        args.putParcelable("listener", listener)
        dialog.arguments = args

        dialog.show(supportFragmentManager, "time_picker")
    }

    /**
     * Creates the date dialog.
     */
    private fun onDateClicked() {
        val dialog = RotatableDatePickerDialog()
        val listener = DateDialogListener()
        listener.viewModel = viewModel

        val args = Bundle()
        args.putInt("year", viewModel.year)
        args.putInt("month", viewModel.month)
        args.putInt("dayOfMonth", viewModel.dayOfMonth)
        args.putParcelable("listener", listener)
        dialog.arguments = args

        dialog.show(supportFragmentManager, "date_picker")
    }

    /**
     * Dialog constructor.
     */
    private fun createDialog(
        dialogTitle: String,
        dialogLayout: View,
        listener: DialogInterface.OnClickListener
    ) {
        val builder = Builder(this)
        builder.apply {
            setTitle(dialogTitle)
            setView(dialogLayout)
            setPositiveButton(R.string.ok, listener)
            setNegativeButton(R.string.cancel, listener)
        }
        builder.show()
    }

    /**
     * Determines the actions required on [v] clicked.
     */
    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

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
     * Create and saves a new entry into the history database.
     */
    private fun saveNewEntry() {
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val unitPref = preference.getString("unit_preference", "km").toString()

        val entry = Entry(
            inputType = "Manual",
            unitSavedAs = unitPref,
            activityType = viewModel.activityType,
            date = viewModel.dateEpoch,
            time = viewModel.timeEpoch,
            duration = viewModel.duration,
            distance = viewModel.distance,
            calories = viewModel.calories,
            heartRate = viewModel.heartRate,
            comment = viewModel.comment
        )
        historyViewModel.insert(entry)
    }
}