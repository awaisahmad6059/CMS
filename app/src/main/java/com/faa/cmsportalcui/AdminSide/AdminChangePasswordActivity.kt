package com.faa.cmsportalcui.AdminSide

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.AdminModel.Admin
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class AdminChangePasswordActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth  // Firebase Authentication instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_change_password)

        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()  // Initialize FirebaseAuth

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
        val cancelButton: Button = findViewById(R.id.button_cancel)
        cancelButton.setOnClickListener {
            finish()
        }

        val saveChangesButton: Button = findViewById(R.id.button_save_changes)
        saveChangesButton.setOnClickListener {

            val currentPasswordEditText: EditText = findViewById(R.id.current_password)
            val newPasswordEditText: EditText = findViewById(R.id.new_password)
            val reenterNewPasswordEditText: EditText = findViewById(R.id.reenter_new_password)

            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val reenterNewPassword = reenterNewPasswordEditText.text.toString()

            if (newPassword == reenterNewPassword) {
                updatePassword(currentPassword, newPassword)
            } else {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePassword(currentPassword: String, newPassword: String) {
        val adminId = "Ae01ooy19BMfZO8y80BwG6jOuP33"

        firestore.collection("admins")
            .document(adminId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val admin = document.toObject(Admin::class.java)
                    if (admin?.password == currentPassword) {
                        // Reauthenticate user before updating password in Firebase Authentication
                        val user = mAuth.currentUser
                        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)

                        user?.reauthenticate(credential)
                            ?.addOnCompleteListener { reAuthTask ->
                                if (reAuthTask.isSuccessful) {
                                    // Reauthentication successful, now update the password
                                    document.reference.update("password", newPassword)
                                        .addOnSuccessListener {
                                            // Update password in Firebase Authentication
                                            user.updatePassword(newPassword)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "Password updated successfully in both Firestore and Firebase Authentication", Toast.LENGTH_SHORT).show()
                                                    finish()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(this, "Error updating password in Firebase Authentication: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Error updating password in Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(this, "Reauthentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Admin not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching admin data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
