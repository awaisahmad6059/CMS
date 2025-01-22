package com.faa.cmsportalcui.AdminAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminModel.PauseTask
import com.faa.cmsportalcui.R

class PauseTaskAdapter(
    private val pauseTaskList: List<PauseTask>,
    private val onViewButtonClick: (PauseTask) -> Unit
) : RecyclerView.Adapter<PauseTaskAdapter.PauseTaskViewHolder>() {

    inner class PauseTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val requestId: TextView = itemView.findViewById(R.id.request_id)
        private val requestTitle: TextView = itemView.findViewById(R.id.request_title)
        private val viewButton: Button = itemView.findViewById(R.id.view_button)

        fun bind(pauseTask: PauseTask) {
            requestId.text = pauseTask.id
            requestTitle.text = pauseTask.title

            // Set click listener for the view button
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
        holder.bind(pauseTaskList[position])
    }

    override fun getItemCount(): Int = pauseTaskList.size
}