package com.faa.cmsportalcui.AdminSide


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
//    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_profile)

        adminId = intent.getStringExtra("adminId")

        profilePhoto = findViewById(R.id.profile_photo)
        editFullName = findViewById(R.id.edit_full_name)
        editExperience = findViewById(R.id.editexperience)
        editSpeciality = findViewById(R.id.editspeciality)
//        editEmail = findViewById(R.id.edit_email)
        editPhone = findViewById(R.id.edit_phone)
        buttonSave = findViewById(R.id.button_save)

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
//                    editEmail.setText(document.getString("email") ?: "")
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
        val adminRef = db.collection("admins").document(adminId!!)
        val updates = hashMapOf<String, Any>(
            "name" to editFullName.text.toString(),
            "experience" to editExperience.text.toString(),
            "specialty" to editSpeciality.text.toString(),
//            "email" to editEmail.text.toString(),
            "phoneNumber" to editPhone.text.toString()
        )

        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("profilePhotos/${UUID.randomUUID()}")
            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        updates["profileImageUrl"] = uri.toString()
                        adminRef.update(updates)
                            .addOnSuccessListener {
                                finish()
                            }
                    }
                }
        } else {
            adminRef.update(updates)
                .addOnSuccessListener {
                    finish()
                }
        }
    }
}
