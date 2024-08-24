package com.faa.cmsportalcui.UserSide


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.Notification
import com.google.firebase.firestore.FirebaseFirestore

class UserNotificationDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var markAsReadButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var userId: String
    private lateinit var notificationId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_notification_detail)

        descriptionTextView = findViewById(R.id.description)
        titleTextView = findViewById(R.id.title)
        markAsReadButton = findViewById(R.id.mark_as_read_button)
        backButton = findViewById(R.id.menu_button)

        notificationId = intent.getStringExtra("NOTIFICATION_ID") ?: ""
        userId = intent.getStringExtra("USER_ID") ?: "" // Get user_id from intent

        if (notificationId.isNotEmpty()) {
            fetchNotificationDetails(notificationId)
        }

        markAsReadButton.setOnClickListener {
            if (notificationId.isNotEmpty()) {
                markNotificationAsRead(notificationId)
            }
        }

        backButton.setOnClickListener {
            finish() // Return to the previous activity
        }
    }

    private fun fetchNotificationDetails(notificationId: String) {
        db.collection("notifications").document(notificationId)
            .get()
            .addOnSuccessListener { document ->
                val notification = document.toObject(Notification::class.java)
                descriptionTextView.text = notification?.description ?: "No details available"
                titleTextView.text = notification?.title ?: "No title available"
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

    private fun markNotificationAsRead(notificationId: String) {
        if (userId.isEmpty()) return

        db.collection("users").document(userId).collection("read_notifications").document(notificationId)
            .set(mapOf("isRead" to true))
            .addOnSuccessListener {
                // Handle success - update UI, mark as read
                finish() // Close activity after marking as read
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}
