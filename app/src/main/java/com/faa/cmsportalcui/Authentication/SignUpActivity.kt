package com.faa.cmsportalcui.Authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var firestore: FirebaseFirestore
    private lateinit var spinnerUserType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        alreadyHaveAccount = findViewById(R.id.tv_sign_in)
        inputEmail = findViewById(R.id.et_email)
        inputPassword = findViewById(R.id.et_password)
        inputConfirmPassword = findViewById(R.id.et_confirm_password)
        inputUsername = findViewById(R.id.et_username)
        btnRegister = findViewById(R.id.btn_sign_up)
        back_button = findViewById(R.id.back_button)
        spinnerUserType = findViewById(R.id.spinner_usertype)
        progressDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        back_button.setOnClickListener {
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }

        alreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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
            return
        } else if (password.isEmpty() || password.length < 6) {
            inputPassword.error = "Enter a proper password"
            return
        } else if (password != confirmPassword) {
            inputConfirmPassword.error = "Password doesn't match"
            return
        }

        progressDialog.setMessage("Please wait while registering...")
        progressDialog.setTitle("Registration")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    val firebaseUser = mAuth.currentUser
                    firebaseUser?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            Toast.makeText(this, "Verification email sent. Please verify and then login.", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            saveUserLocally(email, username, selectedUserType)
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to send verification email: ${verifyTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserLocally(email: String, username: String, userType: String) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        prefs.edit().apply {
            putString("email", email)
            putString("username", username)
            putString("userType", userType)
            apply()
        }
    }

    private fun generateRandomStaffId(): String {
        val prefix = "staff"
        val randomNumber = (1000..9999).random()
        return "$prefix$randomNumber"
    }
}
