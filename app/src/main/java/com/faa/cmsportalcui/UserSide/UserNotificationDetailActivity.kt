package com.faa.cmsportalcui.UserSide


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
        userId = intent.getStringExtra("USER_ID") ?: ""

        if (notificationId.isNotEmpty()) {
            fetchNotificationDetails(notificationId)
        }

        markAsReadButton.setOnClickListener {
            if (notificationId.isNotEmpty()) {
                markNotificationAsRead(notificationId)

                markAsReadButton.setBackgroundColor(Color.BLACK)
                markAsReadButton.text = "Marked as Read"
                markAsReadButton.isEnabled = false
                finish()

            }
        }


        backButton.setOnClickListener {
            finish()
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
            }
    }

    private fun markNotificationAsRead(notificationId: String) {
        if (userId.isEmpty()) return

        db.collection("users").document(userId)
            .collection("read_notifications").document(notificationId)
            .set(mapOf("isRead" to true))
            .addOnSuccessListener {
                markAsReadButton.setBackgroundColor(Color.BLACK)
                markAsReadButton.text = "Marked as Read"
                markAsReadButton.isEnabled = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to mark as read", Toast.LENGTH_SHORT).show()
            }
    }

}
