package com.faa.cmsportalcui.StaffAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminModel.AdminEquipmentRequest
import com.faa.cmsportalcui.R

class EquipmentRequestAdapter(private var requests: MutableList<AdminEquipmentRequest>) :
    RecyclerView.Adapter<EquipmentRequestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val equipment: TextView = view.findViewById(R.id.equipment)
        val time: TextView = view.findViewById(R.id.time)
        val reason: TextView = view.findViewById(R.id.rason)
        val status: Button = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_equiment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]

        holder.equipment.text = request.equipmentName
        holder.time.text = request.requestTimestamp
        holder.reason.text = request.reason
        holder.status.text = request.status

        when (request.status) {
            "Pending" -> holder.status.setBackgroundColor(0xFFFFC107.toInt()) // Yellow
            "approved" -> holder.status.setBackgroundColor(0xFF4CAF50.toInt()) // Green
            "rejected" -> holder.status.setBackgroundColor(0xFFF44336.toInt()) // Red
        }

        holder.status.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = requests.size

    fun updateList(newRequests: List<AdminEquipmentRequest>) {
        requests.clear()
        requests.addAll(newRequests)
        notifyDataSetChanged()
    }
}
