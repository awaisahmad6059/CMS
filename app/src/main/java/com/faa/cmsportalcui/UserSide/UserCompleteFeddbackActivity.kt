package com.faa.cmsportalcui.UserSide

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserAdapter.UserCompleteTaskAdapter
import com.faa.cmsportalcui.UserModel.UserCompleteTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserCompleteFeddbackActivity : AppCompatActivity() {

    private lateinit var adapter: UserCompleteTaskAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val completeTaskList = mutableListOf<UserCompleteTask>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_complete_feddback)

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUserId = intent.getStringExtra("user_id") ?: run {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup RecyclerView Adapter
        adapter = UserCompleteTaskAdapter(this, completeTaskList, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Fetch completed tasks
        fetchCompleteTasks(currentUserId)

        // Set up back button
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
    }

    private fun fetchCompleteTasks(currentUserId: String) {
        Log.d("UserCompleteFeddback", "Fetching tasks for userId: $currentUserId")

        // Query Firestore for tasks where the "userId" matches the current userId
        firestore.collection("completeTask")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                completeTaskList.clear()

                if (documents.isEmpty) {
                    Log.d("UserCompleteFeddback", "No completed tasks found for userId: $currentUserId")
                    Toast.makeText(this, "No completed tasks found", Toast.LENGTH_SHORT).show()
                } else {
                    // Loop through the documents and add them to the list
                    for (document in documents) {
                        Log.d("UserCompleteFeddback", "Document ID: ${document.id}")
                        Log.d("UserCompleteFeddback", "Document Data: ${document.data}")

                        // Convert document to UserCompleteTask object
                        val completeTask = document.toObject(UserCompleteTask::class.java).apply {
                            taskId = document.id // Set the taskId to the Firestore document ID
                        }
                        completeTaskList.add(completeTask)
                        Log.d("UserCompleteFeddback", "Fetched task: ${completeTask.title} with taskId: ${completeTask.taskId}")
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserCompleteFeddback", "Failed to fetch tasks", e)
                Toast.makeText(this, "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
            }
    }

}
