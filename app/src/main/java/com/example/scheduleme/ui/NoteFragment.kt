package com.example.scheduleme.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.scheduleme.R
import com.example.scheduleme.pojo.Note
import com.example.scheduleme.util.AppointmentsDatabase
import com.example.scheduleme.util.MyNoteRecyclerViewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A fragment representing a list of Items.
 */
class NoteFragment : Fragment() {
    lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private var appointmentId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appointmentId = arguments?.get("id") as Int
        var view = inflater.inflate(R.layout.fragment_notes_list, container, false)
        recyclerView = view.findViewById(R.id.list)
        fab = view.findViewById(R.id.fab)
        // Set the adapter
        fab.setOnClickListener { v ->
            showDialog()

        }
        with(recyclerView) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            CoroutineScope(Main).launch {
                val data = withContext(IO) {
                    AppointmentsDatabase.getDatabase(context)
                        .getQueries().getNotes(appointmentId)
                }?.toMutableList()!!
                if (data.isEmpty()) {
                    view.findViewById<ConstraintLayout>(R.id.list_container).visibility =
                        View.GONE
                }
                adapter =
                    MyNoteRecyclerViewAdapter(data)
                val itemTouchHelper: ItemTouchHelper =
                    ItemTouchHelper(object :
                        ItemTouchHelper.SimpleCallback(
                            0,
                            ItemTouchHelper.LEFT
                        ) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val position = viewHolder.adapterPosition
                            val item = data[position]
                            data.remove(item)
                            adapter?.notifyDataSetChanged()
                            CoroutineScope(IO).launch {
                                AppointmentsDatabase.getDatabase(context).getQueries()
                                    .deleteNote(item.id)
                            }
                        }
                    })
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    context,
                    androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
        }
        return view
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
        builder.setTitle("Add new Note to this Appointment")

        // Set up the input

        // Set up the input
        val input = EditText(context!!)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        // Set up the buttons
        builder.setPositiveButton(
            "Add note"
        ) { _, _ ->
            CoroutineScope(Main).launch {
                val note: Note = Note(input.text.toString(), appointmentId)
                val res = withContext(IO) {
                    AppointmentsDatabase.getDatabase(context!!).getQueries().insertNote(note)
                }
                recyclerView.adapter?.notifyDataSetChanged()

            }
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

}