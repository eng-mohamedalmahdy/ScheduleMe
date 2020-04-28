package com.example.scheduleme.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ca.antonious.materialdaypicker.MaterialDayPicker
import ca.antonious.materialdaypicker.MaterialDayPicker.Weekday.*
import com.example.scheduleme.R
import com.example.scheduleme.databinding.FragmentNewAppointmentBinding
import com.example.scheduleme.pojo.Appointment
import com.example.scheduleme.util.AlarmReceiver
import com.example.scheduleme.util.AppointmentsDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class NewAppointmentFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    var start = true
    lateinit var title: EditText
    lateinit var startingTime: Button
    lateinit var endingTime: Button
    lateinit var fab: FloatingActionButton
    lateinit var binding: FragmentNewAppointmentBinding
    lateinit var days: MaterialDayPicker


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_new_appointment,
            container,
            false
        )
        val view = binding.root
        initComponents()
        initListener(view)
        val calendar = Calendar.getInstance()
        val timeHolder: String =
            "${calendar.get(Calendar.HOUR_OF_DAY)} : ${calendar.get(Calendar.MINUTE)}"
        startingTime.text = timeHolder
        endingTime.text = timeHolder
        return view
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (start) {
            startingTime.text = "$hourOfDay : $minute"
        } else {
            endingTime.text = "$hourOfDay : $minute"
        }
    }

    private fun setAlarm(id: Long, time: Long, content: String) {
        val alarmManager: AlarmManager =
            activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent: Intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("content", content)
        val pi: PendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY * 7, pi)
    }

    private fun initComponents() {

        title = binding.appointmentTitle
        startingTime = binding.startTime
        endingTime = binding.endTime
        fab = binding.fab
        days = binding.days
    }

    private fun initListener(view: View) {
        startingTime.setOnClickListener {
            val timePickerFragment = TimePickerFragment(this)
            start = true
            timePickerFragment.show(activity!!.supportFragmentManager, "Starting time")
        }
        endingTime.setOnClickListener {
            val timePickerFragment = TimePickerFragment(this)
            start = false
            timePickerFragment.show(activity!!.supportFragmentManager, "Ending time")
        }
        fab.setOnClickListener {
            val selectedDays = days.selectedDays
            val list = IntArray(selectedDays.size)
            selectedDays.forEachIndexed { index, weekday ->
                val dayOfWeek = when (weekday) {
                    SUNDAY -> Calendar.SUNDAY
                    MONDAY -> Calendar.MONDAY
                    TUESDAY -> Calendar.TUESDAY
                    WEDNESDAY -> Calendar.WEDNESDAY
                    THURSDAY -> Calendar.THURSDAY
                    FRIDAY -> Calendar.FRIDAY
                    SATURDAY -> Calendar.SATURDAY
                    else -> 0
                }
                list[index] = dayOfWeek
            }
            CoroutineScope(Main).launch {
                val text = title.text.toString()
                when {
                    text.isEmpty() -> {
                        title.error = "Title can't be empty"
                    }
                    selectedDays.isEmpty() -> {
                        title.error = "Please select at least one day"
                    }
                    else -> {
                        title.error = null
                        title.error = null
                        val appointment = Appointment(
                            text,
                            startingTime.text.toString(),
                            endingTime.text.toString(),
                            list.toList()
                        )
                        val currentAppointments = withContext(IO) {
                            AppointmentsDatabase.getDatabase(context!!).getQueries()
                                .getAppointments()
                        }
                        val startHour = startingTime.text.split(" : ")[0].toInt()
                        val startMinute = startingTime.text.split(" : ")[1].toInt()
                        val endHour = endingTime.text.split(" : ")[0].toInt()
                        val endMinute = endingTime.text.split(" : ")[1].toInt()

                        val startCalendar = Calendar.getInstance()
                        startCalendar.set(Calendar.HOUR_OF_DAY, startHour)
                        startCalendar.set(Calendar.MINUTE, startMinute)
                        val endCalendar = Calendar.getInstance()
                        endCalendar.set(Calendar.HOUR_OF_DAY, endHour)
                        endCalendar.set(Calendar.MINUTE, endMinute)
                        if (startCalendar.timeInMillis < endCalendar.timeInMillis) {
                            var invalid = false
                            for (currentAppointment in currentAppointments!!) {
                                if (appointment.hasDate(currentAppointment)) {
                                    invalid = true
                                    break
                                }
                            }
                            if (!invalid || currentAppointments.isEmpty()) {
                                val id = withContext(IO) {
                                    AppointmentsDatabase.getDatabase(context!!).getQueries()
                                        .insertAppointment(appointment)
                                }


                                list.forEachIndexed { i, item ->
                                    val calendar: Calendar = Calendar.getInstance()
                                    calendar.set(Calendar.DAY_OF_WEEK, item)
                                    calendar.set(Calendar.HOUR_OF_DAY, startHour)
                                    calendar.set(Calendar.MINUTE, startMinute)
                                    calendar.set(Calendar.SECOND, 0)
                                    if (calendar.timeInMillis < System.currentTimeMillis()) {
                                        calendar.add(Calendar.DAY_OF_YEAR, 7)
                                    }
                                    val time = calendar.timeInMillis
                                    setAlarm(
                                        "${i + 1}$id".toLong(),
                                        time,
                                        appointment.appointmentName
                                    )
                                }
                                view.findNavController().navigate(R.id.appointmentsFragment)
                            } else {
                                title.error = "you are busy in that time !"
                            }
                        } else {
                            title.error = "you have ended it before it start !"
                        }
                    }
                }

            }
        }
    }
}