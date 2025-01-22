package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class StaffAssignedDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var roomTextView: TextView
    private lateinit var assignedByTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var pictureImageView: ImageView
    private lateinit var completeTaskButton: Button
    private lateinit var pauseTaskButton: Button

    private lateinit var firestore: FirebaseFirestore
    private var staffId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_assigned_detail)

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        roomTextView = findViewById(R.id.roomTextView)
        assignedByTextView = findViewById(R.id.assignedByTextView)
        locationTextView = findViewById(R.id.locationTextView)
        pictureImageView = findViewById(R.id.pictureImageView)
        completeTaskButton = findViewById(R.id.completeTask)
        pauseTaskButton = findViewById(R.id.pauseTask)

        firestore = FirebaseFirestore.getInstance()

        // Retrieve data from the intent
        val id = intent.getStringExtra("id")
        val assignedTaskId = intent.getStringExtra("assignedTaskId")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val roomNumber = intent.getStringExtra("roomNumber")
        val assignedBy = intent.getStringExtra("assignedBy")
        val location = intent.getStringExtra("location")
        val photoUrl = intent.getStringExtra("photoUrl")
        val timestamp = intent.getStringExtra("timestamp")
        val userId = intent.getStringExtra("userId")
        val adminId = intent.getStringExtra("adminId")
        staffId = intent.getStringExtra("staffId")

        // Debugging: Log retrieved data
        Log.d("StaffAssignedDetailActivity", "staffId: $staffId")
        Log.d("StaffAssignedDetailActivity", "title: $title")
        Log.d("StaffAssignedDetailActivity", "description: $description")

        // Set data to views
        titleTextView.text = title ?: "No Title"
        descriptionTextView.text = description ?: "No Description"
        roomTextView.text = roomNumber ?: "No Room Number"
        assignedByTextView.text = assignedBy ?: "Unknown"
        locationTextView.text = location ?: "No Location"

        // Load image using Glide
        if (photoUrl != null && photoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.image)
                .into(pictureImageView)
        } else {
            pictureImageView.setImageResource(R.drawable.image)
        }

        // Complete Task Button Click Listener
        completeTaskButton.setOnClickListener {
            if (!assignedTaskId.isNullOrEmpty()) {
                val taskData = mutableMapOf<String, Any>(
                    "id" to id.orEmpty(),
                    "assignedTaskId" to assignedTaskId,
                    "title" to title.orEmpty(),
                    "description" to description.orEmpty(),
                    "roomNumber" to roomNumber.orEmpty(),
                    "assignedBy" to assignedBy.orEmpty(),
                    "location" to location.orEmpty(),
                    "photoUrl" to photoUrl.orEmpty(),
                    "timestamp" to timestamp.orEmpty(),
                    "staffId" to staffId.orEmpty(),
                    "currentDate" to getCurrentDate(),
                    "currentTime" to getCurrentTime()
                )

                val determinedUserType = when {
                    !adminId.isNullOrEmpty() -> {
                        taskData["adminId"] = adminId
                        "admins"
                    }
                    !userId.isNullOrEmpty() -> {
                        taskData["userId"] = userId
                        "users"
                    }
                    else -> "unknown"
                }

                taskData["userType"] = determinedUserType

                firestore.collection("completeTask").document(assignedTaskId)
                    .set(taskData)
                    .addOnSuccessListener {
                        val intent = Intent(this, StaffCompleteTaskActivity::class.java)
                        intent.putExtra("staffId", staffId)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("StaffAssignedDetailActivity", "Error completing task", e)
                    }
            }
        }

        // Pause Task Button Click Listener
        pauseTaskButton.setOnClickListener {
            val intent = Intent(this, StaffPauseTaskActivity::class.java).apply {
                putExtra("id", id)
                putExtra("assignedTaskId", assignedTaskId)
                putExtra("title", title)
                putExtra("description", description)
                putExtra("roomNumber", roomNumber)
                putExtra("assignedBy", assignedBy)
                putExtra("location", location)
                putExtra("photoUrl", photoUrl)
                putExtra("timestamp", timestamp)
                putExtra("userId", userId)
                putExtra("adminId", adminId)
                putExtra("staffId", staffId) // Pass staffId here
            }
            startActivity(intent)
        }

        // Back Button Click Listener
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(Date())
    }
}