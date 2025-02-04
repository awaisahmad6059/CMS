package com.faa.cmsportalcui.AdminSide

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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
    private lateinit var searchBar: EditText
    private val taskList = mutableListOf<AdminCompleteTask>()
    private val staffNames = mutableMapOf<String, String>()
    private var filteredTaskList = mutableListOf<AdminCompleteTask>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_task)

        firestore = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchBar = findViewById(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTasks(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })
        fetchStaffNamesAndTasks()

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

                filteredTaskList = taskList.toMutableList()  // Initially show all tasks
                adapter = AdminCompleteTaskAdapter(filteredTaskList, staffNames)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
            }
    }
    private fun filterTasks(query: String) {
        filteredTaskList = if (query.isEmpty()) {
            taskList.toMutableList()
        } else {
            taskList.filter {
                it.title.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        adapter.updateTaskList(filteredTaskList)
    }
}
