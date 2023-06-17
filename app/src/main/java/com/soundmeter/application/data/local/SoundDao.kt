package com.soundmeter.application.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SoundDao {
    
    @Query("SELECT * FROM sound_data")
    fun getListData(): List<SoundEntity>
    
    @Query("SELECT * FROM sound_data WHERE id=:id")
    fun getDetailData(id: Int): SoundEntity
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: SoundEntity)
    
    @Query("DELETE FROM sound_data WHERE id=:id")
    suspend fun deleteData(id: Int)
}