package com.faa.cmsportalcui.AdminAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.AdminModel.AdminEquipmentRequest
import com.faa.cmsportalcui.AdminModel.Staff
import com.faa.cmsportalcui.AdminSide.AdminEquipmentAproveRejectActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class AdminEquipmentRequestAdapter(
    private val equipmentRequests: List<AdminEquipmentRequest>
) : RecyclerView.Adapter<AdminEquipmentRequestAdapter.EquipmentRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_equiment_approval, parent, false)
        return EquipmentRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentRequestViewHolder, position: Int) {
        val equipmentRequest = equipmentRequests[position]
        holder.requestTitleTextView.text = equipmentRequest.title

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("staff")
            .document(equipmentRequest.staffId)
            .get()
            .addOnSuccessListener { document ->
                val staff = document.toObject(Staff::class.java)
                staff?.let { staffData ->
                    holder.nameTextView.text = staffData.name
                    Glide.with(holder.profileImageView.context)
                        .load(staffData.profileImageUrl)
                        .placeholder(R.drawable.account)
                        .into(holder.profileImageView)

                    holder.viewButton.setOnClickListener {
                        val context = holder.itemView.context
                        val intent = Intent(context, AdminEquipmentAproveRejectActivity::class.java).apply {
                            putExtra("profileImageUrl", staffData.profileImageUrl)
                            putExtra("name", staffData.name)
                            putExtra("experience", staffData.experience)
                            putExtra("title", equipmentRequest.title)
                            putExtra("assignedTaskId", equipmentRequest.assignedTaskId)                         }
                        context.startActivity(intent)
                    }
                }
            }
        holder.viewButton.text = equipmentRequest.status
    }

    override fun getItemCount(): Int = equipmentRequests.size

    class EquipmentRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: CircleImageView = itemView.findViewById(R.id.ProfileImage)
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val requestTitleTextView: TextView = itemView.findViewById(R.id.request_title)
        val viewButton: Button = itemView.findViewById(R.id.status)
    }
}
