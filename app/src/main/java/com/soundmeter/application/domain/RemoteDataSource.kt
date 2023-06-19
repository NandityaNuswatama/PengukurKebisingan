package com.soundmeter.application.domain

import com.soundmeter.application.data.local.SoundEntity

interface RemoteDataSource {
    fun uploadData(soundEntity: SoundEntity, onSuccess: () -> Unit, onFailure: (String) -> Unit)
}