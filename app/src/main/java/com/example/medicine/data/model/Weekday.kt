package com.example.medicine.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekdays",
    foreignKeys = [
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Weekday(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val day: Int // 0-6，0表示周日
)
