package com.faa.cmsportalcui.AdminSide


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AdminEditProfileActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var adminId: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var profilePhoto: ImageView
    private lateinit var editFullName: EditText
    private lateinit var editExperience: EditText
    private lateinit var editSpeciality: EditText
    private lateinit var editPhone: EditText
    private lateinit var buttonSave: Button
    private lateinit var progressBar: ProgressBar  // Add ProgressBar reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_profile)

        adminId = intent.getStringExtra("adminId")

        profilePhoto = findViewById(R.id.profile_photo)
        editFullName = findViewById(R.id.edit_full_name)
        editExperience = findViewById(R.id.editexperience)
        editSpeciality = findViewById(R.id.editspeciality)
        editPhone = findViewById(R.id.edit_phone)
        buttonSave = findViewById(R.id.button_save)
        progressBar = findViewById(R.id.progressBar)  // Initialize ProgressBar

        val buttonEditPhoto: Button = findViewById(R.id.button_edit_photo)
        val backButton: ImageButton = findViewById(R.id.back_button)

        buttonEditPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        buttonSave.setOnClickListener {
            saveAdminDetails()
        }

        backButton.setOnClickListener {
            finish()
        }

        loadAdminDetails(adminId!!)
    }

    private fun loadAdminDetails(adminId: String) {
        db.collection("admins").document(adminId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    editFullName.setText(document.getString("name") ?: "")
                    editExperience.setText(document.getString("experience") ?: "")
                    editSpeciality.setText(document.getString("specialty") ?: "")
                    editPhone.setText(document.getString("phoneNumber") ?: "")

                    val profileImageUrl = document.getString("profileImageUrl")
                    if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(profilePhoto)
                    } else {
                        profilePhoto.setImageResource(R.drawable.account)
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profilePhoto.setImageURI(selectedImageUri)
        }
    }

    private fun saveAdminDetails() {
        progressBar.visibility = View.VISIBLE

        val adminRef = db.collection("admins").document(adminId!!)
        val updates = hashMapOf<String, Any>(
            "name" to editFullName.text.toString(),
            "experience" to editExperience.text.toString(),
            "specialty" to editSpeciality.text.toString(),
            "phoneNumber" to editPhone.text.toString()
        )

        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("profilePhotos/${UUID.randomUUID()}")
            storageRef.putFile(selectedImageUri!!)
                .addOnProgressListener { snapshot ->
                    val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toInt()
                    // Update progress (you can show the progress as needed)
                    // Example: update a progress bar in UI with progress
                }
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        updates["profileImageUrl"] = uri.toString()
                        adminRef.update(updates)
                            .addOnSuccessListener {
                                progressBar.visibility = View.GONE  // Hide the ProgressBar when the operation is complete
                                finish()
                            }
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE  // Hide the ProgressBar if there's an error
                }
        } else {
            adminRef.update(updates)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE  // Hide the ProgressBar when the operation is complete
                    finish()
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE  // Hide the ProgressBar if there's an error
                }
        }
    }
}
