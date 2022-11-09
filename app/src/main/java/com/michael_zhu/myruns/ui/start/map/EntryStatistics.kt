package com.michael_zhu.myruns.ui.start.map

import android.content.Context
import androidx.preference.PreferenceManager

class EntryStatistics(context: Context) {
    private var unitPref = "km"

    private var _activityType = "Running"
    private var _avgSpeed: Double = 0.0
    private var _curSpeed: Double = 0.0
    private var _climb: Double = 0.0
    private var _calories: Double = 0.0
    private var _distance: Double = 0.0

    private var stats = ""

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        unitPref = preferences.getString("unit_preference", "km").toString()
    }

    fun setActivityType(activityType: String) {
        _activityType = activityType
    }

    fun setAverageSpeed(avgSpeed: Double) {
        _avgSpeed = avgSpeed
    }

    fun setCurrentSpeed(curSpeed: Double) {
        _curSpeed = curSpeed
    }

    fun setCalories(calories: Double) {
        _calories = calories
    }

    fun setClimb(climb: Double) {
        _climb = climb
    }

    fun setDistance(distance: Double) {
        _distance = distance
    }

    fun getStats(): String {
        stats =
            "Activity Type: $_activityType\nAvg. Speed: $_avgSpeed $unitPref/h\nCurr. Speed: $_curSpeed $unitPref/h\nClimb: $_climb $unitPref\nCalories: $_calories cal\nDistance: $_distance $unitPref"
        return stats
    }
}