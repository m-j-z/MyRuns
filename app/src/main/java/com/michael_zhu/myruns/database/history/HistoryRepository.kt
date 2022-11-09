package com.michael_zhu.myruns.database.history

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDatabaseDao: HistoryDatabaseDao) {
    val history: Flow<List<Entry>> = historyDatabaseDao.getHistory()

    /**
     * Inserts an entry, [entry], into the history database.
     */
    fun insert(entry: Entry) {
        Thread {
            historyDatabaseDao.insertEntry(entry)
        }.start()
    }

    /**
     * Deletes an entry with [id].
     */
    fun delete(id: Long) {
        Thread {
            historyDatabaseDao.deleteEntry(id)
        }.start()
    }

    /**
     * Gets an entry with [id].
     */
    fun getEntry(id: Long): Flow<Entry> {
        return historyDatabaseDao.getEntry(id)
    }

    /**
     * Gets the last inserted entry's id
     */
    fun getLastEntryId(): Flow<Long> {
        return historyDatabaseDao.getLastEntryId()
    }
}