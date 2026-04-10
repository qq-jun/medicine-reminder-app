package com.example.medicine.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.medicine.R
import com.example.medicine.data.model.Medicine
import com.example.medicine.domain.usecase.GetMedicinesUseCase
import kotlinx.coroutines.flow.collect

@Composable
fun HomeScreen(
    navController: NavHostController,
    getMedicinesUseCase: GetMedicinesUseCase
) {
    val context = LocalContext.current
    var medicines by remember { mutableStateOf<List<Medicine>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        getMedicinesUseCase().collect {
            medicines = it
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("用药提醒") },
                actions = {
                    IconButton(onClick = { navController.navigate("add") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "添加用药"
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (medicines.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "暂无用药记录",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("add") }) {
                        Text("添加用药")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(medicines) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            onClick = { navController.navigate("detail/${medicine.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineCard(
    medicine: Medicine,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "剂量: ${medicine.dose}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${medicine.meal}服用",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "主治: ${medicine.condition}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
