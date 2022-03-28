package com.soundmeter.application.domain

import javax.inject.Inject

class GetListDataUseCase @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    fun invoke() = localDataSource.getListData()
}