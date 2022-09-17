package com.soundmeter.application.view.recording

import androidx.lifecycle.ViewModel
import com.soundmeter.application.data.SoundEntity
import com.soundmeter.application.domain.InsertDataUseCase
import com.soundmeter.application.utils.toDoubleReplaceComma
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val insertDataUseCase: InsertDataUseCase,
) : ViewModel() {

    fun insertData(
        title: String,
        subtitle: String?,
        date: String,
        min: String,
        max: String,
        listRecord: List<Pair<String, String>>
    ) {
        val listTime = mutableListOf<String>()
        listRecord.forEach { listTime.add(it.first) }
        val listDb = mutableListOf<String>()
        listRecord.forEach { listDb.add(it.second) }

        val listDouble = listDb.map { it.toDoubleReplaceComma() }
        val listGrouping = mutableListOf<Double>()

        listDouble.forEach { db ->
            if (db in 30.0..34.9) listGrouping.add(30.0)
            if (db in 35.0..39.9) listGrouping.add(35.0)
            if (db in 40.0..44.9) listGrouping.add(40.0)
            if (db in 45.0..49.9) listGrouping.add(45.0)
            if (db in 50.0..54.9) listGrouping.add(50.0)
            if (db in 55.0..59.9) listGrouping.add(55.0)
            if (db in 60.0..64.9) listGrouping.add(60.0)
            if (db in 65.0..69.9) listGrouping.add(65.0)
            if (db in 70.0..74.9) listGrouping.add(70.0)
            if (db in 75.0..79.9) listGrouping.add(75.0)
            if (db in 80.0..84.9) listGrouping.add(80.0)
            if (db in 85.0..89.9) listGrouping.add(85.0)
            if (db in 90.0..94.9) listGrouping.add(90.0)
            if (db in 95.0..99.9) listGrouping.add(95.0)
            if (db in 100.0..104.9) listGrouping.add(100.0)
            if (db in 105.0..109.9) listGrouping.add(105.0)
            if (db in 110.0..114.9) listGrouping.add(110.0)
        }


        val data = SoundEntity(
            id = 0,
            title = title,
            subtitle = subtitle.toString(),
            date = date,
            minDb = min,
            maxDb = max,
            averageDb = listDouble.average().toString().take(4),
            noiseDb = calculateNoise(listGrouping.sorted()).toString().take(4),
            listTime = listTime,
            listDb = listDb
        )

        insertDataUseCase.invoke(data)
    }

    private fun calculateNoise(listGrouping: List<Double>): Double {
        return if (listGrouping.isNotEmpty()) {
            val setValues = listGrouping.groupBy { it }.values
            val distinct = listGrouping.distinct()
            val modusSize = setValues.maxByOrNull { it.size }?.size?.toDouble() ?: 0.0
            val modus = setValues.maxByOrNull { it.size }?.first() ?: 0.0
            val modusIndex = distinct.indexOf(modus)
            if (modusIndex != 0 && modusIndex != distinct.size - 1) {
                val p1 =
                    if (modus - distinct[modusIndex - 1] > 5.0) modusSize
                    else modusSize - listGrouping.filter { it == distinct[modusIndex - 1] }.size.toDouble()
                val p2 =
                    if (distinct[modusIndex + 1] - modus > 5.0) modusSize
                    else modusSize + listGrouping.filter { it == distinct[modusIndex + 1] }.size.toDouble()
                modus.plus(p1.plus(5.0).div(p1.plus(p2)))
            } else modus
        } else 0.0
    }
}