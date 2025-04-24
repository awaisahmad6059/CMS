package com.faa.cmsportalcui.Authentication

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
    private lateinit var progressDialog: ProgressDialog

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputEmail = findViewById(R.id.et_email)
        inputPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_sign_in)
        createNewAccount = findViewById(R.id.tv_sign_up)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        progressDialog = ProgressDialog(this)

        btnLogin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        createNewAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        progressDialog.setMessage("Logging in...")
        progressDialog.show()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    val currentUser = mAuth.currentUser
                    if (currentUser != null && currentUser.isEmailVerified) {
                        redirectToDashboard(currentUser.uid, currentUser.email ?: "")
                    } else {
                        Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun redirectToDashboard(userId: String, email: String) {
        firestore.collection("admins").document(userId).get()
            .addOnSuccessListener { adminDoc ->
                if (adminDoc.exists()) {
                    navigateToDashboard(AdminDashboardActivity::class.java, userId, "admin_id")
                } else {
                    checkUserOrStaff(userId, email)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking admin status", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkUserOrStaff(userId: String, email: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    navigateToDashboard(UserDashboardActivity::class.java, userId, "user_id")
                } else {
                    firestore.collection("staff").document(userId).get()
                        .addOnSuccessListener { staffDoc ->
                            if (staffDoc.exists()) {
                                navigateToDashboard(StaffDashboardActivity::class.java, userId, "staff_id")
                            } else {
                                saveStaffIfEmailMatches(email, userId)
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking user/staff info", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveStaffIfEmailMatches(email: String, userId: String) {
        firestore.collection("staff").whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val staffId = querySnapshot.documents[0].id
                    navigateToDashboard(StaffDashboardActivity::class.java, staffId, "staff_id")
                } else {
                    // Save new staff only if not exists
                    val username = prefs.getString("username", "") ?: ""
                    val userType = prefs.getString("userType", "") ?: "staff" // default to staff

                    if (userType == "staff") {
                        val staffData = hashMapOf(
                            "email" to email,
                            "name" to username,
                            "userType" to userType
                        )
                        firestore.collection("staff").document(userId)
                            .set(staffData)
                            .addOnSuccessListener {
                                navigateToDashboard(StaffDashboardActivity::class.java, userId, "staff_id")
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save staff: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "User not found in any role", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to check staff by email", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkStaffByEmail(email: String) {
        firestore.collection("staff")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val staffDoc = querySnapshot.documents[0]
                    val staffId = staffDoc.id
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

    private fun generateRandomStaffId(): String {
        val prefix = "staff"
        val randomNumber = (1000..9999).random()
        return "$prefix$randomNumber"
    }
}
