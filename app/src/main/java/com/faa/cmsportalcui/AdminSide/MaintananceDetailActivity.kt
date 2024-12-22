package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class MaintananceDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintanance_detail)

        val tvDueDate: TextView = findViewById(R.id.tvDueDate)
        val tvDescription: TextView = findViewById(R.id.tvDescription)
        val imageView: ImageView = findViewById(R.id.imageView)
        val ivProfileImage: CircleImageView = findViewById(R.id.ivProfileImage)
        val tvCommentAuthor: TextView = findViewById(R.id.tvCommentAuthor)
        val tvCommentText: TextView = findViewById(R.id.tvCommentText)
        val backButton: ImageView = findViewById(R.id.back_button)
        val btnClosed: Button = findViewById(R.id.btnClosed)
        val btnAssign: Button = findViewById(R.id.btnAssign)

        val id = intent.getStringExtra("id")
        val description = intent.getStringExtra("title")
        val timestamp = intent.getStringExtra("timestamp")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")
        val imageUrl = intent.getStringExtra("photoUrl")
        val authorName = intent.getStringExtra("authorname")
        val commentText = intent.getStringExtra("description")
        val userType = intent.getStringExtra("userType")
        val adminId = intent.getStringExtra("adminId")
        val userId = intent.getStringExtra("userId")

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        val formattedDate = outputFormat.format(date ?: Date())

        tvDueDate.text = formattedDate
        tvDescription.text = description
        tvCommentText.text = commentText

        if (userType == "admin") {
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.account)
                .into(ivProfileImage)

            tvCommentAuthor.text = authorName ?: "Admin"
        } else {
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.account)
                .into(ivProfileImage)

            tvCommentAuthor.text = authorName ?: "User"
        }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.image)
            .into(imageView)

        backButton.setOnClickListener {
            navigateToMaintananceActivity()
        }

        btnClosed.setOnClickListener {
            navigateToMaintananceActivity()
        }

        btnAssign.setOnClickListener {
            if (title.isNullOrEmpty() || description.isNullOrEmpty() || imageUrl.isNullOrEmpty() || timestamp.isNullOrEmpty()) {
                Toast.makeText(this, "Error: Missing required data", Toast.LENGTH_SHORT).show()
            } else {
                if (id != null) {
                    navigateToAssignWorkerActivity(
                        id, description, imageUrl,commentText, profileImageUrl, timestamp, adminId, userId
                    )
                }
            }
        }
    }

    private fun navigateToMaintananceActivity() {
        finish()
    }

    private fun navigateToAssignWorkerActivity(
        id: String,
        commentText: String?,
        description: String?,
        photoUrl: String?,
        profileImageUrl: String?,
        timestamp: String?,
        adminId: String?,
        userId: String?
    ) {
        val intent = Intent(this, StaffActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("description", commentText)
        intent.putExtra("title", description)
        intent.putExtra("photoUrl", photoUrl)
        intent.putExtra("profileImageUrl", profileImageUrl)
        intent.putExtra("timestamp", timestamp)
        intent.putExtra("adminId", adminId)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}
