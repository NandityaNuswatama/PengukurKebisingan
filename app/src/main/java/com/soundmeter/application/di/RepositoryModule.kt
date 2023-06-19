package com.soundmeter.application.di

import com.soundmeter.application.data.local.LocalDataSourceImpl
import com.soundmeter.application.data.remote.RemoteDataSourceImpl
import com.soundmeter.application.domain.LocalDataSource
import com.soundmeter.application.domain.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class RepositoryModule {
    
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class LocalDataModule {
        
        @Binds
        abstract fun provideLocalRepository(
            localDataSourceImpl: LocalDataSourceImpl
        ): LocalDataSource
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RemoteDataModule {

        @Binds
        abstract fun provideLocalRepository(
            remoteDataSourceImpl: RemoteDataSourceImpl
        ): RemoteDataSource
    }
}