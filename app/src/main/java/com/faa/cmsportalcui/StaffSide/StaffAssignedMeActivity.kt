package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffAdapter.AssignedMeAdapter
import com.faa.cmsportalcui.StaffModel.AssignedMe
import com.google.firebase.firestore.FirebaseFirestore

class StaffAssignedMeActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var assignedMeAdapter: AssignedMeAdapter
    private val tasks = mutableListOf<AssignedMe>()
    private val completedTaskIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_assigned_me)

        firestore = FirebaseFirestore.getInstance()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        assignedMeAdapter = AssignedMeAdapter(tasks, { task ->
        }, { task ->
            val intent = Intent(this, StaffAssignedDetailActivity::class.java).apply {
                putExtra("id", task.id)
                putExtra("assignedTaskId", task.assignedTaskId)
                putExtra("title", task.title)
                putExtra("description", task.description)
                putExtra("assignedBy", "Loading...")
                putExtra("photoUrl", task.photoUrl)
                putExtra("status", task.status)
                putExtra("timestamp", task.timestamp)
                putExtra("userId", task.userId)
                putExtra("adminId", task.adminId)
                putExtra("userType", task.userType)
                putExtra("staffId", intent.getStringExtra("staffId"))
            }

            if (task.adminId == "PLT9zgmym2RwqCQbQ4WG3WeDY2d2") {
                fetchRequestDetailsForAdmin(task.adminId, task.id) { location, roomNumber ->
                    intent.putExtra("location", location)
                    intent.putExtra("roomNumber", roomNumber)
                    fetchAssignedByName(null, task.adminId) { assignedBy ->
                        intent.putExtra("assignedBy", assignedBy)
                        startActivity(intent)
                    }
                }
            } else if (task.userId != null && task.userId.isNotEmpty()) {
                fetchRequestDetailsForUser(task.userId, task.id) { location, roomNumber ->
                    intent.putExtra("location", location)
                    intent.putExtra("roomNumber", roomNumber)
                    fetchAssignedByName(task.userId, null) { assignedBy ->
                        intent.putExtra("assignedBy", assignedBy)
                        startActivity(intent)
                    }
                }
            } else {
                startActivity(intent)
            }
        })

        recyclerView.adapter = assignedMeAdapter

        val staffId = intent.getStringExtra("staffId")
        if (staffId != null) {
            fetchCompletedTaskIds {
                fetchAssignedTasks(staffId)
            }
        } else {
            Log.e("StaffAssignedMeActivity", "Staff ID is missing")
            Toast.makeText(this, "Staff ID is missing. Please try again.", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
    }

    private fun fetchCompletedTaskIds(callback: () -> Unit) {
        firestore.collection("completeTask")
            .get()
            .addOnSuccessListener { result ->
                completedTaskIds.clear()
                for (document in result) {
                    completedTaskIds.add(document.id)
                }
                callback()
            }
            .addOnFailureListener { exception ->
                Log.e("StaffAssignedMeActivity", "Error fetching completed tasks: ${exception.message}")
                callback()
            }
    }

    private fun fetchAssignedTasks(staffId: String) {
        firestore.collection("staff")
            .document(staffId)
            .collection("assignedTasks")
            .get()
            .addOnSuccessListener { result ->
                tasks.clear()
                for (document in result) {
                    val task = document.toObject(AssignedMe::class.java)
                    task.assignedTaskId = document.id
                    if (!completedTaskIds.contains(task.assignedTaskId)) {
                        tasks.add(task)
                    }
                }
                assignedMeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching tasks: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchRequestDetailsForAdmin(adminId: String, taskId: String, callback: (String, String) -> Unit) {
        val requestsRef = firestore.collection("admins")
            .document(adminId)
            .collection("requests")
            .document(taskId)

        requestsRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val location = document.getString("location") ?: "Unknown"
                    val roomNumber = document.getString("roomNumber") ?: "Unknown"
                    callback(location, roomNumber)
                } else {
                    callback("Unknown", "Unknown")
                }
            }
            .addOnFailureListener { e ->
                Log.e("StaffAssignedMeActivity", "Error fetching request details", e)
                callback("Unknown", "Unknown")
            }
    }

    private fun fetchRequestDetailsForUser(userId: String, taskId: String, callback: (String, String) -> Unit) {
        val requestsRef = firestore.collection("users")
            .document(userId)
            .collection("requests")
            .document(taskId)

        requestsRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val location = document.getString("location") ?: "Unknown"
                    val roomNumber = document.getString("roomNumber") ?: "Unknown"
                    callback(location, roomNumber)
                } else {
                    callback("Unknown", "Unknown")
                }
            }
            .addOnFailureListener { e ->
                Log.e("StaffAssignedMeActivity", "Error fetching request details for user", e)
                callback("Unknown", "Unknown")
            }
    }

    private fun fetchAssignedByName(userId: String?, adminId: String?, callback: (String) -> Unit) {
        when {
            userId != null && userId.isNotEmpty() -> {
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val userName = document.getString("username") ?: "Unknown"
                        callback(userName)
                    }
                    .addOnFailureListener { e ->
                        Log.e("StaffAssignedMeActivity", "Error fetching user name", e)
                        callback("Unknown")
                    }
            }
            adminId != null && adminId.isNotEmpty() -> {
                firestore.collection("admins").document(adminId).get()
                    .addOnSuccessListener { document ->
                        val adminName = document.getString("name") ?: "Unknown"
                        callback(adminName)
                    }
                    .addOnFailureListener { e ->
                        Log.e("StaffAssignedMeActivity", "Error fetching admin name", e)
                        callback("Unknown")
                    }
            }
            else -> {
                callback("Unknown")
            }
        }
    }
}
