package com.faa.cmsportalcui.UserSide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.Authentication.LoginActivity
import com.faa.cmsportalcui.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserSettingActivity : AppCompatActivity() {

    private lateinit var logoutBtn: Button
    private lateinit var passwordSection: RelativeLayout
    private lateinit var notificationsSection: RelativeLayout
    private lateinit var needHelpSection: RelativeLayout
    private lateinit var nameValue: TextView
    private lateinit var mobileNumberValue: TextView
    private lateinit var emailValue: TextView

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)

        logoutBtn = findViewById(R.id.button_logout)
        passwordSection = findViewById(R.id.password_section)
        notificationsSection = findViewById(R.id.notifications_section)
        needHelpSection = findViewById(R.id.need_help_section)
        nameValue = findViewById(R.id.name_value)
        mobileNumberValue = findViewById(R.id.mobile_number_value)
        emailValue = findViewById(R.id.email_value)

        loadUserData()

        logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        passwordSection.setOnClickListener {
            startActivity(Intent(this, UserChangePasswordActivity::class.java))
        }

        notificationsSection.setOnClickListener {
            startActivity(Intent(this, UserNotificationActivity::class.java))
        }

        needHelpSection.setOnClickListener {
            startActivity(Intent(this, UserHelpAndSupportActivity::class.java))
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid  // Get the logged-in user's ID
            emailValue.text = user.email ?: "No Email"  // Show the email from Firebase Auth

            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullName = document.getString("fullName") ?: "No Name"
                        val phone = document.getString("phone") ?: "No Phone"

                        nameValue.text = fullName
                        mobileNumberValue.text = phone
                    } else {
                        nameValue.text = "No Name Found"
                    }
                }
                .addOnFailureListener {
                    nameValue.text = "Error Fetching Name"
                }
        } else {
            nameValue.text = "User Not Logged In"
        }
    }
}
