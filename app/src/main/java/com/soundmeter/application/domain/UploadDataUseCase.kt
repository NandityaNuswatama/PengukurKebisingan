package com.soundmeter.application.domain

import com.soundmeter.application.data.local.SoundEntity
import javax.inject.Inject

class UploadDataUseCase @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {
    fun uploadAll(data: List<SoundEntity>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val notUploaded = data.filter { !it.isUploaded }
        var count = 0
        notUploaded.forEach { soundEntity ->
            remoteDataSource.uploadData(
                soundEntity,
                onSuccess = {
                    localDataSource.updateUploaded(soundEntity.id)
                    count += 1
                    if (count == notUploaded.size) onSuccess.invoke()
                },
                onFailure = {
                    onFailure.invoke(it)
                }
            )
        }
    }
}