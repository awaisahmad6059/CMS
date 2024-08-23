package com.faa.cmsportalcui.UserAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.UserMaintenanceRequest
import com.faa.cmsportalcui.UserSide.UserRequestDetailActivity

class UserMaintenanceRequestAdapter(
    private val requests: List<UserMaintenanceRequest>,
    private val userId: String // Accept userId here
) : RecyclerView.Adapter<UserMaintenanceRequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val date: TextView = itemView.findViewById(R.id.date)
        val detailsButton: Button = itemView.findViewById(R.id.details_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_maintanance_request_item, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        holder.title.text = request.title
        holder.date.text = request.date
        holder.detailsButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UserRequestDetailActivity::class.java).apply {
                putExtra("id", request.id)
                putExtra("title", request.title)
                putExtra("description", request.description)
                putExtra("location", request.location)
                putExtra("roomNumber", request.room)
                putExtra("photoUrl", request.photoUrl)
                putExtra("date", request.date)
                putExtra("user_id", userId) // Pass userId here
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}
