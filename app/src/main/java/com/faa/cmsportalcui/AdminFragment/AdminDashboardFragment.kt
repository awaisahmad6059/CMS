package com.faa.cmsportalcui.AdminFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.AdminSide.AdminEquipmentsApprovalActivity
import com.faa.cmsportalcui.AdminSide.AdminPauseTaskListActivity
import com.faa.cmsportalcui.AdminSide.AdminProfileActivity
import com.faa.cmsportalcui.AdminSide.CompleteTaskActivity
import com.faa.cmsportalcui.AdminSide.FeedbackRequestListActivity
import com.faa.cmsportalcui.AdminSide.MaintananceActivity
import com.faa.cmsportalcui.AdminSide.NotificationActivity
import com.faa.cmsportalcui.AdminSide.StaffActivity
import com.faa.cmsportalcui.Authentication.LoginActivity
import com.faa.cmsportalcui.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var adduserBtn: Button
    private lateinit var assignTaskBtn: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var totalPendingRequestTextView: TextView
    private lateinit var totalCompleteTaskTextView: TextView
    private lateinit var totalpausetask: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

        adduserBtn = rootView.findViewById(R.id.btn_add_staff)
        adduserBtn.setOnClickListener {
            val staffFragment = StaffFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, staffFragment)
                .addToBackStack(null)
                .commit()
        }


        assignTaskBtn = rootView.findViewById(R.id.btn_assign_task)
        assignTaskBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), MaintananceActivity::class.java))
        }

        rootView.findViewById<LinearLayout>(R.id.pendingrequest).setOnClickListener {
            startActivity(Intent(requireActivity(), MaintananceActivity::class.java))
        }

        rootView.findViewById<LinearLayout>(R.id.pauserequests).setOnClickListener {
            startActivity(Intent(requireActivity(), AdminPauseTaskListActivity::class.java))
        }

        rootView.findViewById<LinearLayout>(R.id.completetask).setOnClickListener {
            startActivity(Intent(requireActivity(), CompleteTaskActivity::class.java))
        }
        rootView.findViewById<LinearLayout>(R.id.equipments).setOnClickListener {
            startActivity(Intent(requireActivity(), AdminEquipmentsApprovalActivity::class.java))
        }

        drawerLayout = rootView.findViewById(R.id.drawer_layout)
        navView = rootView.findViewById(R.id.nav_view)
        totalPendingRequestTextView = rootView.findViewById(R.id.total_pending_request_count)
        totalCompleteTaskTextView = rootView.findViewById(R.id.totalcompletetask)
        totalpausetask = rootView.findViewById(R.id.total_pause_request_count)

        toggle = ActionBarDrawerToggle(
            requireActivity(), drawerLayout,
            R.string.drawer_open, R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


        val menuButton: ImageView = rootView.findViewById(R.id.menu_button)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        fetchAdminDetails()
        fetchTotalRequestCount()
        fetchTotalCompleteTask()
        fetchTotalEquipmentsTask()
        fetchTotalPauseTaskCount()

        return rootView
    }




    fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            requireActivity().onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(requireActivity(), AdminProfileActivity::class.java))
            }
            R.id.nav_notifications -> {
                startActivity(Intent(requireActivity(), NotificationActivity::class.java))
            }
            R.id.nav_feedback -> {
                startActivity(Intent(requireActivity(), FeedbackRequestListActivity::class.java))
            }
            R.id.nav_signout -> {
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun fetchAdminDetails() {
        val db = FirebaseFirestore.getInstance()
        val adminId = "PLT9zgmym2RwqCQbQ4WG3WeDY2d2"

        db.collection("admins")
            .document(adminId)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    error.printStackTrace()
                    requireView().findViewById<TextView>(R.id.user_name).text = "Admin"
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    val profileImageUrl = document.getString("profileImageUrl")

                    requireView().findViewById<TextView>(R.id.user_name).text = name ?: "Admin"

                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .into(requireView().findViewById(R.id.profile))
                    }
                } else {
                    requireView().findViewById<TextView>(R.id.user_name).text = "Admin"
                }
            }
    }

    private fun fetchTotalCompleteTask() {
        val db = FirebaseFirestore.getInstance()
        db.collection("completeTask")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("fetchTotalCompleteTask", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val totalTask = snapshot?.size() ?: 0
                totalCompleteTaskTextView.text = totalTask.toString()
            }
    }

    private fun fetchTotalRequestCount() {
        val db = FirebaseFirestore.getInstance()
        db.collectionGroup("requests")
            .get()
            .addOnSuccessListener { result ->
                val totalRequests = result.size()
                var completedRequests = 0
                var unassignedRequestsCount = 0

                if (totalRequests == 0) {
                    totalPendingRequestTextView.text = unassignedRequestsCount.toString()
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val requestId = document.id

                    checkIfRequestAssignedToStaff(requestId) { isAssigned ->
                        if (!isAssigned) {
                            unassignedRequestsCount++
                        }

                        completedRequests++
                        if (completedRequests == totalRequests) {
                            totalPendingRequestTextView.text = unassignedRequestsCount.toString()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("fetchTotalRequestCount", "Error fetching requests", exception)
                exception.printStackTrace()
            }
    }

    private fun checkIfRequestAssignedToStaff(requestId: String, callback: (Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
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
                                callback(true)
                                return@addOnSuccessListener
                            }

                            completedChecks++
                            if (completedChecks == staffCollection.size()) {
                                callback(false)
                            }
                        }
                        .addOnFailureListener { e ->
                            completedChecks++
                            if (completedChecks == staffCollection.size()) {
                                callback(false)
                            }
                            Log.e("checkIfRequestAssignedToStaff", "Error checking assigned tasks", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                callback(false)
                Log.e("checkIfRequestAssignedToStaff", "Error fetching staff collection", e)
            }
    }

    private fun fetchTotalPauseTaskCount() {
        val db = FirebaseFirestore.getInstance()

        db.collection("pauseTask")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("fetchTotalPauseTaskCount", "Error fetching pause tasks", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val totalpausetaskCount = snapshot.size()
                    totalpausetask.text = totalpausetaskCount.toString()
                }
            }
    }
    private fun fetchTotalEquipmentsTask() {
        val db = FirebaseFirestore.getInstance()

        db.collection("equipmentsrequest")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("fetchTotalEquipmentsTaskCount", "Error fetching equipment tasks", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    var pendingCount = 0
                    var approvedCount = 0
                    var rejectedCount = 0

                    for (document in snapshot.documents) {
                        val status = document.getString("status")

                        when (status) {
                            "Pending" -> pendingCount++
                            "approved" -> approvedCount++
                            "rejected" -> rejectedCount++
                        }
                    }

                    view?.findViewById<TextView>(R.id.total_equipments_count)?.apply {
                        text = "Pen: $pendingCount"
                        setTextColor(Color.BLACK)
                    }
                    view?.findViewById<TextView>(R.id.total_equipments_approve)?.apply {
                        text = "Aprove: $approvedCount"
                        setTextColor(Color.GREEN)
                    }
                    view?.findViewById<TextView>(R.id.total_equipments_reject)?.apply {
                        text = "Rej: $rejectedCount"
                        setTextColor(Color.RED)
                    }
                }
            }
    }


}
