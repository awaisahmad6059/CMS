package com.faa.cmsportalcui.UserSide

import android.os.Bundle
import android.view.View
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

        userId = intent.getStringExtra("userId") ?: ""
        taskId = intent.getStringExtra("taskId") ?: ""

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

        setDefaultRatingDistribution()

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            updateRatingDistribution(rating.toInt())
        }

        buttonSubmit.setOnClickListener {
            val rating = ratingBar.rating.toInt()
            val review = editReview.text.toString()
            submitFeedback(userId, taskId, rating, review)
        }

        loadRatingDistribution()
    }

    private fun setDefaultRatingDistribution() {
        updateStarProgress(5, 0)
        updateStarProgress(4, 0)
        updateStarProgress(3, 0)
        updateStarProgress(2, 0)
        updateStarProgress(1, 0)
    }

    private fun updateRatingDistribution(rating: Int) {

        val ratingsCount = mapOf(
            5 to 100,
            4 to 100,
            3 to 100,
            2 to 100,
            1 to 100
        )

        val selectedRatingPercentage = ratingsCount[rating] ?: 0
        updateStarProgress(rating, selectedRatingPercentage)

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
        val progressBar = findViewById<ProgressBar>(R.id.progressBar) // Get ProgressBar
        progressBar.visibility = View.VISIBLE
        val feedbackData: Map<String, Any> = hashMapOf(
            "userId" to userId,
            "rating" to rating,
            "review" to review
        )

        val feedbackCollection = db.collection("completeTask")

        feedbackCollection
            .whereEqualTo("assignedTaskId", taskId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
                } else {
                    val batch = db.batch()
                    for (document in documents) {
                        val documentRef = feedbackCollection.document(document.id)
                        batch.update(documentRef, feedbackData)
                    }

                    batch.commit()
                        .addOnSuccessListener {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Error checking task", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadRatingDistribution() {
    }
}
