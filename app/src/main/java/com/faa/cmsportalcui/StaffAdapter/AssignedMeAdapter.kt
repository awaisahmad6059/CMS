package com.faa.cmsportalcui.StaffAdapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffModel.AssignedMe

class AssignedMeAdapter(
    private val tasks: List<AssignedMe>,
    private val onItemClick: (AssignedMe) -> Unit,
    private val onDetailClick: (AssignedMe) -> Unit
) : RecyclerView.Adapter<AssignedMeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val timestamp: TextView = itemView.findViewById(R.id.date)
        private val detailButton: Button = itemView.findViewById(R.id.detail)

        init {
            itemView.setOnClickListener {
                val task = tasks[adapterPosition]
                onItemClick(task)
            }

            detailButton.setOnClickListener {
                val task = tasks[adapterPosition]
                onDetailClick(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_assignedme, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.timestamp.text = task.timestamp
    }

    override fun getItemCount(): Int = tasks.size
}
