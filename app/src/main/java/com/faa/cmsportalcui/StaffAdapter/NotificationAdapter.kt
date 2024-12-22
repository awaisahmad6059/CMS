package com.faa.cmsportalcui.StaffAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffModel.Notification
import com.faa.cmsportalcui.StaffSide.StaffNotificationDetailActivity

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val notificationTitle: TextView = view.findViewById(R.id.notificationTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_dashboard_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.notificationTitle.text = notification.title

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, StaffNotificationDetailActivity::class.java).apply {
                putExtra("title", notification.title)
                putExtra("description", notification.description)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}
