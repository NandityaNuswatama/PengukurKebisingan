package com.soundmeter.application.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.soundmeter.application.utils.ListStringConverter

@Database(entities = [SoundEntity::class], version = 3, exportSchema = true, autoMigrations = [AutoMigration(from = 1, to = 2)])
@TypeConverters(ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun soundDao(): SoundDao
}