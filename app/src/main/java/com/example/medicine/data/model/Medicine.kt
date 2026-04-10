package com.example.medicine.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dose: String,
    val meal: String, // 饭前/饭后
    val condition: String,
    val remark: String,
    val repeatType: String, // daily1, daily2, daily3, interval, weekly
    val intervalDays: Int,
    val tone: String,
    val volume: Float,
    val createdAt: Long,
    val updatedAt: Long
)
