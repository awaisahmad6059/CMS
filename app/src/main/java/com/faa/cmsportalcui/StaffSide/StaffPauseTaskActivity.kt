package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class StaffPauseTaskActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var reqIdText: TextView
    private lateinit var reqTitleText: TextView
    private lateinit var reqTimeText: TextView
    private lateinit var commentDescription: EditText
    private lateinit var submitButton: Button
    private lateinit var requestBtn: Button
    private lateinit var backButton: ImageButton

    private var staffId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_pause_task)

        firestore = FirebaseFirestore.getInstance()

        reqIdText = findViewById(R.id.reqIdText)
        reqTitleText = findViewById(R.id.taskTitleLabel)
        reqTimeText = findViewById(R.id.taskStartTime)
        commentDescription = findViewById(R.id.commentDescription)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.back_button)
        requestBtn = findViewById(R.id.requestBtn)

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

        Log.d("StaffPauseTaskActivity", "staffId: $staffId")
        Log.d("StaffPauseTaskActivity", "title: $title")

        if (staffId != null) {
            Toast.makeText(this, "Staff ID: $staffId", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Staff ID is null", Toast.LENGTH_SHORT).show()
        }

        reqIdText.text = id ?: "No ID"
        reqTitleText.text = "Title: ${title ?: ""}"
        reqTimeText.text = timestamp ?: ""

        submitButton.setOnClickListener {
            val comment = commentDescription.text.toString().trim()

            if (assignedTaskId != null) {
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
                    "staffId" to staffId.orEmpty(), // Use staffId here
                    "comment" to comment, // Save the comment
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

                firestore.collection("pauseTask").document(assignedTaskId)
                    .set(taskData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task Paused Successfully", Toast.LENGTH_SHORT).show()

                        if (staffId != null) {
                            val intent = Intent(this, StaffPauseSentActivity::class.java)
                            intent.putExtra("staff_id", staffId)
                            startActivity(intent)

                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this, StaffDashboardActivity::class.java)
                                intent.putExtra("staff_id", staffId)
                                startActivity(intent)
                                finish()
                            }, 3000)
                        } else {
                            Toast.makeText(this, "Staff ID is null", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to Pause Task", Toast.LENGTH_SHORT).show()
                        Log.e("StaffPauseTaskActivity", "Error pausing task", e)
                    }
            }
        }
        requestBtn.setOnClickListener {
            val intent = Intent(this, StaffEquipmentRequestActivity::class.java)
            intent.putExtra("id", id.orEmpty())
            intent.putExtra("assignedTaskId", assignedTaskId.orEmpty())
            intent.putExtra("title", title.orEmpty())
            intent.putExtra("description", description.orEmpty())
            intent.putExtra("roomNumber", roomNumber.orEmpty())
            intent.putExtra("assignedBy", assignedBy.orEmpty())
            intent.putExtra("location", location.orEmpty())
            intent.putExtra("photoUrl", photoUrl.orEmpty())
            intent.putExtra("timestamp", timestamp.orEmpty())
            intent.putExtra("staffId", staffId.orEmpty())
            intent.putExtra("comment", commentDescription.text.toString().trim())
            intent.putExtra("currentDate", getCurrentDate())
            intent.putExtra("currentTime", getCurrentTime())

            startActivity(intent)
        }


        // Back Button Click Listener
        backButton.setOnClickListener {
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