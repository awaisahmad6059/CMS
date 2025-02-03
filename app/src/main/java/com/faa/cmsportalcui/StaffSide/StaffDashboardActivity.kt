package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffAdapter.NotificationAdapter
import com.faa.cmsportalcui.StaffAdapter.TaskAdapter
import com.faa.cmsportalcui.StaffModel.Notification
import com.faa.cmsportalcui.StaffModel.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class StaffDashboardActivity : AppCompatActivity() {


    private var completedTaskListener: ListenerRegistration? = null
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var serrAll: TextView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var assignedTasksRecyclerView: RecyclerView
    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var staffId: String? = null
    private lateinit var taskAdapter: TaskAdapter
    private var tasksListener: ListenerRegistration? = null
    private val completedTaskIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_dashboard)

        profileImage = findViewById(R.id.profileImage)
        profileName = findViewById(R.id.profileName)
        serrAll = findViewById(R.id.seeAllTasks)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        assignedTasksRecyclerView = findViewById(R.id.assignedTasksRecyclerView)
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView)

        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        staffId = intent.getStringExtra("staff_id")

        if (staffId != null) {
            taskAdapter = TaskAdapter(emptyList()) { task ->
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
                    putExtra("staffId", staffId)
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
            }


            assignedTasksRecyclerView.layoutManager = LinearLayoutManager(this)
            assignedTasksRecyclerView.adapter = taskAdapter

            setupRealTimeStaffUpdates(staffId!!)
            fetchCompletedTaskIds {
                setupRealTimeTaskUpdates(staffId!!)
            }
            fetchNotifications()
        } else {
            Log.e("StaffDashboardActivity", "No staff ID provided")
        }
        serrAll.setOnClickListener{
            val intent = Intent(this@StaffDashboardActivity, StaffAssignedMeActivity::class.java)
            intent.putExtra("staffId", staffId)
            startActivity(intent)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.complete -> {
                    val intent = Intent(this@StaffDashboardActivity, StaffCompleteTaskActivity::class.java)
                    intent.putExtra("staffId", staffId)
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this@StaffDashboardActivity, StaffProfileDetailActivity::class.java)
                    intent.putExtra("staffId", staffId)
                    startActivity(intent)
                    true
                }
                R.id.equipments -> {
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tasksListener?.remove()
    }

    private fun setupRealTimeStaffUpdates(staffId: String) {
        firestore.collection("staff").document(staffId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StaffDashboardActivity", "Error fetching staff data", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val name = snapshot.getString("name")
                    val profileImageUrl = snapshot.getString("profileImageUrl")

                    profileName.text = name ?: "No Name Found"

                    if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.account)
                            .into(profileImage)
                    } else {
                        profileImage.setImageResource(R.drawable.account)
                    }
                }
            }
    }

    private fun fetchCompletedTaskIds(callback: () -> Unit) {
        completedTaskListener?.remove()

        completedTaskListener = firestore.collection("completeTask")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("StaffDashboardActivity", "Error fetching real-time completed tasks", e)
                    callback()
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    completedTaskIds.clear()

                    for (document in snapshots.documents) {
                        completedTaskIds.add(document.id)
                    }

                    callback()
                } else {
                    Log.d("StaffDashboardActivity", "No completed tasks found")
                    callback()
                }
            }
    }


    private fun setupRealTimeTaskUpdates(staffId: String) {
        // Fetch paused tasks for the current staff member
        firestore.collection("pauseTask")
            .whereEqualTo("staffId", staffId) // Filter by staffId
            .get()
            .addOnSuccessListener { pausedTasksSnapshot ->
                val pausedTaskIds = mutableSetOf<String>()
                for (document in pausedTasksSnapshot) {
                    pausedTaskIds.add(document.id) // Collect paused task IDs
                }

                // Fetch completed tasks for the staff member
                firestore.collection("completeTask")
                    .whereEqualTo("staffId", staffId) // Filter by staffId
                    .get()
                    .addOnSuccessListener { completedTasksSnapshot ->
                        val completedTaskIds = mutableSetOf<String>()
                        for (document in completedTasksSnapshot) {
                            completedTaskIds.add(document.id) // Collect completed task IDs
                        }

                        // Combine paused and completed task IDs
                        val filteredTaskIds = pausedTaskIds + completedTaskIds

                        // Fetch assigned tasks for the staff member
                        tasksListener = firestore.collection("staff")
                            .document(staffId)
                            .collection("assignedTasks")
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    Log.e("StaffDashboardActivity", "Error fetching assigned tasks", e)
                                    return@addSnapshotListener
                                }

                                if (snapshot != null) {
                                    val taskList = mutableListOf<Task>()
                                    for (document in snapshot.documents) {
                                        val task = document.toObject(Task::class.java)
                                        if (task != null) {
                                            task.assignedTaskId = document.id

                                            // Exclude tasks that are in the pausedTask or completeTask collections
                                            if (!filteredTaskIds.contains(task.assignedTaskId)) {
                                                taskList.add(task)
                                            }
                                        }
                                    }
                                    taskAdapter.updateTasks(taskList) // Update the adapter with filtered tasks
                                }
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("StaffDashboardActivity", "Error fetching completed tasks", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("StaffDashboardActivity", "Error fetching paused tasks", e)
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
                Log.e("StaffDashboardActivity", "Error fetching request details", e)
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
                Log.e("StaffDashboardActivity", "Error fetching request details for user", e)
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
                        Log.e("StaffDashboardActivity", "Error fetching user name", e)
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
                        Log.e("StaffDashboardActivity", "Error fetching admin name", e)
                        callback("Unknown")
                    }
            }
            else -> {
                callback("Unknown")
            }
        }
    }

    private fun fetchNotifications() {
        firestore.collection("notifications")
            .get()
            .addOnSuccessListener { documents ->
                val notificationList = mutableListOf<Notification>()
                for (document in documents) {
                    val notification = document.toObject(Notification::class.java)
                    notificationList.add(notification)
                }
                val adapter = NotificationAdapter(notificationList)
                notificationsRecyclerView.layoutManager = LinearLayoutManager(this)
                notificationsRecyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("StaffDashboardActivity", "Error fetching notifications", e)
            }
        fun onBackPressed() {
            finishAffinity()
        }

    }
}
