package com.faa.cmsportalcui.Authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffSide.StaffDashboardActivity
import com.faa.cmsportalcui.UserSide.UserDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var alreadyHaveAccount: TextView
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var inputUsername: EditText
    private lateinit var btnRegister: Button
    private lateinit var back_button: ImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    private var mUser: FirebaseUser? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var spinnerUserType: Spinner
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize views
        alreadyHaveAccount = findViewById(R.id.tv_sign_in)
        inputEmail = findViewById(R.id.et_email)
        inputPassword = findViewById(R.id.et_password)
        inputConfirmPassword = findViewById(R.id.et_confirm_password)
        inputUsername = findViewById(R.id.et_username)
        btnRegister = findViewById(R.id.btn_sign_up)
        back_button = findViewById(R.id.back_button)
        progressDialog = ProgressDialog(this)
        spinnerUserType = findViewById(R.id.spinner_usertype)
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Set listeners
        back_button.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, AuthenticationActivity::class.java))
        }

        alreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            performAuth()
        }
    }

    private fun performAuth() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()
        val confirmPassword = inputConfirmPassword.text.toString().trim()
        val username = inputUsername.text.toString().trim()
        val selectedUserType = spinnerUserType.selectedItem.toString()

        if (selectedUserType == "Select Type") {
            Toast.makeText(this, "Please select user type: User or Staff", Toast.LENGTH_SHORT).show()
            return
        }

        if (!email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
            inputEmail.error = "Enter a correct email"
        } else if (password.isEmpty() || password.length < 6) {
            inputPassword.error = "Enter a proper password"
        } else if (password != confirmPassword) {
            inputConfirmPassword.error = "Password doesn't match"
        } else {
            progressDialog.setMessage("Please wait while registration...")
            progressDialog.setTitle("Registration")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            // Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = mAuth.currentUser
                    val userId = firebaseUser?.uid ?: ""
                    val staffId = if (selectedUserType == "staff") generateRandomStaffId() else ""

                    // Data to save in Firestore
                    val userData = hashMapOf(
                        "email" to email,
                        "password" to password,
                        "name" to username,
                        "userType" to selectedUserType
                    )

                    // Save to Firestore based on user type
                    if (selectedUserType == "user") {
                        firestore.collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                                // Redirect to User Dashboard with user_id
                                val intent = Intent(this@SignUpActivity, UserDashboardActivity::class.java)
                                intent.putExtra("user_id", userId) // Pass the generated user ID
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                progressDialog.dismiss()
                                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else if (selectedUserType == "staff") {
                        firestore.collection("staff").document(staffId)
                            .set(userData)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(this, "Staff registered successfully", Toast.LENGTH_SHORT).show()
                                // Redirect to Staff Dashboard with staffId
                                val intent = Intent(this@SignUpActivity, StaffDashboardActivity::class.java)
                                intent.putExtra("staff_id", staffId) // Pass generated staffId
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                progressDialog.dismiss()
                                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Function to generate a random user ID


    // Function to generate a random staff ID
    private fun generateRandomStaffId(): String {
        val prefix = "staff"
        val randomNumber = (1000..9999).random() // Generates a random number between 1000 and 9999
        return "$prefix$randomNumber"
    }


}
