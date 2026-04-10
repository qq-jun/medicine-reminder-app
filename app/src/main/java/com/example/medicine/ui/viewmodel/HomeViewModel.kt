package com.example.medicine.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicine.domain.usecase.GetMedicinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val getMedicinesUseCase: GetMedicinesUseCase
) : ViewModel() {
}
