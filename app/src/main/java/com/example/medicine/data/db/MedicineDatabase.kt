package com.example.medicine.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medicine.data.db.dao.MedicineDao
import com.example.medicine.data.model.Medicine
import com.example.medicine.data.model.Reminder
import com.example.medicine.data.model.Weekday

@Database(
    entities = [Medicine::class, Reminder::class, Weekday::class],
    version = 1,
    exportSchema = false
)
abstract class MedicineDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    
    companion object {
        @Volatile
        private var INSTANCE: MedicineDatabase? = null
        
        fun getDatabase(context: Context): MedicineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicineDatabase::class.java,
                    "medicine_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
