package com.faa.cmsportalcui.AdminAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.TextView
import android.widget.Button
import com.faa.cmsportalcui.AdminModel.MaintenanceRequest
import com.faa.cmsportalcui.AdminSide.MaintananceDetailActivity

class MaintenanceRequestAdapter(
    private var requests: MutableList<MaintenanceRequest>
) : RecyclerView.Adapter<MaintenanceRequestAdapter.MaintenanceRequestViewHolder>() {

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


    class MaintenanceRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: CircleImageView = itemView.findViewById(R.id.ProfileImage)
        private val requestId: TextView = itemView.findViewById(R.id.request_id)
        private val requestTitle: TextView = itemView.findViewById(R.id.request_title)
        private val viewButton: Button = itemView.findViewById(R.id.view_button)

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

            viewButton.setOnClickListener {
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
