package com.faa.cmsportalcui.AdminSide

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminGeneralSettingActivity : AppCompatActivity() {

    // Firestore reference
    private val db = FirebaseFirestore.getInstance()

    // Known admin ID
    private val adminId = "lzcmCdafqJ6dg8vAYexS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_general_setting)

        val backButton: ImageButton = findViewById(R.id.back_button)
        val buttonSaveChanges: Button = findViewById(R.id.button_save_changes)

        // UI elements
        val mobileNumberTextView: TextView = findViewById(R.id.mobile_number_value)
        val emailTextView: TextView = findViewById(R.id.email_value)

        // Back button logic
        backButton.setOnClickListener {
            finish() // Simply close the activity and go back
        }

        // Fetch admin details from Firestore
        fetchAdminDetails(mobileNumberTextView, emailTextView)

        // Handle Save Changes button click
        buttonSaveChanges.setOnClickListener {
            // Logic to save changes can be added here if needed
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAdminDetails(mobileNumberTextView: TextView, emailTextView: TextView) {
        // Access the document with the given adminId
        db.collection("admins").document(adminId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Populate the TextViews with data from Firestore
                    mobileNumberTextView.text = document.getString("phoneNumber") ?: "N/A"
                    emailTextView.text = document.getString("email") ?: "N/A"
                } else {
                    Toast.makeText(this, "No such admin found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                Toast.makeText(this, "Error fetching admin data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
