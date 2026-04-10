package com.example.medicine.domain.usecase

import android.content.Context
import com.example.medicine.data.model.Medicine
import com.example.medicine.data.model.Reminder
import com.example.medicine.data.model.Weekday
import com.example.medicine.data.repository.MedicineRepository
import com.example.medicine.service.ReminderScheduler
import javax.inject.Inject

class AddMedicineUseCase @Inject constructor(
    private val repository: MedicineRepository,
    private val context: Context
) {
    suspend operator fun invoke(
        medicine: Medicine,
        reminders: List<Reminder>,
        weekdays: List<Weekday>
    ): Long {
        val medicineId = repository.addMedicine(medicine)
        
        // 添加提醒
        val savedReminders = mutableListOf<Reminder>()
        reminders.forEach {
            val reminderId = repository.addReminder(it.copy(medicineId = medicineId.toInt()))
            savedReminders.add(it.copy(id = reminderId.toInt(), medicineId = medicineId.toInt()))
        }
        
        // 添加星期设置
        weekdays.forEach {
            repository.addWeekday(it.copy(medicineId = medicineId.toInt()))
        }
        
        // 调度提醒
        val scheduler = ReminderScheduler(context)
        scheduler.scheduleReminders(medicine.copy(id = medicineId.toInt()), savedReminders)
        
        return medicineId
    }
}
