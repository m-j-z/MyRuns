package com.michael_zhu.myruns.database.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDatabaseDao {
    @Insert
    fun insertLocationEntry(entry: LocationEntry)

    @Query("DELETE FROM location_entry WHERE entryId = :entryId")
    fun deleteLocationEntries(entryId: Long)

    @Query("SELECT * FROM location_entry WHERE entryId = :entryId")
    fun getLocations(entryId: Long): Flow<List<LocationEntry>>
}