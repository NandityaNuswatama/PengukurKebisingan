package com.soundmeter.application.domain

import javax.inject.Inject

class DeleteDataUseCase@Inject constructor(
    private val localDataSource: LocalDataSource
) {

    fun invoke(id: Int) = localDataSource.deleteData(id)
}