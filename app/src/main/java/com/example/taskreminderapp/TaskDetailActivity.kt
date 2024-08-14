package com.example.taskreminderapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var backToCreateTaskButton: Button
    private var taskList: ArrayList<Task> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        recyclerView = findViewById(R.id.recyclerView)
        backToCreateTaskButton = findViewById(R.id.backToCreateTaskButton)
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskList = intent.getParcelableArrayListExtra("TASK_LIST") ?: arrayListOf()
        if (taskList.isNotEmpty()) {
            adapter = TaskAdapter(taskList)
            recyclerView.adapter = adapter

            val latestTask = taskList.last()
            requestNotificationPermissionAndShowNotification(latestTask)
        }

        backToCreateTaskButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putParcelableArrayListExtra("TASK_LIST", taskList)
            }
            startActivity(intent)
        }
    }

    private fun requestNotificationPermissionAndShowNotification(task: Task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                createNotificationChannel()
                sendNotification(task)
            }
        } else {
            createNotificationChannel()
            sendNotification(task)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, proceed with sending the notification
            val latestTask = taskList.lastOrNull()
            if (latestTask != null) {
                sendNotification(latestTask)
            }
        } else {
            Toast.makeText(this, "Notification permission is required to remind you about your tasks.", Toast.LENGTH_LONG).show()

            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }
    }


    private fun sendNotification(task: Task) {
        val builder = NotificationCompat.Builder(this, "taskChannel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Task Reminder")
            .setContentText("${task.title} is due on ${task.dueDate}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 1000))
            .setLights(0xFF0000.toInt(), 3000, 3000)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Channel"
            val descriptionText = "Channel for task reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("taskChannel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
