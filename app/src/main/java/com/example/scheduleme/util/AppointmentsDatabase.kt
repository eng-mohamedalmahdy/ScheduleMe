package com.example.scheduleme.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.scheduleme.R
import com.example.scheduleme.pojo.Appointment
import com.example.scheduleme.pojo.Note
import java.util.*


@Database(entities = [Appointment::class, Note::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppointmentsDatabase : RoomDatabase() {
    abstract fun getQueries(): AppointmentDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppointmentsDatabase? = null

        fun getDatabase(context: Context): AppointmentsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppointmentsDatabase::class.java,
                    (context.getString(R.string.app_name)).toLowerCase(Locale.ROOT) + "_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}