package com.example.medicine.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.medicine.data.model.Medicine
import com.example.medicine.data.model.Reminder
import com.example.medicine.data.model.Weekday
import com.example.medicine.domain.usecase.AddMedicineUseCase
import com.example.medicine.domain.usecase.GetMedicineByIdUseCase
import com.example.medicine.domain.usecase.GetRemindersByMedicineIdUseCase
import com.example.medicine.domain.usecase.GetWeekdaysByMedicineIdUseCase
import com.example.medicine.domain.usecase.UpdateMedicineUseCase
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@Composable
fun AddMedicineScreen(
    navController: NavHostController,
    medicineId: Int = 0,
    addMedicineUseCase: AddMedicineUseCase,
    updateMedicineUseCase: UpdateMedicineUseCase,
    getMedicineByIdUseCase: GetMedicineByIdUseCase,
    getRemindersByMedicineIdUseCase: GetRemindersByMedicineIdUseCase,
    getWeekdaysByMedicineIdUseCase: GetWeekdaysByMedicineIdUseCase
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // 表单状态
    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var meal by remember { mutableStateOf("饭前") }
    var condition by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var repeatType by remember { mutableStateOf("daily1") }
    var intervalDays by remember { mutableStateOf(1) }
    var tone by remember { mutableStateOf("default") }
    var volume by remember { mutableStateOf(0.5f) }
    
    // 时间段状态
    var morningEnabled by remember { mutableStateOf(false) }
    var noonEnabled by remember { mutableStateOf(false) }
    var eveningEnabled by remember { mutableStateOf(false) }
    var morningTime by remember { mutableStateOf("08:00") }
    var noonTime by remember { mutableStateOf("12:00") }
    var eveningTime by remember { mutableStateOf("20:00") }
    
    // 星期状态
    var weekdays by remember { mutableStateOf(List(7) { false }) }
    
    // 加载现有数据（编辑模式）
    LaunchedEffect(medicineId) {
        if (medicineId > 0) {
            val medicine = getMedicineByIdUseCase(medicineId)
            if (medicine != null) {
                name = medicine.name
                dose = medicine.dose
                meal = medicine.meal
                condition = medicine.condition
                remark = medicine.remark
                repeatType = medicine.repeatType
                intervalDays = medicine.intervalDays
                tone = medicine.tone
                volume = medicine.volume
                
                // 加载提醒设置
                val reminders = getRemindersByMedicineIdUseCase(medicineId)
                reminders.forEach {
                    when (it.period) {
                        "morning" -> {
                            morningEnabled = true
                            morningTime = it.time
                        }
                        "noon" -> {
                            noonEnabled = true
                            noonTime = it.time
                        }
                        "evening" -> {
                            eveningEnabled = true
                            eveningTime = it.time
                        }
                    }
                }
                
                // 加载星期设置
                val weekdayList = getWeekdaysByMedicineIdUseCase(medicineId)
                val newWeekdays = MutableList(7) { false }
                weekdayList.forEach {
                    if (it.day in 0..6) {
                        newWeekdays[it.day] = true
                    }
                }
                weekdays = newWeekdays
            }
        }
    }
    
    // 处理重复类型变化
    LaunchedEffect(repeatType) {
        when (repeatType) {
            "daily1" -> {
                morningEnabled = true
                noonEnabled = false
                eveningEnabled = false
            }
            "daily2" -> {
                morningEnabled = true
                noonEnabled = false
                eveningEnabled = true
            }
            "daily3" -> {
                morningEnabled = true
                noonEnabled = true
                eveningEnabled = true
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (medicineId > 0) "编辑用药" else "添加用药") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 药品名称
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("药品名称") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 剂量
            OutlinedTextField(
                value = dose,
                onValueChange = { dose = it },
                label = { Text("用药剂量") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 饭前/饭后
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("饭前/饭后:", modifier = Modifier.alignByBaseline())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RadioButton(
                        selected = meal == "饭前",
                        onClick = { meal = "饭前" }
                    )
                    Text("饭前", modifier = Modifier.alignByBaseline())
                    RadioButton(
                        selected = meal == "饭后",
                        onClick = { meal = "饭后" }
                    )
                    Text("饭后", modifier = Modifier.alignByBaseline())
                }
            }
            
            // 主治症状
            OutlinedTextField(
                value = condition,
                onValueChange = { condition = it },
                label = { Text("主治症状") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 备注
            OutlinedTextField(
                value = remark,
                onValueChange = { remark = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            // 重复类型
            Column {
                Text("用药规则:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = repeatType == "daily1",
                        onClick = { repeatType = "daily1" }
                    )
                    Text("每天1次")
                    RadioButton(
                        selected = repeatType == "daily2",
                        onClick = { repeatType = "daily2" }
                    )
                    Text("每天2次")
                    RadioButton(
                        selected = repeatType == "daily3",
                        onClick = { repeatType = "daily3" }
                    )
                    Text("每天3次")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = repeatType == "interval",
                        onClick = { repeatType = "interval" }
                    )
                    Text("间隔天数")
                    if (repeatType == "interval") {
                        OutlinedTextField(
                            value = intervalDays.toString(),
                            onValueChange = { 
                                intervalDays = it.toIntOrNull() ?: 1 
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp)
                        )
                    }
                    RadioButton(
                        selected = repeatType == "weekly",
                        onClick = { repeatType = "weekly" }
                    )
                    Text("特定星期")
                }
            }
            
            // 星期选择
            if (repeatType == "weekly") {
                Column {
                    Text("选择星期:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("日", "一", "二", "三", "四", "五", "六").forEachIndexed { index, day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Checkbox(
                                    checked = weekdays[index],
                                    onCheckedChange = { 
                                        val newWeekdays = weekdays.toMutableList()
                                        newWeekdays[index] = it
                                        weekdays = newWeekdays
                                    }
                                )
                                Text(day)
                            }
                        }
                    }
                }
            }
            
            // 时间段设置
            Column {
                Text("用药时间段:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = morningEnabled,
                                onCheckedChange = { morningEnabled = it }
                            )
                            Text("早上")
                        }
                        if (morningEnabled) {
                            OutlinedTextField(
                                value = morningTime,
                                onValueChange = { morningTime = it },
                                label = { Text("时间") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = noonEnabled,
                                onCheckedChange = { noonEnabled = it }
                            )
                            Text("中午")
                        }
                        if (noonEnabled) {
                            OutlinedTextField(
                                value = noonTime,
                                onValueChange = { noonTime = it },
                                label = { Text("时间") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = eveningEnabled,
                                onCheckedChange = { eveningEnabled = it }
                            )
                            Text("晚上")
                        }
                        if (eveningEnabled) {
                            OutlinedTextField(
                                value = eveningTime,
                                onValueChange = { eveningTime = it },
                                label = { Text("时间") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            // 提醒音设置
            Column {
                Text("提醒音:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = tone,
                        onValueChange = { tone = it },
                        label = { Text("提醒音") },
                        modifier = Modifier.weight(1f)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("音量: ${(volume * 100).toInt()}%")
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..1f
                        )
                    }
                }
            }
            
            // 保存按钮
            Button(
                onClick = {
                    coroutineScope.launch {
                        val now = System.currentTimeMillis()
                        val medicine = Medicine(
                            id = if (medicineId > 0) medicineId else 0,
                            name = name,
                            dose = dose,
                            meal = meal,
                            condition = condition,
                            remark = remark,
                            repeatType = repeatType,
                            intervalDays = intervalDays,
                            tone = tone,
                            volume = volume,
                            createdAt = if (medicineId > 0) getMedicineByIdUseCase(medicineId)?.createdAt ?: now else now,
                            updatedAt = now
                        )
                        
                        // 构建提醒列表
                        val reminders = mutableListOf<Reminder>()
                        if (morningEnabled) {
                            reminders.add(Reminder(
                                medicineId = 0, // 会在Use Case中设置
                                period = "morning",
                                time = morningTime,
                                remindedDate = null
                            ))
                        }
                        if (noonEnabled) {
                            reminders.add(Reminder(
                                medicineId = 0,
                                period = "noon",
                                time = noonTime,
                                remindedDate = null
                            ))
                        }
                        if (eveningEnabled) {
                            reminders.add(Reminder(
                                medicineId = 0,
                                period = "evening",
                                time = eveningTime,
                                remindedDate = null
                            ))
                        }
                        
                        // 构建星期列表
                        val weekdayList = mutableListOf<Weekday>()
                        if (repeatType == "weekly") {
                            weekdays.forEachIndexed { index, checked ->
                                if (checked) {
                                    weekdayList.add(Weekday(
                                        medicineId = 0,
                                        day = index
                                    ))
                                }
                            }
                        }
                        
                        if (medicineId > 0) {
                            updateMedicineUseCase(medicine, reminders, weekdayList)
                        } else {
                            addMedicineUseCase(medicine, reminders, weekdayList)
                        }
                        
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(if (medicineId > 0) "保存修改" else "添加用药")
            }
        }
    }
}
