package com.soundmeter.application.view.recording

import androidx.lifecycle.ViewModel
import com.soundmeter.application.data.SoundEntity
import com.soundmeter.application.domain.InsertDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

        val listDouble = listDb.map { it.toDouble() }

        val listGrouping = mutableListOf<Pair<Double, Int>>()
        listGrouping.add(Pair(30.0, listDouble.groupBy { it in 30.0..34.0 }.size))
        listGrouping.add(Pair(35.0, listDouble.groupBy { it in 35.0..39.0 }.size))
        listGrouping.add(Pair(40.0, listDouble.groupBy { it in 40.0..44.0 }.size))
        listGrouping.add(Pair(45.0, listDouble.groupBy { it in 45.0..49.0 }.size))
        listGrouping.add(Pair(50.0, listDouble.groupBy { it in 50.0..54.0 }.size))
        listGrouping.add(Pair(55.0, listDouble.groupBy { it in 55.0..59.0 }.size))
        listGrouping.add(Pair(60.0, listDouble.groupBy { it in 60.0..64.0 }.size))
        listGrouping.add(Pair(65.0, listDouble.groupBy { it in 65.0..69.0 }.size))
        listGrouping.add(Pair(70.0, listDouble.groupBy { it in 70.0..74.0 }.size))
        listGrouping.add(Pair(75.0, listDouble.groupBy { it in 75.0..79.0 }.size))
        listGrouping.add(Pair(80.0, listDouble.groupBy { it in 80.0..84.0 }.size))
        listGrouping.add(Pair(85.0, listDouble.groupBy { it in 85.0..89.0 }.size))
        listGrouping.add(Pair(90.0, listDouble.groupBy { it in 90.0..94.0 }.size))
        listGrouping.add(Pair(95.0, listDouble.groupBy { it in 95.0..99.0 }.size))
        listGrouping.add(Pair(100.0, listDouble.groupBy { it in 100.0..104.0 }.size))
        listGrouping.add(Pair(105.0, listDouble.groupBy { it in 105.0..109.0 }.size))
        listGrouping.add(Pair(110.0, listDouble.groupBy { it in 110.0..114.0 }.size))

        val data = SoundEntity(
            id = 0,
            title = title,
            subtitle = subtitle.toString(),
            date = date,
            minDb = min,
            maxDb = max,
            averageDb = listDouble.average().toString(),
            noiseDb = calculateNoise(listGrouping).toString(),
            listTime = listTime,
            listDb = listDb
        )

        insertDataUseCase.invoke(data)
    }

    private fun calculateNoise(listGrouping: List<Pair<Double, Int>>): Double {
        val modus = listGrouping.maxOf { it.second }
        val modusIndex = listGrouping.indexOf(listGrouping.find { it.second == modus })
        val xValue = listGrouping.get(modusIndex).first
        val p1 = listGrouping.get(modusIndex - 1).second
        val p2 = listGrouping.get(modusIndex + 1).second

        return xValue + (p1 / (p1 + p2) * 5)
    }
}