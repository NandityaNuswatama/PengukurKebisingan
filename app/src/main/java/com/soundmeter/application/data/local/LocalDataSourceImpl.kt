package com.soundmeter.application.data.local

import com.soundmeter.application.domain.LocalDataSource
import com.soundmeter.application.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.threeten.bp.DateTimeUtils
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

    override fun updateUploaded(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            soundDao.updateUploadStatus(id, true, DateUtils.getDateFromMillis(System.currentTimeMillis()))
        }
    }
}