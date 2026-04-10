package com.example.medicine.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.medicine.data.model.Medicine
import com.example.medicine.data.model.Reminder
import com.example.medicine.data.model.Weekday
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    // Medicine相关操作
    @Insert
    suspend fun insert(medicine: Medicine): Long
    
    @Update
    suspend fun update(medicine: Medicine)
    
    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun delete(id: Int)
    
    @Query("SELECT * FROM medicines ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Medicine>>
    
    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getById(id: Int): Medicine?
    
    // Reminder相关操作
    @Insert
    suspend fun insertReminder(reminder: Reminder): Long
    
    @Update
    suspend fun updateReminder(reminder: Reminder)
    
    @Query("DELETE FROM reminders WHERE medicineId = :medicineId")
    suspend fun deleteRemindersByMedicineId(medicineId: Int)
    
    @Query("SELECT * FROM reminders WHERE medicineId = :medicineId")
    suspend fun getRemindersByMedicineId(medicineId: Int): List<Reminder>
    
    @Query("UPDATE reminders SET remindedDate = :date WHERE id = :id")
    suspend fun updateRemindedStatus(id: Int, date: String)
    
    // Weekday相关操作
    @Insert
    suspend fun insertWeekday(weekday: Weekday): Long
    
    @Query("DELETE FROM weekdays WHERE medicineId = :medicineId")
    suspend fun deleteWeekdaysByMedicineId(medicineId: Int)
    
    @Query("SELECT * FROM weekdays WHERE medicineId = :medicineId")
    suspend fun getWeekdaysByMedicineId(medicineId: Int): List<Weekday>
}
