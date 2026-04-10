package com.example.medicine.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.medicine.data.db.MedicineDatabase
import com.example.medicine.data.model.Medicine
import com.example.medicine.data.model.Reminder
import com.example.medicine.data.model.Weekday
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MedicineRepository @Inject constructor(
    private val database: MedicineDatabase
) {
    private val dao = database.medicineDao()
    
    // Medicine相关操作
    suspend fun addMedicine(medicine: Medicine): Long {
        return dao.insert(medicine)
    }
    
    fun getMedicines(): Flow<List<Medicine>> {
        return dao.getAll()
    }
    
    suspend fun updateMedicine(medicine: Medicine) {
        dao.update(medicine)
    }
    
    suspend fun deleteMedicine(id: Int) {
        dao.delete(id)
    }
    
    suspend fun getMedicineById(id: Int): Medicine? {
        return dao.getById(id)
    }
    
    // Reminder相关操作
    suspend fun addReminder(reminder: Reminder): Long {
        return dao.insertReminder(reminder)
    }
    
    suspend fun updateReminder(reminder: Reminder) {
        dao.updateReminder(reminder)
    }
    
    suspend fun deleteRemindersByMedicineId(medicineId: Int) {
        dao.deleteRemindersByMedicineId(medicineId)
    }
    
    suspend fun getRemindersByMedicineId(medicineId: Int): List<Reminder> {
        return dao.getRemindersByMedicineId(medicineId)
    }
    
    suspend fun updateRemindedStatus(reminderId: Int, date: String) {
        dao.updateRemindedStatus(reminderId, date)
    }
    
    // Weekday相关操作
    suspend fun addWeekday(weekday: Weekday): Long {
        return dao.insertWeekday(weekday)
    }
    
    suspend fun deleteWeekdaysByMedicineId(medicineId: Int) {
        dao.deleteWeekdaysByMedicineId(medicineId)
    }
    
    suspend fun getWeekdaysByMedicineId(medicineId: Int): List<Weekday> {
        return dao.getWeekdaysByMedicineId(medicineId)
    }
}
