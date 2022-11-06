package com.michael_zhu.myruns.database.location

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "location_entry", indices = [Index(value = ["entryId"], unique = true)])
data class LocationEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var entryId: Long = 0,

    var lat: Double = 0.0,
    var lon: Double = 0.0
)