package com.faa.cmsportalcui.UserAdapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.Notification
import com.faa.cmsportalcui.UserSide.UserNotificationDetailActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class UserNotificationAdapter(
    private val context: Context,
    private var notifications: MutableList<Notification>,
    private val userId: String,
    private val onNotificationRemoved: (Notification) -> Unit
) : RecyclerView.Adapter<UserNotificationAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.text_notification)
        val dateTextView: TextView = itemView.findViewById(R.id.text_date)
        val checkBox: CheckBox = itemView.findViewById(R.id.CheckBox)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val notification = notifications[position]
                    val intent = Intent(context, UserNotificationDetailActivity::class.java).apply {
                        putExtra("NOTIFICATION_ID", notification.id)
                        putExtra("USER_ID", userId) // Pass user_id
                    }
                    context.startActivity(intent)
                }
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val notification = notifications[position]
                    if (isChecked && !notification.isRead) {
                        markAsRead(notification)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_notifiaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.titleTextView.text = notification.title

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = when (val date = notification.date) {
            is Timestamp -> dateFormat.format(date.toDate())
            is Long -> dateFormat.format(Date(date))
            else -> "Unknown date"
        }
        holder.dateTextView.text = formattedDate

        // Apply styles based on read status
        if (notification.isRead) {
            holder.titleTextView.setTypeface(null, Typeface.NORMAL)
            holder.titleTextView.setTextColor(Color.BLACK) // Normal text color for read notifications
        } else {
            holder.titleTextView.setTypeface(null, Typeface.BOLD)
            holder.titleTextView.setTextColor(Color.RED) // Red text color for unread notifications
        }
        holder.checkBox.isChecked = notification.isRead
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun markAllAsRead() {
        notifications.forEach { notification ->
            if (!notification.isRead) {
                notification.isRead = true
                markAsRead(notification)
            }
        }
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged() // Update the UI to remove bold styling and change color
        }
    }

    private fun markAsRead(notification: Notification) {
        notification.isRead = true
        notifyItemChanged(notifications.indexOf(notification))

        notification.id?.let { id ->
            db.collection("users").document(userId).collection("read_notifications").document(id)
                .set(mapOf("isRead" to true))
                .addOnSuccessListener {
                    Log.d("UserNotificationAdapter", "Notification marked as read in Firestore: $id")
                }
                .addOnFailureListener { e ->
                    Log.e("UserNotificationAdapter", "Error updating notification in Firestore: ${e.message}", e)
                }
        }
    }
}
