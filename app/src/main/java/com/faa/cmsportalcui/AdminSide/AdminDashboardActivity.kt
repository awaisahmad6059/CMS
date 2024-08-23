package com.faa.cmsportalcui.AdminSide


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.faa.cmsportalcui.Authentication.WelcomeActivity
import com.faa.cmsportalcui.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var adduserBtn: Button
    private lateinit var assignTaskBtn: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var totalUserTextView: TextView
    private lateinit var totalPendingRequestTextView: TextView
    private lateinit var totalStaffCountTextView: TextView
    private lateinit var totalCompleteTaskTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        adduserBtn = findViewById(R.id.btn_add_staff)
        adduserBtn.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, StaffActivity::class.java))
        }

        assignTaskBtn = findViewById(R.id.btn_assign_task)
        assignTaskBtn.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, MaintananceActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.totaluser).setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, UserManagementActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.pendingrequest).setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, MaintananceActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.completetask).setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, CompleteTaskActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.activeworker).setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, StaffActivity::class.java))
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        totalUserTextView = findViewById(R.id.total_user_count)
        totalPendingRequestTextView = findViewById(R.id.total_pending_request_count)
        totalStaffCountTextView = findViewById(R.id.total_staff_count)
        totalCompleteTaskTextView = findViewById(R.id.totalcompletetask)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener(this)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        val menuButton: ImageButton = findViewById(R.id.menu_button)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        fetchTotalUserCount()
        fetchTotalRequestCount()
        fetchTotalStaffCount()
        fetchTotalCompleteTask()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this, AdminProfileActivity::class.java))
            }
            R.id.nav_notifications -> {
                startActivity(Intent(this, NotificationActivity::class.java))
            }
            R.id.nav_feedback -> {
                startActivity(Intent(this, FeedbackRequestListActivity::class.java))
            }
            R.id.nav_signout -> {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finishAffinity()
            }
            R.id.home -> {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
            }
            R.id.user -> {
                startActivity(Intent(this, UserManagementActivity::class.java))
            }
            R.id.staff -> {
                startActivity(Intent(this, StaffActivity::class.java))
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun fetchTotalUserCount() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").get()
            .addOnSuccessListener { result ->
                val totalUsers = result.size()
                totalUserTextView.text = totalUsers.toString()
            }
            .addOnFailureListener { exception ->
            }
    }
    private fun fetchTotalCompleteTask() {
        val db = FirebaseFirestore.getInstance()
        db.collection("completeTask").get()
            .addOnSuccessListener { result ->
                val totalTask = result.size()
                totalCompleteTaskTextView.text = totalTask.toString()
            }
            .addOnFailureListener { exception ->
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

    private fun fetchTotalStaffCount() {
        val db = FirebaseFirestore.getInstance()
        db.collection("staff").get()
            .addOnSuccessListener { result ->
                val totalStaff = result.size()
                totalStaffCountTextView.text = totalStaff.toString()
            }
            .addOnFailureListener { exception ->
            }
    }

}
