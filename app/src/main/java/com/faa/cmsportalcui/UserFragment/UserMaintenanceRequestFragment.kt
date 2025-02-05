package com.faa.cmsportalcui.UserFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserAdapter.UserMaintenanceRequestAdapter
import com.faa.cmsportalcui.UserModel.UserMaintenanceRequest
import com.google.firebase.firestore.FirebaseFirestore

class UserMaintenanceRequestFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserMaintenanceRequestAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private var userId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_maintenance_request, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.requests_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Get user ID passed through arguments
        userId = arguments?.getString("user_id")

        userId?.let {
            fetchMaintenanceRequests(it)
        }



        return view
    }

    private fun fetchMaintenanceRequests(userId: String) {
        firestore.collection("users").document(userId).collection("requests")
            .get()
            .addOnSuccessListener { result ->
                val requests = mutableListOf<UserMaintenanceRequest>()
                val requestIds = mutableSetOf<String>()

                if (result.isEmpty) {
                    updateRecyclerView(requests)
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val id = document.id
                    requestIds.add(id)
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val location = document.getString("location") ?: ""
                    val room = document.getString("roomNumber") ?: ""
                    val photoUrl = document.getString("photoUrl") ?: ""
                    val date = document.getString("timestamp") ?: ""

                    requests.add(UserMaintenanceRequest(id, title, description, location, room, photoUrl, date))
                    Log.d("FirestoreDebug", "Fetched request: $id")
                }

                Log.d("FirestoreDebug", "Requests fetched: ${requests.size}")

                checkIfRequestsAssignedToStaff(requestIds) { assignedRequests ->
                    val unassignedRequests = requests.filter { request ->
                        !assignedRequests.contains(request.id)
                    }
                    updateRecyclerView(unassignedRequests)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error fetching requests", e)
            }
    }

    private fun checkIfRequestsAssignedToStaff(requestIds: Set<String>, callback: (Set<String>) -> Unit) {
        val assignedRequests = mutableSetOf<String>()
        val staffCollectionRef = firestore.collection("staff")

        staffCollectionRef.get()
            .addOnSuccessListener { staffCollection ->
                if (staffCollection.isEmpty) {
                    callback(assignedRequests)
                    return@addOnSuccessListener
                }

                var completedChecks = 0
                val staffCount = staffCollection.size()

                for (staffDocument in staffCollection) {
                    val staffId = staffDocument.id
                    firestore.collection("staff")
                        .document(staffId)
                        .collection("assignedTasks")
                        .whereIn("id", requestIds.toList()) // Convert Set to List explicitly
                        .get()
                        .addOnSuccessListener { assignedTasks ->
                            for (task in assignedTasks) {
                                assignedRequests.add(task.getString("id") ?: "")
                            }

                            completedChecks++
                            if (completedChecks == staffCount) {
                                callback(assignedRequests)
                            }
                        }
                        .addOnFailureListener {
                            completedChecks++
                            if (completedChecks == staffCount) {
                                callback(assignedRequests)
                            }
                        }
                }
            }
            .addOnFailureListener {
                callback(assignedRequests)
            }
    }

    private fun updateRecyclerView(requests: List<UserMaintenanceRequest>) {
        adapter = UserMaintenanceRequestAdapter(requests, userId ?: "")
        recyclerView.adapter = adapter
        Log.d("RecyclerViewDebug", "Adapter updated with ${requests.size} requests")
    }

    private fun navigateToDashboardActivity() {
        // Navigate to the Dashboard Activity
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UserDashboardFragment())
            .commit()
    }
}
