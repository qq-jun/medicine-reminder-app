package com.example.medicine.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicine.domain.usecase.DeleteMedicineUseCase
import com.example.medicine.domain.usecase.GetMedicineByIdUseCase
import com.example.medicine.domain.usecase.GetRemindersByMedicineIdUseCase
import com.example.medicine.domain.usecase.GetWeekdaysByMedicineIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor(
    val getMedicineByIdUseCase: GetMedicineByIdUseCase,
    val getRemindersByMedicineIdUseCase: GetRemindersByMedicineIdUseCase,
    val getWeekdaysByMedicineIdUseCase: GetWeekdaysByMedicineIdUseCase,
    val deleteMedicineUseCase: DeleteMedicineUseCase
) : ViewModel() {
}
