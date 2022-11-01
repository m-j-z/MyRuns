package com.michael_zhu.myruns.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryViewModelFactory(private val historyRepository: HistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Error")
    }
}