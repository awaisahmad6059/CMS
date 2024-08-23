package com.faa.cmsportalcui.AdminSide

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AdminRequestActivity : AppCompatActivity() {

    private lateinit var inputTitle: EditText
    private lateinit var inputDescription: EditText
    private lateinit var spinnerLocation: Spinner
    private lateinit var inputRoomNumber: EditText
    private lateinit var buttonAddPhoto: ImageButton
    private lateinit var imageViewPhoto: ImageView
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button
    private lateinit var backButton: ImageButton
    private var selectedImageUri: Uri? = null

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val adminId = "lzcmCdafqJ6dg8vAYexS"  // Known admin ID
    private var adminName: String? = null

    private val GALLERY_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_request)

        inputTitle = findViewById(R.id.input_title)
        inputDescription = findViewById(R.id.input_description)
        spinnerLocation = findViewById(R.id.spinner_location)
        inputRoomNumber = findViewById(R.id.input_room_number)
        buttonAddPhoto = findViewById(R.id.button_add_photo)
        imageViewPhoto = findViewById(R.id.image_add_photo)
        submitButton = findViewById(R.id.submit_btn)
        cancelButton = findViewById(R.id.cancel_btn)
        backButton = findViewById(R.id.back_button)

        buttonAddPhoto.setOnClickListener {
            showPhotoOptionsDialog()
        }

        submitButton.setOnClickListener {
            saveRequest()
        }

        cancelButton.setOnClickListener {
            showCancelDialog()
        }

        backButton.setOnClickListener {
            showCancelDialog()
        }

        // Fetch admin name
        fetchAdminName()
    }

    private fun fetchAdminName() {
        firestore.collection("admins").document(adminId)
            .get()
            .addOnSuccessListener { document ->
                adminName = document.getString("name")?.replace(" ", "")?.lowercase()
                Log.d("AdminRequestActivity", "Admin Name: $adminName")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch admin name: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveRequest() {
        val title = inputTitle.text.toString().trim()
        val description = inputDescription.text.toString().trim()
        val location = spinnerLocation.selectedItem.toString().trim()
        val roomNumber = inputRoomNumber.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || roomNumber.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill out all fields and add a photo", Toast.LENGTH_SHORT).show()
            return
        }

        if (adminName.isNullOrEmpty()) {
            Toast.makeText(this, "Admin name is not available", Toast.LENGTH_SHORT).show()
            return
        }

        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Generate Request ID and save request
        generateRequestId { requestId ->
            val request = hashMapOf(
                "title" to title,
                "description" to description,
                "location" to location,
                "roomNumber" to roomNumber,
                "timestamp" to currentTime,
                "photoUrl" to "", // Placeholder, will be updated after photo upload
                "adminId" to adminId, // Add adminId to the request
                "userType" to "admin", // Add userType
                "status" to "pending" // Add status field with initial value "pending"
            )

            firestore.collection("admins").document(adminId).collection("requests").document(requestId).set(request)
                .addOnSuccessListener {
                    uploadPhoto(requestId)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save request: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun generateRequestId(callback: (String) -> Unit) {
        val prefix = (adminName?.take(2) ?: "ad").uppercase()  // Take the first two letters of the admin's name, default to "ad" if null

        firestore.collection("admins").document(adminId)
            .collection("requests")
            .get()
            .addOnSuccessListener { result ->
                val existingRequestIds = result.documents.map { it.id }
                val existingNumbers = existingRequestIds
                    .mapNotNull { id ->
                        // Extract the number part from the ID after the prefix
                        id.removePrefix(prefix).toIntOrNull()
                    }

                val randomNumber = generateRandomNumber(existingNumbers)
                val newRequestId = "$prefix$randomNumber"
                callback(newRequestId)
            }
            .addOnFailureListener { e ->
                Log.e("AdminRequestActivity", "Error generating request ID", e)
                // Handle error by defaulting to 'prefix' followed by a random number if fetching failed
                val defaultRequestId = "$prefix${generateRandomNumber(emptyList())}"
                callback(defaultRequestId)
            }
    }

    // Function to generate a random number with 3 to 4 digits that is not in the existingNumbers list
    private fun generateRandomNumber(existingNumbers: List<Int>): Int {
        var randomNumber: Int
        do {
            randomNumber = (100..9999).random()  // Generate a random number between 100 and 9999
        } while (existingNumbers.contains(randomNumber))
        return randomNumber
    }

    private fun uploadPhoto(requestId: String) {
        val storageRef = storage.reference.child("requests/$adminId/$requestId.jpg")

        selectedImageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val photoUrl = downloadUri.toString()
                        firestore.collection("admins").document(adminId)
                            .collection("requests").document(requestId)
                            .update("photoUrl", photoUrl)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Request saved successfully", Toast.LENGTH_SHORT).show()
                                navigateToRequestSentActivity()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to update photo URL: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upload photo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showPhotoOptionsDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Photo")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    selectedImageUri = data?.data
                    imageViewPhoto.setImageURI(selectedImageUri)
                    imageViewPhoto.visibility = ImageView.VISIBLE
                }
                CAMERA_REQUEST_CODE -> {
                    val photoBitmap = data?.extras?.get("data") as? Bitmap
                    photoBitmap?.let {
                        selectedImageUri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, it, "IMG_" + System.currentTimeMillis(), null))
                        imageViewPhoto.setImageURI(selectedImageUri)
                        imageViewPhoto.visibility = ImageView.VISIBLE
                    }
                }
            }
        }
    }

    private fun showCancelDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cancel Request")
        builder.setMessage("Are you sure you want to cancel this request?")
        builder.setPositiveButton("Yes") { _, _ -> finish() }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    private fun navigateToRequestSentActivity() {
        val intent = Intent(this, AdminRequestSentActivity::class.java)
        startActivity(intent)
        finish()
    }
}
