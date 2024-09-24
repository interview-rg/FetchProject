package com.example.fetchproject.di

import android.content.Context
import androidx.room.Room
import com.example.fetchproject.data.room.AppDatabase
import com.example.fetchproject.data.room.ItemDao
import com.example.fetchproject.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideItemDao(appDatabase: AppDatabase): ItemDao = appDatabase.itemDao()
}