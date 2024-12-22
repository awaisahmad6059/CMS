package com.faa.cmsportalcui.Authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faa.cmsportalcui.AdminSide.AdminDashboardActivity
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffSide.StaffDashboardActivity
import com.faa.cmsportalcui.UserSide.UserDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var createNewAccount: TextView
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var back_button: ImageView


    private val adminId = "lzcmCdafqJ6dg8vAYexS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        createNewAccount = findViewById(R.id.tv_sign_up)
        inputEmail = findViewById(R.id.et_email)
        inputPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_sign_in)
        back_button = findViewById(R.id.back_button)


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
        back_button.setOnClickListener {
            startActivity(Intent(this@LoginActivity, AuthenticationActivity::class.java))
        }

        createNewAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))

        }
    }

    private fun loginUser(email: String, password: String) {
        progressDialog.setMessage("Logging in...")
        progressDialog.show()

        firestore.collection("admins").document(adminId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val adminEmail = document.getString("email")
                    val adminPassword = document.getString("password")
                    if (email == adminEmail && password == adminPassword) {
                        progressDialog.dismiss()
                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                        finish()
                        return@addOnSuccessListener
                    } else {
                        checkUserOrStaff(email, password)
                    }
                } else {
                    checkUserOrStaff(email, password)
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to login: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUserOrStaff(email: String, password: String) {
        firestore.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val user = querySnapshot.documents[0]
                    val userPassword = user.getString("password")
                    val userId = user.id

                    if (userPassword == password) {
                        progressDialog.dismiss()
                        val intent = Intent(this, UserDashboardActivity::class.java)
                        intent.putExtra("user_id", userId)
                        startActivity(intent)
                        finish()
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    checkStaff(email, password)
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to login: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkStaff(email: String, password: String) {
        firestore.collection("staff").whereEqualTo("email", email).get()
            .addOnSuccessListener { staffSnapshot ->
                if (!staffSnapshot.isEmpty) {
                    val staff = staffSnapshot.documents[0]
                    val staffPassword = staff.getString("password")
                    val staffId = staff.id

                    if (staffPassword == password) {
                        progressDialog.dismiss()
                        val intent = Intent(this, StaffDashboardActivity::class.java)
                        intent.putExtra("staff_id", staffId)
                        startActivity(intent)
                        finish()
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to login: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }

}
