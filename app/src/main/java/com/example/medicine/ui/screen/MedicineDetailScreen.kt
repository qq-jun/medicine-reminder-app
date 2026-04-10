package com.example.medicine.ui.screen

import androidx.compose.foundation.layout.*
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
import com.example.medicine.data.model.Reminder
import com.example.medicine.data.model.Weekday
import com.example.medicine.domain.usecase.DeleteMedicineUseCase
import com.example.medicine.domain.usecase.GetMedicineByIdUseCase
import com.example.medicine.domain.usecase.GetRemindersByMedicineIdUseCase
import com.example.medicine.domain.usecase.GetWeekdaysByMedicineIdUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun MedicineDetailScreen(
    navController: NavHostController,
    medicineId: Int,
    getMedicineByIdUseCase: GetMedicineByIdUseCase,
    getRemindersByMedicineIdUseCase: GetRemindersByMedicineIdUseCase,
    getWeekdaysByMedicineIdUseCase: GetWeekdaysByMedicineIdUseCase,
    deleteMedicineUseCase: DeleteMedicineUseCase
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var medicine by remember { mutableStateOf<Medicine?>(null) }
    var reminders by remember { mutableStateOf<List<Reminder>>(emptyList()) }
    var weekdays by remember { mutableStateOf<List<Weekday>>(emptyList()) }
    
    // 加载数据
    LaunchedEffect(medicineId) {
        medicine = getMedicineByIdUseCase(medicineId)
        reminders = getRemindersByMedicineIdUseCase(medicineId)
        weekdays = getWeekdaysByMedicineIdUseCase(medicineId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("药品详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("edit/$medicineId") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "编辑"
                        )
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            deleteMedicineUseCase(medicineId)
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "删除"
                        )
                    }
                }
            )
        }
    ) {
        medicine?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 药品基本信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "剂量: ${it.dose}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${it.meal}服用",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "主治: ${it.condition}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (it.remark.isNotEmpty()) {
                            Text(
                                text = "备注: ${it.remark}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // 用药规则
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "用药规则",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "重复类型: ${getRepeatTypeText(it.repeatType, it.intervalDays, weekdays)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "提醒音: ${it.tone}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "音量: ${(it.volume * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // 提醒时间
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "提醒时间",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        reminders.forEach {
                            Text(
                                text = "${getPeriodText(it.period)}: ${it.time}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        } ?: run {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text("加载中...")
            }
        }
    }
}

fun getRepeatTypeText(repeatType: String, intervalDays: Int, weekdays: List<Weekday>): String {
    return when (repeatType) {
        "daily1" -> "每天1次"
        "daily2" -> "每天2次"
        "daily3" -> "每天3次"
        "interval" -> "间隔 $intervalDays 天"
        "weekly" -> {
            val days = weekdays.map { it.day }.sorted()
            val dayNames = listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
            "每周 ${days.map { dayNames[it] }.joinToString("、")}"
        }
        else -> "未知"
    }
}

fun getPeriodText(period: String): String {
    return when (period) {
        "morning" -> "早上"
        "noon" -> "中午"
        "evening" -> "晚上"
        else -> period
    }
}
