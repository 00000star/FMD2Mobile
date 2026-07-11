package com.fmd2mobile

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

import com.fmd2mobile.parser.extension.ExtensionManager

/**
 * Main Application class for FMD2 Mobile.
 * Configures Dagger Hilt for dependency injection and implements
 * Configuration.Provider to supply Hilt-injected workers to WorkManager.
 */
@HiltAndroidApp
class FMD2Application : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var extensionManager: ExtensionManager

    override fun onCreate() {
        super.onCreate()
        extensionManager.loadExtensions()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
