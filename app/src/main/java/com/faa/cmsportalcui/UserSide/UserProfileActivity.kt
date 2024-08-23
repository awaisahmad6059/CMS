package com.faa.cmsportalcui.UserSide

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.exoplayer.image.ImageDecoder
import com.faa.cmsportalcui.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UserProfileActivity : AppCompatActivity() {
    private var userId: String? = null

    private lateinit var saveBtn: Button
    private lateinit var editPhotoBtn: Button
    private lateinit var profilePhoto: ImageView
    private lateinit var fullName: EditText
    private lateinit var description: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userId = intent.getStringExtra("user_id")  // Retrieve user_id

        saveBtn = findViewById(R.id.button_save)
        editPhotoBtn = findViewById(R.id.button_edit_photo)
        profilePhoto = findViewById(R.id.profile_photo)
        fullName = findViewById(R.id.edit_full_name)
        description = findViewById(R.id.editdescription)
        email = findViewById(R.id.edit_email)
        phone = findViewById(R.id.edit_phone)

        editPhotoBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        saveBtn.setOnClickListener {
            saveUserProfile()
        }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            showExitConfirmationDialog()
        }

        loadUserData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            profilePhoto.setImageURI(imageUri)
        }
    }

    private fun saveUserProfile() {
        val name = fullName.text.toString()
        val desc = description.text.toString()
        val emailText = email.text.toString()
        val phoneText = phone.text.toString()
        val userId = this.userId ?: return  // Use the userId passed in the intent

        if (imageUri != null) {
            val ref = storage.reference.child("profile_images/$userId/${UUID.randomUUID()}")

            // Compress the image
            val compressedImage = compressImage(imageUri!!)

            ref.putBytes(compressedImage)
                .addOnSuccessListener { taskSnapshot ->
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        updateUserDetails(userId, name, desc, emailText, phoneText, uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image Upload Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // No new image selected, so retrieve the current image URL
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val currentImageUrl = document.getString("profileImageUrl")
                        updateUserDetails(userId, name, desc, emailText, phoneText, currentImageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to retrieve current profile image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun compressImage(uri: Uri): ByteArray {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // For Android 9.0 (API level 28) and above
            val source = android.graphics.ImageDecoder.createSource(contentResolver, uri)
            android.graphics.ImageDecoder.decodeBitmap(source)
        } else {
            // For devices below Android 9.0
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // Adjust quality as needed
        return outputStream.toByteArray()
    }


    private fun updateUserDetails(userId: String, name: String, desc: String, email: String, phone: String, profileImageUrl: String?) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val existingPassword = document.getString("password") // Retrieve existing password

                    val user = hashMapOf(
                        "username" to name,
                        "description" to desc,
                        "email" to email,
                        "phone" to phone,
                        "profileImageUrl" to profileImageUrl,
                        "userType" to "user",
                        "password" to existingPassword // Ensure password is not overwritten
                    )

                    firestore.collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                            navigateToDashboard(userId, name, desc, profileImageUrl)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadUserData() {
        val userId = this.userId ?: return
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    fullName.setText(document.getString("username"))
                    description.setText(document.getString("description"))
                    email.setText(document.getString("email"))
                    phone.setText(document.getString("phone"))
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (profileImageUrl != null) {
                        // Load image using a library like Picasso or Glide
                        // Example with Picasso:
                        // Picasso.get().load(profileImageUrl).into(profilePhoto)
                    }
                }
            }
    }

    private fun navigateToDashboard(userId: String, name: String, desc: String, profileImageUrl: String?) {
        val intent = Intent(this, UserDashboardActivity::class.java)
        intent.putExtra("user_id", userId)  // Pass userId to ensure the dashboard reflects the correct user
        intent.putExtra("userName", name)
        intent.putExtra("userDesc", desc)
        intent.putExtra("profileImageUrl", profileImageUrl)
        startActivity(intent)
        finish()
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Unsaved Changes")
        builder.setMessage("You have unsaved changes. Do you really want to go back?")
        builder.setPositiveButton("Yes") { _, _ ->
            navigateToDashboard(this.userId ?: "")
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun navigateToDashboard(userId: String) {
        val intent = Intent(this, UserDashboardActivity::class.java)
        intent.putExtra("user_id", userId)
        startActivity(intent)
        finish()
    }
}
