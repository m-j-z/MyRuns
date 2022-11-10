package com.michael_zhu.myruns.database.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow

class HistoryViewModel(private val historyRepository: HistoryRepository): ViewModel() {
    val historyLiveData = historyRepository.history.asLiveData()

    /**
     * Inserts an entry, [entry], into the history database.
     */
    fun insert(entry: Entry) {
        historyRepository.insert(entry)
    }

    /**
     * Deletes an entry with [id].
     */
    fun delete(id: Long) {
        historyRepository.delete(id)
    }

    /**
     * Gets an entry with [id].
     */
    fun getEntry(id: Long): LiveData<Entry> {
        return historyRepository.getEntry(id).asLiveData()
    }

    /**
     * Gets the last inserted entry's id
     */
    fun getLastEntryId(): Flow<Long> {
        return historyRepository.getLastEntryId()
    }
}