package com.faa.cmsportalcui.Authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.AdminSide.AdminDashboardActivity
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffSide.StaffDashboardActivity
import com.faa.cmsportalcui.UserSide.UserDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var createNewAccount: TextView
    private lateinit var backButton: ImageView
    private lateinit var progressDialog: ProgressDialog

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputEmail = findViewById(R.id.et_email)
        inputPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_sign_in)
        createNewAccount = findViewById(R.id.tv_sign_up)
        backButton = findViewById(R.id.back_button)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)


        btnLogin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                loginUser(email, password)
            }
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }

        createNewAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Already checked in onCreate
    }



    private fun loginUser(email: String, password: String) {
        progressDialog.setMessage("Logging in...")
        progressDialog.show()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    val currentUser = mAuth.currentUser
                    if (currentUser != null) {
                        redirectToDashboard(currentUser.uid, email)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun redirectToDashboard(userId: String, email: String) {
        firestore.collection("admins").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    navigateToDashboard(AdminDashboardActivity::class.java, userId, "admin_id")
                } else {
                    checkUserOrStaff(userId, email)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUserOrStaff(userId: String, email: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    navigateToDashboard(UserDashboardActivity::class.java, userId, "user_id")
                } else {
                    checkStaffByEmail(email)
                }
            }
    }

    private fun checkStaffByEmail(email: String) {
        firestore.collection("staff")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val staffDoc = querySnapshot.documents[0]
                    val staffId = staffDoc.id // Staff ID is stored as document ID
                    navigateToDashboard(StaffDashboardActivity::class.java, staffId, "staff_id")
                } else {
                    Toast.makeText(this, "User not found in any role", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching staff data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToDashboard(activityClass: Class<*>, userId: String, key: String) {
        val intent = Intent(this, activityClass)
        intent.putExtra(key, userId)
        startActivity(intent)
        finish()
    }
}
