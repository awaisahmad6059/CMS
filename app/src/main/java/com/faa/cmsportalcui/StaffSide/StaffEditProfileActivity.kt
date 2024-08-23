package com.faa.cmsportalcui.StaffSide

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class StaffEditProfileActivity : AppCompatActivity() {
    private var staffId: String? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val PICK_IMAGE_REQUEST = 1
    private var profileImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_edit_profile)

        staffId = intent.getStringExtra("staffId")

        // Initialize views
        val profileImage = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileImage)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val experienceEditText = findViewById<EditText>(R.id.experienceEditText)
        val positionEditText = findViewById<EditText>(R.id.positionEditText)
        val specializationEditText = findViewById<EditText>(R.id.specializationEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val editButton = findViewById<Button>(R.id.editButton)
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        // Load existing staff data
        staffId?.let {
            db.collection("staff").document(it).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val staffData = document.data
                        staffData?.let { data ->
                            val profileImageUrl = data["profileImageUrl"] as? String
                            val name = data["name"] as? String
                            val email = data["email"] as? String
                            val position = data["position"] as? String
                            val specialization = data["specialization"] as? String
                            val phone = data["phone"] as? String
                            val experience = data["experience"] as? String

                            // Set existing data in views
                            profileImageUrl?.let {
                                Picasso.get().load(it).into(profileImage)
                            }
                            nameEditText.setText(name ?: "")
                            emailEditText.setText(email ?: "")
                            positionEditText.setText(position ?: "")
                            specializationEditText.setText(specialization ?: "")
                            phoneEditText.setText(phone ?: "")
                            experienceEditText.setText(experience ?: "")
                        }
                    } else {
                        Toast.makeText(this, "Staff not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching staff details", Toast.LENGTH_SHORT).show()
                }
        }

        // Open gallery to select profile image
        editButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Save updated data
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val position = positionEditText.text.toString()
            val specialization = specializationEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val experience = experienceEditText.text.toString()

            if (staffId != null) {
                // Upload profile image if selected
                profileImageUri?.let { uri ->
                    val profileImageRef = storage.child("staff/${staffId}/profile.jpg")
                    val uploadTask = profileImageRef.putFile(uri)
                    uploadTask.addOnSuccessListener {
                        profileImageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            saveToFirestore(name,email, position, specialization, phone, experience, downloadUri.toString())
                        }
                    }.addOnFailureListener {
                        saveToFirestore(name,email, position, specialization, phone, experience, null)
                    }
                } ?: run {
                    saveToFirestore(name,email, position, specialization, phone, experience, null)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            profileImageUri = data.data
            val profileImage = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileImage)
            Picasso.get().load(profileImageUri).into(profileImage)
        }
    }

    private fun saveToFirestore(name: String,email: String, position: String, specification: String, phone: String, experience: String, profileImageUrl: String?) {
        // Check if the profileImageUrl is null, and if so, fetch the existing URL from Firestore
        if (profileImageUrl == null) {
            staffId?.let {
                db.collection("staff").document(it).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val previousImageUrl = document.getString("profileImageUrl")
                            updateFirestore(name,email, position, specification, phone, experience, previousImageUrl)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error fetching previous image URL", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            // If a new image URL is provided, use it directly
            updateFirestore(name,email, position, specification, phone, experience, profileImageUrl)
        }
    }

    private fun updateFirestore(name: String,email: String, position: String, specification: String, phone: String, experience: String, profileImageUrl: String?) {
        val staffData = mapOf(
            "name" to name,
            "email" to email,
            "position" to position,
            "specification" to specification,
            "phone" to phone,
            "experience" to experience,
            "profileImageUrl" to profileImageUrl
        )

        staffId?.let {
            db.collection("staff").document(it).update(staffData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()  // Close the activity and return to the previous screen
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
