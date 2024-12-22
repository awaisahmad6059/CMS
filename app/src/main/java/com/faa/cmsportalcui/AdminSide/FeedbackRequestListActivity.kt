package com.faa.cmsportalcui.AdminSide

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.AdminFeedbackListAdapter
import com.faa.cmsportalcui.AdminModel.AdminFeedbackList
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackRequestListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminFeedbackListAdapter
    private val feedbackList = mutableListOf<AdminFeedbackList>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_request_list)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        loadCompletedTasksWithReview()
    }

    private fun loadCompletedTasksWithReview() {
        db.collection("completeTask")
            .whereNotEqualTo("review", null)
            .get()
            .addOnSuccessListener { result ->
                feedbackList.clear()
                for (document in result) {
                    val feedback = document.toObject(AdminFeedbackList::class.java)
                    feedbackList.add(feedback)
                }

                adapter = AdminFeedbackListAdapter(this, feedbackList) { selectedFeedback ->
                    Toast.makeText(this, "Selected: ${selectedFeedback.title}", Toast.LENGTH_SHORT).show()
                }

                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load tasks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
