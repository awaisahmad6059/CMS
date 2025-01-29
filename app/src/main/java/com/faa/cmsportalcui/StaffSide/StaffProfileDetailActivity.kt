package com.faa.cmsportalcui.StaffSide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.Authentication.LoginActivity
import com.faa.cmsportalcui.Authentication.WelcomeActivity
import com.faa.cmsportalcui.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class StaffProfileDetailActivity : AppCompatActivity() {
    private var staffId: String? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var logoutBtn: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_profile_detail)

        staffId = intent.getStringExtra("staffId")

        logoutBtn = findViewById(R.id.logoutButton)

        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val workerName = findViewById<TextView>(R.id.workerName)
        val workerPosition = findViewById<TextView>(R.id.workerPosition)
        val specificationText = findViewById<TextView>(R.id.specificationText)
        val emailText = findViewById<TextView>(R.id.emailText)
        val experienceText = findViewById<TextView>(R.id.experienceText)
        val phoneText = findViewById<TextView>(R.id.phoneText)
        val editButton = findViewById<Button>(R.id.editButton)
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        editButton.setOnClickListener {
            val intent = Intent(this, StaffEditProfileActivity::class.java)
            intent.putExtra("staffId", staffId)
            startActivity(intent)
        }
        logoutBtn.setOnClickListener {
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        }
    }

    override fun onResume() {
        super.onResume()
        staffId?.let {
            db.collection("staff").document(it).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val staffData = document.data
                        staffData?.let { data ->
                            val profileImageUrl = data["profileImageUrl"] as? String
                            val name = data["name"] as? String
                            val position = data["position"] as? String
                            val specification = data["specification"] as? String
                            val experience = data["experience"] as? String
                            val email = data["email"] as? String
                            val phone = data["phone"] as? String

                            findViewById<TextView>(R.id.workerName).text = name ?: "N/A"
                            findViewById<TextView>(R.id.workerPosition).text = position ?: "N/A"
                            findViewById<TextView>(R.id.specificationText).text = specification ?: "N/A"
                            findViewById<TextView>(R.id.emailText).text = email ?: "N/A"
                            findViewById<TextView>(R.id.experienceText).text = experience ?: "N/A"
                            findViewById<TextView>(R.id.phoneText).text = phone ?: "N/A"

                            val profileImage = findViewById<ImageView>(R.id.profileImage)
                            profileImageUrl?.let { url ->
                                Picasso.get().invalidate(url)
                                Picasso.get().load(url)
                                    .error(R.drawable.account)
                                    .into(profileImage)
                            } ?: profileImage.setImageResource(R.drawable.account)
                        }
                    } else {
                        Toast.makeText(this, "Staff not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching staff details", Toast.LENGTH_SHORT).show()
                }
        } ?: Toast.makeText(this, "Invalid staff ID", Toast.LENGTH_SHORT).show()
    }
}
