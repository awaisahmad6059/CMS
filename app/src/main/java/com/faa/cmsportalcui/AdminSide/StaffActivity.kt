package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.StaffAdapter
import com.faa.cmsportalcui.AdminModel.Staff
import com.faa.cmsportalcui.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class StaffActivity : AppCompatActivity() {
    private lateinit var fabAddStaff: FloatingActionButton
    private lateinit var rvUsers: RecyclerView
    private lateinit var staffAdapter: StaffAdapter
    private val staffList = mutableListOf<Staff>()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar

    private var id: String? = null
    private var description: String? = null
    private var commentText: String? = null
    private var photoUrl: String? = null
    private var profileImageUrl: String? = null
    private var timestamp: String? = null
    private var userId: String? = null
    private var adminId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)

//        fabAddStaff = findViewById(R.id.fab_add_user)
        rvUsers = findViewById(R.id.rvUsers)
        backButton = findViewById(R.id.back_button)
        progressBar = findViewById(R.id.progressBar)

        id = intent.getStringExtra("id")
        description = intent.getStringExtra("title")
        commentText = intent.getStringExtra("description")
        photoUrl = intent.getStringExtra("photoUrl")
        profileImageUrl = intent.getStringExtra("profileImageUrl")
        timestamp = intent.getStringExtra("timestamp")
        userId = intent.getStringExtra("userId")
        adminId = intent.getStringExtra("adminId")


        rvUsers.layoutManager = LinearLayoutManager(this)
        staffAdapter = StaffAdapter(
            this,
            staffList,
            id,
            commentText,
            description,
            photoUrl,
            profileImageUrl,
            timestamp,
            progressBar,
            userId,
            adminId
        )

        rvUsers.adapter = staffAdapter

        loadStaff()

//        fabAddStaff.setOnClickListener {
//            val intent = Intent(this, StaffProfileActivity::class.java)
//            startActivity(intent)
//        }

        backButton.setOnClickListener {
            navigateToAdminDashboard()
        }
    }

    private fun loadStaff() {
        firestore.collection("staff")
            .get()
            .addOnSuccessListener { documents ->
                staffList.clear()
                for (document in documents) {
                    val staff = document.toObject(Staff::class.java)
                    staff.id = document.id
                    staffList.add(staff)
                }
                staffAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("StaffActivity", "Error getting documents", e)
            }
    }

    private fun navigateToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
