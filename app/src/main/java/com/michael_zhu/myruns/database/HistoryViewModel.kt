package com.michael_zhu.myruns.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class HistoryViewModel(private val historyRepository: HistoryRepository): ViewModel() {
    val historyLiveData = historyRepository.history.asLiveData()

    fun insert(entry: Entry) {
        historyRepository.insert(entry)
    }

    fun delete(id: Long) {
        historyRepository.delete(id)
    }

    fun getEntry(id: Long): LiveData<Entry> {
        return historyRepository.getEntry(id).asLiveData()
    }
}