package com.example.medicine.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.medicine.data.repository.MedicineRepository
import com.example.medicine.util.NotificationUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val medicineRepository: MedicineRepository
) : Worker(context, params) {
    override fun doWork(): Result {
        val medicineId = inputData.getInt("medicineId", 0)
        val reminderId = inputData.getInt("reminderId", 0)
        val medicineName = inputData.getString("medicineName") ?: ""
        val message = inputData.getString("message") ?: ""
        val tone = inputData.getString("tone") ?: "default"
        val volume = inputData.getFloat("volume", 0.5f)
        
        if (medicineId == 0 || reminderId == 0) {
            return Result.failure()
        }
        
        // 发送通知
        NotificationUtil.sendReminderNotification(
            applicationContext,
            medicineName,
            message,
            tone,
            volume
        )
        
        // 更新提醒状态
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        medicineRepository.updateRemindedStatus(reminderId, today)
        
        return Result.success()
    }
    
    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): ReminderWorker
    }
}
