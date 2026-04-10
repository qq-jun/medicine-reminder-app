package com.example.medicine.domain.usecase

import com.example.medicine.data.model.Weekday
import com.example.medicine.data.repository.MedicineRepository
import javax.inject.Inject

class GetWeekdaysByMedicineIdUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(medicineId: Int): List<Weekday> {
        return repository.getWeekdaysByMedicineId(medicineId)
    }
}
