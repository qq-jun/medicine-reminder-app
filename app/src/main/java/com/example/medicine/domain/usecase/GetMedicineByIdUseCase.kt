package com.example.medicine.domain.usecase

import com.example.medicine.data.model.Medicine
import com.example.medicine.data.repository.MedicineRepository
import javax.inject.Inject

class GetMedicineByIdUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(medicineId: Int): Medicine? {
        return repository.getMedicineById(medicineId)
    }
}
