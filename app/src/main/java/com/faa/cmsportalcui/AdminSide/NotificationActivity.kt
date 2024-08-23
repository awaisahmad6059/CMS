package com.faa.cmsportalcui.AdminSide


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.NotificationAdapter
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NotificationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var firestore: FirebaseFirestore
    private var notifications: MutableList<Notification> = mutableListOf()
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(this, notifications)
        recyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()

        loadNotifications()

        val notificationBtn: Button = findViewById(R.id.notification_button)
        notificationBtn.setOnClickListener {
            startActivity(Intent(this@NotificationActivity, AddNotificationActivity::class.java))
        }

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            navigateToAdminDashboard()
        }
    }

    private fun loadNotifications() {
        listenerRegistration = firestore.collection("notifications")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                notifications.clear()
                snapshots?.forEach { document ->
                    val notification = document.toObject(Notification::class.java).apply {
                        id = document.id
                    }
                    notifications.add(notification)
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        navigateToAdminDashboard()
    }

    private fun navigateToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
