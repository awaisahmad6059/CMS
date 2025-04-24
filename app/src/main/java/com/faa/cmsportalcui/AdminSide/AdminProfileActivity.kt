package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminProfileActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var adminId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)

        val backButton: ImageButton = findViewById(R.id.back_button)
        val profilePhoto: ImageView = findViewById(R.id.profile_photo)
        val nameText: TextView = findViewById(R.id.name)
        val experienceText: TextView = findViewById(R.id.experience)
        val specialtyText: TextView = findViewById(R.id.specialty)
        val emailText: TextView = findViewById(R.id.email)
        val phoneNumberText: TextView = findViewById(R.id.phoneNumber)
        val editButton: Button = findViewById(R.id.button_edit)

        adminId = "Ae01ooy19BMfZO8y80BwG6jOuP33"

        if (adminId != null) {
            loadAdminDetails(adminId!!, profilePhoto, nameText, experienceText, specialtyText, emailText, phoneNumberText)
        } else {
            Toast.makeText(this, "Admin ID is missing", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            finish()
        }

        editButton.setOnClickListener {
            val intent = Intent(this, AdminEditProfileActivity::class.java)
            intent.putExtra("adminId", adminId)
            startActivity(intent)
        }
    }

    private fun loadAdminDetails(
        adminId: String,
        profilePhoto: ImageView,
        nameText: TextView,
        experienceText: TextView,
        specialtyText: TextView,
        emailText: TextView,
        phoneNumberText: TextView
    ) {
        val adminEmail = FirebaseAuth.getInstance().currentUser?.email ?: "No email found"

        db.collection("admins").document(adminId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    nameText.text = document.getString("name") ?: "null"
                    experienceText.text = "Experience: ${document.getString("experience") ?: "null"}"
                    specialtyText.text = "Specialty: ${document.getString("specialty") ?: "null"}"
//                    emailText.text = document.getString("email") ?: "null"
                    phoneNumberText.text = document.getString("phoneNumber") ?: "null"
                    emailText.text = adminEmail


                    val profileImageUrl = document.getString("profileImageUrl")
                    if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(profilePhoto)
                    } else {
                        profilePhoto.setImageResource(R.drawable.account)
                    }
                } else {
                    Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting document.", Toast.LENGTH_SHORT).show()
            }
    }
}
