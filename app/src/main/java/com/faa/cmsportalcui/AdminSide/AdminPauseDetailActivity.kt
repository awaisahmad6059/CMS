package com.faa.cmsportalcui.AdminSide

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminPauseDetailActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pause_detail)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Handle back button click
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        // Handle close button click
        val closeButton: Button = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            finish()
        }

        // Retrieve data from intent
        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val currentTime = intent.getStringExtra("currentTime")
        val description = intent.getStringExtra("description")
        val location = intent.getStringExtra("location")
        val photoUrl = intent.getStringExtra("photoUrl")
        val roomNumber = intent.getStringExtra("roomNumber")
        val staffId = intent.getStringExtra("staffId")
        val timestamp = intent.getStringExtra("timestamp")
        val userId = intent.getStringExtra("userId")
        val userType = intent.getStringExtra("userType")
        val assignedBy = intent.getStringExtra("assignedBy")
        val assignedTaskId = intent.getStringExtra("assignedTaskId")
        val comment = intent.getStringExtra("comment")
        val currentDate = intent.getStringExtra("currentDate")

        // Display data in TextViews
        findViewById<TextView>(R.id.reqIdText).text = "$id"
        findViewById<TextView>(R.id.comment).text = "$comment"

        // Handle mark as completed button click
        val markAsButton: Button = findViewById(R.id.markasButton)
        markAsButton.setOnClickListener {
            if (assignedTaskId != null) {
                deleteTaskFromFirestore(assignedTaskId)
            } else {
                Toast.makeText(this, "Task ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteTaskFromFirestore(taskId: String) {
        println("Attempting to delete task with ID: $taskId") // Debugging line
        firestore.collection("pauseTask").document(taskId)
            .delete()
            .addOnSuccessListener {
                println("Task deleted successfully") // Debugging line
                Toast.makeText(this, "Task marked as completed and deleted", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after deletion
            }
            .addOnFailureListener { exception ->
                println("Failed to delete task: ${exception.message}") // Debugging line
                Toast.makeText(this, "Failed to delete task: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}