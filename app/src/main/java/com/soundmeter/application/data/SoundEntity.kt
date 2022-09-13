package com.soundmeter.application.data

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
)