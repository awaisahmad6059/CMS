package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminEquipmentAproveRejectActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private var assignedTaskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_equipment_aprove_reject)

        firestore = FirebaseFirestore.getInstance()

        val profileImageView: ImageView = findViewById(R.id.profileImage)
        val userNameTextView: TextView = findViewById(R.id.userName)
        val userExpertiseTextView: TextView = findViewById(R.id.userExpertise)
        val requestTitleTextView: TextView = findViewById(R.id.requesttitle)
        val approveButton: Button = findViewById(R.id.approveButton)
        val rejectButton: Button = findViewById(R.id.rejectButton)
        val reasonForRejectionEditText: EditText = findViewById(R.id.reasonForRejectionEditText)
        val backToDashboardButton: Button = findViewById(R.id.backToDashboardButton)


        assignedTaskId = intent.getStringExtra("assignedTaskId")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")
        val name = intent.getStringExtra("name")
        val experience = intent.getStringExtra("experience")
        val title = intent.getStringExtra("title")

        userNameTextView.text = name
        userExpertiseTextView.text = experience ?: "N/A"
        requestTitleTextView.text = title
        Glide.with(this).load(profileImageUrl)
            .placeholder(R.drawable.account)
            .into(profileImageView)

        approveButton.setOnClickListener {
            updateStatus("approved", null)
        }

        rejectButton.setOnClickListener {
            val rejectionReason = reasonForRejectionEditText.text.toString().trim()
            if (rejectionReason.isNotEmpty()) {
                updateStatus("rejected", rejectionReason)
            } else {
                Toast.makeText(this, "Please enter a reason for rejection", Toast.LENGTH_SHORT).show()
            }
        }
        backToDashboardButton.setOnClickListener {
            navigateToDashboard()
        }
    }

    private fun updateStatus(status: String, rejectionReason: String?) {
        if (assignedTaskId == null) {
            Toast.makeText(this, "Error: Request not found", Toast.LENGTH_SHORT).show()
            return
        }

        val updateData = mutableMapOf<String, Any>("status" to status)
        if (status == "rejected") {
            updateData["rejectionReason"] = rejectionReason ?: ""
        }

        firestore.collection("equipmentsrequest").document(assignedTaskId!!)
            .update(updateData)
            .addOnSuccessListener {
                Toast.makeText(this, "Request $status successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update request: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun navigateToDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
