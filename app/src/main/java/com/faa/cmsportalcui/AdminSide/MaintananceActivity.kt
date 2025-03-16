package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
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
    private lateinit var progressBar: ProgressBar
    private val firestore = FirebaseFirestore.getInstance()
    private var requestsListener: ListenerRegistration? = null
    private var requests = mutableListOf<MaintenanceRequest>() // Store full request list


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintanance)


        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MaintenanceRequestAdapter(ArrayList(requests))
        recyclerView.adapter = adapter
        progressBar = findViewById(R.id.progress_bar)


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
        val searchBar: EditText = findViewById(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRequests(s.toString()) // Call the filter method when text changes
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        requestsListener?.remove()
    }

    private fun setupRealTimeUpdates() {
        progressBar.visibility = View.VISIBLE // Show progress bar

        requestsListener = firestore.collectionGroup("requests")
            .addSnapshotListener { snapshot, e ->
                progressBar.visibility = View.GONE // Hide progress bar once data is loaded

                if (e != null) {
                    Log.e("MaintananceActivity", "Error fetching requests", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    requests.clear() // Purani list clear karein

                    val newRequests = mutableListOf<MaintenanceRequest>()
                    var completedRequests = 0
                    val totalRequests = snapshot.size()

                    if (totalRequests == 0) {
                        updateRecyclerView(newRequests)
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

                                checkIfRequestAssignedToStaff(id, userType, adminId) { isAssigned ->
                                    if (!isAssigned) {
                                        newRequests.add(maintenanceRequest)
                                    }

                                    completedRequests++
                                    if (completedRequests == totalRequests) {
                                        requests = newRequests // Global list update karein
                                        updateRecyclerView(requests)
                                    }
                                }
                            }
                            .addOnFailureListener {
                                completedRequests++
                                if (completedRequests == totalRequests) {
                                    requests = newRequests
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
                        .whereEqualTo("id", requestId)
                        .get()
                        .addOnSuccessListener { assignedTasks ->
                            if (!assignedTasks.isEmpty) {
                                Log.d("CheckRequest", "Request $requestId is assigned to staff ${staffDocument.id}")
                                callback(true)
                                return@addOnSuccessListener
                            }

                            completedChecks++
                            if (completedChecks == staffCollection.size()) {
                                callback(false)
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
        adapter = MaintenanceRequestAdapter(ArrayList(requests))
        recyclerView.adapter = adapter
    }
    private fun filterRequests(query: String) {
        val filteredList = requests.filter { request ->
            request.title.contains(query, ignoreCase = true) ||
                    request.authorName.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList) // RecyclerView update karein
    }

}
