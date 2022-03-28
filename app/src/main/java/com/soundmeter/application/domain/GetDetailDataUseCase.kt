package com.soundmeter.application.domain

import javax.inject.Inject

class GetDetailDataUseCase @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    fun invoke(id: Int) = localDataSource.getDetailData(id)
}