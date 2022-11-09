package com.michael_zhu.myruns.ui.start

import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class InputViewModel : ViewModel() {
    var inputType: String = "GPS"
    var activityType: String = "Running"
    var year: Int = 0
    var month: Int = 0
    var dayOfMonth: Int = 0
    var hourOfDay: Int = 0
    var minute: Int = 0
    var second: Int = 0
    var durationHour: Int = 0
    var durationMinute: Int = 0
    var durationSecond: Int = 0
    var distanceSavedAsUnit: String = "km"
    var climb: Double = 0.0

    // For data entry
    /**
     * Time in milliseconds.
     */
    var dateEpoch: Long = 0

    /**
     * Time in seconds.
     */
    var timeEpoch: Long = 0
    var duration: Double = 0.0
    var distance: Double = 0.0
    var calories: Double = 0.0
    var heartRate: Double = 0.0
    var comment: String = ""

    /**
     * Sets the current date and time on creation of viewmodel.
     */
    init {
        val calendar = Calendar.getInstance()

        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
        second = calendar.get(Calendar.SECOND)

        val localDate = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute)
        dateEpoch =
            localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + second * 1000
        timeEpoch = (hourOfDay * 3600 + minute * 60 + second).toLong()
    }

}