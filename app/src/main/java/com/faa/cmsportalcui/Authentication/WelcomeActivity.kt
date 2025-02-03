package com.faa.cmsportalcui.Authentication

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.AdminSide.AdminDashboardActivity
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffSide.StaffDashboardActivity
import com.faa.cmsportalcui.UserSide.UserDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WelcomeActivity : AppCompatActivity() {
    private lateinit var title: TextView
    private lateinit var logo: ImageView
    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        title = findViewById(R.id.title)
        logo = findViewById(R.id.logo)

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        title.startAnimation(topAnim)
        logo.startAnimation(bottomAnim)
        topAnim.duration = 3000
        bottomAnim.duration = 3000

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        checkUserSession()
    }

    private fun checkUserSession() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            redirectToDashboard(currentUser.uid, currentUser.email ?: "")
        } else {
            navigateToLogin()
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
                navigateToLogin()
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
                    val staffId = staffDoc.id
                    navigateToDashboard(StaffDashboardActivity::class.java, staffId, "staff_id")
                } else {
                    navigateToLogin()
                }
            }
    }

    private fun navigateToDashboard(activityClass: Class<*>, userId: String, key: String) {
        val intent = Intent(this, activityClass)
        intent.putExtra(key, userId)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
