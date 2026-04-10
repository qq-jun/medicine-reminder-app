package com.example.medicine.service

import android.content.Context
import androidx.work.*
import com.example.medicine.data.model.Medicine
import com.example.medicine.data.model.Reminder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {
    fun scheduleReminders(medicine: Medicine, reminders: List<Reminder>) {
        // 取消之前的提醒
        cancelReminders(medicine.id)
        
        // 为每个提醒设置任务
        reminders.forEach {
            scheduleReminder(medicine, it)
        }
    }
    
    private fun scheduleReminder(medicine: Medicine, reminder: Reminder) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val targetTime = LocalTime.parse(reminder.time, timeFormatter)
        val now = LocalDateTime.now()
        val targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime)
        
        // 如果目标时间已过，设置为明天的同一时间
        val scheduledDateTime = if (targetDateTime.isBefore(now)) {
            targetDateTime.plusDays(1)
        } else {
            targetDateTime
        }
        
        val delay = scheduledDateTime.toEpochSecond(now.zone.rules.getOffset(now)) - 
                    now.toEpochSecond(now.zone.rules.getOffset(now))
        
        val data = Data.Builder()
            .putInt("medicineId", medicine.id)
            .putInt("reminderId", reminder.id)
            .putString("medicineName", medicine.name)
            .putString("message", "该服用 ${medicine.dose} 了！")
            .putString("tone", medicine.tone)
            .putFloat("volume", medicine.volume)
            .build()
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_${medicine.id}_${reminder.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun cancelReminders(medicineId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("medicine_$medicineId")
    }
}
