package com.faa.cmsportalcui.UserAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.UserDashboardRequest
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UserDashboardRequestAdapter(private val requests: List<UserDashboardRequest>) :
    RecyclerView.Adapter<UserDashboardRequestAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.request_title)
        val timestamp: TextView = itemView.findViewById(R.id.request_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_activities_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.title.text = request.title

        // Convert timestamp string to Date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = try {
            val date = dateFormat.parse(request.timestamp)
            val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: ""
        } catch (e: ParseException) {
            ""
        }

        holder.timestamp.text = formattedDate
    }

    override fun getItemCount(): Int = requests.size
}
