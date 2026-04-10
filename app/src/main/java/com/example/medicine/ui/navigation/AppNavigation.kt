package com.example.medicine.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medicine.ui.screen.AddMedicineScreen
import com.example.medicine.ui.screen.HomeScreen
import com.example.medicine.ui.screen.MedicineDetailScreen
import com.example.medicine.ui.viewmodel.AddMedicineViewModel
import com.example.medicine.ui.viewmodel.HomeViewModel
import com.example.medicine.ui.viewmodel.MedicineDetailViewModel
import dagger.hilt.android.compose.HiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(
                navController = navController,
                getMedicinesUseCase = homeViewModel.getMedicinesUseCase
            )
        }
        composable("add") {
            val addViewModel: AddMedicineViewModel = viewModel()
            AddMedicineScreen(
                navController = navController,
                medicineId = 0,
                addMedicineUseCase = addViewModel.addMedicineUseCase,
                updateMedicineUseCase = addViewModel.updateMedicineUseCase,
                getMedicineByIdUseCase = addViewModel.getMedicineByIdUseCase,
                getRemindersByMedicineIdUseCase = addViewModel.getRemindersByMedicineIdUseCase,
                getWeekdaysByMedicineIdUseCase = addViewModel.getWeekdaysByMedicineIdUseCase
            )
        }
        composable("edit/{medicineId}") {backStackEntry ->
            val medicineId = backStackEntry.arguments?.getString("medicineId")?.toInt() ?: 0
            val addViewModel: AddMedicineViewModel = viewModel()
            AddMedicineScreen(
                navController = navController,
                medicineId = medicineId,
                addMedicineUseCase = addViewModel.addMedicineUseCase,
                updateMedicineUseCase = addViewModel.updateMedicineUseCase,
                getMedicineByIdUseCase = addViewModel.getMedicineByIdUseCase,
                getRemindersByMedicineIdUseCase = addViewModel.getRemindersByMedicineIdUseCase,
                getWeekdaysByMedicineIdUseCase = addViewModel.getWeekdaysByMedicineIdUseCase
            )
        }
        composable("detail/{medicineId}") {backStackEntry ->
            val medicineId = backStackEntry.arguments?.getString("medicineId")?.toInt() ?: 0
            val detailViewModel: MedicineDetailViewModel = viewModel()
            MedicineDetailScreen(
                navController = navController,
                medicineId = medicineId,
                getMedicineByIdUseCase = detailViewModel.getMedicineByIdUseCase,
                getRemindersByMedicineIdUseCase = detailViewModel.getRemindersByMedicineIdUseCase,
                getWeekdaysByMedicineIdUseCase = detailViewModel.getWeekdaysByMedicineIdUseCase,
                deleteMedicineUseCase = detailViewModel.deleteMedicineUseCase
            )
        }
    }
}
