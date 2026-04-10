package com.example.medicine.domain.usecase

import android.content.Context
import com.example.medicine.data.model.Medicine
import com.example.medicine.data.model.Reminder
import com.example.medicine.data.model.Weekday
import com.example.medicine.data.repository.MedicineRepository
import com.example.medicine.service.ReminderScheduler
import javax.inject.Inject

class UpdateMedicineUseCase @Inject constructor(
    private val repository: MedicineRepository,
    private val context: Context
) {
    suspend operator fun invoke(
        medicine: Medicine,
        reminders: List<Reminder>,
        weekdays: List<Weekday>
    ) {
        // 更新药品信息
        repository.updateMedicine(medicine)
        
        // 删除旧的提醒和星期设置
        repository.deleteRemindersByMedicineId(medicine.id)
        repository.deleteWeekdaysByMedicineId(medicine.id)
        
        // 添加新的提醒
        val savedReminders = mutableListOf<Reminder>()
        reminders.forEach {
            val reminderId = repository.addReminder(it.copy(medicineId = medicine.id))
            savedReminders.add(it.copy(id = reminderId.toInt(), medicineId = medicine.id))
        }
        
        // 添加新的星期设置
        weekdays.forEach {
            repository.addWeekday(it.copy(medicineId = medicine.id))
        }
        
        // 调度提醒
        val scheduler = ReminderScheduler(context)
        scheduler.scheduleReminders(medicine, savedReminders)
    }
}
