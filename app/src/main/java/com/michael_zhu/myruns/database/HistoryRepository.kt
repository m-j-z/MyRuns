package com.michael_zhu.myruns.database

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDatabaseDao: HistoryDatabaseDao) {
    val history: Flow<List<Entry>> = historyDatabaseDao.getHistory()

    fun insert(entry: Entry) {
        Thread {
            historyDatabaseDao.insertEntry(entry)
        }.start()
    }

    fun delete(id: Long) {
        Thread {
            historyDatabaseDao.deleteEntry(id)
        }.start()
    }

    fun getEntry(id: Long): Flow<Entry> {
        return historyDatabaseDao.getEntry(id)
    }

}