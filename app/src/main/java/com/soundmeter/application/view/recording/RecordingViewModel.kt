package com.soundmeter.application.view.recording

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.hawk.Hawk
import com.soundmeter.application.data.local.SoundEntity
import com.soundmeter.application.domain.InsertDataUseCase
import com.soundmeter.application.utils.LATITUDE
import com.soundmeter.application.utils.LONGITUDE
import com.soundmeter.application.utils.toDoubleReplaceComma
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val insertDataUseCase: InsertDataUseCase,
) : ViewModel() {


    private val _onSuccessInsert = MutableLiveData<Any>()
    val onSuccessInsert: LiveData<Any> get() = _onSuccessInsert

    fun insertData(
        title: String,
        subtitle: String?,
        date: String,
        timeStamp: Long,
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

        for (db in listDouble) {
            val range = (db / 5).toInt() * 5
            val grouping = range.coerceIn(30, 110).toDouble()
            listGrouping.add(grouping)
        }

        val data = SoundEntity(
            id = 0,
            title = title,
            subtitle = subtitle.toString(),
            date = date,
            timestamp = timeStamp,
            minDb = min,
            maxDb = max,
            averageDb = listDouble.average().toString().take(4),
            noiseDb = calculateNoise(listGrouping.sorted()).toString().take(4),
            latitude = Hawk.get<Double>(LATITUDE).toString(),
            longitude = Hawk.get<Double>(LONGITUDE).toString(),
            listTime = listTime,
            listDb = listDb,
            isUploaded = false,
            uploadedDate = ""
        )

        insertDataUseCase.invoke(data)
        _onSuccessInsert.value = true
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