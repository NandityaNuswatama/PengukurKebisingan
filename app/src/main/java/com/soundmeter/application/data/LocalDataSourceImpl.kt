package com.soundmeter.application.data

import com.soundmeter.application.domain.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSourceImpl @Inject constructor(
    private val soundDao: SoundDao
): LocalDataSource {
    
    override fun save(data: SoundEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            soundDao.insert(data)
        }
    }
    
    override fun getListData(): List<SoundEntity> {
        return soundDao.getListData()
    }
    
    override fun getDetailData(id: Int): SoundEntity {
        return soundDao.getDetailData(id)
    }
    
    override fun deleteData(id: Int) {
        runBlocking {
            soundDao.deleteData(id)
        }
    }
}