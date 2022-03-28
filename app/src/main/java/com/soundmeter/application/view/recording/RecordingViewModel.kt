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
    ){
        val listTime = mutableListOf<String>()
        listRecord.forEach { listTime.add(it.first) }
        val listDb = mutableListOf<String>()
        listRecord.forEach { listDb.add(it.second) }
        
        val data = SoundEntity(
            id = 0,
            title, subtitle.toString(), date, min, max, listTime, listDb
        )
        
        insertDataUseCase.invoke(data)
    }
}