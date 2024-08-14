package com.example.taskreminderapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val title: String,
    val description: String,
    val dueDate: String
) : Parcelable
