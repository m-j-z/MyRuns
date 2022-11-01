package com.michael_zhu.myruns.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Entry::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {
    abstract val historyDatabaseDao: HistoryDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getInstance(context: Context): HistoryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryDatabase::class.java,
                        "historyDB"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}