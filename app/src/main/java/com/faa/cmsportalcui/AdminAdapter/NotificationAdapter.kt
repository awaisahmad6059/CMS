package com.faa.cmsportalcui.AdminAdapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminSide.AddNotificationActivity
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.Notification
import com.google.firebase.firestore.FirebaseFirestore

class NotificationAdapter(private val context: Context, private var notifications: MutableList<Notification>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        val messageTextView: TextView = itemView.findViewById(R.id.notification_message)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.titleTextView.text = notification.title
        holder.messageTextView.text = notification.description

        holder.editButton.setOnClickListener {
            val intent = Intent(context, AddNotificationActivity::class.java)
            intent.putExtra("notificationId", notification.id)
            intent.putExtra("notificationTitle", notification.title)
            intent.putExtra("notificationDescription", notification.description)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteNotification(position, notification.id.toString())
                }
                .setNegativeButton("No", null)
                .create()

            alertDialog.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    private fun deleteNotification(position: Int, notificationId: String) {
        firestore.collection("notifications").document(notificationId)
            .delete()
            .addOnSuccessListener {
                notifications.removeAt(position)
                notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->

                e.printStackTrace()
            }
    }
}