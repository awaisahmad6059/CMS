package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffAdapter.CompletedTaskAdapter
import com.faa.cmsportalcui.StaffModel.CompletedTask
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class StaffCompleteTaskActivity : AppCompatActivity() {

    private var staffId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var addTask: Button
    private lateinit var backBtn: ImageView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var completedTaskAdapter: CompletedTaskAdapter
    private var tasksListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_complete_task)

        staffId = intent.getStringExtra("staffId")

        recyclerView = findViewById(R.id.recyclerViewCompletedTasks)
        addTask = findViewById(R.id.addCompletedTaskButton)
        backBtn = findViewById(R.id.back_button)

        firestore = FirebaseFirestore.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(this)
        completedTaskAdapter = CompletedTaskAdapter(emptyList())
        recyclerView.adapter = completedTaskAdapter

        setupRealTimeTaskUpdates()

        addTask.setOnClickListener {
            startActivity(Intent(this@StaffCompleteTaskActivity, StaffAssignedMeActivity::class.java).apply {
                putExtra("staffId", staffId)
            })
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, StaffDashboardActivity::class.java)
            intent.putExtra("staff_id", staffId)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tasksListener?.remove()
    }

    private fun setupRealTimeTaskUpdates() {
        staffId?.let {
            tasksListener = firestore.collection("completeTask")
                .whereEqualTo("staffId", it)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("StaffCompleteTaskActivity", "Error fetching tasks", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val tasks = snapshot.documents.mapNotNull { document ->
                            document.toObject(CompletedTask::class.java)
                        }
                        completedTaskAdapter = CompletedTaskAdapter(tasks)
                        recyclerView.adapter = completedTaskAdapter
                    }
                }
        }
    }
}
