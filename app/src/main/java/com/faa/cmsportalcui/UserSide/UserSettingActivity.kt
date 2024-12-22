package com.faa.cmsportalcui.UserSide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.Authentication.WelcomeActivity
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
    private var userId: String? = null

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

        userId = intent.getStringExtra("user_id")

        loadUserData()

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        logoutBtn.setOnClickListener {
            startActivity(Intent(this@UserSettingActivity, WelcomeActivity::class.java))
            finishAffinity()
        }

        passwordSection.setOnClickListener {
            startActivity(Intent(this@UserSettingActivity, UserChangePasswordActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        notificationsSection.setOnClickListener {
            startActivity(Intent(this@UserSettingActivity, UserNotificationActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        needHelpSection.setOnClickListener {
            startActivity(Intent(this@UserSettingActivity, UserHelpAndSupportActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }
    }

    private fun loadUserData() {
        val userId = this.userId ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val email = document.getString("email") ?: ""

                    nameValue.text = username
                    mobileNumberValue.text = phone
                    emailValue.text = email
                }
            }
            .addOnFailureListener { e ->
            }
    }
}
