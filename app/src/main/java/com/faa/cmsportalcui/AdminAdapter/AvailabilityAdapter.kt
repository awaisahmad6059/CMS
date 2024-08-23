package com.faa.cmsportalcui.AdminAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminModel.Availability
import com.faa.cmsportalcui.R

class AvailabilityAdapter(private val availabilityList: List<Availability>) :
    RecyclerView.Adapter<AvailabilityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val availability = availabilityList[position]
        holder.dayLabel.text = availability.day
    }

    override fun getItemCount() = availabilityList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayLabel: TextView = itemView.findViewById(R.id.dayLabel)
    }
}
