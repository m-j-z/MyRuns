package com.michael_zhu.myruns.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_entry")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var unitSavedAs: String = "km",

    var inputType: String = "",
    var activityType: String = "",
    var date: Long = 0,
    var time: Long = 0,
    var duration: Double = 0.0,
    var distance: Double = 0.0,
    var calories: Double = 0.0,
    var heartRate: Double = 0.0,
    var comment: String = ""
)
