package com.michael_zhu.myruns.database.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class LocationViewModel(private val locationRepository: LocationRepository): ViewModel() {
    /**
     * Insert a location entry, [locationEntry], to the location database.
     */
    fun insertLocationEntry(locationEntry: LocationEntry) {
        locationRepository.insert(locationEntry)
    }

    /**
     * Deletes all location entry with reference key [entryId] from the location database.
     */
    fun deleteLocations(entryId: Long) {
        locationRepository.deleteLocationEntries(entryId)
    }

    /**
     * Get all locations with reference key [entryId].
     */
    fun getLocations(entryId: Long): LiveData<List<LocationEntry>> {
        return locationRepository.getLocations(entryId).asLiveData()
    }
}