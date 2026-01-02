package com.iwatdigital.dropdroid.transport.wifidirect.di

import com.iwatdigital.dropdroid.transport.wifidirect.WifiDirectTransportManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WifiDirectModule {
    @Provides
    @Singleton
    fun provideWifiDirectManager(): WifiDirectTransportManager = WifiDirectTransportManager()
}
