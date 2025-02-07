package com.faa.cmsportalcui.StaffFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffAdapter.NotificationAdapter
import com.faa.cmsportalcui.StaffAdapter.TaskAdapter
import com.faa.cmsportalcui.StaffModel.Notification
import com.faa.cmsportalcui.StaffModel.Task
import com.faa.cmsportalcui.StaffSide.StaffAssignedDetailActivity
import com.faa.cmsportalcui.StaffSide.StaffAssignedMeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class StaffDashboardFragment : Fragment() {

    private var completedTaskListener: ListenerRegistration? = null
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var serrAll: TextView
    private lateinit var assignedTasksRecyclerView: RecyclerView
    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var staffId: String? = null
    private lateinit var taskAdapter: TaskAdapter
    private var tasksListener: ListenerRegistration? = null
    private val completedTaskIds = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_staff_dashboard, container, false)

        // Initialize views
        profileImage = view.findViewById(R.id.profileImage)
        profileName = view.findViewById(R.id.profileName)
        serrAll = view.findViewById(R.id.seeAllTasks)
        assignedTasksRecyclerView = view.findViewById(R.id.assignedTasksRecyclerView)
        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView)

        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        staffId = arguments?.getString("staff_id")

        if (staffId != null) {
            taskAdapter = TaskAdapter(emptyList()) { task ->
                val intent = Intent(activity, StaffAssignedDetailActivity::class.java).apply {
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

            assignedTasksRecyclerView.layoutManager = LinearLayoutManager(activity)
            assignedTasksRecyclerView.adapter = taskAdapter

            setupRealTimeStaffUpdates(staffId!!)
            fetchCompletedTaskIds {
                setupRealTimeTaskUpdates(staffId!!)
            }
            fetchNotifications()
        } else {
            Log.e("StaffDashboardFragment", "No staff ID provided")
        }

        serrAll.setOnClickListener {
            val intent = Intent(activity, StaffAssignedMeActivity::class.java)
            intent.putExtra("staffId", staffId)
            startActivity(intent)
        }



        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tasksListener?.remove()
    }

    private fun setupRealTimeStaffUpdates(staffId: String) {
        firestore.collection("staff").document(staffId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StaffDashboardFragment", "Error fetching staff data", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val name = snapshot.getString("name")
                    val profileImageUrl = snapshot.getString("profileImageUrl")

                    profileName.text = name ?: "No Name Found"

                    if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                        Glide.with(requireContext())
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
                    Log.e("StaffDashboardFragment", "Error fetching real-time completed tasks", e)
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
                    Log.d("StaffDashboardFragment", "No completed tasks found")
                    callback()
                }
            }
    }

    private fun setupRealTimeTaskUpdates(staffId: String) {
        firestore.collection("equipmentsrequest") // Collection name
            .whereNotIn("status", listOf("approved"))  // Filter out Pending or Rejected status
            .get()
            .addOnSuccessListener { requestSnapshot ->
                val filteredTaskIds = mutableSetOf<String>()
                for (document in requestSnapshot) {
                    filteredTaskIds.add(document.id)
                }

                firestore.collection("pauseTask")
                    .whereEqualTo("staffId", staffId)
                    .get()
                    .addOnSuccessListener { pausedTasksSnapshot ->
                        val pausedTaskIds = mutableSetOf<String>()
                        for (document in pausedTasksSnapshot) {
                            pausedTaskIds.add(document.id)
                        }

                        firestore.collection("completeTask")
                            .whereEqualTo("staffId", staffId)
                            .get()
                            .addOnSuccessListener { completedTasksSnapshot ->
                                val completedTaskIds = mutableSetOf<String>()
                                for (document in completedTasksSnapshot) {
                                    completedTaskIds.add(document.id)
                                }

                                // Combine paused and completed task IDs
                                val allFilteredTaskIds = filteredTaskIds + pausedTaskIds + completedTaskIds

                                tasksListener = firestore.collection("staff")
                                    .document(staffId)
                                    .collection("assignedTasks")
                                    .addSnapshotListener { snapshot, e ->
                                        if (e != null) {
                                            Log.e("StaffDashboardFragment", "Error fetching assigned tasks", e)
                                            return@addSnapshotListener
                                        }

                                        if (snapshot != null) {
                                            val taskList = mutableListOf<Task>()
                                            for (document in snapshot.documents) {
                                                val task = document.toObject(Task::class.java)
                                                if (task != null) {
                                                    task.assignedTaskId = document.id

                                                    if (!allFilteredTaskIds.contains(task.assignedTaskId)) {
                                                        taskList.add(task)
                                                    }
                                                }
                                            }
                                            taskAdapter.updateTasks(taskList)
                                        }
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e("StaffDashboardFragment", "Error fetching completed tasks", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("StaffDashboardFragment", "Error fetching paused tasks", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("StaffDashboardFragment", "Error fetching equipment requests", e)
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
                Log.e("StaffDashboardFragment", "Error fetching request details", e)
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
                Log.e("StaffDashboardFragment", "Error fetching request details for user", e)
                callback("Unknown", "Unknown")
            }
    }

    private fun fetchAssignedByName(userId: String?, adminId: String?, callback: (String) -> Unit) {
        when {
            userId != null && userId.isNotEmpty() -> {
                firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener { document ->
                        callback(document.getString("name") ?: "Unknown")
                    }
                    .addOnFailureListener { e ->
                        Log.e("StaffDashboardFragment", "Error fetching user name", e)
                        callback("Unknown")
                    }
            }
            adminId != null && adminId.isNotEmpty() -> {
                firestore.collection("admins").document(adminId)
                    .get()
                    .addOnSuccessListener { document ->
                        callback(document.getString("name") ?: "Unknown")
                    }
                    .addOnFailureListener { e ->
                        Log.e("StaffDashboardFragment", "Error fetching admin name", e)
                        callback("Unknown")
                    }
            }
            else -> callback("Unknown")
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
                notificationsRecyclerView.layoutManager = LinearLayoutManager(activity)
                notificationsRecyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("StaffDashboardActivity", "Error fetching notifications", e)
            }


    }
}
