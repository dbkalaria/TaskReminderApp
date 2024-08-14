package com.example.taskreminderapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var dateInput: TextView
    private lateinit var createTaskButton: Button

    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleInput = findViewById(R.id.taskTitleInput)
        descriptionInput = findViewById(R.id.taskDescriptionInput)
        dateInput = findViewById(R.id.taskDueDateInput)
        createTaskButton = findViewById(R.id.createTaskButton)

        // Check if there's an existing task list passed from TaskDetailActivity
        intent.getParcelableArrayListExtra<Task>("TASK_LIST")?.let {
            tasks.addAll(it)
        }

        dateInput.setOnClickListener {
            showDatePickerDialog()
        }

        createTaskButton.setOnClickListener {
            val taskTitle = titleInput.text.toString()
            val taskDescription = descriptionInput.text.toString()
            val taskDueDate = dateInput.text.toString()

            if (taskTitle.isNotEmpty() && taskDescription.isNotEmpty() && taskDueDate.isNotEmpty()) {
                val newTask = Task(taskTitle, taskDescription, taskDueDate)
                tasks.add(newTask)

                val intent = Intent(this, TaskDetailActivity::class.java).apply {
                    putParcelableArrayListExtra("TASK_LIST", ArrayList(tasks)) // Pass the list of tasks
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                dateInput.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}
