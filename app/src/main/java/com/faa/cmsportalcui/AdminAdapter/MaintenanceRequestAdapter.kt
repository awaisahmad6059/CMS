package com.faa.cmsportalcui.AdminAdapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.TextView
import android.widget.Button
import androidx.cardview.widget.CardView
import com.faa.cmsportalcui.AdminModel.MaintenanceRequest
import com.faa.cmsportalcui.AdminSide.MaintananceDetailActivity

class MaintenanceRequestAdapter(
    private var requests: MutableList<MaintenanceRequest>
) : RecyclerView.Adapter<MaintenanceRequestAdapter.MaintenanceRequestViewHolder>() {

    private var colorIndex = 0
    private val colors = listOf("#a2c9fe", "#F9F497", "#C6FDC5", "#DCDDFD")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.maintanance_item, parent, false)
        return MaintenanceRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaintenanceRequestViewHolder, position: Int) {
        val request = requests[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int = requests.size

    fun updateList(newList: List<MaintenanceRequest>) {
        requests.clear()
        requests.addAll(newList)
        notifyDataSetChanged()
    }

    inner class MaintenanceRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: CircleImageView = itemView.findViewById(R.id.ProfileImage)
        private val requestId: TextView = itemView.findViewById(R.id.request_id)
        private val requestTitle: TextView = itemView.findViewById(R.id.request_title)
        private val viewButton: Button = itemView.findViewById(R.id.view_button)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(request: MaintenanceRequest) {
            requestId.text = request.id
            requestTitle.text = request.title

            val profileImageUrl = request.profileImageUrl
            if (profileImageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.account)
                    .into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.account)
            }

            cardView.setCardBackgroundColor(Color.parseColor(colors[colorIndex]))

            colorIndex = (colorIndex + 1) % colors.size

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, MaintananceDetailActivity::class.java).apply {
                    putExtra("id", request.id)
                    putExtra("title", request.title)
                    putExtra("description", request.description)
                    putExtra("timestamp", request.timestamp)
                    putExtra("profileImageUrl", request.profileImageUrl)
                    putExtra("photoUrl", request.imageUrl)
                    putExtra("authorname", request.authorName)
                    putExtra("commentText", request.commentText)
                    putExtra("userType", request.userType)
                    putExtra("adminId", request.adminId)
                    putExtra("userId", request.userId)
                }
                context.startActivity(intent)
            }
        }
    }
}
