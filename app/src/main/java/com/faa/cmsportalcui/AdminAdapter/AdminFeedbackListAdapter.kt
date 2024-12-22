package com.faa.cmsportalcui.AdminAdapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.AdminModel.AdminFeedbackList
import com.faa.cmsportalcui.AdminSide.FeedbackActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class AdminFeedbackListAdapter(
    private val context: Context,
    private val feedbackList: List<AdminFeedbackList>,
    private val onItemClickListener: (AdminFeedbackList) -> Unit
) : RecyclerView.Adapter<AdminFeedbackListAdapter.FeedbackViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_feedback_request, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]

        holder.requestId.text = feedback.assignedTaskId
        holder.requestTitle.text = feedback.title

        loadStaffProfileImage(feedback.staffId, holder.profileImage)

        holder.viewButton.setOnClickListener {
            val intent = Intent(context, FeedbackActivity::class.java).apply {
                putExtra("id", feedback.id)
                putExtra("assignedBy", feedback.assignedBy)
                putExtra("review", feedback.review)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }

    inner class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val requestId: TextView = itemView.findViewById(R.id.request_id)
        val requestTitle: TextView = itemView.findViewById(R.id.request_title)
        val profileImage: CircleImageView = itemView.findViewById(R.id.ProfileImage)
        val viewButton: Button = itemView.findViewById(R.id.view_button)
    }

    private fun loadStaffProfileImage(staffId: String, profileImageView: CircleImageView) {
        db.collection("staff").document(staffId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val imageUrl = document.getString("profileImageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.account)
                            .into(profileImageView)
                    }
                }
            }
            .addOnFailureListener { e ->
                profileImageView.setImageResource(R.drawable.account)
            }
    }
}
