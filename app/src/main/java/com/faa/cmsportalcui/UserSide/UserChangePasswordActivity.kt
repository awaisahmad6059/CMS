package com.faa.cmsportalcui.UserSide

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserChangePasswordActivity : AppCompatActivity() {

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var reenterNewPasswordEditText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var cancelButton: Button

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_change_password)

        // Initialize views
        currentPasswordEditText = findViewById(R.id.current_password)
        newPasswordEditText = findViewById(R.id.new_password)
        reenterNewPasswordEditText = findViewById(R.id.reenter_new_password)
        saveChangesButton = findViewById(R.id.button_save_changes)
        cancelButton = findViewById(R.id.button_cancel)

        userId = intent.getStringExtra("user_id")

        saveChangesButton.setOnClickListener {
            validateAndChangePassword()
        }

        cancelButton.setOnClickListener {
            finish()
        }
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun validateAndChangePassword() {
        val currentPassword = currentPasswordEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val reenteredNewPassword = reenterNewPasswordEditText.text.toString().trim()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || reenteredNewPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != reenteredNewPassword) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Check current password and update password in Firestore
        userId?.let { id ->
            firestore.collection("users").document(id).get()
                .addOnSuccessListener { document ->
                    val storedPassword = document.getString("password") ?: ""
                    if (storedPassword == currentPassword) {
                        // Update password in Firestore
                        firestore.collection("users").document(id)
                            .update("password", newPassword)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to update password: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to retrieve user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
    }
}
