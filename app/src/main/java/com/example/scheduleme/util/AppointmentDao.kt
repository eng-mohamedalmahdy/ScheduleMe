package com.example.scheduleme.util

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scheduleme.pojo.Appointment
import com.example.scheduleme.pojo.Note

@Dao
interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(appointment: Note): Long

    @Query("select * from notes where appointmentId = :id")
    suspend fun getNotes(id: Int): List<Note>?

    @Query("select * from appointments")
    suspend fun getAppointments(): List<Appointment>?

    @Query("delete from appointments where id = :id")
    suspend fun deleteAppointment(id: Int)

    @Query("delete from notes where id = :id")
    suspend fun deleteNote(id: Int)

}

