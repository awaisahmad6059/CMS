package com.faa.cmsportalcui.StaffAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffModel.CompletedTask
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class CompletedTaskAdapter(private val tasks: List<CompletedTask>) :
    RecyclerView.Adapter<CompletedTaskAdapter.TaskViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_complete_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.title
        holder.dateTextView.text = task.currentDate
        holder.timeTextView.text = task.currentTime

        val imageUrl = if (task.userId != null) {
            getUserProfileImageUrl(task.userId) { url ->
                Glide.with(holder.itemView.context)
                    .load(url)
                    .placeholder(R.drawable.account)
                    .into(holder.userImageView)
            }
        } else {
            getAdminProfileImageUrl("Ae01ooy19BMfZO8y80BwG6jOuP33") { url ->
                Glide.with(holder.itemView.context)
                    .load(url)
                    .placeholder(R.drawable.account)
                    .into(holder.userImageView)
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    private fun getUserProfileImageUrl(userId: String, callback: (String) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val imageUrl = document.getString("profileImageUrl")
                    imageUrl?.let { callback(it) }
                }
            }
            .addOnFailureListener {
            }
    }

    private fun getAdminProfileImageUrl(adminId: String, callback: (String) -> Unit) {
        firestore.collection("admins").document(adminId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val imageUrl = document.getString("profileImageUrl")
                    imageUrl?.let { callback(it) }
                }
            }
            .addOnFailureListener {
            }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImageView: CircleImageView = itemView.findViewById(R.id.userImage)
        val titleTextView: TextView = itemView.findViewById(R.id.taskTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.taskDate)
        val timeTextView: TextView = itemView.findViewById(R.id.taskTime)
    }
}