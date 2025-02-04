package com.faa.cmsportalcui.UserSide

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
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
    private val filteredTaskList = mutableListOf<UserCompleteTask>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_complete_feddback)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUserId = intent.getStringExtra("user_id") ?: run {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        adapter = UserCompleteTaskAdapter(this, filteredTaskList, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchCompleteTasks(currentUserId)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        val searchView = findViewById<EditText>(R.id.searchView)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTasks(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun fetchCompleteTasks(currentUserId: String) {
        firestore.collection("completeTask")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                completeTaskList.clear()

                if (documents.isEmpty) {
                    Toast.makeText(this, "No completed tasks found", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in documents) {
                        val completeTask = document.toObject(UserCompleteTask::class.java).apply {
                            taskId = document.id
                        }
                        completeTaskList.add(completeTask)
                    }

                    filteredTaskList.addAll(completeTaskList) // Copy all tasks to filtered list initially
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterTasks(query: String) {
        filteredTaskList.clear()

        if (query.isEmpty()) {
            filteredTaskList.addAll(completeTaskList)
        } else {
            val lowerCaseQuery = query.lowercase()
            completeTaskList.filterTo(filteredTaskList) { task ->
                task.title.lowercase().contains(lowerCaseQuery)
            }
        }

        adapter.notifyDataSetChanged() // Notify adapter that data has changed
    }
}
