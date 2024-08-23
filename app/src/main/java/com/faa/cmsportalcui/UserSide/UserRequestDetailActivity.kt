package com.faa.cmsportalcui.UserSide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.squareup.picasso.Picasso

class UserRequestDetailActivity : AppCompatActivity() {
    private var userId: String? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_request_detail)

        // Retrieve data from intent extras
        userId = intent.getStringExtra("user_id")  // Retrieve user_id
        val id = intent.getStringExtra("id") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val room = intent.getStringExtra("roomNumber") ?: ""
        val photoUrl = intent.getStringExtra("photoUrl") ?: ""
        val date = intent.getStringExtra("date") ?: ""

        // Initialize views
        val idValue: TextView = findViewById(R.id.id_value)
        val titleValue: TextView = findViewById(R.id.title_value)
        val descriptionValue: TextView = findViewById(R.id.description_value)
        val locationValue: TextView = findViewById(R.id.location_value)
        val roomValue: TextView = findViewById(R.id.room_value)
        val photoImage: ImageView = findViewById(R.id.photo_image)
        val cancelButton: Button = findViewById(R.id.cancel_button)
        val backButton: ImageButton = findViewById(R.id.back_button)
        val editButton: Button = findViewById(R.id.edit_request_button)

        // Set data to views
        idValue.text = id
        titleValue.text = title
        descriptionValue.text = description
        locationValue.text = location
        roomValue.text = room

        // Load photo using Picasso
        if (photoUrl.isNotEmpty()) {
            Picasso.get().load(photoUrl).into(photoImage)
        } else {
            photoImage.setImageResource(R.drawable.image) // Placeholder image
        }

        // Set click listeners
        cancelButton.setOnClickListener {
            navigateBackToRequestActivity()
        }

        backButton.setOnClickListener {
            navigateBackToRequestActivity()
        }

        editButton.setOnClickListener {
            navigateToEditRequestActivity(id, title, description, location, room, photoUrl)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        navigateBackToRequestActivity()
    }

    private fun navigateBackToRequestActivity() {
        val intent = Intent(this, UserMaintanancerequestActivity::class.java)
        intent.putExtra("user_id", userId)  // Pass user_id
        startActivity(intent)
        finish()
    }

    private fun navigateToEditRequestActivity(id: String, title: String, description: String, location: String, room: String, photoUrl: String) {
        val intent = Intent(this, UserRequestActivity::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("description", description)
            putExtra("location", location)
            putExtra("roomNumber", room)
            putExtra("photoUrl", photoUrl)
            putExtra("isEditMode", true)
            putExtra("user_id", userId)  // Pass user_id
        }
        startActivity(intent)
        finish()
    }

}
