package com.faa.cmsportalcui.AdminSide

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.AdminModel.Staff
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class StaffProfileActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var jobTitleEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var editButton: Button
    private lateinit var cancelButton: Button
    private lateinit var mondayCheckBox: CheckBox
    private lateinit var tuesdayCheckBox: CheckBox
    private lateinit var wednesdayCheckBox: CheckBox
    private lateinit var thursdayCheckBox: CheckBox
    private lateinit var fridayCheckBox: CheckBox
    private lateinit var saturdayCheckBox: CheckBox
    private lateinit var sundayCheckBox: CheckBox

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null
    private var staffId: String? = null  // This will hold the ID if updating existing staff

    private val imagePickerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    Glide.with(this).load(it).into(findViewById(R.id.profile_image))
                }
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_profile)

        // Initialize all views
        nameEditText = findViewById(R.id.name)
        jobTitleEditText = findViewById(R.id.job_title)
        locationEditText = findViewById(R.id.location)
        phoneEditText = findViewById(R.id.phone)
        selectImageButton = findViewById(R.id.select_image_button)  // Adjust ID to match your layout
        editButton = findViewById(R.id.edit_button)
        cancelButton = findViewById(R.id.cancel_button)
        mondayCheckBox = findViewById(R.id.mondayCheckBox)
        tuesdayCheckBox = findViewById(R.id.tuesdayCheckBox)
        wednesdayCheckBox = findViewById(R.id.wednesdayCheckBox)
        thursdayCheckBox = findViewById(R.id.thursdayCheckBox)
        fridayCheckBox = findViewById(R.id.fridayCheckBox)
        saturdayCheckBox = findViewById(R.id.saturdayCheckBox)
        sundayCheckBox = findViewById(R.id.sundayCheckBox)

        // Check if we're updating an existing staff member
        staffId = intent.getStringExtra("staffId")

        if (staffId != null) {
            // Load existing staff details if staffId is provided
            loadStaffDetails(staffId!!)
        }

        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        editButton.setOnClickListener {
            saveStaffDetails()
        }

        cancelButton.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }

    private fun loadStaffDetails(staffId: String) {
        db.collection("staff").document(staffId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val staff = document.toObject(Staff::class.java)
                    if (staff != null) {
                        nameEditText.setText(staff.name)
                        jobTitleEditText.setText(staff.jobTitle)
                        locationEditText.setText(staff.location)
                        phoneEditText.setText(staff.phone)

                        // Set checkboxes based on availability
                        mondayCheckBox.isChecked = staff.availability.containsKey("monday")
                        tuesdayCheckBox.isChecked = staff.availability.containsKey("tuesday")
                        wednesdayCheckBox.isChecked = staff.availability.containsKey("wednesday")
                        thursdayCheckBox.isChecked = staff.availability.containsKey("thursday")
                        fridayCheckBox.isChecked = staff.availability.containsKey("friday")
                        saturdayCheckBox.isChecked = staff.availability.containsKey("saturday")
                        sundayCheckBox.isChecked = staff.availability.containsKey("sunday")

                        staff.profileImageUrl?.let {
                            Glide.with(this).load(it).into(findViewById(R.id.profile_image))
                        }
                    }
                } else {
                    Log.d("StaffProfileActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.w("StaffProfileActivity", "Error getting document", e)
            }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun saveStaffDetails() {
        // Generate new Staff ID based on the number of existing staff documents
        db.collection("staff").get().addOnSuccessListener { documents ->
            val staffCount = documents.size() + 1
            val randomNum = (100..999).random()  // Generate a random 3-digit number
            val staffId = "staff${staffCount}$randomNum"  // e.g., staff11, staff22, etc.

            val staffName = nameEditText.text.toString()
            val staffEmail = generateEmailFromName(staffName)
            val staffPassword = "staff123"

            val staff = hashMapOf(
                "name" to staffName,
                "jobTitle" to jobTitleEditText.text.toString(),
                "location" to locationEditText.text.toString(),
                "email" to staffEmail,
                "password" to staffPassword,
                "phone" to phoneEditText.text.toString(),
                "availability" to getAvailability()
            )

            // Save image to Firebase Storage if selected
            selectedImageUri?.let { uri ->
                val ref = storage.reference.child("profile_images/${UUID.randomUUID()}")
                ref.putFile(uri).addOnSuccessListener { taskSnapshot ->
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        staff["profileImageUrl"] = downloadUri.toString()
                        saveToFirestore(staff, staffId)
                    }
                }.addOnFailureListener {
                    Log.e("StaffProfileActivity", "Error uploading image", it)
                }
            } ?: run {
                saveToFirestore(staff, staffId)
            }
        }.addOnFailureListener {
            Log.e("StaffProfileActivity", "Error getting staff collection", it)
        }
    }

    private fun generateEmailFromName(name: String): String {
        val formattedName = name.trim().lowercase(Locale.getDefault()).replace("\\s+".toRegex(), "")
        val randomNum = (100..999).random()  // Generate a random 3-digit number
        return "$formattedName$randomNum@cuisahiwal.com"
    }


    private fun getAvailability(): Map<String, String> {
        val availability = mutableMapOf<String, String>()

        if (mondayCheckBox.isChecked) availability["monday"] = "Available"
        if (tuesdayCheckBox.isChecked) availability["tuesday"] = "Available"
        if (wednesdayCheckBox.isChecked) availability["wednesday"] = "Available"
        if (thursdayCheckBox.isChecked) availability["thursday"] = "Available"
        if (fridayCheckBox.isChecked) availability["friday"] = "Available"
        if (saturdayCheckBox.isChecked) availability["saturday"] = "Available"
        if (sundayCheckBox.isChecked) availability["sunday"] = "Available"

        return availability
    }

    private fun saveToFirestore(staff: Map<String, Any>, staffId: String) {
        if (staffId.isEmpty()) {
            Log.e("StaffProfileActivity", "Invalid staff ID")
            return
        }

        val staffRef = db.collection("staff").document(staffId)
        staffRef.set(staff)
            .addOnSuccessListener {
                Log.d("StaffProfileActivity", "DocumentSnapshot successfully written!")
                // Optionally, navigate to another activity or update the UI
                finish()  // Close the activity
            }
            .addOnFailureListener { e ->
                Log.w("StaffProfileActivity", "Error writing document", e)
            }
    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cancel")
            .setMessage("Are you sure you want to cancel?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                // Navigate back or handle cancel action
                finish() // Example: close the activity
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
