package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
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
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pause_task_list)

        val backButton: ImageButton = findViewById(R.id.back_button)
        searchBar = findViewById(R.id.search_bar)
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
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pauseTaskAdapter.filterList(s.toString())
            }
        })
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

                        // Fetch staff profile image
                        db.collection("staff").document(pauseTask.staffId)
                            .get()
                            .addOnSuccessListener { staffDoc ->
                                if (staffDoc.exists()) {
                                    val profileImageUrl = staffDoc.getString("profileImageUrl") ?: ""
                                    pauseTaskList.add(pauseTask.copy(profileImageUrl = profileImageUrl))
                                    pauseTaskAdapter.updateList(pauseTaskList)                                }
                            }
                    }
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        pauseTaskListener?.remove()
    }
}