package com.example.scheduleme.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.scheduleme.R
import com.example.scheduleme.pojo.Appointment
import kotlinx.android.synthetic.main.fragment_appointments_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class MyAppointmentRecyclerViewAdapter(
    private val values: List<Appointment>?,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<MyAppointmentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_appointments_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values!![position]
        initComponents(holder, position)
        setDays(holder, item)
        initListeners(holder, item)
    }

    override fun getItemCount(): Int = values!!.size

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.item_number
        val titleView: TextView = view.title
        val timeView: TextView = view.time
        val daysView: MaterialDayPicker = view.days

    }

    private fun initComponents(holder: ViewHolder, position: Int) {
        val item = values!![position]
        holder.idView.text = (position + 1).toString()
        holder.titleView.text = item.appointmentName
        holder.timeView.text = "from " + item.startingTime + " until " + item.endingTime
    }

    private fun setDays(holder: ViewHolder, item: Appointment) {
        holder.daysView.disableAllDays()
        val daysList = item.days
        val convertedDays = ArrayList<MaterialDayPicker.Weekday>(daysList!!.size)
        daysList.forEach { weekday ->
            val dayOfWeek = when (weekday) {
                Calendar.SUNDAY -> MaterialDayPicker.Weekday.SUNDAY
                Calendar.MONDAY -> MaterialDayPicker.Weekday.MONDAY
                Calendar.TUESDAY -> MaterialDayPicker.Weekday.TUESDAY
                Calendar.WEDNESDAY -> MaterialDayPicker.Weekday.WEDNESDAY
                Calendar.THURSDAY -> MaterialDayPicker.Weekday.THURSDAY
                Calendar.FRIDAY -> MaterialDayPicker.Weekday.FRIDAY
                Calendar.SATURDAY -> MaterialDayPicker.Weekday.SATURDAY
                else -> MaterialDayPicker.Weekday.SUNDAY
            }
            convertedDays.add(dayOfWeek)
        }
        holder.daysView.setSelectedDays(convertedDays)
    }

    private fun initListeners(holder: ViewHolder, item: Appointment) {
        holder.idView.setOnClickListener { doNavigate(item.id) }
        holder.titleView.setOnClickListener { doNavigate(item.id) }
        holder.timeView.setOnClickListener { doNavigate(item.id) }
    }

    private fun doNavigate(id: Int) {
        val bundle: Bundle = Bundle()
        bundle.putInt("id", id)
        activity.findNavController(R.id.nav_host_fragment).navigate(R.id.noteFragment, bundle)
    }


}