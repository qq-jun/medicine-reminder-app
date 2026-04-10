package com.example.medicine.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.medicine.R

object NotificationUtil {
    private const val CHANNEL_ID = "medicine_reminder"
    private const val CHANNEL_NAME = "用药提醒"
    private const val CHANNEL_DESCRIPTION = "提醒用户按时服药"
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun sendReminderNotification(context: Context, medicineName: String, message: String, tone: String, volume: Float) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("用药提醒")
            .setContentText("$medicineName: $message")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notificationBuilder.build())
        
        // 播放提醒音（这里简化处理，实际项目中可以根据tone参数选择不同的提醒音）
        // TODO: 实现提醒音播放功能
    }
}
