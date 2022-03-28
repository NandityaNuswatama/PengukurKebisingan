package com.soundmeter.application.domain

import com.soundmeter.application.data.SoundEntity
import javax.inject.Inject

class InsertDataUseCase@Inject constructor(
    private val localDataSource: LocalDataSource
) {

    fun invoke(data: SoundEntity) = localDataSource.save(data)
}