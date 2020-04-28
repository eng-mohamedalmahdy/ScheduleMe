package com.example.scheduleme.pojo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Notes",
    foreignKeys = [ForeignKey(
        entity = Appointment::class,
        parentColumns = ["id"],
        childColumns = ["appointmentId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Note(val noteBody: String, val appointmentId: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}