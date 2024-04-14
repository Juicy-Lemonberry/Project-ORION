package edu.singaporetech.inf2007.team48.project_orion_sandbox.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.singaporetech.inf2007.team48.project_orion_sandbox.data.comms.AndroidBluetoothController
import edu.singaporetech.inf2007.team48.project_orion_sandbox.domain.comms.BluetoothController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }
}