package com.faa.cmsportalcui.Authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        title = findViewById(R.id.title)
        logo = findViewById(R.id.logo)

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        title.startAnimation(topAnim)
        logo.startAnimation(bottomAnim)

        topAnim.duration = 4000
        bottomAnim.duration = 4000

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        Handler().postDelayed({
            checkUserSession()
        }, 4000)
    }

    private fun checkUserSession() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            redirectToDashboard(currentUser.uid)
        } else {
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun redirectToDashboard(userId: String) {
        firestore.collection("admins").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    navigateToDashboard(AdminDashboardActivity::class.java, userId, "admin_id")
                } else {
                    checkUserOrStaff(userId)
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun checkUserOrStaff(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    navigateToDashboard(UserDashboardActivity::class.java, userId, "user_id")
                } else {
                    checkStaffByEmail(userId)
                }
            }
    }

    private fun checkStaffByEmail(userId: String) {
        firestore.collection("staff")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val staffDoc = querySnapshot.documents[0]
                    val staffId = staffDoc.id // Staff ID is stored as document ID
                    navigateToDashboard(StaffDashboardActivity::class.java, staffId, "staff_id")
                } else {
                    // Handle case where user is not found in any role
                }
            }
    }

    private fun navigateToDashboard(activityClass: Class<*>, userId: String, key: String) {
        val intent = Intent(this, activityClass)
        intent.putExtra(key, userId)
        startActivity(intent)
        finish()
    }
}
