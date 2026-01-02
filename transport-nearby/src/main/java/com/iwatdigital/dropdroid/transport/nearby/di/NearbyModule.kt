package com.iwatdigital.dropdroid.transport.nearby.di

import android.content.Context
import com.iwatdigital.dropdroid.transport.nearby.NearbyTransportManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NearbyModule {
    @Provides
    @Singleton
    fun provideNearbyTransport(
        @ApplicationContext context: Context,
        json: Json
    ): NearbyTransportManager = NearbyTransportManager(context = context, json = json)
}
