package com.example.scheduleme.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "appointments")
data class Appointment(
    val appointmentName: String,
    val startingTime: String,
    val endingTime: String,
    val days: List<Int>?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


    fun hasDate(a: Appointment): Boolean {
        val hours =
            a.startingTime.substring(0, a.startingTime.indexOf(":")).trim { it <= ' ' }.toInt()
        val thisHours =
            startingTime.substring(0, startingTime.indexOf(":")).trim { it <= ' ' }
                .toInt()
        val minutes =
            a.startingTime.substring(a.startingTime.indexOf(":") + 1).trim { it <= ' ' }.toInt()
        val thisMinutes =
            startingTime.substring(startingTime.indexOf(":") + 1).trim { it <= ' ' }
                .toInt()
        val endHours =
            a.endingTime.substring(0, a.endingTime.indexOf(":")).trim { it <= ' ' }.toInt()
        val thisEndHours =
            endingTime.substring(0, endingTime.indexOf(":")).trim { it <= ' ' }.toInt()
        val endMinutes =
            a.endingTime.substring(a.endingTime.indexOf(":") + 1).trim { it <= ' ' }.toInt()
        val thisEndMinutes =
            endingTime.substring(endingTime.indexOf(":") + 1).trim { it <= ' ' }.toInt()
        for (elementi in days!!) {
            for (elementj in a.days!!) {
                if (elementi == elementj) {
                    if (hours == thisHours && minutes == thisMinutes) return true
                    if (hours > thisEndHours) continue
                    if (hours == thisEndHours && minutes >= thisEndMinutes) continue
                    if (endHours < thisHours) continue
                    if (endHours == thisHours && endMinutes <= thisMinutes) continue
                    return true
                }
            }
        }
        return false
    }
}