package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StaffEquipmentRequestActivity : AppCompatActivity() {

    private lateinit var taskTitle: TextView
    private lateinit var taskStartTime: TextView
    private lateinit var equipmentRequest: EditText
    private lateinit var reasonEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button
    private lateinit var backButton: ImageButton


    private val firestore = FirebaseFirestore.getInstance()
    private var staffId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_equipment_request)

        // Initialize UI elements
        taskTitle = findViewById(R.id.taskTitle)
        taskStartTime = findViewById(R.id.taskStartTime)
        equipmentRequest = findViewById(R.id.equipmentrequest)
        reasonEditText = findViewById(R.id.reasonEditText)
        submitButton = findViewById(R.id.submitButton)
        cancelButton = findViewById(R.id.cancelButton)
        backButton = findViewById(R.id.back_button)


        // Get data from intent
        val id = intent.getStringExtra("id")
        val assignedTaskId = intent.getStringExtra("assignedTaskId")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val roomNumber = intent.getStringExtra("roomNumber")
        val assignedBy = intent.getStringExtra("assignedBy")
        val location = intent.getStringExtra("location")
        val photoUrl = intent.getStringExtra("photoUrl")
        val timestamp = intent.getStringExtra("timestamp")
        val comment = intent.getStringExtra("comment")
        val currentDate = intent.getStringExtra("currentDate")
        val currentTime = intent.getStringExtra("currentTime")
        staffId = intent.getStringExtra("staffId")

        // Set data in UI
        taskTitle.text = title ?: "No Title"
        taskStartTime.text = timestamp ?: "No Time"

        // Handle button click
        submitButton.setOnClickListener {
            submitEquipmentRequest(
                id, assignedTaskId, title, description, roomNumber,
                assignedBy, location, photoUrl, timestamp, comment,
                currentDate, currentTime, staffId
            )
        }
        backButton.setOnClickListener {
            finish()
        }
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun submitEquipmentRequest(
        id: String?, assignedTaskId: String?, title: String?, description: String?,
        roomNumber: String?, assignedBy: String?, location: String?, photoUrl: String?,
        timestamp: String?, comment: String?, currentDate: String?, currentTime: String?,
        staffId: String?
    ) {
        val equipmentName = equipmentRequest.text.toString().trim()
        val reason = reasonEditText.text.toString().trim()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val requestTimestamp = dateFormat.format(Date())

        if (equipmentName.isEmpty()) {
            Toast.makeText(this, "Please enter the equipment name", Toast.LENGTH_SHORT).show()
            return
        }

        val requestData = hashMapOf(
            "id" to (id ?: ""),
            "assignedTaskId" to (assignedTaskId ?: ""),
            "title" to (title ?: ""),
            "description" to (description ?: ""),
            "roomNumber" to (roomNumber ?: ""),
            "assignedBy" to (assignedBy ?: ""),
            "location" to (location ?: ""),
            "photoUrl" to (photoUrl ?: ""),
            "timestamp" to (timestamp ?: ""),
            "comment" to (comment ?: ""),
            "currentDate" to (currentDate ?: ""),
            "currentTime" to (currentTime ?: ""),
            "staffId" to (staffId ?: ""),
            "equipmentName" to equipmentName,
            "reason" to reason,
            "requestTimestamp" to requestTimestamp,
            "status" to "Pending" // Default status
        )

        val documentId = assignedTaskId ?: UUID.randomUUID().toString()

        firestore.collection("equipmentsrequest")
            .document(documentId)
            .set(requestData)
            .addOnSuccessListener {
                Toast.makeText(this, "Equipment request submitted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, StaffDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("staff_id", staffId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit request: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
