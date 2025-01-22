package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.PauseTaskAdapter
import com.faa.cmsportalcui.AdminModel.PauseTask
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AdminPauseTaskListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pauseTaskAdapter: PauseTaskAdapter
    private val pauseTaskList = mutableListOf<PauseTask>()
    private var pauseTaskListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pause_task_list)

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pauseTaskAdapter = PauseTaskAdapter(pauseTaskList) { pauseTask ->
            val intent = Intent(this, AdminPauseDetailActivity::class.java).apply {
                putExtra("id", pauseTask.id)
                putExtra("title", pauseTask.title)
                putExtra("currentTime", pauseTask.currentTime)
                putExtra("description", pauseTask.description)
                putExtra("location", pauseTask.location)
                putExtra("photoUrl", pauseTask.photoUrl)
                putExtra("roomNumber", pauseTask.roomNumber)
                putExtra("staffId", pauseTask.staffId)
                putExtra("timestamp", pauseTask.timestamp)
                putExtra("userId", pauseTask.userId)
                putExtra("userType", pauseTask.userType)
                putExtra("assignedBy", pauseTask.assignedBy)
                putExtra("assignedTaskId", pauseTask.assignedTaskId)
                putExtra("comment", pauseTask.comment)
                putExtra("currentDate", pauseTask.currentDate)
            }
            startActivity(intent)
        }
        recyclerView.adapter = pauseTaskAdapter

        fetchPauseTasks()
    }

    private fun fetchPauseTasks() {
        val db = FirebaseFirestore.getInstance()

        pauseTaskListener = db.collection("pauseTask")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("AdminPauseTaskListActivity", "Error fetching pause tasks", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    pauseTaskList.clear()

                    for (document in snapshot) {
                        val pauseTask = document.toObject(PauseTask::class.java)
                        pauseTaskList.add(pauseTask)
                    }

                    // Notify the adapter of data changes
                    pauseTaskAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        pauseTaskListener?.remove()
    }
}