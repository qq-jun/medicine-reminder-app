package com.example.medicine.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicine.domain.usecase.AddMedicineUseCase
import com.example.medicine.domain.usecase.GetMedicineByIdUseCase
import com.example.medicine.domain.usecase.GetRemindersByMedicineIdUseCase
import com.example.medicine.domain.usecase.GetWeekdaysByMedicineIdUseCase
import com.example.medicine.domain.usecase.UpdateMedicineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddMedicineViewModel @Inject constructor(
    val addMedicineUseCase: AddMedicineUseCase,
    val updateMedicineUseCase: UpdateMedicineUseCase,
    val getMedicineByIdUseCase: GetMedicineByIdUseCase,
    val getRemindersByMedicineIdUseCase: GetRemindersByMedicineIdUseCase,
    val getWeekdaysByMedicineIdUseCase: GetWeekdaysByMedicineIdUseCase
) : ViewModel() {
}
