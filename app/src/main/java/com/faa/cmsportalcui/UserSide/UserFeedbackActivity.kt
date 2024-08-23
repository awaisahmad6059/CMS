package com.faa.cmsportalcui.UserSide

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore

class UserFeedbackActivity : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var taskId: String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_feedback)

        // Retrieve userId and taskId from intent extras
        userId = intent.getStringExtra("userId") ?: ""
        taskId = intent.getStringExtra("taskId") ?: ""

        // Initialize views
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        val progress5Star = findViewById<ProgressBar>(R.id.progress_5_star)
        val text5StarPercentage = findViewById<TextView>(R.id.text_5_star_percentage)
        val progress4Star = findViewById<ProgressBar>(R.id.progress_4_star)
        val text4StarPercentage = findViewById<TextView>(R.id.text_4_star_percentage)
        val progress3Star = findViewById<ProgressBar>(R.id.progress_3_star)
        val text3StarPercentage = findViewById<TextView>(R.id.text_3_star_percentage)
        val progress2Star = findViewById<ProgressBar>(R.id.progress_2_star)
        val text2StarPercentage = findViewById<TextView>(R.id.text_2_star_percentage)
        val progress1Star = findViewById<ProgressBar>(R.id.progress_1_star)
        val text1StarPercentage = findViewById<TextView>(R.id.text_1_star_percentage)
        val buttonSubmit = findViewById<Button>(R.id.button_submit)
        val editReview = findViewById<EditText>(R.id.edit_review)

        // Set default values to 0%
        setDefaultRatingDistribution()

        // Set up rating bar listener
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            updateRatingDistribution(rating.toInt())
        }

        // Set up submit button listener
        buttonSubmit.setOnClickListener {
            val rating = ratingBar.rating.toInt()
            val review = editReview.text.toString()
            submitFeedback(userId, taskId, rating, review)
        }

        // Load and display current rating distribution
        loadRatingDistribution()
    }

    private fun setDefaultRatingDistribution() {
        // Set all progress bars to 0%
        updateStarProgress(5, 0)
        updateStarProgress(4, 0)
        updateStarProgress(3, 0)
        updateStarProgress(2, 0)
        updateStarProgress(1, 0)
    }

    private fun updateRatingDistribution(rating: Int) {
        // This should be replaced with logic to fetch and update the real rating distribution
        // For demonstration, just setting some example percentages
        val ratingsCount = mapOf(
            5 to 100,  // Example percentage values
            4 to 100,
            3 to 100,
            2 to 100,
            1 to 100
        )

        // Update progress based on the rating selected
        val selectedRatingPercentage = ratingsCount[rating] ?: 0
        updateStarProgress(rating, selectedRatingPercentage)

        // Update other ratings to display 0%
        (1..5).filter { it != rating }.forEach {
            updateStarProgress(it, 0)
        }
    }

    private fun updateStarProgress(stars: Int, percentage: Int) {
        val progressBar = when (stars) {
            5 -> findViewById<ProgressBar>(R.id.progress_5_star)
            4 -> findViewById<ProgressBar>(R.id.progress_4_star)
            3 -> findViewById<ProgressBar>(R.id.progress_3_star)
            2 -> findViewById<ProgressBar>(R.id.progress_2_star)
            1 -> findViewById<ProgressBar>(R.id.progress_1_star)
            else -> return
        }

        val textPercentage = when (stars) {
            5 -> findViewById<TextView>(R.id.text_5_star_percentage)
            4 -> findViewById<TextView>(R.id.text_4_star_percentage)
            3 -> findViewById<TextView>(R.id.text_3_star_percentage)
            2 -> findViewById<TextView>(R.id.text_2_star_percentage)
            1 -> findViewById<TextView>(R.id.text_1_star_percentage)
            else -> return
        }

        progressBar.progress = percentage
        textPercentage.text = "$percentage%"
    }

    private fun submitFeedback(userId: String, taskId: String, rating: Int, review: String) {
        // Prepare feedback data
        val feedbackData: Map<String, Any> = hashMapOf(
            "userId" to userId,
            "rating" to rating,
            "review" to review
        )

        // Reference to the Firestore collection
        val feedbackCollection = db.collection("completeTask")

        // Query to find a document with the matching assignedTaskId
        feedbackCollection
            .whereEqualTo("assignedTaskId", taskId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No document found with the matching assignedTaskId
                    Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
                } else {
                    // Document exists, update the existing ones
                    val batch = db.batch()
                    for (document in documents) {
                        val documentRef = feedbackCollection.document(document.id)
                        batch.update(documentRef, feedbackData)
                    }

                    batch.commit()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity after submission
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking task", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadRatingDistribution() {
        // Implement this if you need to fetch and display existing rating data
    }
}
