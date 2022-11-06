package com.michael_zhu.myruns.database.location

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocationEntry::class], version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {
    abstract val locationDatabaseDao: LocationDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getInstance(context: Context): LocationDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocationDatabase::class.java,
                        "locationDB"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}