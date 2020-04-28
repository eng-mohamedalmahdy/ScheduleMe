package com.example.scheduleme.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scheduleme.R
import com.example.scheduleme.util.AlarmReceiver
import com.example.scheduleme.util.AppointmentsDatabase
import com.example.scheduleme.util.MyAppointmentRecyclerViewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A fragment representing a list of Items.
 */
class AppointmentsFragment : Fragment() {

    lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointments_list, container, false)
        recyclerView = view.findViewById(R.id.list)
        fab = view.findViewById(R.id.fab)
        // Set the adapter
        fab.setOnClickListener { v ->
            v.findNavController().navigate(R.id.newAppointmentFragment)
        }
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            CoroutineScope(Main).launch {
                val data = withContext(IO) {
                    AppointmentsDatabase.getDatabase(context).getQueries().getAppointments()
                }?.toMutableList()
                if (data!!.isEmpty()) {
                    view.findViewById<ConstraintLayout>(R.id.list_container).visibility =
                        GONE
                }
                adapter = MyAppointmentRecyclerViewAdapter(data, activity!!)
                val itemTouchHelper: ItemTouchHelper =
                    ItemTouchHelper(object :
                        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val position = viewHolder.adapterPosition
                            val item = data.get(position)
                            data.remove(item)
                            adapter?.notifyDataSetChanged()
                            var test = ""
                            item.days?.forEach {
                                val ids = "${it
                                }${item.id}"
                                val intent: Intent = Intent(context, AlarmReceiver::class.java)
                                val pi: PendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    ids.toInt(),
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )
                                test += "$ids \n"
                                val alarmManager: AlarmManager =
                                    activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                alarmManager.cancel(pi)
                                pi.cancel()

                            }
                            CoroutineScope(IO).launch {
                                AppointmentsDatabase.getDatabase(context).getQueries()
                                    .deleteAppointment(item.id)
                            }

                        }
                    })
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )

        }

        return view
    }
}