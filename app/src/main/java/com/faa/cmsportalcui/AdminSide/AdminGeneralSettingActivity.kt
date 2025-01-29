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

    private val db = FirebaseFirestore.getInstance()

    private val adminId = "PLT9zgmym2RwqCQbQ4WG3WeDY2d2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_general_setting)

        val backButton: ImageButton = findViewById(R.id.back_button)
        val buttonSaveChanges: Button = findViewById(R.id.button_save_changes)

        val mobileNumberTextView: TextView = findViewById(R.id.mobile_number_value)
        val emailTextView: TextView = findViewById(R.id.email_value)

        backButton.setOnClickListener {
            finish()
        }

        fetchAdminDetails(mobileNumberTextView, emailTextView)

        buttonSaveChanges.setOnClickListener {
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAdminDetails(mobileNumberTextView: TextView, emailTextView: TextView) {
        db.collection("admins").document(adminId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    mobileNumberTextView.text = document.getString("phoneNumber") ?: "N/A"
                    emailTextView.text = document.getString("email") ?: "N/A"
                } else {
                    Toast.makeText(this, "No such admin found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching admin data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
