package com.faa.cmsportalcui.AdminSide

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R

class FeedbackActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.close).setOnClickListener {
            finish()
        }

        val itemId = intent.getStringExtra("id")
        val assignedBy = intent.getStringExtra("assignedBy")
        val review = intent.getStringExtra("review")
        val rating = intent.getFloatExtra("rating", 0f)

        val itemIdTextView = findViewById<TextView>(R.id.item_id)
        val assignedByTextView = findViewById<TextView>(R.id.item_user)
        val reviewTextView = findViewById<TextView>(R.id.item_description)
        val ratingTextView = findViewById<TextView>(R.id.rating_number)
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)

        itemIdTextView.text = itemId ?: "N/A"
        assignedByTextView.text = assignedBy ?: "N/A"
        reviewTextView.text = review ?: "No review provided."

        ratingTextView.text = rating.toString()
        ratingBar.rating = rating
    }
}
