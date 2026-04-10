package com.example.medicine.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val period: String, // morning, noon, evening
    val time: String, // HH:mm格式
    val remindedDate: String? // yyyy-MM-dd格式
)
