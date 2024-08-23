package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.MaintenanceRequestAdapter
import com.faa.cmsportalcui.AdminModel.MaintenanceRequest
import com.faa.cmsportalcui.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MaintananceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MaintenanceRequestAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private var requestsListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintanance)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        adapter = MaintenanceRequestAdapter(emptyList())
        recyclerView.adapter = adapter

        // Setup real-time updates for maintenance requests
        setupRealTimeUpdates()

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AdminRequestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requestsListener?.remove() // Remove the listener when activity is destroyed
    }

    private fun setupRealTimeUpdates() {
        requestsListener = firestore.collectionGroup("requests")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("MaintananceActivity", "Error fetching requests", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val requests = mutableListOf<MaintenanceRequest>()
                    var completedRequests = 0
                    val totalRequests = snapshot.size()

                    if (totalRequests == 0) {
                        updateRecyclerView(requests)
                        return@addSnapshotListener
                    }

                    for (document in snapshot) {
                        val id = document.id
                        val title = document.getString("title") ?: ""
                        val description = document.getString("description") ?: ""
                        val timestamp = document.getString("timestamp") ?: ""
                        val imageUrl = document.getString("photoUrl") ?: ""
                        val commentText = document.getString("commentText") ?: ""
                        val userId = document.getString("userId") ?: ""
                        val userType = document.getString("userType") ?: "user"
                        val adminId = document.getString("adminId") ?: ""

                        // Depending on the userType, fetch either user or admin data
                        val collectionPath = if (userType == "user") "users" else "admins"
                        val docId = if (userType == "user") userId else adminId

                        firestore.collection(collectionPath).document(docId)
                            .get()
                            .addOnSuccessListener { userDocument ->
                                val profileImageUrl = userDocument.getString("profileImageUrl") ?: ""
                                val authorName = if (userType == "user") {
                                    userDocument.getString("username") ?: "Unknown"
                                } else {
                                    userDocument.getString("name") ?: "Unknown"
                                }

                                val maintenanceRequest = MaintenanceRequest(
                                    id, title, description, timestamp, profileImageUrl, imageUrl,
                                    authorName, commentText, userType, adminId, userId
                                )

                                // Check if this request is in any staff's assignedTasks
                                checkIfRequestAssignedToStaff(id, userType, adminId) { isAssigned ->
                                    if (!isAssigned) {
                                        requests.add(maintenanceRequest)
                                    }

                                    completedRequests++
                                    if (completedRequests == totalRequests) {
                                        updateRecyclerView(requests)
                                    }
                                }
                            }
                            .addOnFailureListener {
                                completedRequests++
                                if (completedRequests == totalRequests) {
                                    updateRecyclerView(requests)
                                }
                            }
                    }
                }
            }
    }

    private fun checkIfRequestAssignedToStaff(requestId: String, userType: String, adminId: String, callback: (Boolean) -> Unit) {
        firestore.collection("staff")
            .get()
            .addOnSuccessListener { staffCollection ->
                if (staffCollection.isEmpty) {
                    callback(false)
                    return@addOnSuccessListener
                }

                var completedChecks = 0
                for (staffDocument in staffCollection) {
                    firestore.collection("staff")
                        .document(staffDocument.id)
                        .collection("assignedTasks")
                        .whereEqualTo("id", requestId) // Adjusted to check for the specific "id" field
                        .get()
                        .addOnSuccessListener { assignedTasks ->
                            if (!assignedTasks.isEmpty) {
                                // Log that this request is already assigned
                                Log.d("CheckRequest", "Request $requestId is assigned to staff ${staffDocument.id}")
                                callback(true)
                                return@addOnSuccessListener // Exit early if found
                            }

                            completedChecks++
                            if (completedChecks == staffCollection.size()) {
                                callback(false) // Only callback false if it wasn't found in any
                            }
                        }
                        .addOnFailureListener {
                            completedChecks++
                            if (completedChecks == staffCollection.size()) {
                                callback(false)
                            }
                        }
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    private fun updateRecyclerView(requests: List<MaintenanceRequest>) {
        adapter = MaintenanceRequestAdapter(requests)
        recyclerView.adapter = adapter
    }
}
