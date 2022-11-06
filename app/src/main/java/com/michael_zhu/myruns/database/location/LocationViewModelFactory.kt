package com.michael_zhu.myruns.database.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LocationViewModelFactory(private val locationRepository: LocationRepository) :
    ViewModelProvider.Factory {

    /**
     * Creates and returns LocationViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(locationRepository) as T
        }
        throw IllegalArgumentException("Error, LocationViewModel.")
    }
}