package com.example.medicine

import android.app.Application
import com.example.medicine.util.NotificationUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MedicineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 创建通知渠道
        NotificationUtil.createNotificationChannel(this)
    }
}
