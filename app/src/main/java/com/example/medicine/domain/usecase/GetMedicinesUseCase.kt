package com.example.medicine.domain.usecase

import com.example.medicine.data.model.Medicine
import com.example.medicine.data.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMedicinesUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    operator fun invoke(): Flow<List<Medicine>> {
        return repository.getMedicines()
    }
}
