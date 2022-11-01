package com.michael_zhu.myruns.ui.start.manual

import androidx.lifecycle.ViewModel
import java.util.Calendar

class ManualInputViewModel : ViewModel() {
    var activityType: String = "Running"
    var year: Int = 0
    var month: Int = 0
    var dayOfMonth: Int = 0
    var hourOfDay: Int = 0
    var minute: Int = 0
    var durationHour: Int = 0
    var durationMinute: Int = 0
    var durationSecond: Int = 0

    // For data entry
    var dateEpoch: Long = 0
    var timeEpoch: Long = 0
    var duration: Double = 0.0
    var distance: Double = 0.0
    var calories: Double = 0.0
    var heartRate: Double = 0.0
    var comment: String = ""

    init {
        val calendar = Calendar.getInstance()

        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
    }

}