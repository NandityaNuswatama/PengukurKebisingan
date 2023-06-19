package com.soundmeter.application.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.soundmeter.application.utils.ListStringConverter

@Database(entities = [SoundEntity::class], version = 5, exportSchema = true)
@TypeConverters(ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun soundDao(): SoundDao

    companion object {
        val migration_3_4 = object : Migration(3,4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE sound_data ADD COLUMN latitude TEXT NOT NULL DEFAULT '0.0'")
                database.execSQL("ALTER TABLE sound_data ADD COLUMN longitude TEXT NOT NULL DEFAULT '0.0'")
                database.execSQL("ALTER TABLE sound_data ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE sound_data ADD COLUMN isUploaded INTEGER NOT NULL DEFAULT 0")
            }
        }
        val migration_4_5 = object : Migration(4,5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE sound_data ADD COLUMN uploadedDate TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}