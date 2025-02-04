package com.faa.cmsportalcui.AdminSide


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.NotificationAdapter
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Locale

class NotificationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var firestore: FirebaseFirestore
    private var notifications: MutableList<Notification> = mutableListOf()
    private var filteredNotifications: MutableList<Notification> = mutableListOf()
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var backButton: ImageView
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(this, filteredNotifications)
        recyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()

        searchBar = findViewById(R.id.search_bar)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterNotifications(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


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

                filteredNotifications.clear()
                filteredNotifications.addAll(notifications)
                adapter.notifyDataSetChanged()
            }
    }

    private fun filterNotifications(query: String) {
        filteredNotifications.clear()

        if (query.isEmpty()) {
            filteredNotifications.addAll(notifications)
        } else {
            val lowerCaseQuery = query.toLowerCase(Locale.ROOT)
            notifications.forEach { notification ->
                if (notification.title?.toLowerCase(Locale.ROOT)?.contains(lowerCaseQuery) == true ||
                    notification.message?.toLowerCase(Locale.ROOT)?.contains(lowerCaseQuery) == true) {
                    filteredNotifications.add(notification)
                }
            }
        }

        adapter.notifyDataSetChanged()
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
