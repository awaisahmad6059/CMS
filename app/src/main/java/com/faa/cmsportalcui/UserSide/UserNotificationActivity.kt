package com.faa.cmsportalcui.UserSide

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserAdapter.UserNotificationAdapter
import com.faa.cmsportalcui.UserModel.Notification
import com.google.firebase.firestore.FirebaseFirestore

class UserNotificationActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UserNotificationAdapter
    private lateinit var notifications: MutableList<Notification>
    private val userId by lazy { intent.getStringExtra("user_id") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_notification)

        val backButton: ImageButton = findViewById(R.id.back_button)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val markAllReadButton: Button = findViewById(R.id.button_mark_all_read)

        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchNotifications()

        notifications = mutableListOf()
        adapter = UserNotificationAdapter(this, notifications, userId) { notification -> }
        recyclerView.adapter = adapter


        backButton.setOnClickListener {
            finish()
        }

        markAllReadButton.setOnClickListener {
            if (notifications.isNotEmpty()) {
                markAllNotificationsRead()
            } else {
                Log.w("UserNotificationActivity", "No notifications to mark as read")
            }
        }
    }

    private fun fetchNotifications() {
        db.collection("notifications")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("UserNotificationActivity", "Error fetching notifications: ${e.message}", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val fetchedNotifications = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Notification::class.java)?.apply { id = doc.id }
                    }

                    applyReadState(fetchedNotifications)
                }
            }
    }

    private fun applyReadState(fetchedNotifications: List<Notification>) {
        db.collection("users").document(userId).collection("read_notifications")
            .get()
            .addOnSuccessListener { readDocuments ->
                val readNotificationIds = readDocuments.map { it.id }
                fetchedNotifications.forEach { notification ->
                    notification.isRead = notification.id in readNotificationIds
                }

                notifications.clear()
                notifications.addAll(fetchedNotifications)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("UserNotificationActivity", "Error fetching read notifications: ${e.message}", e)
            }
    }


    private fun markAllNotificationsRead() {
        if (notifications.isEmpty()) {
            Log.w("UserNotificationActivity", "No notifications available to mark as read.")
            return
        }

        adapter.markAllAsRead()
    }
}
