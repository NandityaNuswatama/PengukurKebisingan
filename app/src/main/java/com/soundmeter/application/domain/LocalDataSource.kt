package com.soundmeter.application.domain

import com.soundmeter.application.data.local.SoundEntity

interface LocalDataSource {
    
    fun save(data: SoundEntity)
    
    fun getListData(): List<SoundEntity>
    
    fun getDetailData(id: Int): SoundEntity
    
    fun deleteData(id: Int)

    fun updateUploaded(id: Int)
}