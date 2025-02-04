package com.faa.cmsportalcui.AdminSide
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.AdminAdapter.AvailabilityAdapter
import com.faa.cmsportalcui.AdminModel.Availability
import com.faa.cmsportalcui.AdminModel.Staff
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MaintananceStaffDetailsActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var staffId: String? = null
    private var taskId: String? = null
    private var id: String? = null
    private var commentText: String? = null
    private var description: String? = null
    private var photoUrl: String? = null
    private var profileImageUrl: String? = null
    private var timestamp: String? = null
    private var userId: String? = null
    private var adminId: String? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintanance_staff_details)

        val profileImage: ImageView = findViewById(R.id.profileImage)
        val nameText: TextView = findViewById(R.id.nameText)
//        val specializationText: TextView = findViewById(R.id.specializationText)
        val experienceText: TextView = findViewById(R.id.experienceDescription)
        val positionText: TextView = findViewById(R.id.positionDescription)
        val specificationText: TextView = findViewById(R.id.specificationDescription)
        val emailTxt: TextView = findViewById(R.id.email)
        val backButton: ImageButton = findViewById(R.id.back_button)
        val cancelBtn: Button = findViewById(R.id.cancelButton)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val saveAssignButton: Button = findViewById(R.id.saveAssignButton)
        val progressToast = Toast.makeText(this, "Creating subcollection...", Toast.LENGTH_SHORT)

        staffId = intent.getStringExtra("staffId")
        taskId = intent.getStringExtra("taskId")
        id = intent.getStringExtra("id")
        description = intent.getStringExtra("title")
        commentText = intent.getStringExtra("description")
        photoUrl = intent.getStringExtra("photoUrl")
        profileImageUrl = intent.getStringExtra("profileImageUrl")
        timestamp = intent.getStringExtra("timestamp")
        userId = intent.getStringExtra("userId")
        adminId = intent.getStringExtra("adminId")

        if (staffId == null || taskId == null) {
            Log.e("MaintananceStaffDetailsActivity", "Received null staffId or taskId")
            return
        }

        staffId?.let {
            loadStaffDetails(it, profileImage, nameText, emailTxt,experienceText, positionText, specificationText)
        }

        saveAssignButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE

            Thread {
                for (progress in 1..100) {
                    Thread.sleep(50)  // Simulate a delay
                    runOnUiThread {
                        progressBar.progress = progress
                    }
                }
            }.start()
            if (id.isNullOrEmpty() || title.isNullOrEmpty() || description.isNullOrEmpty() || photoUrl.isNullOrEmpty() || timestamp.isNullOrEmpty()) {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Not Assign", Toast.LENGTH_SHORT).show()
                goToAdminDashboard()
                return@setOnClickListener
            }

            val subcollectionData = hashMapOf(
                "id" to id,
                "title" to commentText,
                "description" to photoUrl,
                "photoUrl" to description,
                "profileImageUrl" to profileImageUrl,
                "timestamp" to timestamp
            )

            when {
                !userId.isNullOrEmpty() -> subcollectionData["adminId"] = userId
                !adminId.isNullOrEmpty() -> subcollectionData["userId"] = adminId
            }

            staffId?.let { staffId ->
                taskId?.let { taskId ->
                    db.collection("staff").document(staffId)
                        .collection("assignedTasks")
                        .document(taskId)
                        .set(subcollectionData, SetOptions.merge()) // Use SetOptions.merge() to avoid overwriting other fields
                        .addOnSuccessListener {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Request assigned successfully", Toast.LENGTH_SHORT).show()
                            goToAdminDashboard()
                        }
                        .addOnFailureListener { e ->
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Failed to assign request", Toast.LENGTH_SHORT).show()
                            goToAdminDashboard()
                        }
                }
            }
        }

        backButton.setOnClickListener {
            finish()
        }
        cancelBtn.setOnClickListener {
            finish()
        }

    }

    private fun goToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun loadStaffDetails(
        staffId: String,
        profileImage: ImageView,
        nameText: TextView,
        emailTxt: TextView,
        experienceText: TextView,
        positionText: TextView,

        specificationText: TextView

//        specializationText: TextView,

    ) {
        db.collection("staff").document(staffId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val staff = document.toObject(Staff::class.java)
                    if (staff != null) {
                        staff.profileImageUrl?.let { url ->
                            Glide.with(this).load(url).into(profileImage)
                        }
                        nameText.text = staff.name
//                        specializationText.text = staff.jobTitle
                        emailTxt.text = staff.email
                        experienceText.text = staff.experience ?: "Not available"
                        positionText.text = staff.position ?: "Not available"
                        specificationText.text = staff.specification ?: "Not available"


                    }
                } else {
                    Log.d("MaintananceStaffDetailsActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.w("MaintananceStaffDetailsActivity", "Error getting documents.", e)
            }
    }
}
