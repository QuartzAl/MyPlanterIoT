package com.example.sproutlink.model

import android.content.Context
import com.example.sproutlink.constants.SERVER_URI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttAsyncClient.generateClientId

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesMqttClient(@ApplicationContext appContext: Context): MqttAndroidClient {
        return MqttAndroidClient(appContext, SERVER_URI, generateClientId())
    }

}