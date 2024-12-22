package com.faa.cmsportalcui.UserAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserModel.UserCompleteTask
import com.faa.cmsportalcui.UserSide.UserFeedbackActivity

class UserCompleteTaskAdapter(
    private val context: Context,
    private val taskList: List<UserCompleteTask>,
    private val userId: String
) : RecyclerView.Adapter<UserCompleteTaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_complete_task_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.title.text = task.title
        holder.date.text = "Submitted on ${task.currentDate}"

        holder.feedbackButton.setOnClickListener {
            val intent = Intent(context, UserFeedbackActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("taskId", task.taskId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val date: TextView = itemView.findViewById(R.id.date)
        val feedbackButton: Button = itemView.findViewById(R.id.feedback)
    }
}
