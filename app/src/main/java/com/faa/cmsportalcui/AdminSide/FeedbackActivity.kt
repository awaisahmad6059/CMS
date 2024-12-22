package com.faa.cmsportalcui.AdminSide

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        val itemIdTextView = findViewById<TextView>(R.id.item_id)
        val assignedByTextView = findViewById<TextView>(R.id.item_user)
        val reviewTextView = findViewById<TextView>(R.id.item_description)

        itemIdTextView.text = itemId ?: "N/A"
        assignedByTextView.text = assignedBy ?: "N/A"
        reviewTextView.text = review ?: "No review provided."
    }
}
