package com.soundmeter.application.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
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
    var listTime: List<String>,
    var listDb: List<String>,
)