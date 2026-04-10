package com.example.medicine.di

import android.content.Context
import androidx.work.WorkerParameters
import com.example.medicine.service.ReminderWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.assisted.Assisted
import dagger.hilt.assisted.AssistedFactory
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    @Provides
    fun provideReminderWorkerFactory(
        factory: ReminderWorker.Factory
    ): androidx.work.WorkerFactory {
        return object : androidx.work.WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): androidx.work.ListenableWorker? {
                return when (workerClassName) {
                    ReminderWorker::class.java.name -> {
                        factory.create(appContext, workerParameters)
                    }
                    else -> null
                }
            }
        }
    }
}

