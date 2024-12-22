package com.faa.cmsportalcui.AdminAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.AdminModel.Staff
import com.faa.cmsportalcui.AdminSide.AdminDashboardActivity
import com.faa.cmsportalcui.AdminSide.MaintananceStaffDetailsActivity
import com.faa.cmsportalcui.AdminSide.StaffProfileActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class StaffAdapter(
    private val context: Context,
    private val staffList: List<Staff>,
    private val id: String?,
    private val commentText: String?,
    private val description: String?,
    private val photoUrl: String?,
    private val profileImageUrl: String?,
    private val timestamp: String?,
    private val progressBar: ProgressBar,
    private val adminId: String?,
    private val userId: String?
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.staff_item, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = staffList[position]

        holder.tvName.text = staff.name
        holder.staffEmail.text = staff.email
        Glide.with(holder.itemView.context)
            .load(staff.profileImageUrl)
            .placeholder(R.drawable.account)
            .into(holder.ivProfileImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MaintananceStaffDetailsActivity::class.java).apply {
                putExtra("staffId", staff.id)
                putExtra("taskId", generateRandomNumericId())
                putExtra("id", id)
                putExtra("title", description)
                putExtra("description", commentText)
                putExtra("photoUrl", photoUrl)
                putExtra("profileImageUrl", profileImageUrl)
                putExtra("timestamp", timestamp)
                putExtra("adminId", adminId)
                putExtra("userId", userId)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, StaffProfileActivity::class.java).apply {
                putExtra("staffId", staff.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            showDeleteConfirmationDialog(staff.id, position)
            true
        }
    }

    override fun getItemCount() = staffList.size

    private fun showDeleteConfirmationDialog(staffId: String, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this staff member?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteStaff(staffId, position)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteStaff(staffId: String, position: Int) {
        if (staffId.isNotEmpty()) {
            FirebaseFirestore.getInstance().collection("staff").document(staffId)
                .delete()
                .addOnSuccessListener {
                    Log.d("StaffAdapter", "DocumentSnapshot successfully deleted!")
                    (staffList as MutableList).removeAt(position)
                    notifyItemRemoved(position)
                }
                .addOnFailureListener { e ->
                    Log.w("StaffAdapter", "Error deleting document", e)
                }
        } else {
            Log.e("StaffAdapter", "Invalid staffId: $staffId")
        }
    }

    private fun createSubcollectionForStaff(staffId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val subcollectionData = hashMapOf(
            "id" to id,
            "title" to description,
            "description" to commentText,
            "photoUrl" to photoUrl,
            "profileImageUrl" to profileImageUrl,
            "timestamp" to timestamp
        )

        progressBar.visibility = View.VISIBLE

        val randomId = generateRandomNumericId()
        firestore.collection("staff").document(staffId)
            .collection("assignedTasks")
            .document(randomId)
            .set(subcollectionData)
            .addOnSuccessListener {
                Log.d("StaffAdapter", "Subcollection document successfully created!")
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Request assigned successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, AdminDashboardActivity::class.java)
                context.startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w("StaffAdapter", "Error creating subcollection document", e)
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Failed to assign request", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateRandomNumericId(): String {
        return Random.nextInt(10000, 99999).toString()
    }

    class StaffViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val staffEmail: TextView = itemView.findViewById(R.id.staffemail)
        val editButton: ImageView = itemView.findViewById(R.id.editButton)
    }
}
