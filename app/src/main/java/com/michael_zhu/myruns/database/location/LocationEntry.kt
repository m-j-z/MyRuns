package com.michael_zhu.myruns.database.location

import androidx.room.Entity

@Entity(tableName = "location_entry", primaryKeys = ["id", "entryId"])
data class LocationEntry(
    var id: Long = 0,
    var entryId: Long = 0,

    var lat: Double = 0.0,
    var lon: Double = 0.0
)