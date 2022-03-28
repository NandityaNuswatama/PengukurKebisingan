package com.soundmeter.application.di

import com.soundmeter.application.data.LocalDataSourceImpl
import com.soundmeter.application.domain.LocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class RepositoryModule {
    
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class LocalFieldDataModule {
        
        @Binds
        abstract fun provideLocalRepository(
            localDataSourceImpl: LocalDataSourceImpl
        ): LocalDataSource
    }
}