package com.michael_zhu.myruns.ui.start.map

import android.content.Context
import androidx.preference.PreferenceManager
import com.michael_zhu.myruns.misc.Utility

class EntryStatistics(context: Context) {
    private var unitPref = "km"

    private var _activityType = "Running"
    private var _avgSpeed: Double = 0.0
    private var _curSpeed: Double = 0.0
    private var _climb: Double = 0.0
    private var _calories: Double = 0.0
    private var _distance: Double = 0.0

    private var stats = ""

    /**
     * Get unit preference.
     */
    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        unitPref = preferences.getString("unit_preference", "km").toString()
    }

    fun setActivityType(activityType: String) {
        _activityType = activityType
    }

    fun setAverageSpeed(avgSpeed: Double) {
        _avgSpeed = Utility.convertUnits(unitPref, "km", avgSpeed)
    }

    fun setCurrentSpeed(curSpeed: Double) {
        _curSpeed = Utility.convertUnits(unitPref, "km", curSpeed)
    }

    fun setCalories(calories: Double) {
        _calories = Utility.roundToDecimalPlaces(calories, 5)
    }

    fun setClimb(climb: Double) {
        _climb = Utility.convertUnits(unitPref, "km", climb)
    }

    fun setDistance(distance: Double) {
        _distance = Utility.convertUnits(unitPref, "km", distance)
    }

    fun getStats(): String {
        stats =
            "Activity Type: $_activityType\nAvg. Speed: $_avgSpeed $unitPref/h\nCurr. Speed: $_curSpeed $unitPref/h\nClimb: $_climb $unitPref\nCalories: $_calories cal\nDistance: $_distance $unitPref"
        return stats
    }
}