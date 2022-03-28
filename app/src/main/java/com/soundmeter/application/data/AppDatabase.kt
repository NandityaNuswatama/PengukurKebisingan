package com.soundmeter.application.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.soundmeter.application.utils.ListStringConverter

@Database(entities = [SoundEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListStringConverter::class)
abstract class AppDatabase: RoomDatabase() {
    
    abstract fun soundDao(): SoundDao
}