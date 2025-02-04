package com.faa.cmsportalcui.AdminAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminModel.PauseTask
import com.faa.cmsportalcui.R
import com.squareup.picasso.Picasso

class PauseTaskAdapter(
    private var pauseTaskList: List<PauseTask>,
    private val onViewButtonClick: (PauseTask) -> Unit
) : RecyclerView.Adapter<PauseTaskAdapter.PauseTaskViewHolder>() {

    private var filteredList: MutableList<PauseTask> = pauseTaskList.toMutableList()

    inner class PauseTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val requestId: TextView = itemView.findViewById(R.id.request_id)
        private val requestTitle: TextView = itemView.findViewById(R.id.request_title)
        private val viewButton: Button = itemView.findViewById(R.id.view_button)
        private val profileImage: ImageView = itemView.findViewById(R.id.ProfileImage)

        fun bind(pauseTask: PauseTask) {
            requestId.text = pauseTask.id
            requestTitle.text = pauseTask.title

            // Load profile image using Picasso
            if (pauseTask.profileImageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(pauseTask.profileImageUrl)
                    .placeholder(R.drawable.account)  // Default image
                    .error(R.drawable.account)  // Error image
                    .into(profileImage)
            }

            viewButton.setOnClickListener {
                onViewButtonClick(pauseTask)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PauseTaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pause_request, parent, false)
        return PauseTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: PauseTaskViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    // Function to update the entire list
    fun updateList(newList: List<PauseTask>) {
        pauseTaskList = newList
        filteredList = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            pauseTaskList.toMutableList()
        } else {
            pauseTaskList.filter {
                it.title.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

}
