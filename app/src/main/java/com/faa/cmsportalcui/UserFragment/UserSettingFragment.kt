package com.faa.cmsportalcui.UserFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.faa.cmsportalcui.Authentication.LoginActivity
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserSide.UserChangePasswordActivity
import com.faa.cmsportalcui.UserSide.UserHelpAndSupportActivity
import com.faa.cmsportalcui.UserSide.UserNotificationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserSettingFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_user_setting, container, false)

        logoutBtn = view.findViewById(R.id.button_logout)
        passwordSection = view.findViewById(R.id.password_section)
        notificationsSection = view.findViewById(R.id.notifications_section)
        needHelpSection = view.findViewById(R.id.need_help_section)
        nameValue = view.findViewById(R.id.name_value)
        mobileNumberValue = view.findViewById(R.id.mobile_number_value)
        emailValue = view.findViewById(R.id.email_value)

        userId = arguments?.getString("user_id")

        loadUserData()



        logoutBtn.setOnClickListener {
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        passwordSection.setOnClickListener {
            startActivity(Intent(activity, UserChangePasswordActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        notificationsSection.setOnClickListener {
            startActivity(Intent(activity, UserNotificationActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        needHelpSection.setOnClickListener {
            startActivity(Intent(activity, UserHelpAndSupportActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        return view
    }

    private fun loadUserData() {
        val userId = this.userId ?: return

        // Get the authenticated user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "No email found"

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("fullName") ?: ""
                    val phone = document.getString("phone") ?: ""

                    nameValue.text = username
                    mobileNumberValue.text = phone
                    emailValue.text = userEmail // Display the email fetched from FirebaseAuth
                }
            }
            .addOnFailureListener { e ->
                // Handle failure (optional)
            }
    }
}
