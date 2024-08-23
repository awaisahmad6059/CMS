package com.faa.cmsportalcui.AdminSide

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.Notification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddNotificationActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var cancelButton: Button
    private lateinit var backButton: ImageView
    private lateinit var firestore: FirebaseFirestore

    private var notificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notification)

        titleEditText = findViewById(R.id.notificationTitle)
        descriptionEditText = findViewById(R.id.notificationDescription)
        sendButton = findViewById(R.id.sendButton)
        cancelButton = findViewById(R.id.cancelButton)
        backButton = findViewById(R.id.back_button)
        firestore = FirebaseFirestore.getInstance()

        notificationId = intent.getStringExtra("notificationId")
        titleEditText.setText(intent.getStringExtra("notificationTitle"))
        descriptionEditText.setText(intent.getStringExtra("notificationDescription"))

        sendButton.setOnClickListener {
            if (notificationId.isNullOrEmpty()) {
                saveNotification()
            } else {
                updateNotification(notificationId!!)
            }
        }

        cancelButton.setOnClickListener {
            showCancelConfirmationDialog()
        }

        backButton.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }

    private fun saveNotification() {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please enter both title and description", Toast.LENGTH_SHORT).show()
            return
        }

        val newId = UUID.randomUUID().toString()
        val notification = Notification(
            id = newId,
            title = title,
            description = description,
            date = Timestamp.now()
        )

        firestore.collection("notifications").document(newId).set(notification)
            .addOnSuccessListener {
                Toast.makeText(this, "Notification saved", Toast.LENGTH_SHORT).show()
                navigateToNotification()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save notification", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNotification(notificationId: String) {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please enter both title and description", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("notifications").document(notificationId)
            .update(
                "title", title,
                "description", description,
                "date", Timestamp.now()
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Notification updated", Toast.LENGTH_SHORT).show()
                navigateToNotification()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update notification", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cancel")
            .setMessage("Do you really want to cancel?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                navigateToNotification()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showCancelConfirmationDialog()
    }

    private fun navigateToNotification() {
        val intent = Intent(this, NotificationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
