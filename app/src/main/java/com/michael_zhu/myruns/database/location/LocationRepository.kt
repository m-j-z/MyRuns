package com.michael_zhu.myruns.database.location

import kotlinx.coroutines.flow.Flow

class LocationRepository(private val locationDatabaseDao: LocationDatabaseDao) {
    /**
     * Insert a location entry, [locationEntry], to the location database.
     */
    fun insert(locationEntry: LocationEntry) {
        Thread {
            locationDatabaseDao.insertLocationEntry(locationEntry)
        }.start()
    }

    /**
     * Delete all location entries with reference key [entryId].
     */
    fun deleteLocationEntries(entryId: Long) {
        Thread {
            locationDatabaseDao.deleteLocationEntries(entryId)
        }.start()
    }

    /**
     * Get all location entries with reference key [entryId]
     */
    fun getLocations(entryId: Long): Flow<List<LocationEntry>> {
        return locationDatabaseDao.getLocations(entryId)
    }
}