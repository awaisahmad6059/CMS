package com.faa.cmsportalcui.AdminSide

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.AdminCompleteTaskAdapter
import com.faa.cmsportalcui.AdminModel.AdminCompleteTask
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore

class CompleteTaskActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: AdminCompleteTaskAdapter
    private lateinit var recyclerView: RecyclerView
    private val taskList = mutableListOf<AdminCompleteTask>()
    private val staffNames = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_task)

        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch tasks and staff data
        fetchStaffNamesAndTasks()
        // Set up back button click listener
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
    }

    private fun fetchStaffNamesAndTasks() {
        firestore.collection("staff")
            .get()
            .addOnSuccessListener { staffDocuments ->
                for (staffDoc in staffDocuments) {
                    val staffId = staffDoc.id
                    val staffName = staffDoc.getString("name") ?: "Unknown Staff"
                    staffNames[staffId] = staffName
                }
                fetchCompleteTasks()
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

    private fun fetchCompleteTasks() {
        firestore.collection("completeTask")
            .get()
            .addOnSuccessListener { taskDocuments ->
                taskList.clear()
                for (taskDoc in taskDocuments) {
                    val task = taskDoc.toObject(AdminCompleteTask::class.java)
                    taskList.add(task)
                }
                adapter = AdminCompleteTaskAdapter(taskList, staffNames)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}
