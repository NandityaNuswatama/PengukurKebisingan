package com.soundmeter.application.di

import android.content.Context
import androidx.room.Room
import com.soundmeter.application.data.AppDatabase
import com.soundmeter.application.data.SharedPreferenceManager
import com.soundmeter.application.data.SoundDao
import com.soundmeter.application.utils.appPreferencesName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocalModule {
    
    @Singleton
    @Provides
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferenceManager {
        return SharedPreferenceManager(
            context.getSharedPreferences(
                appPreferencesName,
                Context.MODE_PRIVATE
            ), context
        )
    }
    
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "soundmeter.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    
    @Singleton
    @Provides
    fun provideSoundDao(db: AppDatabase): SoundDao = db.soundDao()
}