package com.example.medicine.domain.usecase

import android.content.Context
import com.example.medicine.data.repository.MedicineRepository
import com.example.medicine.service.ReminderScheduler
import javax.inject.Inject

class DeleteMedicineUseCase @Inject constructor(
    private val repository: MedicineRepository,
    private val context: Context
) {
    suspend operator fun invoke(medicineId: Int) {
        // 取消提醒
        val scheduler = ReminderScheduler(context)
        scheduler.cancelReminders(medicineId)
        
        // 删除药品
        repository.deleteMedicine(medicineId)
    }
}
