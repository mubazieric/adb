package com.iwatdigital.dropdroid.data.di

import android.content.Context
import androidx.room.Room
import com.iwatdigital.dropdroid.data.db.DropDroidDao
import com.iwatdigital.dropdroid.data.db.DropDroidDatabase
import com.iwatdigital.dropdroid.data.repository.HistoryRepository
import com.iwatdigital.dropdroid.data.repository.TrustedPeersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DropDroidDatabase =
        Room.databaseBuilder(
            context,
            DropDroidDatabase::class.java,
            "dropdroid.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideDao(database: DropDroidDatabase): DropDroidDao = database.dao()

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        dao: DropDroidDao,
        json: Json
    ): HistoryRepository = HistoryRepository(dao, json)

    @Provides
    @Singleton
    fun provideTrustedPeersRepository(
        dao: DropDroidDao
    ): TrustedPeersRepository = TrustedPeersRepository(dao)
}
