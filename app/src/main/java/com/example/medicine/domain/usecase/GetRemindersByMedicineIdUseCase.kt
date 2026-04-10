package com.example.medicine.domain.usecase

import com.example.medicine.data.model.Reminder
import com.example.medicine.data.repository.MedicineRepository
import javax.inject.Inject

class GetRemindersByMedicineIdUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(medicineId: Int): List<Reminder> {
        return repository.getRemindersByMedicineId(medicineId)
    }
}
