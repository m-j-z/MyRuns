package com.michael_zhu.myruns.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDatabaseDao {
    @Insert
    fun insertEntry(entry: Entry)

    @Query("SELECT * FROM activity_entry")
    fun getHistory(): Flow<List<Entry>>

    @Query("DELETE FROM activity_entry WHERE id = :id")
    fun deleteEntry(id: Long)

    @Query("SELECT * FROM activity_entry WHERE id = :id")
    fun getEntry(id: Long): Flow<Entry>
}