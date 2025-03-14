package com.faa.cmsportalcui.UserSide

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class UserRequestActivity : AppCompatActivity() {

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
    private var isEditMode: Boolean = false
    private var userId: String? = null
    private var requestId: String? = null
    private lateinit var progressDialog: ProgressDialog


    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    private val GALLERY_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_request)

        userId = intent.getStringExtra("user_id") ?: auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User ID is not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        inputTitle = findViewById(R.id.input_title)
        inputDescription = findViewById(R.id.input_description)
        spinnerLocation = findViewById(R.id.spinner_location)
        inputRoomNumber = findViewById(R.id.input_room_number)
        buttonAddPhoto = findViewById(R.id.button_add_photo)
        imageViewPhoto = findViewById(R.id.image_add_photo)
        submitButton = findViewById(R.id.submit_btn)
        cancelButton = findViewById(R.id.cancel_btn)
        backButton = findViewById(R.id.back_button)
        progressDialog = ProgressDialog(this).apply {
            setTitle("Uploading Request")
            setMessage("Please wait...")
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            max = 100
            setCancelable(false)
        }

        buttonAddPhoto.setOnClickListener {
            if (!isEditMode) {
                showPhotoOptionsDialog()
            }
        }

        submitButton.setOnClickListener {
            if (isEditMode) {
                updateRequest()
            } else {
                saveRequest()
            }
        }

        cancelButton.setOnClickListener {
            showCancelDialog()
        }

        backButton.setOnClickListener {
            showCancelDialog()
        }

        isEditMode = intent.getBooleanExtra("isEditMode", false)
        if (isEditMode) {
            populateFieldsForEdit()
            buttonAddPhoto.visibility = Button.GONE
        }
    }

    private fun populateFieldsForEdit() {
        requestId = intent.getStringExtra("id")
        inputTitle.setText(intent.getStringExtra("title"))
        inputDescription.setText(intent.getStringExtra("description"))
        inputRoomNumber.setText(intent.getStringExtra("roomNumber"))
        val photoUrl = intent.getStringExtra("photoUrl")

        setSpinnerLocation(intent.getStringExtra("location"))

        if (!photoUrl.isNullOrEmpty()) {
            Picasso.get().load(photoUrl).into(imageViewPhoto)
            imageViewPhoto.visibility = ImageView.VISIBLE
        }
    }

    private fun setSpinnerLocation(location: String?) {
        val adapter = spinnerLocation.adapter as ArrayAdapter<String>
        val position = adapter.getPosition(location)
        spinnerLocation.setSelection(position)
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
        progressDialog.show()
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val request = hashMapOf(
            "title" to title,
            "description" to description,
            "location" to location,
            "roomNumber" to roomNumber,
            "userId" to userId,
            "timestamp" to currentTime,
            "photoUrl" to "",
            "status" to "pending",
            "userType" to "user"
        )

        generateRequestId(userId!!) { customRequestId ->
            firestore.collection("users").document(userId!!)
                .collection("requests").document(customRequestId)
                .set(request)
                .addOnSuccessListener {
                    uploadPhoto(userId!!, customRequestId)
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to save request: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun generateRequestId(userId: String, callback: (String) -> Unit) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { document ->
            val username = document.getString("fullName") ?: return@addOnSuccessListener
            val baseId = username.toLowerCase(Locale.getDefault())
            val randomThreeDigitNumber = (100..999).random()

            var customRequestId = "$baseId$randomThreeDigitNumber"

            userDocRef.collection("requests").get().addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { doc ->
                    val docId = doc.id
                    if (docId.startsWith(baseId)) {
                        val idNumber = docId.removePrefix(baseId).toIntOrNull()
                        if (idNumber != null && idNumber >= randomThreeDigitNumber) {
                            customRequestId = "$baseId${randomThreeDigitNumber + 1}"
                        }
                    }
                }
                callback(customRequestId)
            }
        }
    }

    private fun updateRequest() {
        val title = inputTitle.text.toString().trim()
        val description = inputDescription.text.toString().trim()
        val location = spinnerLocation.selectedItem.toString().trim()
        val roomNumber = inputRoomNumber.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || roomNumber.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val updatedRequest = hashMapOf<String, Any>(
            "title" to title,
            "description" to description,
            "location" to location,
            "roomNumber" to roomNumber,
            "userId" to userId.orEmpty(),
            "timestamp" to currentTime
        )

        if (selectedImageUri != null) {
            uploadPhoto(userId!!, requestId!!, updatedRequest)
        } else {
            updatedRequest["photoUrl"] = intent.getStringExtra("photoUrl").orEmpty()
            updateRequestInFirestore(userId!!, requestId!!, updatedRequest)
        }
    }


    private fun updateRequestInFirestore(userId: String, requestId: String, updatedRequest: HashMap<String, Any>) {
        firestore.collection("users").document(userId)
            .collection("requests").document(requestId)
            .set(updatedRequest)
            .addOnSuccessListener {
                Toast.makeText(this, "Request updated successfully", Toast.LENGTH_SHORT).show()
                navigateToRequestSentActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update request: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPhoto(userId: String, requestId: String, updatedRequest: HashMap<String, Any>? = null) {
        val storageRef = storage.reference.child("requests/$userId/$requestId.jpg")

        selectedImageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val photoUrl = downloadUri.toString()
                        if (updatedRequest != null) {
                            updatedRequest["photoUrl"] = photoUrl
                            updateRequestInFirestore(userId, requestId, updatedRequest)
                        } else {
                            firestore.collection("users").document(userId)
                                .collection("requests").document(requestId)
                                .update("photoUrl", photoUrl)
                                .addOnSuccessListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Request saved successfully", Toast.LENGTH_SHORT).show()
                                    navigateToRequestSentActivity()
                                }
                                .addOnFailureListener { e ->
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Failed to update photo URL: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
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
        val intent = Intent(this, RequestSentActivity::class.java).apply {
            putExtra("user_id", userId)
        }
        startActivity(intent)
        finish()
    }
}
