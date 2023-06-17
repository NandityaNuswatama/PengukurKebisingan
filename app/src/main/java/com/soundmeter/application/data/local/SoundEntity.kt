package com.soundmeter.application.data.local

import androidx.room.*
import com.soundmeter.application.utils.ListStringConverter

@Entity(
    tableName = "sound_data"
)
data class SoundEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String,
    var subtitle: String,
    var date: String,
    var maxDb: String,
    var minDb: String,
    @ColumnInfo(defaultValue = "0.0")
    var averageDb: String,
    @ColumnInfo(defaultValue = "0.0")
    var noiseDb: String,
    var listTime: List<String>,
    var listDb: List<String>,
    @ColumnInfo(defaultValue = "0.0")
    var latitude: String,
    @ColumnInfo(defaultValue = "0.0")
    var longitude: String,
    @ColumnInfo(defaultValue = "0")
    var timestamp: Long,
    @ColumnInfo(defaultValue = "0")
    var isUploaded: Boolean,
)